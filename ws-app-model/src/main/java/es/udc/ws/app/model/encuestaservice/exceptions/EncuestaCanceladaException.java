package es.udc.ws.app.model.encuestaservice.exceptions;

public class EncuestaCanceladaException extends Exception {
    private final long encuestaId;

    public EncuestaCanceladaException(long encuestaId) {
        super("La encuesta " + encuestaId + " ya está cancelada.");
        this.encuestaId = encuestaId;
    }

    public long getEncuestaId() {
        return encuestaId;
    }
}
