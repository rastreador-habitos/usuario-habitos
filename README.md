# usuario

Microsserviço de gestao de usuarios do sistema Rastreador de Habitos. Responsavel pelo cadastro, autenticacao e operacoes sobre contas, emitindo tokens JWT consumidos pelos demais servicos.

## Stack

- Java 21
- Spring Boot
- Spring Security + JWT
- PostgreSQL
- JPA / Hibernate
- Maven

## API

| Metodo | Rota | Auth | Descricao |
|--------|------|------|-----------|
| POST | /usuario | Nao | Criar conta |
| POST | /usuario/login | Nao | Autenticar e receber JWT |
| GET | /usuario | Sim | Buscar dados do usuario por e-mail |
| PUT | /usuario | Sim | Atualizar conta |
| DELETE | /usuario/{email} | Sim | Deletar conta |

Rotas autenticadas exigem o header `Authorization` com o token JWT retornado pelo `/usuario/login`.

## Regras de negocio

- Senhas sao armazenadas com hash via Spring Security.
- A propriedade do token e validada antes de qualquer alteracao ou exclusao de conta.

## Executando localmente

Requer PostgreSQL em `localhost:5432` com o banco `db_usuario_habitos` criado.

```bash
mvn spring-boot:run
```

Servico sobe na porta **8080**.

## Executando com Docker

Este servico faz parte de um ambiente multi-container. Consulte o repositorio principal da organizacao [rastreador-habitos](https://github.com/rastreador-habitos) para o `docker-compose.yml` completo.
