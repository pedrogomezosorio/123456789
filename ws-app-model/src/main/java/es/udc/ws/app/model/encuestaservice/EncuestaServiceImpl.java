package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaDao;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EncuestaServiceImpl implements EncuestaService
{

    private final SqlEncuestaDao encuestaDao;
    private final SqlRespuestaDao respuestaDao;

    public EncuestaServiceImpl(SqlEncuestaDao encuestaDao, SqlRespuestaDao respuestaDao)
    {
        this.encuestaDao = encuestaDao;
        this.respuestaDao = respuestaDao;
    }

    @Override
    public void cancelarEncuesta(long encuestaId)
            throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {

        Optional<Encuesta> optEncuesta = encuestaDao.findById(encuestaId);

        if (optEncuesta.isEmpty()) {
            throw new InstanceNotFoundException("Encuesta", encuestaId);
        }

        Encuesta encuesta = optEncuesta.get();

        if (encuesta.isCancelada()) {
            throw new EncuestaCanceladaException(encuestaId);
        }

        if (encuesta.estaFinalizada(LocalDateTime.from(Instant.now()))) {
            throw new EncuestaFinalizadaException(encuestaId);
        }

        encuestaDao.setCancelada(encuestaId, true);
    }

    @Override
    public List<Respuesta> obtenerRespuestas(long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException {

        Optional<Encuesta> encuesta = encuestaDao.findById(encuestaId);
        if (encuesta.isEmpty()) {
            throw new InstanceNotFoundException("Encuesta", encuestaId);
        }

        return soloPositivas
                ? respuestaDao.findPositivasByEncuesta(encuestaId)
                : respuestaDao.findByEncuesta(encuestaId);
    }

    @Override
    public void crearEncuesta(Encuesta encuesta) throws InstanceNotFoundException
    {

    }
}
