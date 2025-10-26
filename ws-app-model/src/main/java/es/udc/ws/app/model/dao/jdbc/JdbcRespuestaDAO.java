package es.udc.ws.app.model.dao.jdbc;

import es.udc.ws.app.model.respuesta.RespuestaDAO;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;
import java.time.Instant;
import java.util.List;

public class JdbcRespuestaDAO implements RespuestaDAO {

    private static final String DATA_SOURCE = "ws-javaexamples-ds";

    @Override
    public void upsert(long encuestaId, String email, boolean positiva, Instant fechaRespuestaUtc) {
        final String selectSql = "SELECT 1 FROM respuesta WHERE encuesta_id=? AND email=? FOR UPDATE";
        final String insertSql = "INSERT INTO respuesta (encuesta_id, email, positiva, fecha_respuesta) VALUES (?,?,?,?)";
        final String updateSql = "UPDATE respuesta SET positiva=?, fecha_respuesta=? WHERE encuesta_id=? AND email=?";

        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            boolean exists;
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setLong(1, encuestaId);
                ps.setString(2, email);
                try (ResultSet rs = ps.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                    ps.setBoolean(1, positiva);
                    ps.setTimestamp(2, Timestamp.from(fechaRespuestaUtc));
                    ps.setLong(3, encuestaId);
                    ps.setString(4, email);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    ps.setLong(1, encuestaId);
                    ps.setString(2, email);
                    ps.setBoolean(3, positiva);
                    ps.setTimestamp(4, Timestamp.from(fechaRespuestaUtc));
                    ps.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error en upsert de respuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Respuesta> findByEncuesta(long encuestaId) {
        final String sql = "SELECT encuesta_id, email, positiva, fecha_respuesta FROM respuesta WHERE encuesta_id=?";
        return selectRespuestas(sql, encuestaId);
    }

    @Override
    public List<Respuesta> findPositivasByEncuesta(long encuestaId) {
        final String sql = "SELECT encuesta_id, email, positiva, fecha_respuesta FROM respuesta WHERE encuesta_id=? AND positiva=TRUE";
        return selectRespuestas(sql, encuestaId);
    }

    @Override
    public long countPositivas(long encuestaId) {
        final String sql = "SELECT COUNT(*) FROM respuesta WHERE encuesta_id=? AND positiva=TRUE";
        return count(sql, encuestaId);
    }

    @Override
    public long countNegativas(long encuestaId) {
        final String sql = "SELECT COUNT(*) FROM respuesta WHERE encuesta_id=? AND positiva=FALSE";
        return count(sql, encuestaId);
    }

    // Métodos auxiliares privados
/*
    private List<Respuesta> selectRespuestas(String sql, long encuestaId) {
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Respuesta> respuestas = new ArrayList<>();
                while (rs.next()) {
                    respuestas.add(new Respuesta(
                            rs.getLong("encuesta_id"),
                            rs.getString("email"),
                            rs.getBoolean("positiva"),
                            rs.getTimestamp("fecha_respuesta").toInstant()
                    ));
                }
                return respuestas;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar respuestas: " + e.getMessage(), e);
        }
    }
*/
    private long count(String sql, long encuestaId) {
        try (Connection connection = DataSourceLocator.getDataSource(DATA_SOURCE).getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar respuestas: " + e.getMessage(), e);
        }
    }
}
