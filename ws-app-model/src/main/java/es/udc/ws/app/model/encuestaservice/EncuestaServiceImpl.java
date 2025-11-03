package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator; // Para validar datos
import java.time.LocalDateTime; // Para manejar fechas y horas

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

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
    public void crearEncuesta(Encuesta encuesta) throws InputValidationException {
// 1. Validación de los datos que vienen en el objeto
        PropertyValidator.validateMandatoryString("pregunta", encuesta.getPregunta());
        if (encuesta.getFechaFin() == null || !encuesta.getFechaFin().isAfter(LocalDateTime.now())) {
            throw new InputValidationException("La fecha de finalización debe ser futura y no nula.");
        }

        // 2. Establecer datos que genera el servidor [cite: 466]
        encuesta.setFechaCreacion(LocalDateTime.now());
        encuesta.setCancelada(false); // Valor por defecto al crear

        // ¡OJO! Tu Encuesta.java tiene contadores que la BD no tiene.
        // Los inicializamos a 0 para que no den problemas, pero este diseño es incorrecto.
        encuesta.setRepuestasPositivas(0);
        encuesta.setRespuestasNegativas(0);


        // 3. Transacción y llamada al DAO (Patrón Tema 3 [cite: 1211-1221])
        try (Connection connection = dataSource.getConnection()) {
            try {
                // Preparar conexión para transacción
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Llamar al DAO (encuestaDao) pasando la Connection
                // El DAO.create devuelve la encuesta con el ID. Lo usamos para actualizar el objeto original.
                Encuesta encuestaCreada = encuestaDao.create(connection, encuesta);

                // Actualizamos el ID del objeto que nos pasaron
                encuesta.setId(encuestaCreada.getId());

                // Confirmar transacción
                connection.commit();

                // El método es void, no devuelve nada.

            } catch (SQLException e) {
                connection.rollback(); // Deshacer en caso de error SQL
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback(); // Deshacer en caso de otros errores
                throw e;
            }
        } catch (SQLException e) {
            // Error al obtener/cerrar la conexión
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> obtenerEncuestas(long id, boolean incluirFinalizadas) throws InstanceNotFoundException {
        return List.of();
    }

    @Override
    public Encuesta obtenerInformacion(long encuestaId) throws InstanceNotFoundException
    {
        /*
        Devuelve:
        - fecha de creación
        - Empleados que respondieron afirmativamente
        - Empleados que respondieron negativamente
        - Si la encuesta ha sido cancelada

        Lo único no incluido cuando hacemos el find de la encuesta son las respuestas,
        el resto se aprovecha de la creación de la encuesta.
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
            encuesta.setRepuestasPositivas(afirmativas);
            encuesta.setRespuestasNegativas(negativas);
            return encuesta;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstanceNotFoundException e)
        {
            throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
        }
    }

    @Override
    public Respuesta responder(long encuestaId, String email, boolean voto) throws InstanceNotFoundException, EncuestaFinalizadaException, InputValidationException, EncuestaCanceladaException
    {
        LocalDateTime horaRespuesta = LocalDateTime.now().withNano(0);
        Random random = new Random();
        Respuesta respuesta =  null;
        Encuesta encuesta = null;

        try(Connection connection = dataSource.getConnection()) {
            try
            {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                List<Respuesta> respuestas = respuestaDao.findByEmail(connection, email);

                for(Respuesta respuestaEncuesta : respuestas)
                {
                    if(respuestaEncuesta.getEncuestaId() == encuestaId)
                    {
                        respuesta =  respuestaEncuesta;
                        break;
                    }
                }

                if(respuesta == null)
                {
                    long respuestaId = random.nextLong();
                    encuesta = encuestaDao.find(connection, encuestaId); // Si no la encuestra no se puede responder, daria una excepción
                    if (encuesta == null) throw new InputValidationException("La encuesta no existe");
                    if (encuesta.getFechaFin().isBefore(horaRespuesta)) throw new EncuestaFinalizadaException(encuestaId);
                    if (encuesta.isCancelada()) throw new EncuestaCanceladaException(encuestaId);
                    respuesta = new Respuesta(respuestaId, encuestaId, email, voto, horaRespuesta);
                    respuestaDao.create(connection, respuesta);
                    return respuesta;
                }
                else
                {
                    respuesta.setFechaRespuesta(horaRespuesta);
                    respuesta.setPositiva(voto);

                    encuesta = encuestaDao.find(connection, encuestaId); // Si no la encuestra no se puede responder, daria una excepción
                    if (encuesta == null) throw new InputValidationException("La encuesta no existe");
                    if (encuesta.getFechaFin().isBefore(horaRespuesta)) throw new EncuestaFinalizadaException(encuestaId);
                    if (encuesta.isCancelada()) throw new EncuestaCanceladaException(encuestaId);

                    respuestaDao.update(connection, respuesta);
                }
            }
            catch (SQLException e)
            {
                connection.rollback();
                throw new SQLException(e);
            }
            catch (RuntimeException e)
            {
                connection.rollback();
                throw new RuntimeException(e);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstanceNotFoundException e)
        {
            throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
        }
        return null;
    }

    @Override
    public void cancelarEncuesta(long encuestaId) throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {

    }

    @Override
    public List<Respuesta> obtenerRespuestas(long encuestaId, boolean soloPositivas) throws InstanceNotFoundException {
        return List.of();
    }
}
