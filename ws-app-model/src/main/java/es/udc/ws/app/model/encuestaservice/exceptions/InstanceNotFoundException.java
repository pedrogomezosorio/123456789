package es.udc.ws.app.model.encuestaservice.exceptions;

public class InstanceNotFoundException extends Exception {
    private final Object key;
    private final Long id;

    public InstanceNotFoundException(Long id, Object key)
    {
        super("No se encontró la instancia de " + id + " con clave '" + key + "'");
        this.key = key;
        this.id = id;
    }

    public Object getKey() { return key; }
    public Long getId() { return id; }
}
