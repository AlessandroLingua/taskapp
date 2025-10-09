TaskApp — README

Applicazione di esempio Task Manager con Java 21, Spring Boot 3.5.x, JPA/Hibernate, Micrometer/Actuator, H2 e frontend AngularJS 1.3 servito dallo stesso backend.
Obiettivo: mostrare una struttura “da progetto reale” con layer puliti, DTO, validazione, paginazione/filtri, metriche e test.

Stack & Requisiti

Java 21, Maven 3.9+

Spring Boot 3.5.x (web, validation, data-jpa, actuator)

DB: H2 (file in dev/prod, in-memory nei test)

Frontend: AngularJS 1.3 statico, servito da Spring (cartella static/)

Lombok: plugin IntelliJ + Enable Annotation Processing

Micrometer/Actuator (+ Prometheus registry opzionale)

Architettura (layer)
Controller (web)  ->  Service  ->  Repository (Spring Data JPA)  ->  DB (H2)
^              |                 ^
|              |                 |
DTO           Mapper            Entity


DTO (TaskRequest, TaskResponse) per disaccoppiare API dalle entità JPA.

Validazione Bean Validation sui DTO.

Error handling uniforme via @ControllerAdvice → JSON consistente 400/404/500.

Paginazione/ricerca con Pageable + repository methods.

Metriche custom (counter/timer) con Micrometer.

Correlation-Id in log via filtro X-Correlation-Id.

Struttura del progetto
src/
├─ main/java/com/example/taskapp/
│   ├─ TaskAppApplication.java
│   ├─ boot/DataSeed.java             (solo profilo dev)
│   ├─ config/CorrelationIdFilter.java
│   ├─ domain/                        (Entity JPA)
│   │   ├─ Task.java
│   │   └─ Category.java
│   ├─ repo/                          (Spring Data)
│   │   ├─ TaskRepository.java
│   │   └─ CategoryRepository.java
│   ├─ service/
│   │   ├─ TaskService.java
│   │   ├─ TaskServiceImpl.java
│   │   └─ NotFoundException.java
│   └─ web/
│       ├─ TaskController.java
│       ├─ CategoryController.java
│       ├─ error/GlobalExceptionHandler.java
│       └─ dto/
│           ├─ TaskRequest.java
│           ├─ TaskResponse.java
│           ├─ PagedResponse.java
│           └─ TaskMapper.java
├─ main/resources/
│   ├─ application.properties
│   ├─ application-dev.properties
│   ├─ application-prod.properties
│   ├─ logback-spring.xml
│   └─ static/                       (frontend)
│       ├─ index.html
│       └─ js/app.js
└─ test/
├─ java/com/example/taskapp/
│   ├─ web/TaskControllerTest.java
│   └─ web/TaskControllerPagingTest.java
│   └─ repo/CategoryRepositoryTest.java
└─ resources/application-test.properties

Modello dati

Category(1) —— (N) Task
Campi principali:

Category: id, name (unique, not null), description

Task: id, title (not null), description, category_id (nullable)

Indice: idx_task_category_id su task(category_id)

Eliminando una Category, la FK dei task viene impostata a NULL (comportamento gestito a livello service).

Configurazioni & Profili
application.properties
spring.application.name=taskapp
spring.profiles.default=dev
management.endpoint.health.show-details=always

application-dev.properties (sviluppo)
spring.datasource.url=jdbc:h2:file:./data/taskdb-dev;AUTO_SERVER=TRUE
spring.datasource.username=admin
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
management.endpoints.web.exposure.include=health,info,metrics,prometheus
logging.level.com.example.taskapp=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
spring.threads.virtual.enabled=true

application-prod.properties (demo produzione)
spring.datasource.url=jdbc:h2:file:./data/taskdb-prod;AUTO_SERVER=TRUE
spring.datasource.username=admin
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.h2.console.enabled=false
management.endpoints.web.exposure.include=health,info
logging.level.com.example.taskapp=INFO


In produzione reale sostituisci H2 con Postgres/MySQL e imposta ddl-auto=validate + migrazioni (Flyway).

Avvio
Dev (default)
mvn spring-boot:run


Frontend: http://localhost:8080/

API: http://localhost:8080/api/tasks
…

H2 Console: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./data/taskdb-dev;AUTO_SERVER=TRUE

User/Pass: admin / password

Prod (demo)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

Jar eseguibile
mvn -q -DskipTests package
java -jar target/taskapp-0.0.1-SNAPSHOT.jar

Frontend (AngularJS) — Uso rapido

Pagina unica su /, con:

