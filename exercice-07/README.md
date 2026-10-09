# Exercice Docker #7 : base MySQL kennelDB pré-remplie

## Objectif

Construire une image MySQL personnalisée qui crée et remplit la base `kennelDB` au premier lancement, grâce à un script SQL.

## Fichiers

- `init.sql` : création des tables et insertion des données
- `Dockerfile` : part de `mysql:8.4` et copie le script dans `/docker-entrypoint-initdb.d/`

```dockerfile
FROM mysql:8.4

ENV MYSQL_DATABASE=kennelDB

COPY init.sql /docker-entrypoint-initdb.d/init.sql
```

Au premier démarrage (volume de données vide), l'image officielle MySQL exécute automatiquement les scripts `.sql` présents dans `/docker-entrypoint-initdb.d/`.

## Contenu de la base kennelDB

| Table | Colonnes |
|---|---|
| `clients` | nom, prénom, date de naissance, pseudonyme |
| `adresses` | numéro, rue, code postal, commune |
| `clients_adresses` | association client ↔ adresse (plusieurs-à-plusieurs) |
| `chiens` | nom, date de naissance, race, stérilisé |
| `chats` | nom, date de naissance, race, stérilisé |

## Étapes

Depuis le dossier `exercice-07` :

### 1. Construire l'image

```bash
docker build -t kennel-db .
```

### 2. Lancer le conteneur

```bash
docker run -d --name kennel-db -e MYSQL_ROOT_PASSWORD=root kennel-db
```

Attendre une vingtaine de secondes que MySQL s'initialise.

### 3. Vérifier les tables et les données

```bash
docker exec kennel-db mysql -uroot -proot kennelDB -e "SHOW TABLES; SELECT * FROM clients; SELECT * FROM chiens; SELECT * FROM chats;"
```

## Vérification

![Tables et données kennelDB](screenshots/kennel-db.png)
