package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.util.List;

/**
 * Servicio de dominio para gestionar encuestas.
 * Implementa las funcionalidades 5 y 6 del enunciado:
 *  - Cancelar encuesta.
 *  - Obtener respuestas (todas o solo positivas).
 */
public interface EncuestaService {

    void crearEncuesta(Encuesta encuesta) throws InstanceNotFoundException;

    void cancelarEncuesta(long encuestaId) throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException;

    List<Respuesta> obtenerRespuestas(long encuestaId, boolean soloPositivas) throws InstanceNotFoundException;
}
