# Exercice Docker #8 : API Spring Boot conteneurisée

## Objectif

Développer une API CRUD en Java / Spring Boot (entité : chiens), connectée à MySQL via Hibernate, puis conteneuriser l'API et la base.

## Structure

```
exercice-08/
└── dogs-api/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/
        ├── java/com/ynov/dogs/
        │   ├── DogsApiApplication.java
        │   └── dog/
        │       ├── Dog.java              (entité JPA)
        │       ├── DogRepository.java    (Spring Data JPA)
        │       └── DogController.java    (endpoints REST)
        └── resources/application.properties
```

Driver JPA / Hibernate : `com.mysql:mysql-connector-j`.

## Endpoints

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/api/v1/dogs` | Lister les chiens |
| GET | `/api/v1/dogs/{dogId}` | Détail d'un chien |
| POST | `/api/v1/dogs` | Ajouter un chien |
| PUT | `/api/v1/dogs/{dogId}` | Modifier un chien |
| DELETE | `/api/v1/dogs/{dogId}` | Supprimer un chien |

## Étapes

### 1. Créer la base de données avec Docker

```bash
docker network create reseau-ex08
docker run -d --name dogs-db --network reseau-ex08 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=dogsdb mysql:8.4
```

Le port 3306 est exposé pour que l'API lancée en local puisse s'y connecter.

### 2. Développer et tester l'API en local

Depuis `exercice-08/dogs-api` :

```bash
mvn spring-boot:run
```

L'API se connecte à `localhost:3306` (voir `application.properties`) et écoute sur http://localhost:8080. Hibernate crée la table `dogs` automatiquement (`ddl-auto=update`). Arrêter l'API avec `Ctrl + C` avant de passer à la suite.

### 3. Dockerfile de l'API

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/dogs-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build en deux étapes : la première compile avec Maven, la seconde ne garde que le JRE et le `.jar`. Pas besoin de Maven ni de Java en local pour construire l'image.

### 4. Créer l'image de l'API

```bash
docker build -t dogs-api .
```

### 5. Lancer l'API dans Docker

```bash
docker run -d --name dogs-api --network reseau-ex08 -p 8084:8080 -e SPRING_DATASOURCE_URL="jdbc:mysql://dogs-db:3306/dogsdb" dogs-api
```

Dans Docker, l'API ne joint plus la base via `localhost` mais via le nom du conteneur `dogs-db`. La variable `SPRING_DATASOURCE_URL` remplace l'URL définie dans `application.properties`.

### 6. Tester l'API

PowerShell :

```powershell
# Ajouter un chien
Invoke-RestMethod -Method Post -Uri http://localhost:8084/api/v1/dogs -ContentType "application/json" -Body '{"name":"Rex","birthDate":"2019-03-10","breed":"Berger allemand","sterilized":true}'

# Lister les chiens
Invoke-RestMethod http://localhost:8084/api/v1/dogs

# Détail du chien 1
Invoke-RestMethod http://localhost:8084/api/v1/dogs/1

# Modifier le chien 1
Invoke-RestMethod -Method Put -Uri http://localhost:8084/api/v1/dogs/1 -ContentType "application/json" -Body '{"name":"Rex","birthDate":"2019-03-10","breed":"Berger allemand","sterilized":false}'

# Supprimer le chien 1
Invoke-RestMethod -Method Delete -Uri http://localhost:8084/api/v1/dogs/1
```

Vérifier côté base :

```bash
docker exec dogs-db mysql -uroot -proot dogsdb -e "SELECT * FROM dogs;"
```

## Captures

![Image dogs-api](screenshots/docker-images.png)
![Tests de l'API](screenshots/tests-api.png)
