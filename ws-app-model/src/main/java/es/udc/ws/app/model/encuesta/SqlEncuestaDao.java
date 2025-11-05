package es.udc.ws.app.model.encuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlEncuestaDao
{
    Encuesta create(Connection connection, Encuesta encuesta);

    Encuesta find(Connection connection, long respuestaId) throws InstanceNotFoundException;

    List<Encuesta> findByKeyword(Connection connection, String keyword, boolean incluirPasadas);

    void update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException;

    void remove(Connection connection, long respuestaId) throws InstanceNotFoundException;

    long countPositivas(Connection connection, Long encuestaId);

    long countNegativas(Connection connection, Long encuestaId);
}
