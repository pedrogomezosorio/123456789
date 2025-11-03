package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.respuesta.SqlRespuestaDao;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;
import es.udc.ws.app.model.respuesta.SqlRespuestaDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.sql.DataSourceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class EncuestaServiceImpl implements EncuestaService
{
    private final DataSource dataSource;
    private SqlEncuestaDao encuestaDao = null;
    private SqlRespuestaDao respuestaDao = null;

    public EncuestaServiceImpl()
    {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        respuestaDao = SqlRespuestaDaoFactory.getDao();
    }

    @Override
    public Encuesta obtenerInformacion(long encuestaId) throws InstanceNotFoundException
    {
        /* Tiene que devolver
            fecha y hora de la creación
            número de empleados
            número de respuestas afirmativas
            número de respuestas negativas
            si la encuesta está cancelada o no
         */

        try(Connection connection = dataSource.getConnection())
        {
            Encuesta encuesta = encuestaDao.find(connection, encuestaId);
            List<Respuesta> respuesta = respuestaDao.findByEncuesta(connection, encuestaId, false);

            int afirmativas = 0;
            int negativas = 0;

            for (Respuesta respuestaEncuesta : respuesta)
            {
                if(respuestaEncuesta.isPositiva()) afirmativas++;
                else negativas++;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void cancelarEncuesta(long encuestaId)
            throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

                Encuesta e = encuestaDao.find(connection, encuestaId);

                if (e.isCancelada()) {
                    connection.rollback();
                    throw new EncuestaCanceladaException(encuestaId);
                }
                LocalDateTime now = LocalDateTime.now();
                if (!now.isBefore(e.getFechaFin())) {
                    connection.rollback();
                    throw new EncuestaFinalizadaException(encuestaId);
                }

                e.setCancelada(true);
                encuestaDao.update(connection, e);

                connection.commit();

            } catch (InstanceNotFoundException ex) {
                connection.rollback();
                throw ex;
            } catch (SQLException ex) {
                connection.rollback();
                throw new RuntimeException(ex);
            } catch (RuntimeException | Error ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public java.util.List<Respuesta> obtenerRespuestas(long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true); // opcional
            encuestaDao.find(connection, encuestaId); // valida existencia
            return respuestaDao.findByEncuesta(connection, encuestaId, soloPositivas);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
