-- Normaliza os nomes existentes, removendo espaços externos.
UPDATE categorias
SET nome = BTRIM(nome);

-- Transfere as movimentações das categorias duplicadas
-- para a categoria principal do mesmo usuário.
WITH categorias_ranqueadas AS (
    SELECT
        id,
        FIRST_VALUE(id) OVER (
            PARTITION BY usuario_id, LOWER(nome)
            ORDER BY id::text
        ) AS categoria_principal_id,
        ROW_NUMBER() OVER (
            PARTITION BY usuario_id, LOWER(nome)
            ORDER BY id::text
        ) AS posicao
    FROM categorias
),
     categorias_duplicadas AS (
         SELECT
             id AS categoria_duplicada_id,
             categoria_principal_id
         FROM categorias_ranqueadas
         WHERE posicao > 1
     )
UPDATE movimentacoes AS movimentacao
SET categoria_id =
        duplicada.categoria_principal_id
    FROM categorias_duplicadas AS duplicada
WHERE movimentacao.categoria_id =
    duplicada.categoria_duplicada_id;

-- Remove as categorias duplicadas depois que suas
-- movimentações já foram transferidas.
WITH categorias_ranqueadas AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY usuario_id, LOWER(nome)
            ORDER BY id::text
        ) AS posicao
    FROM categorias
)
DELETE FROM categorias AS categoria
    USING categorias_ranqueadas AS ranqueada
WHERE categoria.id = ranqueada.id
  AND ranqueada.posicao > 1;

-- O índice antigo permitia nomes repetidos.
DROP INDEX IF EXISTS idx_categorias_usuario_nome;

-- Impede nomes duplicados para o mesmo usuário,
-- ignorando diferenças entre maiúsculas e minúsculas.
CREATE UNIQUE INDEX
    uk_categorias_usuario_nome_normalizado
    ON categorias (
                   usuario_id,
                   LOWER(BTRIM(nome))
        );