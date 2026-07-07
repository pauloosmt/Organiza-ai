# Organiza-ai

Aplicação para ajudar estudantes universitários a:

- **Controlar faltas** por disciplina, acompanhando o limite permitido e recebendo alertas antes de ultrapassá-lo.
- **Organizar a grade de horário**, visualizando as disciplinas cursadas, seus horários e identificando conflitos.

Múltiplos usuários podem se cadastrar e gerenciar seus próprios dados de forma isolada.

## Stack

- **Backend:** Java 21 + Spring Boot (Data JPA, Security, Validation, Web MVC)
- **Frontend:** Angular
- **Build:** Gradle

## Estrutura do projeto

- `src/main/java/com/organizaai` — código do backend
- `specs/` — especificações das features (veja `CLAUDE.md` para o processo de desenvolvimento)

## Rodando o backend

```bash
./gradlew bootRun
```
