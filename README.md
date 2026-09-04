# DevOpsFlow Web Application & CI/CD Pipeline

A complete, production-grade Maven Java web application packaged as an external deployable `*.war` file for **Apache Tomcat**, designed to implement the end-to-end CI/CD pipeline with **Jenkins, Maven, SonarQube, Trivy, Nexus, and Tomcat**.

---

## 🏗️ Architecture & Pipeline Flow

The project directly maps to the 8 stages shown in the architecture:

```
[Git Commit] ➔ [GitHub Push] ➔ [Jenkins CI/CD] ➔ [Maven Build & Test] ➔ [SonarQube Quality] ➔ [Trivy Scan] ➔ [Nexus Repository] ➔ [Tomcat Deploy] ➔ [Monitor]
```

| # | Pipeline Stage | Tool | Activity / Responsibility |
|---|---|---|---|
| **1** | **Code Commit** | **Git** | Developer commits code locally in the feature/master branch |
| **2** | **Push Code** | **GitHub** | Push commits to the central GitHub repository to trigger the webhook |
| **3** | **Build & Test** | **Maven** | Compile code, run JUnit 5 unit tests, generate JaCoCo coverage, package `devops-flow.war` |
| **4** | **Code Quality** | **SonarQube** | Perform static code analysis, enforce Quality Gates (>80% test coverage, 0 critical bugs) |
| **5** | **Security Scan** | **Trivy** | Vulnerability scan on project dependencies, filesystem, and Docker container images |
| **6** | **Artifact Repository** | **Sonatype Nexus** | Store and version the built `.war` artifact in Nexus `maven-releases` / `maven-snapshots` |
| **7** | **Deploy** | **Apache Tomcat** | Deploy `devops-flow.war` to Apache Tomcat (`webapps/`) |
| **8** | **Monitor** | **Live App** | Real-time health check (`/api/health`, `/actuator/health`) and live monitoring dashboard |

---

## 📋 Technology Stack

- **Language & Runtime**: Java 21 LTS
- **Framework**: Spring Boot 3.3.4 (Jakarta EE 10)
- **Build Tool**: Apache Maven 3.9+
- **Artifact Packaging**: `WAR` (`target/devops-flow.war`)
- **Unit Testing & Coverage**: JUnit 5, Mockito, JaCoCo Maven Plugin
- **Static Analysis**: SonarQube Maven Scanner Plugin
- **Application Server Target**: Apache Tomcat 10.1+
- **Frontend**: Glassmorphic HTML5 / CSS3 / JavaScript dashboard

---

## 🚀 Quick Start & Building the WAR

### Prerequisites
- JDK 21 (or JDK 17+) installed and configured on your `PATH`
- Apache Maven 3.9+ installed and configured on your `PATH`

### 1. Build the Project and Generate `*.war`
Run the Maven package command in the root folder:

```powershell
mvn clean test package
```

This will:
1. Compile all Java sources in `src/main/java`.
2. Execute all unit and integration tests in `src/test/java`.
3. Generate JaCoCo code coverage report at `target/site/jacoco/jacoco.xml`.
4. Package the application into **`target/devops-flow.war`**.

---

### 2. Running the Application Locally

#### Option A: Run directly using Java (Standalone)
Because `SpringBootServletInitializer` and executable WAR repackaging are enabled, you can run the WAR directly:
```powershell
java -jar target/devops-flow.war
```

#### Option B: Run via Maven Spring Boot Plugin
```powershell
mvn spring-boot:run
```

#### Option C: Deploy to Standalone Apache Tomcat
1. Copy `target/devops-flow.war` into your Tomcat `webapps/` folder:
   ```powershell
   copy target\devops-flow.war C:\apache-tomcat-10.1.x\webapps\
   ```
2. Start Tomcat (`bin/startup.bat` or `bin/catalina.sh run`).
3. Access the application at: `http://localhost:8080/devops-flow/`

---

## 🌐 Endpoints & Web Dashboard

When the application is running, access:

- **Web Dashboard**: [http://localhost:8080/](http://localhost:8080/)
- **Health Telemetry**: [http://localhost:8080/api/health](http://localhost:8080/api/health)
- **Spring Boot Actuator**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Deployments API**: [http://localhost:8080/api/deployments](http://localhost:8080/api/deployments)
- **Services API**: [http://localhost:8080/api/services](http://localhost:8080/api/services)
- **Pipeline Stages**: [http://localhost:8080/api/pipeline/info](http://localhost:8080/api/pipeline/info)

---

## 🛠️ Jenkins CI/CD Pipeline Setup

The included `Jenkinsfile` orchestrates the entire workflow shown in the image.

### Pipeline Stage Details:
1. **Checkout Code**: Checks out the repository from Git/GitHub.
2. **Build & Unit Test**: Runs `mvn clean test package`, records JUnit XML test reports, and archives `target/*.war`.
3. **Code Quality (SonarQube)**:
   ```powershell
   mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
   ```
4. **Sonar Quality Gate**: Asserts that code coverage and quality thresholds are met before continuing.
5. **Security Scan (Trivy)**:
   ```powershell
   trivy fs --severity HIGH,CRITICAL .
   ```
6. **Upload Artifact to Nexus**: Publishes `devops-flow.war` into Sonatype Nexus 3 repository:
   ```powershell
   mvn deploy:deploy-file -DgroupId=com.devops.app -DartifactId=devops-flow -Dversion=1.0.0-SNAPSHOT -Dpackaging=war -Dfile=target/devops-flow.war -Durl=http://nexus:8081/repository/maven-releases/
   ```
7. **Deploy to Tomcat**: Copies WAR to Tomcat `webapps/` or triggers Tomcat Manager API.
8. **Monitor**: Performs automated health check against `http://tomcat:8080/api/health`.

---

## 🐳 Docker & Docker Compose Sandbox (Optional)

To spin up the entire DevOps ecosystem (Jenkins, SonarQube, Nexus, and Tomcat) locally:

```powershell
docker compose up -d
```

Services exposed:
- **Tomcat**: `http://localhost:8080`
- **Nexus 3**: `http://localhost:8081`
- **Jenkins**: `http://localhost:8082`
- **SonarQube**: `http://localhost:9000`
