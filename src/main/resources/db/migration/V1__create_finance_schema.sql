CREATE TABLE IF NOT EXISTS categorias (
                                          id UUID NOT NULL,
                                          nome VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS movimentacoes (
                                             id UUID NOT NULL,
                                             data DATE NOT NULL,
                                             nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    categoria_id UUID NOT NULL,

    CONSTRAINT pk_movimentacoes
    PRIMARY KEY (id),

    CONSTRAINT fk_movimentacoes_categoria
    FOREIGN KEY (categoria_id)
    REFERENCES categorias (id)
    );