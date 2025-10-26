package es.udc.ws.app.model.respuesta;

import java.time.Instant;
import java.util.List;

public interface RespuestaDAO {

    void upsert(long encuestaId, String email, boolean positiva, Instant fechaRespuestaUtc);

    List<Respuesta> findByEncuesta(long encuestaId);

    List<Respuesta> findPositivasByEncuesta(long encuestaId);

    long countPositivas(long encuestaId);

    long countNegativas(long encuestaId);
}
