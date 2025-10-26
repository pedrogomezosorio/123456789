package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao
{

    protected AbstractSqlEncuestaDao() {
    }

    @Override
    public Encuesta findById(Connection connection, Long id)
            throws InstanceNotFoundException
    {

        /* Create "queryString". */  //Cambiar esto
        String queryString = "SELECT pregunta, runtime, "
                + " description, price, creationDate FROM ENCUESTA WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString))
        {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, id.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
            {
                throw new InstanceNotFoundException(id,
                        Encuesta.class.getName());
            }

            /* Get results. */

            i = 1;
            long EncuestaId = resultSet.getLong(i++);
            String pregunta = resultSet.getString(i++);
            Timestamp fechaCreacion = resultSet.getTimestamp(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            boolean cancelada = resultSet.getBoolean(i++);

            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
            LocalDateTime endDate = creationDateAsTimestamp.toLocalDateTime();

            /* Return movie. */
            return new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada);

        } catch (SQLException e) {
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

            /* Read movies. */
            List<Encuesta> movies = new ArrayList<Encuesta>();

            while (resultSet.next()) {

                i = 1;
                long EncuestaId = resultSet.getLong(i++);
                String pregunta = resultSet.getString(i++);
                Timestamp fechaCreacion = resultSet.getTimestamp(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                boolean cancelada = resultSet.getBoolean(i++);

                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                LocalDateTime endDate = creationDateAsTimestamp.toLocalDateTime();


                movies.add(new Encuesta(EncuestaId, pregunta, creationDate, endDate, cancelada));
            }

            /* Return movies. */
            return movies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Encuesta encuesta)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "UPDATE Encuesta"
                + " SET pregunta = ?, fechaCreacion = ?, fechaFin = ?, "
                + "cancelada = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, encuesta.getId());
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setDate(i++, java.sql.Date.valueOf(encuesta.getFechaCreacion().toLocalDate()));
            preparedStatement.setDate(i++, java.sql.Date.valueOf(encuesta.getFechaFin().toLocalDate()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());


            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(encuesta.getId(), encuesta.getPregunta());
            }

        } catch (SQLException e) {
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
                throw new InstanceNotFoundException(encuestaId,
                        Encuesta.class.getName());
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
