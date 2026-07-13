# 💰 API Finanças

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-green?style=for-the-badge\&logo=springboot)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-success?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge\&logo=postgresql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge\&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge\&logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge\&logo=swagger)
![JUnit](https://img.shields.io/badge/JUnit-Tests-25A162?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-lightgrey?style=for-the-badge)

---

## 📌 Sobre o projeto

A **API Finanças** é uma aplicação backend desenvolvida em **Java** com **Spring Boot**, destinada ao gerenciamento de finanças pessoais.

O sistema permitirá organizar receitas e despesas por categorias, consultar movimentações financeiras e acompanhar informações importantes para o controle financeiro do usuário.

O projeto utiliza arquitetura REST, persistência de dados com PostgreSQL e recursos modernos do ecossistema Spring.

O desenvolvimento está sendo realizado com foco em:

* organização em camadas;
* separação de responsabilidades;
* utilização de DTOs;
* aplicação de regras de negócio;
* validação de dados;
* tratamento de exceções;
* documentação com Swagger/OpenAPI;
* persistência com Spring Data JPA;
* testes automatizados;
* boas práticas de desenvolvimento backend.

---

## 🚀 Tecnologias utilizadas

* Java 21
* Spring Boot 4
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* RabbitMQ
* Docker
* Docker Compose
* Swagger/OpenAPI
* Maven
* JUnit
* MockMvc
* ObjectMapper

---

## 📂 Estrutura do projeto

O projeto segue uma organização em camadas, separando as responsabilidades para facilitar a manutenção, os testes e a evolução da aplicação.

### Controllers

Classes responsáveis por disponibilizar os endpoints REST da aplicação, receber as requisições HTTP e retornar as respostas adequadas.

### Services

Classes responsáveis pelas regras de negócio, validações e comunicação entre os controllers e os repositories.

### Repositories

Interfaces responsáveis pela comunicação com o banco de dados utilizando Spring Data JPA.

### Entities

Classes que representam as tabelas e os relacionamentos existentes no banco de dados.

### DTOs

Objetos utilizados para transportar os dados de entrada e saída da API sem expor diretamente as entidades.

### Exceptions

Classes responsáveis por representar erros de validação e situações em que registros não são encontrados.

### Enums

Classes responsáveis por representar conjuntos fixos de valores utilizados pela aplicação, como os tipos de movimentação financeira.

### Configurations

Classes responsáveis pelas configurações gerais do projeto, como Swagger/OpenAPI e ObjectMapper.

### Security

Camada prevista para a futura implementação da autenticação dos usuários e proteção dos endpoints.

---

## ✨ Funcionalidades previstas

* Cadastro de usuários
* Autenticação com JWT
* Cadastro de categorias
* Cadastro de receitas
* Cadastro de despesas
* Alteração de movimentações
* Exclusão de movimentações
* Consulta de movimentações
* Consulta por período
* Filtros por categoria
* Paginação dos resultados
* Dashboard financeiro
* Relatórios financeiros
* Documentação da API com Swagger
* Testes automatizados da aplicação

---

## ✅ Funcionalidades implementadas

### 🧩 CRUD de categorias

Foi desenvolvido o fluxo completo de gerenciamento de categorias financeiras.

A API permite cadastrar, alterar, excluir, consultar e obter categorias por identificador.

As categorias poderão ser vinculadas às movimentações financeiras para organizar receitas e despesas.

A implementação contempla:

* endpoint para cadastro de categorias;
* endpoint para alteração de categorias;
* endpoint para exclusão de categorias;
* endpoint para consulta de todas as categorias;
* endpoint para obtenção de categoria por ID;
* criação do DTO `CategoriaRequest`;
* criação do DTO `CategoriaResponse`;
* desenvolvimento do serviço `CategoriaService`;
* integração com o repositório `CategoriaRepository`;
* retorno das categorias contendo `id` e `nome`;
* validação do nome durante o cadastro;
* validação do nome durante a alteração;
* criação da exceção `ValidacaoException`;
* criação da exceção `RegistroNaoEncontradoException`;
* tratamento de dados inválidos;
* tratamento de categoria não encontrada;
* configuração do ObjectMapper;
* configuração da documentação com Swagger/OpenAPI;
* desenvolvimento de testes automatizados;
* configuração de um perfil específico para o ambiente de testes.

---

### 💸 Cadastro de movimentações

Foi implementado o fluxo inicial de cadastro de movimentações financeiras.

A aplicação permite cadastrar uma receita ou despesa e associá-la a uma categoria previamente cadastrada no banco de dados.

A implementação contempla:

* criação do endpoint para cadastro de movimentações;
* criação do DTO `MovimentacaoRequest`;
* criação do DTO `MovimentacaoResponse`;
* desenvolvimento inicial do serviço `MovimentacaoService`;
* integração com o repositório `MovimentacaoRepository`;
* associação entre movimentação e categoria;
* validação do nome da movimentação;
* validação da data da movimentação;
* validação do valor da movimentação;
* validação do tipo da movimentação;
* validação do identificador da categoria;
* verificação da existência da categoria;
* conversão do valor recebido para `BigDecimal` antes da persistência;
* persistência da movimentação no banco de dados;
* conversão da entidade para o DTO de resposta;
* tratamento de dados inválidos;
* tratamento de categoria não encontrada;
* criação de consulta JPQL por intervalo de datas;
* preparação do repository para paginação.

Os tipos de movimentação aceitos são:

```text
RECEITA
DESPESA
```

---

## 🚧 Funcionalidades em desenvolvimento

### 💸 API de movimentações

O cadastro de movimentações já está implementado.

As seguintes operações ainda estão em desenvolvimento:

* alteração de movimentações;
* exclusão de movimentações;
* consulta de movimentações;
* obtenção de movimentação por identificador;
* consulta por intervalo de datas;
* paginação dos resultados;
* filtros por categoria;
* testes automatizados para movimentações.

Os métodos correspondentes a algumas dessas operações já possuem estruturas iniciais no `MovimentacaoController`, mas ainda não possuem implementação definitiva no service.

---

## 📡 Endpoints

### Categorias

#### Criar categoria

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

Resposta esperada:

```http
201 Created
```

Possíveis erros:

```http
400 Bad Request
```

Exemplos de mensagens:

```text
O nome da categoria é obrigatório.
```

```text
O nome da categoria deve ter pelo menos 6 caracteres.
```

---

#### Alterar categoria

```http
PUT /api/v1/categorias/alterar/{id}
```

Exemplo de requisição:

```json
{
  "nome": "Categoria Alterada"
}
```

Exemplo de resposta:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Categoria Alterada"
}
```

Resposta esperada:

```http
200 OK
```

Possíveis erros:

```http
400 Bad Request
```

Exemplos de mensagens:

```text
O nome da categoria é obrigatório.
```

```text
O nome da categoria deve ter pelo menos 6 caracteres.
```

Também poderá ser retornado:

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

#### Excluir categoria

```http
DELETE /api/v1/categorias/excluir/{id}
```

Exemplo de resposta:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Categoria Alterada"
}
```

