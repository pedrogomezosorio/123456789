package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuestaservice.EncuestaService;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceImpl;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.respuesta.Respuesta;
// ¡Importar excepciones de ws-util!
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource; // ¡¡ IMPORTAR ESTO !!

import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AppServiceTest {

    private static DataSource dataSource; // Cambiado a 'dataSource'
    private EncuestaService service;

    // --- ¡¡ARREGLO DEL TEMA 4!! ---
    @BeforeAll
    static void initDs() {
        // 1. Crear el DataSource de prueba
        SimpleDataSource testDataSource = new SimpleDataSource();

        // 2. Registrarlo en el DataSourceLocator
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, testDataSource);

        // 3. Guardarlo para usarlo en los helpers
        dataSource = testDataSource;
    }

    @BeforeEach
    void initService() {
        service = new EncuestaServiceImpl();
        // Borramos todas las encuestas y respuestas antes de CADA test
        // para asegurar que están limpios (independencia de tests)
        try (Connection c = dataSource.getConnection()) {
            c.prepareStatement("DELETE FROM Respuesta").executeUpdate();
            c.prepareStatement("DELETE FROM Encuesta").executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- Helpers SQL (Corregidos para usar 'dataSource') ----------

    private long insertEncuesta(String pregunta, LocalDateTime fin) throws SQLException {
        final String sql = "INSERT INTO Encuesta(pregunta, fecha_creacion, fecha_fin, cancelada) VALUES (?, ?, ?, 0)";
        try (Connection c = dataSource.getConnection(); // Usar 'dataSource'
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
        final String sql = "INSERT INTO Respuesta(encuesta_id, email, positiva, fecha_respuesta) VALUES (?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection(); // Usar 'dataSource'
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
        try (Connection c = dataSource.getConnection(); // Usar 'dataSource'
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
                return false;
            }
        }
    }

    // Este helper ya no es necesario si usamos @BeforeEach para limpiar
    // private void deleteEncuestaCascade(long encuestaId) throws SQLException { ... }


    // ----------------------- TESTS FUNC-1 (Crear Encuesta) -----------------------
    // (Añadidos aquí, usando la firma incorrecta que tienes en la interfaz)

    private LocalDateTime getFechaFutura() {
        return LocalDateTime.now().plusDays(1).withNano(0);
    }
    private LocalDateTime getFechaPasada() {
        return LocalDateTime.now().minusDays(1).withNano(0);
    }

    @Test
    @DisplayName("F1.1 — Crear encuesta válida")
    void testCreateEncuesta_Valida() throws InputValidationException, SQLException, InstanceNotFoundException {

        LocalDateTime fechaFin = getFechaFutura();
        Encuesta encuestaParaCrear = new Encuesta();
        encuestaParaCrear.setPregunta("Pregunta válida");
        encuestaParaCrear.setFechaFin(fechaFin);

        // 1. Act
        // Usamos la excepción de ws-util
        assertDoesNotThrow(() -> service.crearEncuesta(encuestaParaCrear));

        // 2. Assert
        assertNotNull(encuestaParaCrear.getId(), "El Service debe actualizar el ID de la encuesta");

        // (Opcional: comprobar en BD)
        // ... (código helper findEncuestaById si lo creas) ...
    }

    @Test
    @DisplayName("F1.2 — Crear encuesta con fecha pasada ⇒ InputValidationException")
    void testCreateEncuesta_FechaPasada() {
        Encuesta encuestaInvalida = new Encuesta();
        encuestaInvalida.setPregunta("Test");
        encuestaInvalida.setFechaFin(getFechaPasada());

        // Usamos la excepción de ws-util
        assertThrows(InputValidationException.class, () -> {
            service.crearEncuesta(encuestaInvalida);
        });
    }

    // ----------------------- TESTS P5 y P6 (Corregidos) -----------------------

    @Test
    @DisplayName("P5.1 — Cancelar antes de la fecha fin ⇒ OK")
    void cancelar_ok() throws Exception {
        long id = insertEncuesta("P5_OK", LocalDateTime.now().plusMinutes(30));
        // ¡Usa la excepción de ws-util!
        assertDoesNotThrow(() -> service.cancelarEncuesta(id));
        assertTrue(isCancelada(id), "La encuesta debe quedar cancelada");
    }

    @Test
    @DisplayName("P5.2 — Ya cancelada ⇒ EncuestaCanceladaException")
    void cancelar_ya_cancelada() throws Exception {
        long id = insertEncuesta("P5_YaCancelada", LocalDateTime.now().plusMinutes(30));
        service.cancelarEncuesta(id); // Cancelar una vez
        assertThrows(EncuestaCanceladaException.class, () -> service.cancelarEncuesta(id)); // Comprobar la segunda
    }

    @Test
    @DisplayName("P5.3 — Finalizada ⇒ EncuestaFinalizadaException")
    void cancelar_finalizada() throws Exception {
        long id = insertEncuesta("P5_Finalizada", LocalDateTime.now().minusMinutes(1));
        assertThrows(EncuestaFinalizadaException.class, () -> service.cancelarEncuesta(id));
    }

    @Test
    @DisplayName("P6.1 — Listar todas y solo positivas")
    void obtenerRespuestas_listado() throws Exception {
        long id = insertEncuesta("P6_Listado", LocalDateTime.now().plusMinutes(30));
        insertRespuesta(id, "a@ex.com", true,  LocalDateTime.now().plusSeconds(10));
        insertRespuesta(id, "b@ex.com", false, LocalDateTime.now().plusSeconds(20));
        insertRespuesta(id, "c@ex.com", true,  LocalDateTime.now().plusSeconds(30));

        // ¡Usa la excepción de ws-util!
        List<Respuesta> todas = assertDoesNotThrow(() -> service.obtenerRespuestas(id, false));
        assertEquals(3, todas.size(), "Debe devolver todas las respuestas");

        List<Respuesta> soloPos = assertDoesNotThrow(() -> service.obtenerRespuestas(id, true));
        assertEquals(2, soloPos.size(), "Debe filtrar solo las positivas");
        assertTrue(soloPos.stream().allMatch(Respuesta::isPositiva));
    }

    @Test
    @DisplayName("P6.2 — Encuesta inexistente ⇒ InstanceNotFoundException")
    void obtenerRespuestas_inexistente() {
        // ¡Usa la excepción de ws-util!
        assertThrows(InstanceNotFoundException.class, () -> service.obtenerRespuestas(-9999L, false));
    }
}