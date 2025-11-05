package es.udc.ws.app.model.respuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlRespuestaDao
{
    Respuesta create(Connection connection, Respuesta respuesta);

    List<Respuesta> findPositivasByEncuesta(long encuestaId) throws InstanceNotFoundException;

    Respuesta find(Connection conection, long encuestaId) throws InstanceNotFoundException;

    List<Respuesta> findByEncuesta(Connection connection, long encuestaId, boolean soloPositivas) throws InstanceNotFoundException;

    List<Respuesta> findByEmail(Connection connection, String email) throws InstanceNotFoundException;

    void update(Connection conection, Respuesta respuesta) throws InstanceNotFoundException;

    void remove(Connection conection, long encuestaId) throws InstanceNotFoundException;
}
