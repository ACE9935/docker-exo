# Exercice Docker #3 : site web personnalisé avec NGINX

## Objectif

Déployer un site web avec une page d'accueil personnalisée, servi par NGINX et accessible depuis l'hôte.

## Fichiers

- `index.html` : page d'accueil personnalisée ("Hello World")
- `Dockerfile` : part de l'image `nginx` et remplace la page par défaut

```dockerfile
FROM nginx:latest

COPY index.html /usr/share/nginx/html/index.html

EXPOSE 80
```

## Étapes

Depuis le dossier `exercice-03` :

### 1. Construire l'image

```bash
docker build -t site-exercice03 .
```

### 2. Lancer le conteneur

```bash
docker run -d --name site-exercice03 -p 8081:80 site-exercice03
```

Le port 8081 est utilisé pour ne pas entrer en conflit avec le jeu 2048 de l'exercice 2 (port 8080).

### 3. Accéder au site

Ouvrir http://localhost:8081 dans le navigateur.

## Vérification

![Page Hello World](screenshots/site-exercice03.png)
