CREATE TABLE pagamentos
(
    id             BIGSERIAL      NOT NULL,
    pedido_id      BIGINT         NOT NULL,
    valor          NUMERIC(10, 2) NOT NULL,
    forma          VARCHAR(20)    NOT NULL,
    data_pagamento TIMESTAMP      NOT NULL,
    responsavel    VARCHAR(150)   NOT NULL,

    CONSTRAINT pk_pagamentos PRIMARY KEY (id),
    CONSTRAINT fk_pagamentos_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT ck_pagamentos_valor CHECK (valor > 0)
);

CREATE INDEX idx_pagamentos_pedido ON pagamentos (pedido_id);
