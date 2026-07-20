# 💰 API Finanças

![Java](https://img.shields.io/badge/Java-25-red?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green?style=for-the-badge\&logo=springboot)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-success?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge\&logo=postgresql)
![H2](https://img.shields.io/badge/H2-Test%20Database-09476B?style=for-the-badge)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge\&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge\&logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge\&logo=swagger)
![JUnit](https://img.shields.io/badge/JUnit-27%20Tests-25A162?style=for-the-badge\&logo=junit5)

---

## 📌 Sobre o projeto

A **API Finanças** é uma aplicação backend desenvolvida em **Java** com **Spring Boot** para o gerenciamento de finanças pessoais.

A API permite organizar receitas e despesas por categorias, executar o CRUD de categorias e movimentações, consultar movimentações por intervalo de datas com paginação e solicitar o processamento assíncrono de relatórios financeiros utilizando RabbitMQ.

O projeto utiliza arquitetura REST, persistência com PostgreSQL, mensageria com RabbitMQ, testes automatizados com banco H2 em memória e recursos do ecossistema Spring.

O desenvolvimento foi realizado com foco em:

* organização em camadas;
* separação de responsabilidades;
* utilização de DTOs;
* aplicação de regras de negócio;
* validação de dados;
* tratamento de exceções;
* documentação com Swagger/OpenAPI;
* persistência com Spring Data JPA;
* consultas JPQL;
* paginação;
* mensageria assíncrona;
* serialização de dados com Jackson;
* testes automatizados.

---

## 🚀 Tecnologias utilizadas

* Java 25
* Spring Boot 4.1.0
* Spring Web MVC
* Spring Data JPA
* Spring AMQP
* Hibernate
* PostgreSQL
* H2 Database
* RabbitMQ
* Docker
* Docker Compose
* Swagger/OpenAPI
* Maven
* JUnit
* MockMvc
* Mockito
* Jackson ObjectMapper
* Lombok

---

## 📂 Estrutura do projeto

O projeto segue uma organização em camadas para facilitar a manutenção, os testes e a evolução da aplicação.

### Controllers

Responsáveis por disponibilizar os endpoints REST, receber as requisições HTTP e retornar as respostas adequadas.

### Services

Responsáveis pelas regras de negócio, validações e comunicação entre controllers, repositories e serviços de mensageria.

O `MovimentacaoService` também prepara e publica as solicitações de relatórios no RabbitMQ.

O `WorkerService` atua como consumidor da fila, recebendo as mensagens publicadas para o processamento dos relatórios.

### Repositories

Responsáveis pela comunicação com o banco de dados por meio do Spring Data JPA e das consultas JPQL.

### Entities

Representam as tabelas, os atributos e os relacionamentos existentes no banco de dados.

### DTOs

Transportam os dados de entrada, saída e mensageria sem expor diretamente as entidades da aplicação.

### Exceptions

Representam erros de validação e situações em que registros não são encontrados.

### Handlers

Realizam o tratamento global de erros inesperados da aplicação.

### Enums

Representam conjuntos fixos de valores, como os tipos de movimentação financeira.

### Configurations

Contêm as configurações do Swagger/OpenAPI, Jackson ObjectMapper e RabbitMQ.

---

## ✅ Funcionalidades implementadas

### 🧩 CRUD de categorias

* cadastro de categoria;
* alteração de categoria;
* exclusão de categoria;
* consulta de todas as categorias;
* obtenção de categoria por identificador;
* validação de dados obrigatórios;
* validação de nome obrigatório;
* validação de nome com no mínimo seis caracteres;
* remoção de espaços extras antes da persistência;
* tratamento de dados inválidos;
* tratamento de categoria não encontrada;
* conversão da entidade para `CategoriaResponse`.

### 💸 CRUD de movimentações

* cadastro de receita ou despesa;
* alteração de movimentação;
* exclusão de movimentação;
* obtenção de movimentação por identificador;
* consulta por intervalo de datas;
* paginação dos resultados;
* limite máximo de 25 registros por página;
* validação do índice e do tamanho da página;
* associação entre movimentação e categoria;
* validação de nome, data, valor, tipo e categoria;
* normalização do tipo informado;
* verificação da existência da categoria;
* persistência do valor monetário com `BigDecimal`;
* consulta JPQL utilizando `BETWEEN`;
* ordenação das movimentações por data decrescente;
* conversão das entidades para `MovimentacaoResponse`.

### 📨 Relatórios e mensageria

* solicitação de relatório por intervalo de datas;
* validação das datas informadas;
* validação da ordem do período;
* consulta das movimentações destinadas ao relatório;
* retorno específico quando não existem movimentações no período;
* criação do DTO `RelatorioMovimentacaoRequest`;
* conversão das movimentações para DTOs de resposta;
* serialização dos dados para JSON;
* publicação da solicitação no RabbitMQ;
* utilização da fila `relatorios-movimentacoes`;
* consumo das mensagens pelo `WorkerService`;
* leitura e exibição do payload recebido no console;
* suporte à serialização de `LocalDate` com `JavaTimeModule`.

A geração da análise por inteligência artificial e o envio do relatório por e-mail ainda serão implementados.

Atualmente, o worker recebe a mensagem da fila e exibe o conteúdo no console, representando a etapa inicial da comunicação com a futura API de agentes de inteligência artificial.

### ⚠️ Tratamento de exceções

* `ValidacaoException` para regras de validação;
* `RegistroNaoEncontradoException` para registros inexistentes;
* tratamento de erros conhecidos nos controllers;
* retorno dos status HTTP adequados;
* `GlobalExceptionHandler` para erros inesperados;
* `ErrorResponse` com status, mensagem e data/hora.

---

## 🔄 Fluxo da solicitação de relatório

```text
Cliente
   ↓
MovimentacaoController
   ↓
MovimentacaoService
   ↓
Validação do período
   ↓
Consulta das movimentações no PostgreSQL
   ↓
Conversão das entidades para DTOs
   ↓
Serialização dos dados com ObjectMapper
   ↓
Fila relatorios-movimentacoes no RabbitMQ
   ↓
WorkerService
   ↓
Futura integração com agente de IA e envio por e-mail
```

O processamento é assíncrono. A API publica a solicitação na fila sem precisar aguardar a geração completa do relatório.

---

## 📡 Endpoints

A URL base da aplicação é:

```text
http://localhost:8083
```

## Categorias

### Criar categoria

```http
POST /api/v1/categorias/criar
```

Exemplo de requisição:

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

Status de sucesso:

```http
201 Created
```

Possível erro:

```http
400 Bad Request
```

Mensagens de validação:

```text
Os dados da categoria são obrigatórios.
O nome da categoria é obrigatório.
O nome da categoria deve ter pelo menos 6 caracteres.
```

---

### Alterar categoria

```http
PUT /api/v1/categorias/alterar/{id}
```

Exemplo de requisição:

```json
{
  "nome": "Categoria Alterada"
}
```

Status de sucesso:

```http
200 OK
```

Possíveis erros:

```http
400 Bad Request
404 Not Found
```

---

### Excluir categoria

```http
DELETE /api/v1/categorias/excluir/{id}
```

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
404 Not Found
```

---

### Consultar categorias

```http
GET /api/v1/categorias/consultar
```

Exemplo de resposta:

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nome": "Alimentação"
  },
  {
    "id": "8b9f3a9d-3a3b-4d1a-9c2e-9f0a5a6b7c8d",
    "nome": "Transporte"
  }
]
```

Status de sucesso:

```http
200 OK
```

---

### Obter categoria por ID

```http
GET /api/v1/categorias/obter/{id}
```

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
404 Not Found
```

---

## Movimentações

Os tipos aceitos são:

```text
RECEITA
DESPESA
```

As datas devem ser enviadas no padrão ISO:

```text
AAAA-MM-DD
```

### Criar movimentação

```http
POST /api/v1/movimentacoes/criar
```

Exemplo de requisição:

```json
{
  "nome": "Salário mensal",
  "data": "2026-07-15",
  "valor": 3500.00,
  "tipo": "RECEITA",
  "categoriaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Exemplo de resposta:

```json
{
  "id": "f68b6327-f8eb-4743-b76f-02377ab2d401",
  "nome": "Salário mensal",
  "data": "2026-07-15",
  "valor": 3500.0,
  "tipo": "RECEITA",
  "categoria": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nome": "Salário"
  }
}
```

Status de sucesso:

```http
201 Created
```

Possíveis erros:

```http
400 Bad Request
404 Not Found
```

Mensagens de validação:

```text
Os dados da movimentação são obrigatórios.
O nome da movimentação é obrigatório.
O nome da movimentação deve ter pelo menos 6 caracteres.
A data da movimentação é obrigatória.
O valor da movimentação é obrigatório.
O valor da movimentação deve ser maior do que zero.
O tipo da movimentação é obrigatório.
O tipo da movimentação deve ser RECEITA ou DESPESA.
O ID da categoria é obrigatório.
Categoria não encontrada.
```

---

### Alterar movimentação

```http
PUT /api/v1/movimentacoes/alterar/{id}
```

O corpo da requisição utiliza a mesma estrutura da criação.

Status de sucesso:

```http
200 OK
```

Possíveis erros:

```http
400 Bad Request
404 Not Found
```

---

### Excluir movimentação

```http
DELETE /api/v1/movimentacoes/excluir/{id}
```

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
404 Not Found
```

---

### Obter movimentação por ID

```http
GET /api/v1/movimentacoes/obter/{id}
```

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
404 Not Found
```

---

### Consultar movimentações por período

```http
GET /api/v1/movimentacoes/consultar
```

Parâmetros:

| Parâmetro    | Tipo     | Obrigatório | Valor padrão |
| ------------ | -------- | ----------: | -----------: |
| `dataInicio` | Data ISO |         Sim |            — |
| `dataFim`    | Data ISO |         Sim |            — |
| `pageIndex`  | Inteiro  |         Não |          `0` |
| `pageSize`   | Inteiro  |         Não |         `25` |

Exemplo:

```http
GET /api/v1/movimentacoes/consultar?dataInicio=2026-07-01&dataFim=2026-07-31&pageIndex=0&pageSize=10
```

A consulta utiliza `BETWEEN`, incluindo as movimentações registradas nas datas inicial e final.

O tamanho máximo permitido é de 25 registros por página. Valores maiores são limitados automaticamente.

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
400 Bad Request
```

Mensagens de validação:

```text
As datas de início e fim são obrigatórias.
A data de início não pode ser maior do que a data de fim.
O índice da página não pode ser negativo.
O tamanho da página deve ser maior que zero.
```

---

### Solicitar relatório de movimentações

```http
POST /api/v1/movimentacoes/gerar-relatorio
```

Parâmetros:

| Parâmetro    | Tipo     | Obrigatório |
| ------------ | -------- | ----------: |
| `dataInicio` | Data ISO |         Sim |
| `dataFim`    | Data ISO |         Sim |

Exemplo:

```http
POST /api/v1/movimentacoes/gerar-relatorio?dataInicio=2026-07-01&dataFim=2026-07-31
```

Ao receber a solicitação, a API:

1. valida o intervalo informado;
2. consulta as movimentações existentes no período;
3. converte as entidades para DTOs de resposta;
4. serializa os dados para JSON;
5. cria uma solicitação de relatório;
6. publica a mensagem na fila `relatorios-movimentacoes`;
7. permite que o `WorkerService` consuma a mensagem de forma assíncrona.

Exemplo de resposta:

```text
Sucesso! Os dados foram enviados para a fila de processamento do relatório.
```

Caso nenhuma movimentação seja encontrada:

```text
Nenhuma movimentação foi encontrada para o período de datas informado.
```

Possível mensagem de validação:

```text
A data de início não pode ser maior do que a data de fim.
```

Status de sucesso:

```http
200 OK
```

Possível erro:

```http
400 Bad Request
```

---

## 🧪 Testes automatizados

A aplicação possui **27 testes automatizados** para os módulos de categorias, movimentações e geração de relatórios.

Os testes utilizam:

* JUnit 5;
* MockMvc;
* Mockito;
* `@MockitoBean`;
* ObjectMapper;
* contexto do Spring Boot;
* perfil `test`;
* banco H2 em memória;
* mock do `RabbitTemplate`;
* desativação do listener do RabbitMQ durante os testes.

O mock do `RabbitTemplate` permite validar a publicação das mensagens sem exigir uma conexão real com o RabbitMQ durante a execução da suíte.

### Categorias

Os testes verificam:

* criação com sucesso;
* validação de nome obrigatório;
* validação do tamanho mínimo do nome;
* alteração com sucesso;
* alteração de categoria inexistente;
* exclusão com sucesso;
* exclusão de categoria inexistente;
* obtenção por ID;
* obtenção de categoria inexistente;
* consulta de todas as categorias.

### Movimentações

Os testes verificam:

* criação com sucesso;
* validação de nome obrigatório;
* validação do tamanho mínimo do nome;
* validação de valor maior que zero;
* validação do tipo;
* validação de categoria inexistente;
* alteração com sucesso;
* alteração de movimentação inexistente;
* exclusão com sucesso;
* exclusão de movimentação inexistente;
* obtenção por ID;
* obtenção de movimentação inexistente;
* consulta por período;
* validação de intervalo de datas inválido.

### Relatórios

Os testes verificam:

* solicitação de relatório com sucesso;
* publicação dos dados na fila `relatorios-movimentacoes`;
* retorno quando não existem movimentações no período;
* ausência de publicação no RabbitMQ quando não existem movimentações;
* validação de intervalo de datas inválido;
* ausência de publicação no RabbitMQ quando o período é inválido.

Para executar os testes no Windows:

```bash
.\mvnw.cmd clean test
```

Resultado esperado:

```text
Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
```

Caso o Maven esteja instalado globalmente:

```bash
mvn clean test
```

---

## 📖 Swagger/OpenAPI

Com a aplicação em execução, a documentação pode ser acessada em:

```text
http://localhost:8083/swagger-ui/index.html
```

A especificação OpenAPI fica disponível em:

```text
http://localhost:8083/v3/api-docs
```

---

## ▶️ Como executar o projeto

### Pré-requisitos

* Java 25;
* Docker Desktop;
* Git.

### 1. Clonar o repositório

```bash
git clone https://github.com/beatrizlima-tech/api-financas.git
```

### 2. Acessar a pasta

```bash
cd api-financas
```

### 3. Subir a infraestrutura

```bash
docker compose up -d
```

### 4. Executar a aplicação

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8083
```

---

## 🗄️ Infraestrutura com Docker

O Docker Compose disponibiliza:

* PostgreSQL na porta `5435`;
* pgAdmin na porta `5056`;
* RabbitMQ na porta `5672`;
* painel de gerenciamento do RabbitMQ na porta `15672`.

Iniciar os containers:

```bash
docker compose up -d
```

Verificar os containers:

```bash
docker ps
```

Interromper os containers:

```bash
docker compose down
```

Painel de gerenciamento do RabbitMQ:

```text
http://localhost:15672
```

Credenciais locais:

```text
Usuário: coti
Senha: coti
```

---

## 🚧 Próximas funcionalidades

* integração do `WorkerService` com a API do agente de inteligência artificial;
* geração da análise financeira;
* envio do relatório por e-mail;
* integração com o frontend;
* cadastro de usuários;
* autenticação com JWT;
* proteção dos endpoints;
* filtros por categoria;
* dashboard financeiro;
* testes para as futuras integrações externas.

---

## 🧾 Comandos Git úteis

Verificar as alterações:

```bash
git status
```

Adicionar as alterações:

```bash
git add .
```

Executar os testes:

```bash
.\mvnw.cmd clean test
```

Criar um commit:

```bash
git commit -m "mensagem do commit"
```

Enviar para o GitHub:

```bash
git push origin main
```

---

## 📖 Status do projeto

🚧 **Projeto em desenvolvimento.**

Os módulos de categorias e movimentações possuem CRUD completo, validações, tratamento de exceções, consulta por período, paginação e testes automatizados.

O projeto também possui o fluxo inicial de processamento assíncrono de relatórios com RabbitMQ. As movimentações são consultadas por período, convertidas para JSON, publicadas em uma fila e consumidas pelo `WorkerService`.

A aplicação possui atualmente **27 testes automatizados executados com sucesso**.

A próxima etapa prevista é integrar o worker com a API do agente de inteligência artificial e implementar o envio dos relatórios por e-mail.

---

## 👩‍💻 Autora

**Beatriz Lima de Oliveira**

GitHub: `beatrizlima-tech`
