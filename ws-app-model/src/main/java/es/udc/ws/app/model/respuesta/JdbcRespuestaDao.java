package es.udc.ws.app.model.respuesta;

import java.sql.*;

public abstract class JdbcRespuestaDao extends AbstractSqlRespuestaDao
{
    @Override
    public Respuesta create(Connection connection, Respuesta respuesta)
    {
        String queryString = "INSERT INTO Respuesta(respuestaId, encuestaId, email, positiva, fechaRespuesta) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS))
        {
            int i = 1;

            preparedStatement.setLong(i++, respuesta.getRespuestaId());
            preparedStatement.setLong(i++, respuesta.getEncuestaId());
            preparedStatement.setString(i++, respuesta.getEmail());
            preparedStatement.setBoolean(i++, respuesta.isPositiva());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(respuesta.getFechaRespuesta()));

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) throw new SQLException("JDBC driver did not return generated key.");

            return new Respuesta(respuesta.getRespuestaId(), respuesta.getEncuestaId(), respuesta.getEmail(), respuesta.isPositiva(), respuesta.getFechaRespuesta());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}