Resposta esperada:

```http
200 OK
```

Possíveis erros:

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

#### Consultar categorias

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

Resposta esperada:

```http
200 OK
```

---

#### Obter categoria por ID

```http
GET /api/v1/categorias/obter/{id}
```

Exemplo de resposta:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Alimentação"
}
```

Resposta esperada:

```http
200 OK
```

Possíveis erros:

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

### Movimentações

#### Criar movimentação

```http
POST /api/v1/movimentacoes/criar
```

Exemplo de requisição para uma receita:

```json
{
  "nome": "Salário mensal",
  "data": "2026-07-13",
  "valor": 3500.00,
  "tipo": "RECEITA",
  "categoriaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Exemplo de requisição para uma despesa:

```json
{
  "nome": "Compra mensal",
  "data": "2026-07-13",
  "valor": 750.90,
  "tipo": "DESPESA",
  "categoriaId": "8b9f3a9d-3a3b-4d1a-9c2e-9f0a5a6b7c8d"
}
```

Exemplo de resposta:

```json
{
  "id": "f68b6327-f8eb-4743-b76f-02377ab2d401",
  "nome": "Salário mensal",
  "data": "2026-07-13",
  "valor": 3500.0,
  "tipo": "RECEITA",
  "categoria": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nome": "Salário"
  }
}
```

Resposta esperada:

```http
201 Created
```

Possíveis erros de validação:

```http
400 Bad Request
```

Exemplos de mensagens:

```text
Os dados da movimentação são obrigatórios.
```

```text
O nome da movimentação é obrigatório.
```

```text
O nome da movimentação deve ter pelo menos 6 caracteres.
```

```text
A data da movimentação é obrigatória.
```

```text
O valor da movimentação é obrigatório.
```

```text
O valor da movimentação deve ser maior que zero.
```

```text
O tipo da movimentação é obrigatório.
```

```text
O tipo da movimentação deve ser RECEITA ou DESPESA.
```

```text
A categoria da movimentação é obrigatória.
```

Também poderá ser retornado:

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

## 🧪 Testes automatizados

Foram implementados testes automatizados para validar os principais comportamentos da API de categorias.

Os testes utilizam:

* JUnit;
* MockMvc;
* ObjectMapper;
* contexto do Spring Boot;
* perfil específico para o ambiente de testes.

Atualmente, os testes verificam os seguintes cenários:

* criação de uma categoria com sucesso;
* retorno do status HTTP `201 Created`;
* preenchimento do identificador da categoria criada;
* retorno correto do nome enviado no cadastro;
* retorno `400 Bad Request` quando o nome está vazio;
* retorno `400 Bad Request` quando o nome possui menos de seis caracteres;
* validação das mensagens de erro do cadastro;
* alteração de uma categoria com sucesso;
* manutenção do identificador após a alteração;
* retorno `404 Not Found` ao alterar uma categoria inexistente;
* exclusão de uma categoria com sucesso;
* retorno dos dados da categoria excluída;
* retorno `404 Not Found` ao excluir uma categoria inexistente;
* obtenção de uma categoria por ID;
* retorno `404 Not Found` ao obter uma categoria inexistente;
* consulta da lista de categorias cadastradas.

Os testes automatizados do módulo de movimentações serão desenvolvidos nas próximas etapas.

Para executar os testes no Windows:

```bash
.\mvnw.cmd test
```

Caso o Maven esteja instalado globalmente:

```bash
mvn test
```

---

## ▶️ Como executar o projeto

Clone o repositório:

```bash
git clone https://github.com/beatrizlima-tech/api-financas.git
```

Acesse a pasta do projeto:

```bash
cd api-financas
```

Suba os containers:

```bash
docker-compose up -d
```

Execute a aplicação pelo IntelliJ IDEA ou por outro ambiente compatível com Spring Boot.

A API ficará disponível em:

```text
http://localhost:8083
```

A documentação da API poderá ser acessada pelo Swagger conforme a configuração existente no projeto.

---

## 🗄️ Infraestrutura com Docker

O projeto utiliza Docker Compose para disponibilizar os serviços necessários para o funcionamento da aplicação.

Entre os serviços configurados estão:

* PostgreSQL;
* RabbitMQ;
* interface de gerenciamento do RabbitMQ;
* pgAdmin.

Para iniciar os containers:

```bash
docker-compose up -d
```

Para verificar os containers em execução:

```bash
docker ps
```

Para interromper os containers:

```bash
docker-compose down
```

---

## 🧾 Comandos Git úteis

Verificar as alterações realizadas:

```bash
git status
```

Adicionar todas as alterações:

```bash
git add .
```

Executar os testes antes do commit:

```bash
.\mvnw.cmd test
```

Criar o commit desta atualização:

```bash
git commit -m "feat: implementar cadastro de movimentações financeiras"
```

Caso o README também seja incluído na atualização:

```bash
git commit -m "feat: implementar cadastro de movimentações e atualizar documentação"
```

Enviar as alterações para o GitHub:

```bash
git push origin main
```

---

## 📖 Status do projeto

🚧 **Projeto em desenvolvimento.**

Atualmente, a API possui o CRUD de categorias implementado, incluindo:

* cadastro;
* alteração;
* exclusão;
* consulta geral;
* obtenção por identificador;
* validações;
* tratamento de exceções;
* testes automatizados.

O módulo de movimentações possui o cadastro de receitas e despesas implementado, incluindo:

* DTO de entrada;
* DTO de saída;
* camada de serviço;
* validações;
* associação com categorias;
* persistência no banco de dados;
* conversão da entidade para resposta;
* tratamento de categoria não encontrada.

O repositório de movimentações também possui uma consulta JPQL preparada para buscar registros por intervalo de datas e utilizar paginação.

As operações de alteração, exclusão, consulta e obtenção de movimentações serão desenvolvidas nas próximas etapas do projeto.
