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
    public List<Encuesta> find(Connection connection, Long id) throws InstanceNotFoundException
    {
        String queryString = "SELECT pregunta, runtime, "
                + " description, price, creationDate FROM ENCUESTA WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {

            int i = 1;
            preparedStatement.setLong(i++, id.longValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
            {
                throw new InstanceNotFoundException(id, Encuesta.class.getName());
            }


            List<Encuesta> encuestas = new ArrayList<Encuesta>();

            while (resultSet.next()) {

                i = 1;
                long EncuestaId = resultSet.getLong(i++);
                String pregunta = resultSet.getString(i++);
                Timestamp fechaCreacion = resultSet.getTimestamp(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                boolean cancelada = resultSet.getBoolean(i++);

                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                LocalDateTime endDate = creationDateAsTimestamp.toLocalDateTime();


                encuestas.add(new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada));
            }

            return encuestas;;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Encuesta> findByKeywords(Connection connection, String keywords) {

        /* Create "queryString". */
        String[] words = keywords != null ? keywords.split(" ") : null;
        String queryString = "SELECT id, pregunta, fechaCreacion, fechafin, cancelada FROM ENCUESTA";
        int i;
        if (words != null && words.length > 0)
        {
            queryString += " WHERE";
            for (i = 0; i < words.length; i++)
            {
                if (i > 0) 
                {
                    queryString += " AND";
                }
                queryString += " LOWER(pregunta) LIKE LOWER(?)";
            }
        }
        queryString += " ORDER BY pregunta";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {

            if (words != null)
            {
                /* Fill "preparedStatement". */
                for (i = 0; i < words.length; i++) {
                    preparedStatement.setString(i + 1, "%" + words[i] + "%");
                }
            }

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Encuesta> encuestas = new ArrayList<Encuesta>();

            while (resultSet.next()) {

                i = 1;
                long EncuestaId = resultSet.getLong(i++);
                String pregunta = resultSet.getString(i++);
                Timestamp fechaCreacion = resultSet.getTimestamp(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                boolean cancelada = resultSet.getBoolean(i++);

                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                LocalDateTime endDate = creationDateAsTimestamp.toLocalDateTime();


                encuestas.add(new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada));
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException
    {
        String queryString = "UPDATE encuesta "
                + "SET pregunta = ?, fecha_creacion = ?, fecha_fin = ?, cancelada = ? "
                + "WHERE id = ?";

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

        /* Create "queryString". */
        String queryString = "DELETE FROM ENCUESTA WHERE " + " id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, encuestaId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0)
            {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
