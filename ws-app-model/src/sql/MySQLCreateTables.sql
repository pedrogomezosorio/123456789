DROP TABLE Encuesta;
DROP TABLE Respuesta;

CREATE TABLE Encusta (encuestaId BIGINT NOT NULL AUTO_INCREMENT,
    pregunta VARCHAR(1023) NOT NULL,
    fechaCreacion DATETIME NOT NULL,
    fechaFin DATETIME NOT NULL,
    cancelada BIT NOT NULL,
    respuestasPositivas, INTEGER,
    respuestasNegativas, INTEGER,
    CONSTRAINT EncuestaPK PRIMARY KEY (encuestaId),
    CONSTRAINT validRespuestaPositiva CHECK (respuestasPositivas >= 0)
    CONSTRAINT validRespuestaNegativa CHECK (respuestasNegativas >= 0)) ENGINE = InnoBD;

CREATE TABLE Respuesta(respuestaId BIGINT NOT NULL AUTO_INCREMENT,
    encuestaId BIGINT NOT NULL
    email VARCHAR(255) NOT NULL,
    positiva BIT NOT NULL,
    fechaRespuesta DATETIME NOT NULL,
    CONSTRAINT RespuestaPK PRIMARY KEY(respuestaId),
    CONSTRAINT encuestaIdFK FOREIGN KEY(encuestaId)
        REFERENCES Encuesta(encuestaId) ON DELETE CASCADE) ENGINE = InnoBD
)
