package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del DAO de Encuesta.
 *
 * Esta clase usa el DataSourceLocator del entorno ISD para obtener conexiones
 * a la base de datos (ya configurado en SimpleDataSource.properties o JNDI).
 */
public class JdbcEncuestaDao extends AbstractSqlEncuestaDao {

    private static final String DATA_SOURCE = "ws-javaexamples-ds";

    @Override
    public long create(Encuesta encuesta)
    {
        final String sql = "INSERT INTO encuesta (pregunta, fecha_creacion, fecha_fin, cancelada) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            int i = 1;

            preparedStatement.setLong(i++, encuesta.getId());
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());

            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys())
            {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("Error: no se devolvió el ID generado.");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al crear la encuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Encuesta> findById(long id)
    {
        final String sql = "SELECT id, pregunta, fecha_creacion, fecha_fin, cancelada FROM encuesta WHERE id=?";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {

            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery())
            {
                if (rs.next()) return Optional.of(mapEncuesta(rs));
                else return Optional.empty();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al buscar encuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Encuesta> findByKeyword(String keyword, boolean incluirFinalizadas)
    {
        String sql = "SELECT id, pregunta, fecha_creacion, fecha_fin, cancelada FROM encuesta WHERE pregunta LIKE ?";
        if (!incluirFinalizadas) sql += " AND fecha_fin > CURRENT_TIMESTAMP";

        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet rs = preparedStatement.executeQuery())
            {
                List<Encuesta> encuestas = new ArrayList<>();
                while (rs.next()) encuestas.add(mapEncuesta(rs));
                return encuestas;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error en findByKeyword: " + e.getMessage(), e);
        }
    }

    @Override
    public void setCancelada(long id, boolean cancelada)
    {
        final String sql = "UPDATE encuesta SET cancelada=? WHERE id=?";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setBoolean(1, cancelada);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al actualizar el estado cancelada: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isCancelada(long id)
    {
        final String sql = "SELECT cancelada FROM encuesta WHERE id=?";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {

            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery())
            {
                return rs.next() && rs.getBoolean(1);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al consultar cancelada: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFinalizada(long id, Instant now) {
        final String sql = "SELECT fecha_fin FROM encuesta WHERE id=?";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) return false;
                Instant fechaFin = rs.getTimestamp(1).toInstant();
                return !fechaFin.isAfter(now);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en isFinalizada: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Encuesta> findAll() {
        final String sql = "SELECT id, pregunta, fecha_creacion, fecha_fin, cancelada FROM encuesta";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            List<Encuesta> encuestas = new ArrayList<>();
            while (rs.next()) encuestas.add(mapEncuesta(rs));
            return encuestas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las encuestas: " + e.getMessage(), e);
        }
    }

    private Encuesta mapEncuesta(ResultSet rs) throws SQLException
    {
        Encuesta encuesta = new Encuesta();
        encuesta.setId(rs.getLong("id"));
        encuesta.setPregunta(rs.getString("pregunta"));
        encuesta.setFechaCreacion(rs.getTimestamp("fechaCreacion").toLocalDateTime());
        encuesta.setFechaFin(rs.getTimestamp("fechaFin").toLocalDateTime());
        encuesta.setCancelada(rs.getBoolean("cancelada"));
        return encuesta;
    }
}