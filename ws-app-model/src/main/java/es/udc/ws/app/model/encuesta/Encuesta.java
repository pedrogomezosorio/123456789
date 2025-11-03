package es.udc.ws.app.model.encuesta;
import java.time.LocalDateTime;
import java.util.Objects;

public class Encuesta
{
    private long id;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private boolean cancelada;
    private int repuestasPositivas;
    private int respuestasNegativas;

    public Encuesta() {}

    public Encuesta(long id, String pregunta, LocalDateTime fechaCreacion, LocalDateTime fechaFin, boolean cancelada, int repuestasPositivas, int respuestasNegativas) {
        this.id = id;
        this.pregunta = pregunta;
        this.fechaCreacion = fechaCreacion;
        this.fechaFin = fechaFin;
        this.cancelada = cancelada;
        this.repuestasPositivas = repuestasPositivas;
        this.respuestasNegativas = respuestasNegativas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public boolean estaFinalizada(LocalDateTime ahora) {
        return !fechaFin.isAfter(ahora);
    }

    public int getRepuestasPositivas() {
        return repuestasPositivas;
    }

    public void setRepuestasPositivas(int repuestasPositivas) {
        this.repuestasPositivas = repuestasPositivas;
    }

    public int getRespuestasNegativas() {
        return respuestasNegativas;
    }

    public void setRespuestasNegativas(int respuestasNegativas) {
        this.respuestasNegativas = respuestasNegativas;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Encuesta encuesta = (Encuesta) o;
        return cancelada == encuesta.cancelada && repuestasPositivas == encuesta.repuestasPositivas && respuestasNegativas == encuesta.respuestasNegativas && Objects.equals(id, encuesta.id) && Objects.equals(pregunta, encuesta.pregunta) && Objects.equals(fechaCreacion, encuesta.fechaCreacion) && Objects.equals(fechaFin, encuesta.fechaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pregunta, fechaCreacion, fechaFin, cancelada, repuestasPositivas, respuestasNegativas);
    }

    @Override
    public String toString() {
        return "Encuesta{" +
                "id=" + id +
                ", pregunta='" + pregunta + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaFin +
                ", cancelada=" + cancelada +
                ", repuestasPositivas=" + repuestasPositivas +
                ", respuestasNegativas=" + respuestasNegativas +
                '}';
    }
}

