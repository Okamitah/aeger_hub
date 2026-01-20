# AEGERHUB



## Stack Technique

* **Backend :** Java 17, Spring Boot 3, Spring Data JPA
* **Simulation :** Java Scheduled Services (Calcul mathématique temps réel)
* **Base de données :** PostgreSQL 15
* **Frontend :** Vue.js 3 / Vite (Node.js 20)
* **DevOps :** Docker, Jenkins, Git

## Informations Importantes

* **VM d'intégration :** 172.31.249.107
* **Base de données (Docker) :**
  * Utilisateur : aeger
  * Mot de passe : aeger
  * Nom de la DB : aeger_hub_db
  * Port : 5432

## Comment lancer le projet localement

### 1. Lancer la base de données
Assurez-vous d'avoir Docker, puis lancez le conteneur PostgreSQL :
```bash
docker run -d --name postgres-db \
  -e POSTGRES_PASSWORD=aeger \
  -e POSTGRES_USER=aeger \
  -e POSTGRES_DB=aeger_hub_db \
  -p 5432:5432 postgres:15```