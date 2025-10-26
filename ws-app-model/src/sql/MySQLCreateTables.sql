CREATE TABLE IF NOT EXISTS encuesta (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    pregunta         VARCHAR(500)     NOT NULL,
    fecha_creacion   DATETIME(6)      NOT NULL,
    fecha_fin        DATETIME(6)      NOT NULL,
    cancelada        BOOLEAN          NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_encuesta PRIMARY KEY (id),
    CONSTRAINT chk_fechas CHECK (fecha_fin > fecha_creacion)
)
ENGINE=InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_encuesta_fin
    ON encuesta (fecha_fin);

CREATE INDEX idx_encuesta_cancelada
    ON encuesta (cancelada);

CREATE TABLE IF NOT EXISTS respuesta (
    encuesta_id      BIGINT UNSIGNED  NOT NULL,
    email            VARCHAR(254)     NOT NULL,
    positiva         BOOLEAN          NOT NULL,
    fecha_respuesta  DATETIME(6)      NOT NULL,
    CONSTRAINT pk_respuesta PRIMARY KEY (encuesta_id, email),

    CONSTRAINT fk_resp_encuesta
    FOREIGN KEY (encuesta_id)
    REFERENCES encuesta (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)
    ENGINE=InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_resp_encuesta_pos
    ON respuesta (encuesta_id, positiva);