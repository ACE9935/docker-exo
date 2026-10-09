# Exercice Docker #2 : jeu 2048

## Objectif

Déployer une image du jeu 2048 trouvée sur Docker Hub et y accéder depuis le navigateur de l'hôte.

## Étapes

### 1. Trouver une image 2048 sur Docker Hub

```bash
docker search 2048
```

Image retenue : `alexwhen/docker-2048` (sert le jeu via NGINX sur le port 80).

### 2. Récupérer l'image

```bash
docker pull alexwhen/docker-2048
```

### 3. Lancer le conteneur sur le port hôte 8080

```bash
docker run -d --name jeu-2048 -p 8080:80 alexwhen/docker-2048
```

Si le port 8080 est déjà pris :

```bash
docker run -d --name jeu-2048 -p 8090:80 alexwhen/docker-2048
```

### 4. Accéder au jeu

Ouvrir http://localhost:8080 dans le navigateur de l'ordinateur.

## Vérification

```bash
docker ps --filter name=jeu-2048
```

![Jeu 2048 dans le navigateur](screenshots/jeu-2048.png)