Form create/edit Task (titolo, descrizione, categoria)

Creazione veloce Category

Filtro per categoria e search titolo

Tabella Task con Modifica / Elimina

Paginazione (Prev/Next) e sort di default title,asc

API
Task

GET /api/tasks → lista completa (non paginata)

GET /api/tasks/paged?page=0&size=5&sort=title,asc&q=<search>&categoryId=<id>
Risposta:

{
"content":[{ "id":1, "title":"...", "categoryId":2, "categoryName":"..." }],
"page":0,"size":5,"totalElements":42,"totalPages":9,"first":true,"last":false
}


GET /api/tasks/{id}

POST /api/tasks

{ "title":"T1", "description":"...", "categoryId": 2 }


PUT /api/tasks/{id} (stessa payload di POST)

DELETE /api/tasks/{id}

Category

GET /api/categories

POST /api/categories

{ "name":"Studio", "description":"Formazione" }

Esempi curl
curl -X POST http://localhost:8080/api/categories \
-H "Content-Type: application/json" \
-d '{"name":"Lavoro","description":"Progetto"}'

curl -X POST http://localhost:8080/api/tasks \
-H "Content-Type: application/json" \
-d '{"title":"Imparare Spring","description":"API + JPA","categoryId":1}'

curl "http://localhost:8080/api/tasks/paged?page=0&size=5&sort=title,desc&q=spring"

Error handling (JSON)

Esempi:

400 (validazione):

{
"status":400,"error":"Bad Request",
"message":"Validazione fallita",
"path":"/api/tasks",
"timestamp":"2025-10-08T10:00:00Z",
"details":{"title":"title è obbligatorio"}
}


404:

{ "status":404, "error":"Not Found", "message":"Task 999 non trovato", ... }

Metriche & Actuator

Health: /actuator/health

Metrics index: /actuator/metrics

Prometheus (se registry incluso): /actuator/prometheus

Custom:

Counter: tasks.created.count, tasks.deleted.count

Timer: tasks.create.timer, tasks.update.timer, tasks.delete.timer, tasks.findAllPaged.timer

Esempio:

GET /actuator/metrics/tasks.created.count

Logging & Correlation Id

Filtro CorrelationIdFilter:

legge header X-Correlation-Id o ne genera uno (UUID)

lo scrive nell’MDC con chiave cid e lo rimanda in risposta nello stesso header

Pattern Logback: [%X{cid}]
Esempio riga log:

12:34:56.789  INFO [http-nio-8080-exec-1] [a1b2c3...] com.example.taskapp.web.TaskController - GET /api/tasks

Testing

Profilo test: H2 in-memory, ddl-auto=create-drop

Test API (MockMvc): TaskControllerTest, TaskControllerPagingTest

Test JPA: CategoryRepositoryTest

Esecuzione:

mvn -q -Dspring.profiles.active=test test

SQL schema + seed (opzionale, idempotente)

Puoi eseguirlo su H2 Console se preferisci inizializzare a mano:

create table if not exists category (
id bigint generated by default as identity primary key,
name varchar(255) not null,
description varchar(255),
constraint uk_category_name unique (name),
constraint chk_category_name_not_blank check (length(trim(name)) > 0)
);

create table if not exists task (
id bigint generated by default as identity primary key,
title varchar(255) not null,
description varchar(255),
category_id bigint,
constraint chk_task_title_not_blank check (length(trim(title)) > 0),
constraint fk_task_category foreign key (category_id) references category(id) on delete set null
);

create index if not exists idx_task_category_id on task(category_id);

merge into category (name, description) key(name) values
('Studio', 'Formazione e studio'),
('Lavoro', 'Attività di progetto');

Troubleshooting

Lombok non genera builder/getter

IntelliJ → Plugins → installa Lombok

IntelliJ → Build → Compiler → Annotation Processors → Enable annotation processing

mvn clean compile (se compila da Maven ma IDEA segna rosso: Invalidate Caches / Restart)

H2 Console non vede tabelle

Usa la stessa JDBC URL dell’app (dev: jdbc:h2:file:./data/taskdb-dev;AUTO_SERVER=TRUE)

Verifica user/password e riavvia l’app

Porta diversa

Controlla log “Tomcat started on port(s): …” o imposta server.port=8081

Roadmap (idee next)

OpenAPI/Swagger UI

Spring Security (Basic/JWT), ruoli e protezione Actuator

Migrazioni Flyway + DB esterno (Postgres)

Test di integrazione con Testcontainers

UI: routing con ngRoute, pagina dettaglio, toaster errori