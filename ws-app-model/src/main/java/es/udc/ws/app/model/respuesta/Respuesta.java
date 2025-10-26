package es.udc.ws.app.model.respuesta;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad del modelo que representa una respuesta de un empleado.
 *
 * Correspondencia con tabla MySQL:
 *   encuesta_id      -> BIGINT (FK a encuesta.id)
 *   email            -> VARCHAR(254)
 *   positiva         -> BOOLEAN
 *   fecha_respuesta  -> DATETIME(6) (UTC)
 */
public class Respuesta {

    private Long encuestaId;
    private String email;
    private boolean positiva;
    private Instant fechaRespuesta;

    public Respuesta() {}

    public Long getEncuestaId() {
        return encuestaId;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPositiva() {
        return positiva;
    }

    public Instant getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPositiva(boolean positiva) {
        this.positiva = positiva;
    }

    public void setFechaRespuesta(Instant fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Respuesta(Long encuestaId, String email, boolean positiva, Instant fechaRespuesta) {
        this.encuestaId = encuestaId;
        this.email = email;
        this.positiva = positiva;
        this.fechaRespuesta = fechaRespuesta;
    }

    // --- Métodos auxiliares ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Respuesta)) return false;
        Respuesta r = (Respuesta) o;
        return positiva == r.positiva &&
                Objects.equals(encuestaId, r.encuestaId) &&
                Objects.equals(email, r.email) &&
                Objects.equals(fechaRespuesta, r.fechaRespuesta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encuestaId, email, positiva, fechaRespuesta);
    }

    @Override
    public String toString() {
        return "Respuesta{" +
                "encuestaId=" + encuestaId +
                ", email='" + email + '\'' +
                ", positiva=" + positiva +
                ", fechaRespuesta=" + fechaRespuesta +
                '}';
    }
}
