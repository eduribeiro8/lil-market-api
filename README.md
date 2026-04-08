# LilMarket API

O **LilMarket API** é uma aplicação backend desenvolvida em Spring Boot para o gerenciamento de um sistema de mercado. A API fornece recursos para controle de produtos, estoque, vendas, entre outras operações, contando com autenticação, segurança e controle de fluxo de requisições.

## Funcionalidades e Estrutura

*   Gerenciamento de Produtos, Categorias e Lotes (Batches).
*   Registro e processamento de Vendas.
*   Sistema de permissões baseada em *Roles* (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`).
*   Proteção contra sobrecarga de requisições com Rate Limiting baseado no IP do usuário.
*   Tratamento global e centralizado de exceções (`@RestControllerAdvice`).
*   Auditoria automática de entidades (`createdAt`, `updatedAt`).

## Tecnologias e Ferramentas

O projeto foi construído utilizando as seguintes tecnologias:

*   **Java 17**
*   **Spring Boot** (Web, Data JPA, Security, Validation, Test)
*   **MySQL** - Banco de dados relacional
*   **Spring Security & JWT (JJWT)** - Autenticação e autorização de acesso
*   **Bucket4j** - *Rate Limiting* para prevenção de abusos de requisições
*   **MapStruct** - Mapeamento eficiente e automático entre DTOs e Entidades
*   **Lombok** - Redução de código *boilerplate*
*   **SpringDoc OpenAPI (Swagger)** - Documentação interativa da API
*   **Maven** - Gerenciador de dependências e build (via Maven Wrapper)

## Pré-requisitos

Para rodar o projeto localmente, você precisará ter instalado:

*   [JDK 17](https://jdk.java.net/17/)
*   [MySQL Server](https://www.mysql.com/)

Certifique-se de configurar o banco de dados e atualizar as credenciais (URL, usuário e senha) no arquivo `src/main/resources/application.properties` ou através de variáveis de ambiente.

## Como Executar

O projeto utiliza o Maven Wrapper (`mvnw`), portanto, não é obrigatório ter o Maven instalado globalmente no sistema.

1.  **Clone o repositório e acesse a pasta do projeto:**
    ```bash
    git clone <url-do-repositorio>
    cd lil-market-api
    ```

2.  **Construa o projeto (isso também gerará as implementações do MapStruct):**
    ```bash
    ./mvnw clean install
    ```

3.  **Inicie a aplicação:**
    ```bash
    ./mvnw spring-boot:run
    ```

Por padrão, a API estará disponível no endereço `http://localhost:8080`.

## Testes

O projeto possui testes unitários utilizando JUnit 5 e Mockito. Para executá-los, utilize:

```bash
# Rodar todos os testes
./mvnw test

# Rodar os testes de uma classe específica
./mvnw test -Dtest=NomeDaClasseTest

# Rodar um método de teste específico
./mvnw test -Dtest=NomeDaClasseTest#nomeDoMetodo
```

## 📖 Documentação da API (Swagger)

A API possui documentação detalhada gerada automaticamente pelo **SpringDoc OpenAPI**. Com a aplicação em execução, você pode acessá-la através do navegador:

*   **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Na interface do Swagger, você encontrará os detalhes de todos os endpoints disponíveis, seus parâmetros, *schemas* (DTOs) e exemplos de respostas. A documentação exige autenticação com o token JWT em endpoints protegidos.

## 🏗️ Padrões do Projeto

*   **DTOs (Data Transfer Objects):** Implementados utilizando `records` do Java.
*   **Mappers:** Uso exclusivo de interfaces MapStruct no pacote `mapper` para conversão de DTO <-> Entity.
*   **Serviços:** Divididos em Interface e Implementação (`*ServiceImpl`), com lógica de negócios e injeção via construtor (`@RequiredArgsConstructor`).
*   **Tratamento de Valores Financeiros:** Utilização rigorosa de `BigDecimal` com escala definida e arredondamento `HALF_UP`.
