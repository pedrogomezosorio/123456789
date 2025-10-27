package es.udc.ws.app.model.encuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlEncuestaDao
{
    Encuesta create(Connection connection, Encuesta encuesta);

    List<Encuesta> find(Connection connection, Long id) throws InstanceNotFoundException;

    List<Encuesta> findByKeywords(Connection connection, String keyword);

    void update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException;

    void remove(Connection connection, Long encuestaId) throws InstanceNotFoundException;
}
