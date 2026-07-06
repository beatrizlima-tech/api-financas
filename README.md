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

# 📌 Sobre o projeto

A **API Finanças** é uma aplicação backend desenvolvida em **Java** com **Spring Boot**, destinada ao gerenciamento de finanças pessoais.

O sistema permite organizar receitas e despesas por categorias, utilizando uma arquitetura REST, persistência com PostgreSQL e recursos modernos do ecossistema Spring.

O projeto está sendo desenvolvido com foco em boas práticas, organização em camadas, separação de responsabilidades, uso de DTOs, documentação da API e testes automatizados.

---

# 🚀 Tecnologias

* Java 21
* Spring Boot 4
* Spring Data JPA
* Hibernate
* PostgreSQL
* RabbitMQ
* Docker
* Swagger/OpenAPI
* Maven
* JUnit
* MockMvc

---

# 📂 Estrutura do projeto

O projeto segue uma organização em camadas, separando responsabilidades para facilitar manutenção, testes e evolução da aplicação.

* **Entities**: classes que representam as tabelas do banco de dados.
* **Repositories**: interfaces responsáveis pela comunicação com o banco de dados.
* **Controllers**: classes responsáveis por disponibilizar os endpoints da API.
* **Services**: classes responsáveis pelas regras de negócio.
* **DTOs**: objetos utilizados para entrada e saída de dados da API.
* **Configurations**: classes de configuração do projeto, como Swagger e ObjectMapper.
* **Security**: camada prevista para autenticação e proteção dos endpoints.

---

# ✨ Funcionalidades previstas

* Cadastro de usuários
* Autenticação com JWT
* Cadastro de categorias
* Cadastro de receitas
* Cadastro de despesas
* Consulta de movimentações
* Filtros por categoria e período
* Dashboard financeiro
* Documentação da API com Swagger
* Testes automatizados da API

---

# ✅ Funcionalidades implementadas

## 🧩 Criação de categorias

Foi desenvolvido o fluxo de criação de categorias financeiras, permitindo cadastrar categorias que poderão ser utilizadas para organizar receitas e despesas.

A implementação contempla:

* Criação do endpoint para cadastro de categorias.
* Criação dos DTOs `CategoriaRequest` e `CategoriaResponse`.
* Desenvolvimento do serviço `CategoriaService`.
* Integração com o repositório `CategoriaRepository`.
* Retorno da categoria cadastrada contendo `id` e `nome`.
* Configuração do `ObjectMapper`.
* Configuração da documentação da API com Swagger/OpenAPI.
* Desenvolvimento de teste automatizado com JUnit e MockMvc.

---

# 📡 Endpoints

## Categorias

### Criar categoria

```http
POST /api/v1/categorias/criar
```

### Exemplo de requisição

```json
{
  "nome": "Alimentação"
}
```

### Exemplo de resposta

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Alimentação"
}
```

### Resposta esperada

```http
201 Created
```

---

# 🧪 Testes

Foi implementado um teste de integração para validar o endpoint de criação de categorias.

O teste verifica se:

* a API retorna o status HTTP `201 Created`;
* o campo `id` é retornado preenchido;
* o nome retornado na resposta é igual ao nome enviado na requisição.

Para executar os testes, utilize:

```bash
mvn test
```

---

# ▶️ Como executar

Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
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

A documentação da API poderá ser acessada pelo Swagger, conforme configuração do projeto.

---

# 📖 Status

🚧 Projeto em desenvolvimento.

Atualmente, o projeto possui o fluxo de criação de categorias implementado, com endpoint REST, camada de serviço, DTOs, integração com repositório, documentação Swagger/OpenAPI e teste automatizado com JUnit e MockMvc.
