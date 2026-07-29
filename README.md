# 💰 API Finanças

![Java](https://img.shields.io/badge/Java-25-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge&logo=flyway)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge&logo=docker)
![Tests](https://img.shields.io/badge/tests-32%20passing-brightgreen?style=for-the-badge)

API REST para gerenciamento de finanças pessoais, protegida por JWT e integrada à API de autenticação do ecossistema Finanças.

---

## 📌 Sobre o projeto

A **API Finanças** permite gerenciar categorias, receitas e despesas de maneira segura e isolada por usuário.

A aplicação recebe os tokens JWT emitidos pela `api-autenticacao`, valida assinatura, emissor e expiração e utiliza o UUID público presente no `sub` do token para identificar o proprietário dos dados.

Cada usuário acessa somente suas próprias categorias e movimentações. Tentativas de consultar, alterar ou excluir registros de outro usuário não expõem a existência desses dados.

O projeto também possui um fluxo assíncrono de solicitação de relatórios financeiros utilizando RabbitMQ.

---

## 🏗️ Arquitetura

A aplicação está organizada em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Componentes principais:

- **Controllers:** recebem as requisições HTTP e retornam as respostas da API;
- **Services:** concentram regras de negócio, validações e conversões;
- **Repositories:** realizam a persistência e as consultas com Spring Data JPA;
- **Entities:** representam as tabelas e relacionamentos;
- **DTOs:** transportam os dados sem expor diretamente as entidades;
- **Components:** extraem de forma centralizada os dados do usuário autenticado;
- **Configurations:** configuram segurança, CORS, Swagger, RabbitMQ e serialização;
- **Handlers:** centralizam o tratamento de erros inesperados;
- **Migrations:** versionam a estrutura do banco com Flyway.

---

## 🔐 Segurança e isolamento de dados

A API funciona como um **OAuth2 Resource Server** stateless.

O token é emitido pela `api-autenticacao` e validado pela `api-financas` utilizando:

- assinatura HMAC SHA-256;
- segredo JWT em Base64 com pelo menos 256 bits;
- validação do emissor;
- validação da expiração;
- ausência de sessão no servidor;
- UUID público do usuário no claim `sub`;
- e-mail do usuário no claim `email`;
- perfil do usuário no claim `perfil`.

O segredo e o emissor configurados nas duas APIs devem ser iguais.

As consultas aos repositories utilizam o UUID do usuário autenticado:

```text
Usuário A → categorias e movimentações do usuário A
Usuário B → categorias e movimentações do usuário B
```

Rotas públicas:

- Swagger/OpenAPI;
- health check do Actuator;
- requisições CORS `OPTIONS`.

As demais rotas exigem:

```http
Authorization: Bearer <accessToken>
```

---

## ✅ Funcionalidades

### Categorias

- cadastrar categoria;
- alterar categoria;
- excluir categoria;
- consultar categorias do usuário autenticado;
- obter categoria por UUID;
- validar nome obrigatório e tamanho mínimo;
- normalizar espaços;
- impedir acesso a categorias de outro usuário.

### Movimentações

- cadastrar receitas e despesas;
- alterar movimentações;
- excluir movimentações;
- obter movimentação por UUID;
- consultar por intervalo de datas;
- paginar os resultados;
- limitar o tamanho da página;
- ordenar por data decrescente;
- persistir valores monetários com `BigDecimal`;
- associar movimentações às categorias;
- validar se a categoria pertence ao usuário autenticado;
- impedir acesso a movimentações de outro usuário.

### Relatórios e RabbitMQ

- consultar movimentações por período;
- montar a solicitação de relatório;
- incluir o e-mail do usuário autenticado;
- serializar os dados em JSON;
- publicar mensagens no RabbitMQ;
- consumir mensagens por meio do `WorkerService`;
- utilizar a fila `relatorios-movimentacoes`.

A geração da análise financeira por inteligência artificial e o envio do relatório por e-mail serão implementados na integração com a `api-agentesia`.

---

## 🔄 Integração entre as APIs

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
UUID do usuário extraído do claim sub
   ↓
Consultas filtradas por usuario_id
```

A `api-financas` não armazena senhas e não realiza login. Essas responsabilidades pertencem à `api-autenticacao`.

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
- PostgreSQL 16;
- Flyway;
- Spring AMQP;
- RabbitMQ;
- H2 Database;
- Docker;
- Docker Compose;
- Swagger/OpenAPI;
- Spring Boot Actuator;
- Maven;
- JUnit;
- MockMvc;
- Mockito;
- Jackson;
- Lombok.

---

## 🗄️ Banco de dados e Flyway

O Flyway gerencia a evolução do banco por meio de migrações versionadas:

```text
src/main/resources/db/migration/
├── V1__create_finance_schema.sql
├── V2__add_user_ownership.sql
└── V3__make_user_ownership_required.sql
```

As migrações atuais:

- criam as tabelas de categorias e movimentações;
- adicionam `usuario_id`;
- criam índices para consultas por usuário;
- tornam o proprietário obrigatório;
- preservam a integridade dos dados.

O Hibernate utiliza:

```yaml
ddl-auto: validate
```

Assim, o Hibernate valida as entidades, enquanto o Flyway controla as alterações estruturais do banco.

---

## 📡 Endpoints

URL local:

```text
http://localhost:8083
```

Todos os endpoints abaixo exigem um token JWT válido.

### Categorias

| Método   | Endpoint                                | Descrição              |
|----------|-----------------------------------------|------------------------|
| `POST`   | `/api/v1/categorias/criar`              | Cadastra uma categoria |
| `PUT`    | `/api/v1/categorias/alterar/{id}`       | Altera uma categoria   |
| `DELETE` | `/api/v1/categorias/excluir/{id}`       | Exclui uma categoria   |
| `GET`    | `/api/v1/categorias/consultar`          | Consulta as categorias |
| `GET`    | `/api/v1/categorias/obter/{id}`         | Obtém uma categoria    |

Exemplo de cadastro:

```json
{
  "nome": "Alimentação"
}
```

Exemplo de resposta:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Alimentação"
}
```

### Movimentações

| Método   | Endpoint                                    | Descrição                         |
|----------|---------------------------------------------|-----------------------------------|
| `POST`   | `/api/v1/movimentacoes/criar`               | Cadastra uma movimentação         |
| `PUT`    | `/api/v1/movimentacoes/alterar/{id}`        | Altera uma movimentação           |
| `DELETE` | `/api/v1/movimentacoes/excluir/{id}`        | Exclui uma movimentação           |
| `GET`    | `/api/v1/movimentacoes/consultar`           | Consulta movimentações por período |
| `GET`    | `/api/v1/movimentacoes/obter/{id}`          | Obtém uma movimentação            |
| `POST`   | `/api/v1/movimentacoes/gerar-relatorio`     | Solicita um relatório assíncrono  |

Tipos aceitos:

```text
RECEITA
DESPESA
```

Exemplo de cadastro:

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

Parâmetros:

| Parâmetro    | Tipo     | Obrigatório | Padrão |
|--------------|----------|-------------|--------|
| `dataInicio` | Data ISO | Sim         | —      |
| `dataFim`    | Data ISO | Sim         | —      |
| `pageIndex`  | Inteiro  | Não         | `0`    |
| `pageSize`   | Inteiro  | Não         | `25`   |

Exemplo:

```http
GET /api/v1/movimentacoes/consultar?dataInicio=2026-07-01&dataFim=2026-07-31&pageIndex=0&pageSize=25
```

---

## 📨 Fluxo de relatório

```text
Cliente autenticado
   ↓
MovimentacaoController
   ↓
UUID e e-mail extraídos do JWT
   ↓
MovimentacaoService
   ↓
Movimentações filtradas pelo usuário e período
   ↓
Serialização com Jackson
   ↓
RabbitMQ
   ↓
Fila relatorios-movimentacoes
   ↓
WorkerService
   ↓
Futura integração com a api-agentesia
```

O processamento é assíncrono: a API publica a solicitação e não precisa aguardar a geração completa do relatório.

---

## ⚙️ Configuração local

A aplicação utiliza o arquivo `.env.properties` para os segredos locais.

Crie o arquivo na raiz do projeto:

```text
.env.properties
```

Exemplo:

```properties
DB_PASSWORD=coti
RABBITMQ_PASSWORD=coti
JWT_SECRET=COLE_AQUI_A_MESMA_CHAVE_BASE64_DA_API_AUTENTICACAO
```

O arquivo está incluído no `.gitignore` e não deve ser enviado ao GitHub.

Variáveis disponíveis:

| Variável                    | Descrição                               | Padrão local |
|-----------------------------|-----------------------------------------|--------------|
| `DB_URL`                    | URL do PostgreSQL                       | `jdbc:postgresql://localhost:5435/bd-api-financas` |
| `DB_USER`                   | Usuário do PostgreSQL                   | `coti` |
| `DB_PASSWORD`               | Senha do PostgreSQL                     | obrigatório |
| `JWT_SECRET`                | Chave Base64 compartilhada              | obrigatório |
| `JWT_ISSUER`                | Emissor esperado no token               | `api-autenticacao` |
| `RABBITMQ_HOST`             | Host do RabbitMQ                        | `localhost` |
| `RABBITMQ_PORT`             | Porta do RabbitMQ                       | `5672` |
| `RABBITMQ_USER`             | Usuário do RabbitMQ                     | `coti` |
| `RABBITMQ_PASSWORD`         | Senha do RabbitMQ                       | obrigatório |
| `RABBITMQ_VIRTUAL_HOST`     | Virtual host                            | `/` |
| `CORS_FRONTEND`             | Origem permitida para o frontend        | `http://localhost:4200` |
| `SERVER_PORT`               | Porta da API                            | `8083` |

---

## ▶️ Executando o projeto

### Pré-requisitos

- Java 25;
- Docker Desktop;
- Git.

### 1. Clonar o repositório

```bash
git clone https://github.com/beatrizlima-tech/api-financas.git
cd api-financas
```

### 2. Criar `.env.properties`

Configure a senha do banco, a senha do RabbitMQ e a mesma chave JWT utilizada pela `api-autenticacao`.

### 3. Subir a infraestrutura

```bash
docker compose up -d
```

### 4. Executar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

O Flyway aplicará automaticamente as migrações pendentes.

---

## 🐳 Infraestrutura Docker

O Docker Compose disponibiliza:

| Serviço    | Porta local |
|------------|-------------|
| PostgreSQL | `5435`      |
| pgAdmin    | `5056`      |
| RabbitMQ   | `5672`      |
| RabbitMQ Management | `15672` |

Acessos locais:

```text
pgAdmin:   http://localhost:5056
RabbitMQ:  http://localhost:15672
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

Para testar endpoints protegidos:

1. autentique um usuário na `api-autenticacao`;
2. copie o `accessToken`;
3. abra o Swagger da `api-financas`;
4. clique em **Authorize**;
5. cole somente o token iniciado por `eyJ`;
6. execute os endpoints.

---

## 🧪 Testes automatizados

A aplicação possui **32 testes automatizados**:

- 27 testes funcionais de categorias, movimentações e relatórios;
- 5 testes de integração de segurança e isolamento entre usuários.

Os testes utilizam:

- JUnit;
- MockMvc;
- Mockito;
- `@MockitoBean`;
- H2 em memória;
- perfil `test`;
- RabbitMQ desativado durante a suíte;
- Flyway desativado no ambiente de teste;
- tokens JWT de teste;
- validação de acesso entre usuários diferentes.

Executar no Windows:

```powershell
.\mvnw.cmd clean test
```

Resultado atual:

```text
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Gerar o pacote:

```powershell
.\mvnw.cmd clean package
```

---

## 🚧 Próximas melhorias

- concluir a adoção de injeção por construtor;
- aplicar Bean Validation aos DTOs;
- padronizar erros com `ProblemDetail`;
- impedir categorias duplicadas por usuário;
- ampliar os testes unitários dos services;
- adicionar perfil de produção;
- integrar o worker à `api-agentesia`;
- gerar análises financeiras com inteligência artificial;
- enviar relatórios por e-mail;
- integrar a API ao frontend Angular;
- preparar a infraestrutura para AWS.

---

## 📊 Status

🚧 **Projeto em evolução ativa.**

Implementado atualmente:

- CRUD de categorias;
- CRUD de movimentações;
- paginação e filtros por período;
- segurança JWT;
- isolamento de dados por usuário;
- UUIDs públicos;
- migrações Flyway;
- CORS configurável;
- health check;
- RabbitMQ;
- Swagger/OpenAPI;
- 32 testes aprovados.

---

## 👩‍💻 Autora

**Beatriz Lima de Oliveira**

GitHub: [beatrizlima-tech](https://github.com/beatrizlima-tech)