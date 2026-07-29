ALTER TABLE categorias
    ADD COLUMN IF NOT EXISTS usuario_id UUID;

ALTER TABLE movimentacoes
    ADD COLUMN IF NOT EXISTS usuario_id UUID;

CREATE INDEX IF NOT EXISTS idx_categorias_usuario_nome
    ON categorias (usuario_id, nome);

CREATE INDEX IF NOT EXISTS idx_movimentacoes_usuario_data
    ON movimentacoes (usuario_id, data DESC);