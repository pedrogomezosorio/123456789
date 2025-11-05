package es.udc.ws.app.model.respuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractSqlRespuestaDao implements SqlRespuestaDao
{
    protected AbstractSqlRespuestaDao() {}

    @Override
    public Respuesta find(Connection connection, long respuestaId) throws InstanceNotFoundException
    {
        String queryString = "SELECT respuestaId, encuestaId, email, positiva, fechaRespuesta FROM Respuesta WHERE respuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, respuestaId);

            ResultSet resultSet = preparedStatement.executeQuery();

            i = 1;
            long id = resultSet.getLong(i++);
            long encuestaId = resultSet.getLong(i++);
            String email = resultSet.getString(i++);
            boolean positiva = resultSet.getBoolean(i++);
            LocalDateTime fechaRespuesta = resultSet.getTimestamp(i++).toLocalDateTime();

            if (!resultSet.next()) throw new InstanceNotFoundException(respuestaId, Respuesta.class.getName());

            return new Respuesta(id, encuestaId, email, positiva, fechaRespuesta);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Respuesta> findByEncuesta(Connection connection, long encuestaId, boolean soloPositivas) throws InstanceNotFoundException
    {
        String queryString = "SELECT respuestaId, encuestaId, email, positiva, fechaRespuesta FROM Respuesta WHERE encuestaId= ?";

        queryString += " ORDER BY email";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            preparedStatement.setLong(1, encuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Respuesta> respuestas = new ArrayList<Respuesta>();

            while (resultSet.next())
            {
                int i = 1;
                long id = resultSet.getLong(i++);
                long encuesta_Id= resultSet.getLong(i++);
                String email = resultSet.getString(i++);
                boolean positiva = resultSet.getBoolean(i++);
                LocalDateTime fechaRespuesta = resultSet.getTimestamp(i++).toLocalDateTime();

                if(soloPositivas && !positiva) continue;

                respuestas.add(new Respuesta(id, encuesta_Id, email, positiva, fechaRespuesta));
            }
            if(respuestas.isEmpty())
                throw new InstanceNotFoundException(encuestaId, Respuesta.class.getName());

            return respuestas;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException
    {
        String queryString = "UPDATE Respuesta SET email = ?, positiva = ?, fechaRespuesta = ? WHERE  respuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            LocalDateTime now = LocalDateTime.now().withNano(0);
            preparedStatement.setString(i++, respuesta.getEmail());
            preparedStatement.setBoolean(i++, respuesta.isPositiva());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(now));
            preparedStatement.setLong(i++, respuesta.getRespuestaId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0)
                throw new InstanceNotFoundException(respuesta.getEncuestaId(), Respuesta.class.getName());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, long respuestaId) throws InstanceNotFoundException
    {
        String queryString = "DELETE FROM Respuesta WHERE respuestaId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, respuestaId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0)
                throw new es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException(respuestaId, Respuesta.class.getName());

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Respuesta> findByEmail(Connection connection, String email) throws InstanceNotFoundException
    {
        String queryString = "SELECT respuestaId, encuestaId, email, positiva, fechaRespuesta FROM Respuesta WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            List<Respuesta> respuestas = new ArrayList<>();

            while (resultSet.next())
            {
                int i = 1;
                long respuestaId = resultSet.getLong(i++);
                String email1 = resultSet.getString(i++);
                long encuestaId = resultSet.getLong(i++);
                boolean positiva = resultSet.getBoolean(i++);
                LocalDateTime fechaRespuesta = resultSet.getTimestamp(i++).toLocalDateTime();

                respuestas.add(new Respuesta(respuestaId, encuestaId, email, positiva, fechaRespuesta));
            }
            if(respuestas.isEmpty())
                throw new InstanceNotFoundException((long)0, Respuesta.class.getName());
            return respuestas;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
