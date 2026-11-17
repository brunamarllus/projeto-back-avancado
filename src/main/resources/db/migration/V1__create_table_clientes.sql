CREATE TABLE clientes
(
    id            BIGSERIAL    NOT NULL,
    nome          VARCHAR(150) NOT NULL,
    cpf           VARCHAR(11)  NOT NULL,
    telefone      VARCHAR(20)  NOT NULL,
    email         VARCHAR(150),
    logradouro    VARCHAR(150),
    numero        VARCHAR(20),
    complemento   VARCHAR(100),
    bairro        VARCHAR(100),
    cidade        VARCHAR(100),
    uf            VARCHAR(2),
    cep           VARCHAR(8),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP    NOT NULL,

    CONSTRAINT pk_clientes PRIMARY KEY (id),
    CONSTRAINT uk_clientes_cpf UNIQUE (cpf)
);
