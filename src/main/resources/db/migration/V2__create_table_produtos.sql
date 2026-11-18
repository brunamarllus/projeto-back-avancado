CREATE TABLE produtos
(
    id        BIGSERIAL      NOT NULL,
    nome      VARCHAR(150)   NOT NULL,
    descricao VARCHAR(500),
    preco     NUMERIC(10, 2) NOT NULL,
    ativo     BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_produtos PRIMARY KEY (id),
    CONSTRAINT ck_produtos_preco CHECK (preco > 0)
);