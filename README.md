# spring-boot-graylog

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-graylog/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-graylog/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?logo=springboot)
![Graylog](https://img.shields.io/badge/Graylog-6.1-blue?logo=graylog)

A Spring Boot application that ships its logs to [Graylog](https://graylog.org/) over GELF/UDP. It uses
[logback-gelf](https://github.com/osiegmar/logback-gelf). Every HTTP request and response is traced as a JSON document,
so you can search for it in Graylog.

## Tech stack

| Component      | Version                                      |
|----------------|----------------------------------------------|
| Java           | 25 (Temurin in CI)                           |
| Spring Boot    | 4.1.x (Spring Framework 7, Jackson 3)        |
| Maven          | 3.10.x via the bundled Maven Wrapper         |
| logback-gelf   | 6.1.x                                        |
| Graylog stack  | Graylog 6.1, OpenSearch 2.15, MongoDB 7.0    |

## How it works

```
HTTP request ──► RestControllerInterceptor ──► SLF4J / Logback ──► GELF UDP :12201 ──► Graylog
                 (request + response trace)        │
                                                    ├──► console
                                                    └──► logs/*.log (rolling files)
```

- `RestControllerInterceptor` is a `HandlerInterceptor` registered for `/**` in `WebConfig`. It logs one
  `server-incoming-request` record (URI, method, remote address, headers) and one `server-outgoing-response` record
  (status, headers, execution time). Matching pairs share the same `request-id`.
- `logback-spring.xml` sends the `id.my.hendisantika.graylog` loggers to the `GELF` appender and to rolling log files.
  The interceptor logs at `DEBUG` to `logs/incoming-requests-debug.*.log`. Every GELF message carries the static fields
  `app_name`, `os_arch`, `os_name` and `os_version`.
- `json.log.pretty` in `application.properties` turns pretty-printing of the traced JSON on or off.

## Prerequisites

- JDK 25
- Docker with the Compose plugin (to run Graylog locally)

## Getting started

### 1. Run the application

```bash
./mvnw spring-boot:run
```

`spring-boot-docker-compose` reads `compose.yaml` and starts MongoDB, OpenSearch and Graylog before the application
starts. It stops them again on shutdown. Graylog can take about a minute to come up the first time.

To manage the stack yourself, run `docker compose up -d` first. Spring Boot reuses the containers that are already
running.

### 2. Create a GELF UDP input in Graylog (one-time)

Open <http://localhost:9000> and log in as `admin` / `admin`. Then go to **System → Inputs**, choose **GELF UDP**, and
launch a global input on port `12201`.

You can also create it from the command line:

```bash
curl -u admin:admin -H 'X-Requested-By: cli' -H 'Content-Type: application/json' \
  -X POST http://localhost:9000/api/system/inputs \
  -d '{"title":"GELF UDP","type":"org.graylog2.inputs.gelf.udp.GELFUDPInput","global":true,
       "configuration":{"bind_address":"0.0.0.0","port":12201,"recv_buffer_size":262144}}'
```

The input is stored in the `graylog-data` / `mongodb-data` volumes, so it survives restarts.

### 3. Generate some logs

```bash
curl http://localhost:8080/app/status
# Service controller is up
```

In Graylog, go to **Search** and query `server-incoming-request` or `app_name:backend` to see the traces.

## Endpoints

| Method | Path          | Description                     |
|--------|---------------|---------------------------------|
| GET    | `/app/status` | Health-style status message     |

## Configuration

| Where                      | Setting                                 | Default        |
|----------------------------|-----------------------------------------|----------------|
| `application.properties`   | `server.port`                           | `8080`         |
| `application.properties`   | `json.log.pretty`                       | `true`         |
| `logback-spring.xml`       | `graylogHost` / `graylogPort`           | `127.0.0.1:12201` |
| `logback-spring.xml`       | `staticField app_name`                  | `backend`      |
| `compose.yaml`             | `GRAYLOG_ROOT_PASSWORD_SHA2` (`admin`)  | SHA-256 of `admin` |

> [!WARNING]
> The credentials and `GRAYLOG_PASSWORD_SECRET` in `compose.yaml` are for local development only. Change them before
> you use this stack anywhere else.

## Build and test

```bash
./mvnw verify
```

The integration test (`@SpringBootTest`) skips Docker Compose support, so it doesn't need Docker. GELF over UDP is
fire-and-forget, so the tests pass even when Graylog isn't running.

## CI/CD and dependency updates

- **GitHub Actions** (`.github/workflows/maven.yml`) builds and tests on Temurin JDK 25 for every push and pull request
  to `main`. Superseded runs are cancelled. Pushes to `main` also submit the Maven dependency graph to GitHub.
- **Dependabot** (`.github/dependabot.yml`) keeps these up to date:
  - **Maven** (daily): Spring Boot, Maven plugins and other minor/patch updates each go into one grouped PR. A cooldown
    waits 3 days for new releases (7 days for majors).
  - **GitHub Actions** (daily): all updates go into one grouped PR.
  - **Docker Compose images** (weekly): Graylog, OpenSearch and MongoDB. Major versions are left for manual upgrades,
    because Graylog only supports specific OpenSearch and MongoDB versions.
  - Commit messages follow Conventional Commits (`build(deps)`, `ci(deps)`, `chore(deps)`).

## Author

**Hendi Santika**

- Email: hendisantika@gmail.com
- Telegram: [@hendisantika34](https://t.me/hendisantika34)
