package es.udc.ws.app.model.encuesta;

import es.udc.ws.app.model.encuestaservice.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao
{
    protected AbstractSqlEncuestaDao() {}

    @Override
    public Encuesta find(Connection connection, Long id) throws InstanceNotFoundException
    {
        String queryString = "SELECT pregunta, runtime, description, price, creationDate FROM ENCUESTA WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, id.longValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new InstanceNotFoundException(id, Encuesta.class.getName());

            i = 1;
            long EncuestaId = resultSet.getLong(i++);
            String pregunta = resultSet.getString(i++);
            LocalDateTime creationDate = resultSet.getTimestamp(i++).toLocalDateTime();
            LocalDateTime endDate = resultSet.getTimestamp(i++).toLocalDateTime();
            boolean cancelada = resultSet.getBoolean(i++);
            int postivias = resultSet.getInt(i++);
            int negativas = resultSet.getInt(i++);

            return new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada, postivias, negativas);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean incluirPasadas)
    {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String queryString = "SELECT id, pregunta, fechaCreacion, fechafin, cancelada FROM ENCUESTA WHERE LOWER(pregunta) LIKE ?";
        if(!incluirPasadas)
        {
            queryString += " AND fechafin > ?";
        }

        queryString += " ORDER BY pregunta";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Encuesta> encuestas = new ArrayList<Encuesta>();

            while (resultSet.next())
            {
                int i = 1;
                long EncuestaId = resultSet.getLong(i++);
                String pregunta = resultSet.getString(i++);
                LocalDateTime creationDate = resultSet.getTimestamp(i++).toLocalDateTime();
                LocalDateTime endDate = resultSet.getTimestamp(i++).toLocalDateTime();
                boolean cancelada = resultSet.getBoolean(i++);
                int positivas = resultSet.getInt(i++);
                int negativas = resultSet.getInt(i++);

                encuestas.add(new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada, positivas, negativas));
            }
            return encuestas;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException
    {
        String queryString = "UPDATE encuesta SET pregunta = ?, fecha_creacion = ?, fecha_fin = ?, cancelada = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());
            preparedStatement.setLong(i++, encuesta.getId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0)
                throw new InstanceNotFoundException(encuesta.getId(), Encuesta.class.getName());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

}

    @Override
    public void remove(Connection connection, Long encuestaId) throws InstanceNotFoundException
    {
        String queryString = "DELETE FROM ENCUESTA WHERE " + " id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            int i = 1;
            preparedStatement.setLong(i++, encuestaId);
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0)
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
