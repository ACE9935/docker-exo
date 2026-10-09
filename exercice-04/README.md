# Exercice Docker #4 : deux conteneurs qui communiquent

## Objectif

Faire communiquer deux conteneurs sur un même réseau Docker, et vérifier la résolution DNS interne par le nom du conteneur.

## Étapes

### 1. Créer le réseau

```bash
docker network create reseau-ping
```

### 2. Créer un premier conteneur et y installer ping

```bash
docker run -dit --name conteneur1 --network reseau-ping ubuntu
docker exec conteneur1 bash -c "apt update && apt install -y iputils-ping"
```

### 3. Sauvegarder l'image avec ping installé

```bash
docker commit conteneur1 ubuntu-ping
```

### 4. Créer le second conteneur à partir de cette image

```bash
docker run -dit --name conteneur2 --network reseau-ping ubuntu-ping
```

### 5. Vérifier que les deux conteneurs sont sur le même réseau

```bash
docker network inspect reseau-ping
```

La section `Containers` doit lister `conteneur1` et `conteneur2`.

### 6. Tester la communication par le nom du conteneur

```bash
docker exec conteneur1 ping -c 3 conteneur2
docker exec conteneur2 ping -c 3 conteneur1
```

Les pings répondent : le DNS interne de Docker résout bien le nom de chaque conteneur.

## Vérification

![network inspect](screenshots/network-inspect.png)
![ping entre les conteneurs](screenshots/ping.png)
