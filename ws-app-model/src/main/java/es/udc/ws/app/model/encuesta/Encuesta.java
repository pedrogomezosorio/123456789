package es.udc.ws.app.model.encuesta;
import java.time.LocalDateTime;
import java.util.Objects;

public class Encuesta
{
    private Long id;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private boolean cancelada;

    public Encuesta() {}

    public Encuesta(Long id, String pregunta, LocalDateTime fechaCreacion,
                    LocalDateTime fechaFin, boolean cancelada) {
        this.id = id;
        this.pregunta = pregunta;
        this.fechaCreacion = fechaCreacion;
        this.fechaFin = fechaFin;
        this.cancelada = cancelada;
    }

    /** Creador auxiliar para nuevas encuestas (sin id aún asignado). */
    public static Encuesta nueva(String pregunta, LocalDateTime fechaFin, LocalDateTime ahora) {
        return new Encuesta(null, pregunta, ahora, fechaFin, false);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Encuesta)) return false;
        Encuesta e = (Encuesta) o;
        return cancelada == e.cancelada &&
                Objects.equals(id, e.id) &&
                Objects.equals(pregunta, e.pregunta) &&
                Objects.equals(fechaCreacion, e.fechaCreacion) &&
                Objects.equals(fechaFin, e.fechaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pregunta, fechaCreacion, fechaFin, cancelada);
    }

    @Override
    public String toString() {
        return "Encuesta{" +
                "id=" + id +
                ", pregunta='" + pregunta + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaFin +
                ", cancelada=" + cancelada +
                '}';
    }
}

