# AEGERHUB

AEGERHUB est une application web complète avec une authentification sécurisée et un déploiement automatique via Jenkins.

## Stack Technique

* **Backend :** Java 17, Spring Boot 3, Spring Data JPA
* **Base de données :** PostgreSQL 15
* **Frontend :** React / Vite (Node.js 20)
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
Assurez-vous d'avoir Docker, puis lancez :
```bash
docker run -d --name postgres-db -e POSTGRES_PASSWORD=aeger -e POSTGRES_USER=aeger -e POSTGRES_DB=aeger_hub_db -p 5432:5432 postgres:15