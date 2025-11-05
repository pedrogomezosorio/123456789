package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlRespuestaDaoFactory
{
    private final static String CLASS_NAME_PARAMETER = "SqlRespuestaDaoFactory.className";
    private static SqlRespuestaDao dao = null;

    private void SqlMovieDaoFactory() {}

    @SuppressWarnings("rawtypes")
    private static SqlRespuestaDao getInstance()
    {
        try
        {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlRespuestaDao) daoClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlRespuestaDao getDao()
    {
        if (dao == null) dao = getInstance();
        return dao;
    }
}
