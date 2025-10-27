package es.udc.ws.app.model.respuesta;

import java.time.LocalDateTime;
import java.util.Objects;

public class Respuesta
{
    private Long encuestaId;
    private String email;
    private boolean positiva;
    private LocalDateTime fechaRespuesta;


    public Respuesta(Long encuestaId, String email, boolean positiva, LocalDateTime fechaRespuesta)
    {
        this.encuestaId = encuestaId;
        this.email = email;
        this.positiva = positiva;
        this.fechaRespuesta = fechaRespuesta;
    }
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

    public LocalDateTime getFechaRespuesta() {
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

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
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
