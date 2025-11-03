package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;
import es.udc.ws.util.exceptions.InputValidationException;

import java.util.List;

public interface EncuestaService
{
    /* 1 */ void crearEncuesta(Encuesta encuesta) throws InputValidationException;

    /* 2 */ List<Encuesta> obtenerEncuestas(long id, boolean incluirFinalizadas) throws InstanceNotFoundException;

    /* 3 */ Encuesta obtenerInformacion(long encuestaId) throws InstanceNotFoundException;

    /* 4 */ Encuesta responder(long encuestaId, String email, boolean voto) throws InstanceNotFoundException, EncuestaFinalizadaException, InputValidationException, EncuestaCanceladaException;

    /* 5 */ void cancelarEncuesta(long encuestaId) throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException;

    /* 6 */ List<Respuesta> obtenerRespuestas(long encuestaId, boolean soloPositivas) throws InstanceNotFoundException;
}
