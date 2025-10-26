package es.udc.ws.app.model.util.encuesta;

import java.time.Instant;

public class Encuesta
{
    private Long id;
    private String descripcion;
    private Instant fechaInicio;
    private Instant fechaFin;
    private boolean cancelado;

    public Encuesta(Long id, String descripcion, Instant fechaInicio, Instant fechaFin, boolean cancelado) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cancelado = cancelado;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(Instant fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public boolean isCancelado() {
        return cancelado;
    }
}