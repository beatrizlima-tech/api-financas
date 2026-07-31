# 💰 API Finanças

![Java](https://img.shields.io/badge/Java-25-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-FF6600?style=for-the-badge&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge&logo=docker)
![Tests](https://img.shields.io/badge/tests-50%20passing-brightgreen?style=for-the-badge)

API REST para gerenciamento de finanças pessoais, protegida por JWT, isolada por usuário e integrada ao ecossistema de autenticação e processamento de relatórios financeiros.

---

## 📌 Sobre o projeto

A **API Finanças** permite que usuários autenticados gerenciem categorias, receitas e despesas de forma segura.

A aplicação recebe os tokens JWT emitidos pela `api-autenticacao`, valida assinatura, emissor e expiração e utiliza o UUID público presente no claim `sub` para identificar o proprietário dos dados.

Cada usuário acessa somente suas próprias categorias e movimentações. Consultas, alterações e exclusões são sempre filtradas pelo UUID do usuário autenticado.

A API também possui um fluxo assíncrono de relatórios financeiros com RabbitMQ e integração HTTP autenticada com a `api-agentesia`.

---

## 🏗️ Arquitetura

A aplicação utiliza uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Principais responsabilidades:

- **Controllers:** recebem as requisições HTTP e retornam respostas tipadas;
- **Services:** concentram regras de negócio, transações e conversões;
- **Repositories:** realizam persistência e consultas com Spring Data JPA;
- **Entities:** representam as tabelas do banco de dados;
- **DTOs:** transportam dados sem expor as entidades;
- **Components:** extraem os dados do usuário autenticado;
- **Configurations:** configuram segurança, CORS, Swagger, RabbitMQ e integrações;
- **Handlers:** centralizam os erros utilizando `ProblemDetail`;
- **Migrations:** versionam a estrutura e a integridade do banco com Flyway.

A aplicação utiliza injeção de dependências por construtor e transações declarativas com `@Transactional`.

---

## 🔐 Segurança e isolamento de dados

A API funciona como um **OAuth2 Resource Server** stateless.

O JWT é validado considerando:

- assinatura HMAC SHA-256;
- segredo Base64 com pelo menos 256 bits;
- emissor esperado;
- data de expiração;
- UUID público do usuário no claim `sub`;
- e-mail no claim `email`;
- perfil no claim `perfil`;
- ausência de sessão no servidor.

O segredo e o emissor configurados na `api-autenticacao` e na `api-financas` devem ser os mesmos.

Todas as operações utilizam o UUID do usuário autenticado:

```text
Usuário A → categorias e movimentações do usuário A
Usuário B → categorias e movimentações do usuário B
```

Tentativas de acessar registros de outro usuário retornam recurso não encontrado, sem revelar a existência do dado.

As rotas protegidas exigem:

```http
Authorization: Bearer <accessToken>
```

A comunicação da `api-financas` com a `api-agentesia` utiliza uma credencial interna separada:

```http
X-Internal-Api-Key: <chave-interna>
```

Essa chave não substitui o JWT do usuário. Ela protege exclusivamente a comunicação entre os serviços.

---

## ✅ Funcionalidades

### Categorias

- cadastrar categoria;
- alterar categoria;
- excluir categoria;
- consultar categorias;
- obter categoria por UUID;
- validar nome obrigatório entre 2 e 50 caracteres;
- remover espaços externos do nome;
- impedir nomes duplicados para o mesmo usuário;
- comparar duplicidades ignorando maiúsculas e minúsculas;
- impedir exclusão de categorias com movimentações vinculadas;
- impedir acesso a categorias de outro usuário;
- proteger a unicidade também no PostgreSQL;
- tratar conflitos concorrentes durante a gravação.

### Movimentações

- cadastrar receitas e despesas;
- alterar movimentações;
- excluir movimentações;
- obter movimentação por UUID;
- consultar por intervalo de datas;
- paginar os resultados;
- limitar o tamanho da página;
- ordenar por data decrescente;
- utilizar `BigDecimal` para valores monetários;
- aceitar no máximo duas casas decimais;
- normalizar nomes e tipos recebidos;
- associar movimentações a categorias;
- validar se a categoria pertence ao usuário autenticado;
- impedir acesso a movimentações de outro usuário.

### Erros HTTP

As respostas de erro utilizam o padrão `ProblemDetail`:

- `400 Bad Request` para dados inválidos;
- `401 Unauthorized` para autenticação ausente ou inválida;
- `404 Not Found` para recursos inexistentes;
- `409 Conflict` para categorias duplicadas ou em uso;
- `500 Internal Server Error` para falhas inesperadas.

As mensagens de validação são agrupadas por campo.

Exemplo de conflito:

```json
{
  "detail": "Já existe uma categoria com esse nome para este usuário.",
  "instance": "/api/v1/categorias/criar",
  "status": 409,
  "title": "Categoria já cadastrada"
}
```

---

## 📨 Relatórios e RabbitMQ

O fluxo de relatório é assíncrono:

```text
Cliente autenticado
        ↓
MovimentacaoController
        ↓
MovimentacaoService
        ↓
Consulta das movimentações do usuário
        ↓
Serialização com Jackson 3
        ↓
RabbitMQ
        ↓
Fila relatorios-movimentacoes
        ↓
WorkerService
        ↓
RestClient com X-Internal-Api-Key
        ↓
API Agentes IA
```

O worker utiliza um `RestClient` configurado pelas variáveis:

```text
AGENTES_IA_BASE_URL
AGENTES_IA_API_KEY
```

Em caso de falha na API de agentes:

1. o erro é registrado no log;
2. o RabbitMQ realiza uma tentativa inicial e até três novas tentativas;
3. o intervalo entre tentativas aumenta gradualmente;
4. após esgotar as tentativas, a mensagem é encaminhada para a DLQ.

Filas utilizadas:

```text
relatorios-movimentacoes
relatorios-movimentacoes.dlq
```

A DLQ preserva mensagens que não puderam ser processadas para análise posterior.

---

## 🔄 Integração entre as APIs

### Autenticação do usuário

```text
Cliente
   ↓
api-autenticacao
   ↓
JWT assinado
   ↓
api-financas
   ↓
Validação da assinatura, emissor e expiração
   ↓
UUID extraído do claim sub
   ↓
Consultas filtradas por usuario_id
```

A `api-financas` não armazena senhas e não realiza login. Essas responsabilidades pertencem à `api-autenticacao`.

### Processamento do relatório

```text
api-financas
   ↓
RabbitMQ
   ↓
WorkerService
   ↓
X-Internal-Api-Key
   ↓
api-agentesia
```

A variável `AGENTES_IA_API_KEY` da `api-financas` deve possuir o mesmo valor da variável `INTERNAL_API_KEY` da `api-agentesia`.

---

## 🚀 Tecnologias

- Java 25;
- Spring Boot 4.1.0;
- Spring Web MVC;
- Spring Security;
- OAuth2 Resource Server;
- JWT;
- Spring Data JPA;
- Hibernate;
- Bean Validation;
- PostgreSQL 16;
- Flyway;
- Spring AMQP;
- RabbitMQ;
- RestClient;
- Jackson 3;
- H2 Database;
- Docker;
- Docker Compose;
- Swagger/OpenAPI;
- Spring Boot Actuator;
- Maven;
- JUnit;
- MockMvc;
- Mockito;
- MockRestServiceServer;
- Lombok.

---

## 🗄️ Banco de dados e Flyway

O Flyway gerencia a evolução do banco por meio de migrations:

```text
src/main/resources/db/migration/
├── V1__create_finance_schema.sql
├── V2__add_user_ownership.sql
├── V3__make_user_ownership_required.sql
└── V4__merge_duplicate_categories_and_add_unique_index.sql
```

As migrations:

- criam as tabelas de categorias e movimentações;
- utilizam UUIDs como identificadores;
- adicionam o campo `usuario_id`;
- criam índices para consultas por usuário;
- tornam o proprietário obrigatório;
- normalizam nomes de categorias existentes;
- transferem movimentações de categorias duplicadas;
- removem categorias duplicadas antigas;
- criam um índice único normalizado por usuário;
- preservam a integridade dos dados.

A migration `V4` garante que o mesmo usuário não possua categorias equivalentes, como:

```text
Alimentação
alimentação
  Alimentação
```

O Hibernate utiliza:

```yaml
ddl-auto: validate
```

Dessa forma, o Hibernate valida as entidades enquanto o Flyway controla as alterações estruturais.

---

## 📡 Endpoints

URL local:

```text
http://localhost:8083
```

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/categorias/criar` | Cadastra uma categoria |
| `PUT` | `/api/v1/categorias/alterar/{id}` | Altera uma categoria |
| `DELETE` | `/api/v1/categorias/excluir/{id}` | Exclui uma categoria |
| `GET` | `/api/v1/categorias/consultar` | Consulta as categorias |
| `GET` | `/api/v1/categorias/obter/{id}` | Obtém uma categoria |

Exemplo:

```json
{
  "nome": "Alimentação"
}
```

Resposta:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Alimentação"
}
```

### Movimentações

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/movimentacoes/criar` | Cadastra uma movimentação |
| `PUT` | `/api/v1/movimentacoes/alterar/{id}` | Altera uma movimentação |
| `DELETE` | `/api/v1/movimentacoes/excluir/{id}` | Exclui uma movimentação |
| `GET` | `/api/v1/movimentacoes/consultar` | Consulta por período |
| `GET` | `/api/v1/movimentacoes/obter/{id}` | Obtém uma movimentação |
| `POST` | `/api/v1/movimentacoes/gerar-relatorio` | Solicita um relatório assíncrono |

Tipos aceitos:

```text
RECEITA
DESPESA
```

Exemplo:

```json
{
  "nome": "Salário mensal",
  "data": "2026-07-15",
  "valor": 3500.00,
  "tipo": "RECEITA",
  "categoriaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Consulta por período

```http
GET /api/v1/movimentacoes/consultar
```

| Parâmetro | Tipo | Obrigatório | Padrão |
|---|---|---|---|
| `dataInicio` | Data ISO | Sim | — |
| `dataFim` | Data ISO | Sim | — |
| `pageIndex` | Inteiro | Não | `0` |
| `pageSize` | Inteiro | Não | `25` |

Exemplo:

```http
GET /api/v1/movimentacoes/consultar?dataInicio=2026-07-01&dataFim=2026-07-31&pageIndex=0&pageSize=25
```

---

## ⚙️ Configuração local

Crie na raiz do projeto:

```text
.env.properties
```

Exemplo:

```properties
DB_PASSWORD=coti
RABBITMQ_PASSWORD=coti
JWT_SECRET=COLE_AQUI_A_MESMA_CHAVE_BASE64_DA_API_AUTENTICACAO
AGENTES_IA_API_KEY=COLE_AQUI_A_MESMA_INTERNAL_API_KEY_DA_API_AGENTESIA
```

O arquivo `.env.properties` está no `.gitignore` e não deve ser enviado ao GitHub, compartilhado publicamente ou incluído em arquivos ZIP.

### Variáveis de ambiente

| Variável | Descrição | Padrão local |
|---|---|---|
| `DB_URL` | URL do PostgreSQL | `jdbc:postgresql://localhost:5435/bd-api-financas` |
| `DB_USER` | Usuário do PostgreSQL | `coti` |
| `DB_PASSWORD` | Senha do PostgreSQL | obrigatório |
| `JWT_SECRET` | Chave Base64 compartilhada com a autenticação | obrigatório |
| `JWT_ISSUER` | Emissor esperado do token | `api-autenticacao` |
| `RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Porta do RabbitMQ | `5672` |
| `RABBITMQ_USER` | Usuário do RabbitMQ | `coti` |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ | obrigatório |
| `RABBITMQ_VIRTUAL_HOST` | Virtual host | `/` |
| `AGENTES_IA_BASE_URL` | URL-base da API Agentes IA | `http://localhost:8084` |
| `AGENTES_IA_API_KEY` | Chave de comunicação interna com a API Agentes IA | obrigatório |
| `CORS_ALLOWED_ORIGINS` | Origens autorizadas, separadas por vírgula | `http://localhost:4200` |
| `SERVER_PORT` | Porta da API | `8083` |
| `SPRINGDOC_API_DOCS_ENABLED` | Habilita o documento OpenAPI em produção | `false` |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Habilita o Swagger UI em produção | `false` |

Exemplo com mais de uma origem CORS:

```properties
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://meu-frontend.com
```

---

## ▶️ Executando o projeto

### Pré-requisitos

- Java 25;
- Docker Desktop;
- Git.

### 1. Clonar

```bash
git clone https://github.com/beatrizlima-tech/api-financas.git
cd api-financas
```

### 2. Criar `.env.properties`

Configure:

- senha local do PostgreSQL;
- senha local do RabbitMQ;
- mesma chave JWT utilizada pela `api-autenticacao`;
- mesma chave interna utilizada pela `api-agentesia`.

### 3. Subir a infraestrutura

```bash
docker compose up -d
docker compose ps
```

### 4. Executar

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

O Flyway aplicará automaticamente as migrations pendentes.

> Em ambientes que já possuam a fila `relatorios-movimentacoes` sem configuração de DLQ, a fila antiga deverá ser recriada antes da inicialização da nova versão.

---

## 🐳 Infraestrutura Docker

| Serviço | Porta local |
|---|---:|
| PostgreSQL | `5435` |
| pgAdmin | `5056` |
| RabbitMQ | `5672` |
| RabbitMQ Management | `15672` |

Acessos:

```text
pgAdmin:  http://localhost:5056
RabbitMQ: http://localhost:15672
```

Comandos:

```bash
docker compose up -d
docker compose ps
docker compose down
```

---

## 📖 Swagger e monitoramento

Swagger UI:

```text
http://localhost:8083/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8083/v3/api-docs
```

Health check:

```text
http://localhost:8083/actuator/health
```

No perfil `prod`, o Swagger UI e o documento OpenAPI ficam desativados por padrão. Eles podem ser habilitados pelas variáveis:

```text
SPRINGDOC_SWAGGER_UI_ENABLED
SPRINGDOC_API_DOCS_ENABLED
```

Para testar endpoints protegidos:

1. autentique um usuário na `api-autenticacao`;
2. copie o `accessToken`;
3. abra o Swagger da `api-financas`;
4. clique em **Authorize**;
5. cole somente o token iniciado por `eyJ`;
6. confirme a autorização;
7. execute os endpoints.

O Swagger não remove a necessidade de autenticação, pois o JWT identifica o proprietário das categorias e movimentações.

---

## 🧪 Testes automatizados

A aplicação possui **50 testes aprovados**:

- 36 testes de aplicação, validações, CRUD, parâmetros, JSON, CORS e relatórios;
- 7 testes de segurança e isolamento entre usuários;
- 3 testes unitários do `CategoriaService`;
- 2 testes da configuração do `RestClient`;
- 2 testes unitários do `WorkerService`.

Os testes utilizam:

- JUnit;
- MockMvc;
- Mockito;
- H2 em memória;
- perfil `test`;
- tokens JWT de teste;
- MockRestServiceServer;
- RabbitMQ desativado durante a suíte.

Executar:

```powershell
.\mvnw.cmd clean test
```

Resultado atual:

```text
Tests run: 50, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Gerar o pacote:

```powershell
.\mvnw.cmd clean package
```

---

## 🚧 Próximas melhorias

- validar o fluxo completo com a `api-agentesia`;
- integrar ao frontend Angular;
- adicionar testes com PostgreSQL e Testcontainers;
- avaliar JWT com chaves assimétricas;
- adicionar observabilidade distribuída;
- preparar a infraestrutura de produção na AWS.

---

## 📊 Status

🚧 **Projeto em evolução ativa.**

Implementado:

- CRUD de categorias;
- CRUD de movimentações;
- paginação e filtros por período;
- segurança JWT;
- isolamento de dados por usuário;
- UUIDs;
- valores monetários com `BigDecimal`;
- validações com Bean Validation;
- normalização dos DTOs;
- categorias únicas por usuário;
- proteção de categorias em uso;
- transações declarativas;
- erros padronizados com `ProblemDetail`;
- migrations Flyway;
- índice único normalizado no PostgreSQL;
- CORS configurável para múltiplas origens;
- health check;
- RabbitMQ com retry e DLQ;
- integração autenticada com a API Agentes IA;
- documentação Swagger/OpenAPI;
- 50 testes automatizados aprovados.

---

## 👩‍💻 Autora

**Beatriz Lima de Oliveira**

GitHub: [beatrizlima-tech](https://github.com/beatrizlima-tech)