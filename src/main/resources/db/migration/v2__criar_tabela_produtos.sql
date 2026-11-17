
CREATE TABLE produtos (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          descricao VARCHAR(255),
                          preco NUMERIC(10, 2) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE
);
