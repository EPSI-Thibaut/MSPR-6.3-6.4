# Backend Spring Boot - Solution ETL pour l'Analyse des Données de Pandémies

## Description

Cette application Spring Boot implémente une solution ETL (Extract, Transform, Load) pour traiter et analyser les données de pandémies (COVID-19 et SARS). Elle extrait les données depuis des fichiers CSV, les transforme et les charge dans une base de données MariaDB pour permettre leur analyse comparative via une API REST.

## Prérequis

- **Java 23** ou version supérieure
- **Maven 3.6+**
- **MariaDB 10.5+**
- **Git** (pour cloner le projet)

## Installation

### 1. Vérification de Java

Vérifiez que Java 23 est installé :

```bash
java -version
javac -version
```

Si Java 23 n'est pas installé, téléchargez-le depuis [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.org/).

### 2. Installation de MariaDB

#### Sur Windows :
- Téléchargez MariaDB depuis [le site officiel](https://mariadb.org/download/)
- Suivez l'assistant d'installation
- Notez le mot de passe root

#### Sur macOS :
```bash
# Avec Homebrew
brew install mariadb
brew services start mariadb
```

#### Sur Linux (Ubuntu/Debian) :
```bash
sudo apt update
sudo apt install mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

### 3. Configuration de la base de données

1. **Connexion à MariaDB** :
```bash
mysql -u root -p
```

2. **Création de la base de données** :
```sql
CREATE DATABASE msprdatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'mspr_user'@'localhost' IDENTIFIED BY 'mspr_password';
GRANT ALL PRIVILEGES ON msprdatabase.* TO 'mspr_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 4. Clonage du projet

```bash
git clone <url-du-repository>
cd Backend
```

### 5. Configuration de l'application

Créez un fichier `application.properties` dans [`src/main/resources/`](Backend/src/main/resources/) :

```properties
# Configuration de la base de données
spring.datasource.url=jdbc:mariadb://localhost:3306/msprdatabase
spring.datasource.username=mspr_user
spring.datasource.password=mspr_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect

# Configuration du serveur
server.port=8080

# Configuration de Spring Data REST
spring.data.rest.base-path=/api

# Configuration CORS
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

## Lancement de l'application

### 1. Avec Maven Wrapper (recommandé)

#### Sur Linux/macOS :
```bash
./mvnw clean install
./mvnw spring-boot:run
```

#### Sur Windows :
```cmd
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### 2. Avec Maven (si installé globalement)

```bash
mvn clean install
mvn spring-boot:run
```

### 3. Exécution du JAR

```bash
# Compilation
./mvnw clean package

# Exécution
java -jar target/Backend-0.0.1-SNAPSHOT.jar
```

## Vérification de l'installation

### 1. Vérification du serveur

L'application démarre sur le port **8080**. Vérifiez que le serveur fonctionne :

```bash
curl http://localhost:8080/api
```

### 2. Documentation API Swagger

Accédez à la documentation Swagger UI :
- URL : http://localhost:8080/swagger-ui.html

### 3. Endpoints disponibles

- **API REST** : `http://localhost:8080/api/`
- **Pays** : `http://localhost:8080/api/countrieses`
- **Continents** : `http://localhost:8080/api/continentses`
- **Régions** : `http://localhost:8080/api/regionses`
- **Pandémies** : `http://localhost:8080/api/pandemicses`
- **Données quotidiennes** : `http://localhost:8080/api/totalByDays`

## Structure du projet

```
Backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── fr/epsib3devc2/backend/
│   │   │       ├── SpringBatchApplication.java      # Point d'entrée
│   │   │       ├── dto/                             # Data Transfer Objects
│   │   │       ├── entities/                        # Entités JPA
│   │   │       ├── repositories/                    # Repositories Spring Data
│   │   │       └── services/                        # Services métier
│   │   └── resources/
│   │       └── application.properties               # Configuration
│   ├── Data/                                        # Fichiers CSV
│   └── test/                                        # Tests unitaires
├── target/                                          # Fichiers compilés
├── mvnw                                            # Maven Wrapper Unix
├── mvnw.cmd                                        # Maven Wrapper Windows
├── pom.xml                                         # Configuration Maven
└── README.MD                                       # Ce fichier
```

## Processus ETL

Au démarrage, l'application exécute automatiquement le processus ETL :

1. **Extraction** : Lecture des fichiers CSV depuis [`src/Data/`](Backend/src/Data/)
2. **Transformation** : Normalisation via [`DataNormalizationService`](Backend/src/main/java/fr/epsib3devc2/backend/services/DataNormalizationService.java)
3. **Chargement** : Insertion en base via [`DataInsertionService`](Backend/src/main/java/fr/epsib3devc2/backend/services/DataInsertionService.java)

## Développement

### Tests

```bash
./mvnw test
```

### Mode développement avec hot-reload

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Génération du JAR de production

```bash
./mvnw clean package -DskipTests
```

## Résolution des problèmes

### Erreur de connexion à la base de données

1. Vérifiez que MariaDB est démarré :
```bash
# Linux/macOS
sudo systemctl status mariadb

# Windows
net start mysql
```

2. Testez la connexion :
```bash
mysql -u mspr_user -p msprdatabase
```

### Erreur de version Java

Vérifiez la version de Java et JAVA_HOME :
```bash
echo $JAVA_HOME
java -version
```

### Port 8080 déjà utilisé

Changez le port dans `application.properties` :
```properties
server.port=8081
```

### Problèmes de permissions sur mvnw

```bash
chmod +x mvnw
```

## Variables d'environnement (optionnel)

Vous pouvez surcharger la configuration via des variables d'environnement :

```bash
export DB_URL=jdbc:mariadb://localhost:3306/msprdatabase
export DB_USERNAME=mspr_user
export DB_PASSWORD=mspr_password
export SERVER_PORT=8080
```

## Logs et monitoring

Les logs sont affichés dans la console. Pour les personnaliser, ajoutez dans `application.properties` :

```properties
# Configuration des logs
logging.level.fr.epsib3devc2.backend=DEBUG
logging.level.org.springframework.web=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## Contact et support

Pour toute question ou problème :
- Consultez les logs de l'application
- Vérifiez la documentation Swagger
- Contactez l'équipe de développement

---

**Note** : Cette application fait partie du projet MSPR 6.3 - EPSI NANTES B3 DEV IA FS