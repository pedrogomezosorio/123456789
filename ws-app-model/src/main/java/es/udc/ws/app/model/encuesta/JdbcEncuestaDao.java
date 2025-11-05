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
        final String sql = "INSERT INTO Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada, respuestasPositivas, respuestasNegativas) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
    public Encuesta find(Connection connection, long id) throws InstanceNotFoundException
    {
        String queryString = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, cancelada, respuestasPositivas, respuestasNegativas FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, id);
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
        String sql = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, cancelada, respuestasPositivas, respuestasNegativas FROM Encuesta WHERE pregunta LIKE ?";

        LocalDateTime now = LocalDateTime.now().withNano(0);

        if (!incluirPasadas)
        {
            sql += " AND fechaFin > ?";
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
            throw new RuntimeException("Error ejecutando findByKeyword buscando por: " + keyword + ". Detalles: " + e.getMessage(), e);
        }
    }

    private Encuesta mapEncuesta(ResultSet resultSet) throws SQLException
    {
        Encuesta encuesta = new Encuesta();
        encuesta.setId(resultSet.getLong("encuestaId"));
        encuesta.setPregunta(resultSet.getString("pregunta"));
        encuesta.setFechaCreacion(resultSet.getTimestamp("fechaCreacion").toLocalDateTime());
        encuesta.setFechaFin(resultSet.getTimestamp("fechaFin").toLocalDateTime());
        encuesta.setCancelada(resultSet.getBoolean("cancelada"));
        encuesta.setRepuestasPositivas(resultSet.getInt("respuestasPositivas"));
        encuesta.setRespuestasNegativas(resultSet.getInt("respuestasNegativas"));

        return encuesta;
    }
    @Override
    public long countPositivas(Connection connection, Long encuestaId) {
        // TODO: Arreglar esto para que use la 'connection' recibida
        String sql = "SELECT COUNT(*) FROM Respuesta WHERE encuesta_id=? AND positiva=TRUE";
        // ... (Implementa la lógica SQL, ¡usando la 'connection'!)
        throw new UnsupportedOperationException("countPositivas no implementado");
    }

    @Override
    public long countNegativas(Connection connection, Long encuestaId) {
        // TODO: Arreglar esto para que use la 'connection' recibida
        String sql = "SELECT COUNT(*) FROM Respuesta WHERE encuesta_id=? AND positiva=FALSE";
        // ... (Implementa la lógica SQL, ¡usando la 'connection'!)
        throw new UnsupportedOperationException("countNegativas no implementado");
    }
}