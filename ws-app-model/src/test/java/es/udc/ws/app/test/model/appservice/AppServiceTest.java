package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.encuestaservice.EncuestaService;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceImpl;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;

import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * P5 (cancelar) y P6 (obtener respuestas).
 * Inserta datos por SQL directo (permitido en tests).
 * Ajusta nombres de tabla/columnas si en tu BD usas snake_case o minúsculas.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AppServiceTest {

    private static DataSource ds;
    private EncuestaService service;

    @BeforeAll
    static void initDs() {
        ds = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
    }

    @BeforeEach
    void initService() {
        service = new EncuestaServiceImpl();
    }

    // ---------- Helpers SQL (usa tu esquema CamelCase) ----------

    private long insertEncuesta(String pregunta, LocalDateTime fin) throws SQLException {
        final String sql = "INSERT INTO Encuesta(pregunta, fechaCreacion, fechaFin, cancelada) VALUES (?, ?, ?, 0)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pregunta);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(3, Timestamp.valueOf(fin));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No se generó id de Encuesta");
                return rs.getLong(1);
            }
        }
    }

    private void insertRespuesta(long encuestaId, String email, boolean positiva, LocalDateTime fecha) throws SQLException {
        final String sql = "INSERT INTO Respuesta(encuestaId, email, positiva, fechaRespuesta) VALUES (?, ?, ?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            ps.setString(2, email);
            ps.setBoolean(3, positiva);
            ps.setTimestamp(4, Timestamp.valueOf(fecha));
            ps.executeUpdate();
        }
    }

    private boolean isCancelada(long encuestaId) throws SQLException {
        final String sql = "SELECT cancelada FROM Encuesta WHERE id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    private void deleteEncuestaCascade(long encuestaId) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Encuesta WHERE id=?")) {
            ps.setLong(1, encuestaId);
            ps.executeUpdate();
        }
    }

    // ----------------------- TESTS P5 -----------------------

    @Test
    @DisplayName("P5.1 — Cancelar antes de la fecha fin ⇒ OK")
    void cancelar_ok() throws Exception {
        long id = insertEncuesta("P5_OK", LocalDateTime.now().plusMinutes(30));
        try {
            service.cancelarEncuesta(id);
            assertTrue(isCancelada(id), "La encuesta debe quedar cancelada");
        } finally {
            deleteEncuestaCascade(id);
        }
    }

    @Test
    @DisplayName("P5.2 — Ya cancelada ⇒ EncuestaCanceladaException")
    void cancelar_ya_cancelada() throws Exception {
        long id = insertEncuesta("P5_YaCancelada", LocalDateTime.now().plusMinutes(30));
        try {
            service.cancelarEncuesta(id);
            assertThrows(EncuestaCanceladaException.class, () -> service.cancelarEncuesta(id));
        } finally {
            deleteEncuestaCascade(id);
        }
    }

    @Test
    @DisplayName("P5.3 — Finalizada ⇒ EncuestaFinalizadaException")
    void cancelar_finalizada() throws Exception {
        long id = insertEncuesta("P5_Finalizada", LocalDateTime.now().minusMinutes(1));
        try {
            assertThrows(EncuestaFinalizadaException.class, () -> service.cancelarEncuesta(id));
        } finally {
            deleteEncuestaCascade(id);
        }
    }

    // ----------------------- TESTS P6 -----------------------

    @Test
    @DisplayName("P6.1 — Listar todas y solo positivas")
    void obtenerRespuestas_listado() throws Exception {
        long id = insertEncuesta("P6_Listado", LocalDateTime.now().plusMinutes(30));
        try {
            insertRespuesta(id, "a@ex.com", true,  LocalDateTime.now().plusSeconds(10));
            insertRespuesta(id, "b@ex.com", false, LocalDateTime.now().plusSeconds(20));
            insertRespuesta(id, "c@ex.com", true,  LocalDateTime.now().plusSeconds(30));

            List<Respuesta> todas = service.obtenerRespuestas(id, false);
            assertEquals(3, todas.size(), "Debe devolver todas las respuestas");

            List<Respuesta> soloPos = service.obtenerRespuestas(id, true);
            assertEquals(2, soloPos.size(), "Debe filtrar solo las positivas");
            assertTrue(soloPos.stream().allMatch(Respuesta::isPositiva));
        } finally {
            deleteEncuestaCascade(id);
        }
    }

    @Test
    @DisplayName("P6.2 — Encuesta inexistente ⇒ InstanceNotFoundException")
    void obtenerRespuestas_inexistente() {
        assertThrows(InstanceNotFoundException.class, () -> service.obtenerRespuestas(-9999L, false));
    }
}
