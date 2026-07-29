ALTER TABLE categorias
    ALTER COLUMN usuario_id SET NOT NULL;

ALTER TABLE movimentacoes
    ALTER COLUMN usuario_id SET NOT NULL;