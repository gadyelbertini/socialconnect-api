# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · H2 (dev) · PostgreSQL (prod)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd socialconnect-api

# 2. Compile o projeto
mvn clean compile

# 3. Rode a aplicação
mvn spring-boot:run
```

Com o wrapper Maven no Windows, use `.\mvnw.cmd clean compile` e
`.\mvnw.cmd spring-boot:run`.

## Swagger / OpenAPI

Com a aplicação em execução, acesse:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Especificação OpenAPI: <http://localhost:8080/api-docs>

## Módulo de Produtos

Os produtos são persistidos pela migration Flyway
`V4__create_produtos_table.sql`. A API valida os dados, impede estoque
negativo, rejeita nomes duplicados e informa quando o estoque atual está
abaixo do mínimo cadastrado.

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/produtos` | Lista produtos paginados |
| GET | `/api/v1/produtos/{id_produto}` | Consulta um produto |
| POST | `/api/v1/produtos` | Cadastra um produto |
| PUT | `/api/v1/produtos/{id_produto}` | Atualiza todos os dados do produto |
| DELETE | `/api/v1/produtos/{id_produto}` | Remove um produto |

Parâmetros de listagem opcionais: `nome` (busca parcial), `categoria`,
`page`, `size` e `sort`. Exemplo:

```text
GET /api/v1/produtos?nome=arroz&categoria=ALIMENTO&page=0&size=10&sort=nome,asc
```

Exemplo de corpo para criação e atualização:

```json
{
  "nome": "Arroz 5kg",
  "categoria": "ALIMENTO",
  "estoqueAtual": 3,
  "estoqueMinimo": 10,
  "unidadeMedida": "unidade"
}
```

O POST retorna `201 Created` com o cabeçalho `Location`. Estoque negativo
retorna `422 Unprocessable Entity`, nome duplicado retorna `409 Conflict` e
recursos inexistentes retornam `404 Not Found`; os erros usam Problem Details.

## Testes

Execute os testes unitários com:

```bash
.\mvnw.cmd -Dtest=ProdutoServiceTest test
```

Os testes de integração do módulo usam PostgreSQL 17 via Testcontainers e
requerem Docker em execução:

```bash
.\mvnw.cmd -Dtest=ProdutoControllerIntegrationTest test