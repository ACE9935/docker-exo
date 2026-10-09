# Exercice Docker #6 : conteneur NGINX de développement

## Objectif

Développer un site web dans un dossier de l'ordinateur, servi en direct par NGINX dans un conteneur, sans rien installer localement.

## Principe

Le dossier `site/` de l'ordinateur est monté dans le conteneur à la place du dossier web de NGINX (`/usr/share/nginx/html`) grâce à un volume de type **bind mount** (`-v`). Toute modification d'un fichier dans `site/` est immédiatement visible dans le navigateur.

## Étapes

Depuis le dossier `exercice-06` :

### 1. Lancer le conteneur avec le volume

PowerShell :

```powershell
docker run -d --name nginx-dev -p 8083:80 -v "${PWD}/site:/usr/share/nginx/html" nginx
```

Bash (Linux / macOS / Git Bash) :

```bash
docker run -d --name nginx-dev -p 8083:80 -v "$(pwd)/site:/usr/share/nginx/html" nginx
```

### 2. Accéder au site

Ouvrir http://localhost:8083.

### 3. Modifier le site en direct

Modifier `site/index.html` (par exemple le titre `<h1>`), enregistrer, puis rafraîchir la page : la modification apparaît sans reconstruire d'image ni relancer le conteneur.

## Vérification

```bash
docker inspect nginx-dev --format "{{json .Mounts}}"
```

![Avant modification](screenshots/avant.png)
![Après modification](screenshots/apres.png)
