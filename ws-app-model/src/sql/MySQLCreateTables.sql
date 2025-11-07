DROP TABLE IF EXISTS Respuesta; -- Invertido para borrar la que tiene la FK primero
DROP TABLE IF EXISTS Encuesta;

CREATE TABLE Encuesta (
                          encuestaId BIGINT NOT NULL AUTO_INCREMENT,
                          pregunta VARCHAR(1023) NOT NULL,
                          fechaCreacion DATETIME NOT NULL,
                          fechaFin DATETIME NOT NULL,
                          cancelada BIT NOT NULL,
                          respuestasPositivas INTEGER, -- (Ojo: Esto es un mal diseño, pero el DAO lo espera)
                          respuestasNegativas INTEGER, -- (Ojo: Esto es un mal diseño, pero el DAO lo espera)
                          CONSTRAINT EncuestaPK PRIMARY KEY (encuestaId),
                          CONSTRAINT validRespuestaPositiva CHECK (respuestasPositivas >= 0),
                          CONSTRAINT validRespuestaNegativa CHECK (respuestasNegativas >= 0)
) ENGINE = InnoDB; -- CORREGIDO

CREATE TABLE Respuesta(
                          respuestaId BIGINT NOT NULL AUTO_INCREMENT,
                          encuestaId BIGINT NOT NULL,
                          email VARCHAR(255) NOT NULL, -- FALTABA UNA COMA ANTES
                          positiva BIT NOT NULL,
                          fechaRespuesta DATETIME NOT NULL,
                          CONSTRAINT RespuestaPK PRIMARY KEY(respuestaId),
                          CONSTRAINT encuestaIdFK FOREIGN KEY(encuestaId)
                              REFERENCES Encuesta(encuestaId) ON DELETE CASCADE
) ENGINE = InnoDB; -- CORREGIDO