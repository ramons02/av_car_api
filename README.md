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

## Banco de Dados

O sistema **não** cria tabelas automaticamente. Configure o banco `avcar` no PostgreSQL e execute na ordem:

1. `db/schema.sql` — criação das tabelas e relacionamentos
2. `db/seed.sql` — massa de dados inicial

As credenciais são definidas no `av_car_infra/.env`.

## Ambiente

* **JDK:** 21 (obrigatório — o JDK 25 quebra a compilação do Lombok)