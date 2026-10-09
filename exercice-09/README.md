# Exercice Docker #9 : API CRUD + API Logs conteneurisées

## Objectif

Développer deux APIs Spring Boot conteneurisées :

- **API CRUD** (chiens) connectée à MySQL via Hibernate, qui envoie un log à l'API Logs à chaque appel
- **API Logs** qui stocke et restitue ces logs (stockage en base MySQL)

## Architecture

```
navigateur / client
        │
        ▼
  crud-api :8085 ──── RestTemplate (POST /api/v1/logs) ───► logs-api :8086
        │                                                      │
        └────────────── MySQL (db) :3306 ──────────────────────┘
                     bases dogsdb et logsdb
```

Les trois conteneurs communiquent par leur nom de service (`db`, `logs-api`) sur le réseau créé par Docker Compose.

## Structure

```
exercice-09/
├── compose.yaml
├── logs-api/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/ynov/logs/
│       ├── LogsApiApplication.java
│       └── log/
│           ├── LogEntry.java       (entité : message, source, timestamp, level)
│           ├── LogLevel.java       (enum INFO | WARN | ERR)
│           ├── LogRepository.java
│           └── LogController.java  (GET / POST /api/v1/logs)
└── crud-api/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/ynov/crud/
        ├── CrudApiApplication.java
        ├── dog/                    (Dog, DogRepository, DogController)
        ├── logging/
        │   ├── LogRequest.java          (corps envoyé à l'API Logs)
        │   ├── LogClient.java           (client HTTP RestTemplate)
        │   ├── ApiExceptionHandler.java (capture le message d'erreur et son horodatage)
        │   └── LoggingInterceptor.java  (envoie un log après chaque appel)
        └── config/
            ├── RestTemplateConfig.java
            └── WebConfig.java
```

## Fonctionnement des logs

Après chaque appel sur `/api/**`, l'API CRUD envoie un log à l'API Logs :

| Réponse | level | message |
|---|---|---|
| 2xx / 3xx | `INFO` | `HTTP 200`, `HTTP 201`… |
| 4xx | `WARN` | message d'erreur (ex. `Dog 42 not found`) |
| 5xx | `ERR` | message de l'exception |

- `source` : `[CrudAPI] GET /api/v1/dogs/42`
- `timestamp` : instant où l'erreur a été levée (ou instant de l'appel si pas d'erreur), format ISO-8601

Si l'API Logs est indisponible, l'API CRUD continue de répondre normalement.

## Endpoints

**API Logs** (http://localhost:8086)

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/api/v1/logs` | Récupérer tous les logs |
| POST | `/api/v1/logs` | Ajouter un log |

**API CRUD** (http://localhost:8085)

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/api/v1/dogs` | Lister les chiens |
| GET | `/api/v1/dogs/{dogId}` | Détail d'un chien |
| POST | `/api/v1/dogs` | Ajouter un chien |
| PUT | `/api/v1/dogs/{dogId}` | Modifier un chien |
| DELETE | `/api/v1/dogs/{dogId}` | Supprimer un chien |

## Étapes

### 1. Base de données avec Docker (développement local)

```bash
docker run -d --name ex09-db-dev -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.4
```

Le port hôte 3307 évite le conflit avec la base de l'exercice 8 (3306). Les bases `dogsdb` et `logsdb` sont créées automatiquement par les APIs (`createDatabaseIfNotExist=true`).

### 2. Développer les APIs en local

Dans deux terminaux :

```bash
cd logs-api
mvn spring-boot:run
```

```bash
cd crud-api
mvn spring-boot:run
```

Une fois les tests locaux faits, arrêter les APIs (`Ctrl + C`) et la base de dev :

```bash
docker rm -f ex09-db-dev
```

### 3. Dockerfiles et images

Chaque API a son `Dockerfile` en deux étapes (build Maven, puis image JRE légère), comme à l'exercice 8.

```bash
docker build -t logs-api ./logs-api
docker build -t crud-api ./crud-api
```

### 4. Tout lancer avec Docker Compose

Depuis `exercice-09` :

```bash
docker compose up -d --build
docker compose ps
```

Dans Docker, les APIs joignent la base via `db:3306` et l'API CRUD joint l'API Logs via `http://logs-api:8086` (variables d'environnement dans `compose.yaml`).

### 5. Tester

PowerShell :

```powershell
# Ajouter un chien -> log INFO
Invoke-RestMethod -Method Post -Uri http://localhost:8085/api/v1/dogs -ContentType "application/json" -Body '{"name":"Rex","birthDate":"2019-03-10","breed":"Berger allemand","sterilized":true}'

# Lister les chiens -> log INFO
Invoke-RestMethod http://localhost:8085/api/v1/dogs

# Chien inexistant -> erreur 404 + log WARN
Invoke-RestMethod http://localhost:8085/api/v1/dogs/42

# Consulter les logs
Invoke-RestMethod http://localhost:8086/api/v1/logs
```

Exemple de log reçu :

```json
{
  "message": "Dog 42 not found",
  "source": "[CrudAPI] GET /api/v1/dogs/42",
  "timestamp": "2026-10-09T08:30:12.345Z",
  "level": "WARN"
}
```

### 6. Arrêter

```bash
docker compose down
```

## Captures

![docker compose ps](screenshots/compose-ps.png)
![Logs récupérés](screenshots/logs.png)
