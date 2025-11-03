package es.udc.ws.app.model.encuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEncuestaDao extends AbstractSqlEncuestaDao
{
    @Override
    public Encuesta create(Connection connection, Encuesta encuesta)
    {
        final String sql = "INSERT INTO encuesta (id, pregunta, fecha_creacion, fecha_fin, cancelada) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            int i = 1;

            preparedStatement.setLong(i++, encuesta.getId());
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());

            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys())
            {
                if (resultSet.next())
                {
                    encuesta.setId(resultSet.getLong(1));
                    return encuesta;
                }
                throw new SQLException("Error: no se devolvió el ID generado.");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al crear la encuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public Encuesta find(Connection connection, Long id) throws InstanceNotFoundException
    {
        String queryString = "SELECT pregunta, runtime, description, price, creationDate FROM ENCUESTA WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, id.longValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new InstanceNotFoundException(id, Encuesta.class.getName());

            List<Encuesta> encuestas = new ArrayList<Encuesta>();

            i = 1;
            long EncuestaId = resultSet.getLong(i++);
            String pregunta = resultSet.getString(i++);
            LocalDateTime creationDate = resultSet.getTimestamp(i++).toLocalDateTime();
            LocalDateTime endDate = resultSet.getTimestamp(i++).toLocalDateTime();
            boolean cancelada = resultSet.getBoolean(i++);
            int positivas = resultSet.getInt(i++);
            int negativas = resultSet.getInt(i++);

            return new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada, positivas, negativas);

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean incluirPasadas)
    {
        String sql = "SELECT id, pregunta, fecha_creacion, fecha_fin, cancelada FROM encuesta WHERE pregunta LIKE ?";

        LocalDateTime now = LocalDateTime.now().withNano(0);

        if (!incluirPasadas)
        {
            sql += " AND fecha_fin > ?";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            int paramIndex = 1;

            preparedStatement.setString(paramIndex++, "%" + keyword + "%");

            if (!incluirPasadas)
            {
                preparedStatement.setObject(paramIndex, now);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                List<Encuesta> encuestas = new ArrayList<>();
                while (resultSet.next())
                {
                    encuestas.add(mapEncuesta(resultSet));
                }
                return encuestas;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error executing findByKeywords for keyword: " + keyword + ". Details: " + e.getMessage(), e);
        }
    }

    private Encuesta mapEncuesta(ResultSet resultSet) throws SQLException
    {
        Encuesta encuesta = new Encuesta();
        encuesta.setId(resultSet.getLong("id"));
        encuesta.setPregunta(resultSet.getString("pregunta"));
        encuesta.setFechaCreacion(resultSet.getTimestamp("fecha_creacion").toLocalDateTime());
        encuesta.setFechaFin(resultSet.getTimestamp("fecha_fin").toLocalDateTime());
        encuesta.setCancelada(resultSet.getBoolean("cancelada"));
        
        return encuesta;
    }
}