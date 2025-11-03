package es.udc.ws.app.model.respuesta;

import java.time.LocalDateTime;
import java.util.Objects;

public class Respuesta
{
    private long encuestaId;
    private long respuestaId;
    private String email;
    private boolean positiva;
    private LocalDateTime fechaRespuesta;


    public Respuesta(long respuestaId, long encuestaId, String email, boolean positiva, LocalDateTime fechaRespuesta)
    {
        this.respuestaId = respuestaId;
        this.encuestaId = encuestaId;
        this.email = email;
        this.positiva = positiva;
        this.fechaRespuesta = fechaRespuesta;
    }
    public Respuesta() {}

    public long getEncuestaId() {
        return encuestaId;
    }

    public String getEmail() {
        return email;
    }

    public long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public boolean isPositiva() {
        return positiva;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setEncuestaId(long encuestaId) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Respuesta respuesta = (Respuesta) o;
        return encuestaId == respuesta.encuestaId && respuestaId == respuesta.respuestaId && positiva == respuesta.positiva && Objects.equals(email, respuesta.email) && Objects.equals(fechaRespuesta, respuesta.fechaRespuesta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encuestaId, respuestaId, email, positiva, fechaRespuesta);
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
