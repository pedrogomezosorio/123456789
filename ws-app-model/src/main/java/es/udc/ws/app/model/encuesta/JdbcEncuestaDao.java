package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEncuestaDao extends AbstractSqlEncuestaDao
{
    @Override
    public Encuesta create(Connection connection, Encuesta encuesta)
    {
        final String sql = "INSERT INTO encuesta (id, pregunta, fecha_creacion, fecha_fin, cancelada) VALUES (?, ?, ?, ?)";
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
    public Optional<Encuesta> find(Connection connection, long id)
    {
        final String sql = "SELECT id, pregunta, fecha_creacion, fecha_fin, cancelada FROM encuesta WHERE id=?";
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next()) return Optional.of(mapEncuesta(resultSet));
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
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                List<Encuesta> encuestas = new ArrayList<>();
                while (resultSet.next()) encuestas.add(mapEncuesta(resultSet));
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
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                return resultSet.next() && resultSet.getBoolean(1);
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) return false;
                Instant fechaFin = resultSet.getTimestamp(1).toInstant();
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
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Encuesta> encuestas = new ArrayList<>();
            while (resultSet.next()) encuestas.add(mapEncuesta(resultSet));
            return encuestas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las encuestas: " + e.getMessage(), e);
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