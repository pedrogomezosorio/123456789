package es.udc.ws.app.model.encuestaservice.exceptions;

public class InstanceNotFoundException extends Exception {
    private final Object key;
    private final String className;

    public InstanceNotFoundException(String className, Object key) {
        super("No se encontró la instancia de " + className + " con clave '" + key + "'");
        this.key = key;
        this.className = className;
    }

    public Object getKey() { return key; }
    public String getClassName() { return className; }
}
