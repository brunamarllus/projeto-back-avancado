CREATE TABLE pedidos
(
    id                    BIGSERIAL    NOT NULL,
    codigo_rastreio       VARCHAR(30),
    cliente_id            BIGINT       NOT NULL,
    status                VARCHAR(30)  NOT NULL,
    data_pedido           TIMESTAMP    NOT NULL,
    data_entrega_prevista DATE         NOT NULL,
    data_conclusao        TIMESTAMP,
    observacao            VARCHAR(500),

    CONSTRAINT pk_pedidos PRIMARY KEY (id),
    CONSTRAINT uk_pedidos_codigo_rastreio UNIQUE (codigo_rastreio),
    CONSTRAINT fk_pedidos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE INDEX idx_pedidos_cliente ON pedidos (cliente_id);
CREATE INDEX idx_pedidos_status ON pedidos (status);

CREATE TABLE itens_pedido
(
    id             BIGSERIAL      NOT NULL,
    pedido_id      BIGINT         NOT NULL,
    produto_id     BIGINT         NOT NULL,
    quantidade     INTEGER        NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,

    CONSTRAINT pk_itens_pedido PRIMARY KEY (id),
    CONSTRAINT fk_itens_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT fk_itens_pedido_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT ck_itens_pedido_quantidade CHECK (quantidade > 0)
);

CREATE INDEX idx_itens_pedido_pedido ON itens_pedido (pedido_id);

CREATE TABLE historico_status
(
    id              BIGSERIAL    NOT NULL,
    pedido_id       BIGINT       NOT NULL,
    status_anterior VARCHAR(30),
    novo_status     VARCHAR(30)  NOT NULL,
    data_alteracao  TIMESTAMP    NOT NULL,
    responsavel     VARCHAR(150) NOT NULL,
    observacao      VARCHAR(500),

    CONSTRAINT pk_historico_status PRIMARY KEY (id),
    CONSTRAINT fk_historico_status_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id)
);

CREATE INDEX idx_historico_status_pedido ON historico_status (pedido_id);
