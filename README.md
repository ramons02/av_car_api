# av_car-api — Backend (Biblioteca)

Backend do **AV-CAR**: camada de regras de negócio e acesso a dados. Compila como **biblioteca** (`av-car-1.0.0.jar`) usada pelo frontend (`av_car_app`).

## Arquitetura

Separação clara entre visual e regras:

| Camada          | Papel                                                        |
|-----------------|--------------------------------------------------------------|
| `business/`     | 8 domínios: clientes, colaboradores, veículos, serviços, peças, fornecedores, parceiros e `ordemservico` (coração do sistema). Cada um com Controller, Service, Validation, Repository, Mapper, DTO e Model |
| `core/`         | Classes abstratas (DRY): `BaseModel`, `AbstractRepository` (JDBC/Template Method), `GenericService`, `GenericController`, `ApiResponse`, exceções e validações |
| `datastructures/`| Algoritmos próprios: fila circular (FilaEsperaOS), busca binária, quicksort e cálculo de OS |
| `helpers/`      | Formatação utilitária                                        |
| `config/`       | Beans de infraestrutura (`JdbcTemplate`, conexão singleton)  |
| `db/`           | Scripts SQL — `schema.sql` e `seed.sql`                      |

## Destaques

* **Ordem de Serviço**: fluxo de status (Aberta → Em andamento → Aguardando peça → Concluída → Entregue) com recálculo de peças, mão de obra e descontos.
* **Repositories com JDBC** (sem JPA): `JdbcTemplate` + `RowMapper`.
* **API REST** disponível via controllers (`ApiResponse` padronizada) — acessível na porta 8080.

## Como compilar / instalar

```bash
mvnw clean install
```

O artefato `br.edu.senai.fatesg:av-car:1.0.0` é instalado no repositório local e consumido pela `av_car_app`.

## Como executar a aplicação

> O backend é uma **biblioteca**: quem executa tudo (Swing + API REST) é a `av_car_app`. O `av_car_api` precisa apenas ser compilado/instalado antes.

### Pré-requisitos

* **JDK 21** obrigatório (JDK 25 quebra a compilação do Lombok). No Windows: `set JAVA_HOME=C:\Program Files\Java\jdk-21`
* **Maven Wrapper**: no Windows use `mvnw.cmd` em vez de `mvnw` nos comandos abaixo.
* **PostgreSQL** rodando com o banco `avcar` criado.
* Arquivo **`av_car_infra/.env`** com as credenciais do banco (ou variáveis de ambiente reais — sempre têm prioridade).

### Passo a passo

**1. Preparar o banco** — o sistema não cria tabelas automaticamente. Execute em ordem no banco `avcar`:

```bash
# a partir de av_car_api/
psql -U postgres -h localhost -d avcar -f db/schema.sql
psql -U postgres -h localhost -d avcar -f db/seed.sql
```

**2. Compilar e instalar o backend**

```bash
# av_car_api/
mvnw clean install
```

**3. Compilar o frontend**

```bash
# av_car_app/
mvnw clean package
```

Gera o executável `target/av-car-app-1.0.0.jar`.

**4. Executar a aplicação** — rode **a partir da pasta `av_car_app`** (o `springdotenv` localiza o `.env` por caminho relativo `../av_car_infra`):

```bash
# av_car_app/
java -jar target/av-car-app-1.0.0.jar
```

Ao iniciar: abre a **janela Swing** (tema FlatLaf) e a **API REST** fica disponível em `http://localhost:8080` (Swagger em `http://localhost:8080/swagger-ui/index.html`).

## Banco de Dados

O sistema **não** cria tabelas automaticamente. Configure o banco `avcar` no PostgreSQL e execute na ordem:

1. `db/schema.sql` — criação das tabelas e relacionamentos
2. `db/seed.sql` — massa de dados inicial

As credenciais são definidas no `av_car_infra/.env`.

## Ambiente

* **JDK:** 21 (obrigatório — o JDK 25 quebra a compilação do Lombok)