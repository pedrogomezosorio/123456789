package es.udc.ws.app.model.encuesta;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SqlEncuestaDao
{
    public long create(Encuesta encuesta);

    public Optional<Encuesta> findById(long id);

    public List<Encuesta> findByKeyword(String keyword, boolean incluirFinalizadas);

    public void setCancelada(long id, boolean cancelada);

    public boolean isCancelada(long id);

    public boolean isFinalizada(long id, Instant nowUtc);

    public List<Encuesta> findAll();
}
