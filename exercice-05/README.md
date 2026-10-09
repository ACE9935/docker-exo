# Exercice Docker #5 : MySQL et Adminer

## Objectif

Déployer MySQL et Adminer dans deux conteneurs distincts qui communiquent par leur nom, avec des données persistantes et un redémarrage automatique. Seul Adminer est exposé sur l'hôte.

## Étapes

### 1. Créer le réseau et le volume

```bash
docker network create reseau-ex05
docker volume create mysql-data
```

### 2. Lancer MySQL

```bash
docker run -d --name mysql-ex05 --network reseau-ex05 --restart unless-stopped -v mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=ecole -e MYSQL_USER=etudiant -e MYSQL_PASSWORD=etudiant mysql:8.4
```

- Pas d'option `-p` : MySQL n'est pas accessible depuis l'hôte.
- `-v mysql-data:/var/lib/mysql` : les données sont stockées dans un volume, indépendant du conteneur.
- `--restart unless-stopped` : redémarrage automatique en cas d'arrêt inattendu ou de redémarrage de Docker.

### 3. Lancer Adminer

```bash
docker run -d --name adminer-ex05 --network reseau-ex05 --restart unless-stopped -p 8082:8080 adminer
```

### 4. Se connecter depuis Adminer

Ouvrir http://localhost:8082 et remplir :

| Champ | Valeur |
|---|---|
| Système | MySQL |
| Serveur | `mysql-ex05` |
| Utilisateur | `etudiant` |
| Mot de passe | `etudiant` |
| Base de données | `ecole` |

Le serveur est désigné par le **nom du conteneur**, pas par une IP.

### 5. Créer une table et insérer des données

Dans Adminer, menu **Requête SQL** :

```sql
CREATE TABLE etudiants (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50) NOT NULL,
  prenom VARCHAR(50) NOT NULL
);

INSERT INTO etudiants (nom, prenom) VALUES
  ('Dupont', 'Alice'),
  ('Martin', 'Lucas'),
  ('Bernard', 'Emma');
```

## Vérifications

### Consulter les enregistrements

Dans Adminer : table `etudiants` → **Afficher les données**.

### Persistance : supprimer puis recréer MySQL

```bash
docker rm -f mysql-ex05
docker run -d --name mysql-ex05 --network reseau-ex05 --restart unless-stopped -v mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=ecole -e MYSQL_USER=etudiant -e MYSQL_PASSWORD=etudiant mysql:8.4
```

Se reconnecter dans Adminer : la table `etudiants` et ses 3 lignes sont toujours là.

### Redémarrage automatique : simuler une panne

```bash
docker exec mysql-ex05 kill 1
docker ps --filter name=mysql-ex05
```

Le processus principal s'arrête, puis Docker relance le conteneur tout seul (colonne `STATUS` : `Up X seconds`).

## Bonus : compose.yaml

Le fichier `compose.yaml` de ce dossier reproduit tout le déploiement. Depuis `exercice-05` :

```bash
docker compose up -d
```

## Captures

![Connexion Adminer](screenshots/adminer-connexion.png)
![Données après recréation](screenshots/donnees-persistantes.png)
![Redémarrage automatique](screenshots/redemarrage.png)
