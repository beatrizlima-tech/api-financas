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

O projeto está sendo desenvolvido com foco em boas práticas, organização em camadas, separação de responsabilidades, uso de DTOs, documentação da API, tratamento de exceções e testes automatizados.

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
* **Exceptions**: classes responsáveis por representar erros de validação e registros não encontrados.
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

## 🧩 CRUD de categorias

Foi desenvolvido o fluxo de gerenciamento de categorias financeiras, permitindo cadastrar, alterar, excluir e consultar categorias que poderão ser utilizadas para organizar receitas e despesas.

A implementação contempla:

* Criação do endpoint para cadastro de categorias.
* Criação do endpoint para alteração de categorias.
* Criação do endpoint para exclusão de categorias.
* Criação do endpoint para consulta de categorias.
* Criação dos DTOs `CategoriaRequest` e `CategoriaResponse`.
* Desenvolvimento do serviço `CategoriaService`.
* Integração com o repositório `CategoriaRepository`.
* Retorno da categoria contendo `id` e `nome`.
* Criação da exceção `ValidacaoException`.
* Criação da exceção `RegistroNaoEncontradoException`.
* Validação do nome da categoria.
* Tratamento de erro para dados inválidos.
* Tratamento de erro para categoria não encontrada.
* Configuração do `ObjectMapper`.
* Configuração da documentação da API com Swagger/OpenAPI.
* Desenvolvimento de testes automatizados com JUnit e MockMvc.

---

# 📡 Endpoints

## Categorias

### Criar categoria

```http
POST /api/v1/categorias/criar
```

#### Exemplo de requisição

```json
{
  "nome": "Alimentação"
}
```

#### Exemplo de resposta

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Alimentação"
}
```

#### Resposta esperada

```http
201 Created
```

#### Possíveis erros

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

### Alterar categoria

```http
PUT /api/v1/categorias/alterar/{id}
```

#### Exemplo de requisição

```json
{
  "nome": "Mercado"
}
```

#### Exemplo de resposta

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Mercado"
}
```

#### Resposta esperada

```http
200 OK
```

#### Possíveis erros

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

### Excluir categoria

```http
DELETE /api/v1/categorias/excluir/{id}
```

#### Exemplo de resposta

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Mercado"
}
```

#### Resposta esperada

```http
200 OK
```

#### Possíveis erros

```http
404 Not Found
```

Exemplo de mensagem:

```text
Categoria não encontrada.
```

---

### Consultar categorias

```http
GET /api/v1/categorias/consultar
```

#### Exemplo de resposta

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

#### Resposta esperada

```http
200 OK
```

---

# 🧪 Testes

Foram implementados testes automatizados para validar o comportamento do endpoint de criação de categorias.

Os testes verificam se:

* a API retorna o status HTTP `201 Created` ao criar uma categoria válida;
* o campo `id` é retornado preenchido;
* o nome retornado na resposta é igual ao nome enviado na requisição;
* a API retorna `400 Bad Request` quando o nome da categoria está vazio;
* a API retorna `400 Bad Request` quando o nome da categoria possui menos de 6 caracteres;
* as mensagens de erro são retornadas corretamente.

Para executar os testes no Windows, utilize:

```bash
.\mvnw.cmd test
```

Ou, caso tenha o Maven instalado globalmente:

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

# 🧾 Comandos Git úteis

Verificar alterações:

```bash
git status
```

Adicionar alterações:

```bash
git add .
```

Criar commit:

```bash
git commit -m "feat: implementar CRUD de categorias"
```

Enviar para o GitHub:

```bash
git push origin main
```

---

# 📖 Status

🚧 Projeto em desenvolvimento.

Atualmente, o projeto possui o CRUD de categorias implementado, com endpoints REST, camada de serviço, DTOs, integração com repositório, tratamento de exceções, documentação Swagger/OpenAPI e testes automatizados com JUnit e MockMvc.
