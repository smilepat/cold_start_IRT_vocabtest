<!--
Short, focused instructions to help AI-powered coding assistants work productively
with this repository. Keep this file concise and concrete — reference files and
examples rather than generic advice.
-->

# Copilot / AI Assistant Instructions

Purpose: Provide the essential, repository-specific knowledge an AI coding
assistant needs to be immediately productive when editing, building, or
debugging this project.

- **Big picture (what this app is):** Spring Boot Java backend (packaged as a
  WAR named `Marvlus`) with an integrated React frontend under
  `src/main/vocabulary-react-app`. Primary features: vocabulary/word exams and
  an adaptive reading test. The backend exposes REST APIs (see
  `controller/api/*`) and uses JPA repositories and some direct JDBC utilities.

- **Project structure highlights**
  - `src/main/java/com/marvrus/vocabularytest/` — main code. Key packages:
    - `controller` — REST controllers; most API controllers are under
      `controller/api` and use Swagger annotations.
    - `service` / `service/impl` — service interfaces + implementations.
    - `repository` — Spring Data repositories for JPA access.
    - `model/entity` — JPA entities (Word, WordExam, etc.).
  - `src/main/vocabulary-react-app/` — React frontend (built by Maven's
    frontend-maven-plugin during package). Node is installed automatically by
    Maven (see `pom.xml` plugin config).
  - `src/main/resources-local/application.properties` — recommended local
    configuration (H2 enabled by default). Profile-specific resources are
    loaded from `src/main/resources-${environment}` driven by the Maven
    profile/property `environment`.

- **How to build & run (concrete commands)**
  - Preferred (if Maven installed):
    - `mvn clean package -P local`
    - `mvn spring-boot:run -P local` or `mvnw.cmd spring-boot:run -P local`
  - If `mvnw` is broken, fall back to installing Maven (see `BUILD_INSTRUCTIONS.md`).
  - To run the war produced locally: `java -jar target/Marvlus.war` (when
    packaged as an executable war).

- **Local DB options**
  - H2 (default local config): enabled in
    `src/main/resources-local/application.properties` and `spring.h2.console.path=/h2-console`.
    Use H2 when you want zero setup.
  - MySQL/XAMPP: follow `QUICK_START.md` and `XAMPP_SETUP_GUIDE.md`. To use
    MySQL, uncomment MySQL lines in `application.properties` and set
    `vocabulary.jpa.password`. The repo contains `schema.sql` and
    `sample_data.sql` for bootstrapping.

- **Frontend build details**
  - `pom.xml` uses `frontend-maven-plugin` to install Node/npm and run
    `npm install` and `npm run build`. Node version is pinned in `pom.xml`.
  - If frontend issues occur, manually inspect
    `src/main/vocabulary-react-app/package.json` and try local `npm install`
    and `npm run build` in that directory.

- **Conventions & patterns to follow in code changes**
  - Controllers return domain objects or an `ApiResponse<T>` wrapper (see
    `config.ApiResponse`). Prefer existing response patterns when adding
    endpoints.
  - Service layer uses interfaces in `service/` and implementations in
    `service/impl/`. Add new public methods to the interface and implement in
    the `impl` package.
  - Use `config.exception.ApiException` for HTTP error responses — throw it
    with an appropriate `HttpStatus` rather than returning nulls.
  - Boolean-like flags often use the `model.enums.YesNo` enum — follow that
    convention instead of raw booleans when interacting with entities.

- **Key files to reference when making changes**
  - `pom.xml` — build lifecycle, frontend plugin, profiles (`local`, `test`, `release`).
  - `BUILD_INSTRUCTIONS.md` and `QUICK_START.md` — practical dev/run guidance.
  - `src/main/resources-local/application.properties` — local config and H2/MySQL examples.
  - `src/main/java/com/marvrus/vocabularytest/controller/api/` — canonical API examples (e.g. `WordExamApiController`, `AdaptiveReadingTestApiController`).

- **API examples (useful snippets for tests / quick checks)**
  - Start a word exam: `POST /api/word-exams` → returns `WordExam` with `wordExamSeqno`.
  - Get exam word: `GET /api/word-exams/{wordExamSeqno}/orders/{examOrder}`
  - Adaptive test start: `POST /api/adaptive-test/start?userId=<id>`

- **Common troubleshooting notes**
  - If `mvnw.cmd` fails, install Maven and use `mvn` directly (see
    `BUILD_INSTRUCTIONS.md`).
  - When port conflicts occur, change `server.port` in
    `application.properties` or kill the process using port 8080.
  - If frontend build fails inside Maven, try building frontend manually and
    re-run Maven package.

- **Testing & profiles**
  - Maven profiles are used to switch environments (`-P local` recommended
    for development). Profiles set `environment` property that selects resource
    folder `src/main/resources-${environment}`.
  - Most Maven profiles set `<maven.test.skip>true</maven.test.skip>` — tests
    are not the primary CI gate in this repo.

If anything in this summary is unclear or you'd like more examples (unit
tests, common refactors, or API contract samples), tell me which area to
expand and I will update this file.
