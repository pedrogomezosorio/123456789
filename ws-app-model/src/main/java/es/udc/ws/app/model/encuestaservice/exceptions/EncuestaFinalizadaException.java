package es.udc.ws.app.model.encuestaservice.exceptions;

public class EncuestaFinalizadaException extends Exception {
    private final long encuestaId;

    public EncuestaFinalizadaException(long encuestaId) {
        super("La encuesta " + encuestaId + " ya ha finalizado.");
        this.encuestaId = encuestaId;
    }

    public long getEncuestaId() {
        return encuestaId;
    }
}
