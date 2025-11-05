package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class EncuestaServiceFactory
{
    private final static String CLASS_NAME_PARAMETER = "EncuestaServiceFactory.className";
    private static EncuestaService service = null;

    private EncuestaServiceFactory() {}

    @SuppressWarnings("rawtypes")
    private static EncuestaService getInstance()
    {
        try
        {
            String serviceClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (EncuestaService) serviceClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public synchronized static EncuestaService getService()
    {
        if (service == null) service = getInstance();
        return service;
    }
}
