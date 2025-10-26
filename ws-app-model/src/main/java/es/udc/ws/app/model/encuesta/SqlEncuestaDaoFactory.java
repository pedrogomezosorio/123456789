package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlEncuestaDaoFactory
{
    private final static String CLASS_NAME_PARAMETER = "SqlEncuestaDaoFactory.className";
    private static SqlEncuestaDao dao = null;

    private SqlEncuestaDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlEncuestaDao getInstance()
    {
        try
        {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlEncuestaDao) daoClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlEncuestaDao getDao()
    {
        if (dao == null) dao = getInstance();
        return dao;
    }
}
