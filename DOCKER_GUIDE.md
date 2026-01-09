# Docker Guide for Project Service

## Overview

Your Dockerfile uses a **multi-stage build** to create an optimized Docker image for the project-service.

## Dockerfile Explanation

```dockerfile
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR app
COPY pom.xml .
RUN mvn dependency:go-offline -B          # Download dependencies (cached layer)
COPY src ./src
RUN mvn clean package -DskipTests         # Build the JAR file

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre-alpine       # Smaller JRE-only image
WORKDIR app
COPY --from=build app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Benefits:**
- ✅ Smaller final image (only JRE, not full JDK + Maven)
- ✅ Cached dependency layer (faster rebuilds)
- ✅ Production-ready Alpine Linux base

---

## How to Build and Run

### 1. Build the Docker Image

```bash
cd d:\G Drive\task-management-system-jira\backend\project-service
docker build -t project-service:latest .
```

**Build time:** ~5-10 minutes (first time), ~1-2 minutes (subsequent builds with cache)

### 2. Run the Container

#### Option A: Standalone (for testing)

```bash
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/project-service-db \
  -e SPRING_DATASOURCE_USERNAME=neondb_owner \
  -e SPRING_DATASOURCE_PASSWORD=npg_ldDN8gsV3aBv \
  -e EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://host.docker.internal:8761/eureka/ \
  project-service:latest
```

**Note:** Use `host.docker.internal` to access services running on your host machine from inside Docker.

#### Option B: With Docker Compose (recommended)

Create `docker-compose.yml` in the backend folder:

```yaml
version: '3.8'

services:
  eureka-server:
    build: ./taskflow-service-registry
    ports:
      - "8761:8761"
    networks:
      - taskflow-network

  ums-service:
    build: ./taskflow-user-service
    ports:
      - "8081:8081"
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      - eureka-server
    networks:
      - taskflow-network

  project-service:
    build: ./project-service
    ports:
      - "8082:8082"
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      - eureka-server
      - ums-service
    networks:
      - taskflow-network

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      - eureka-server
      - ums-service
      - project-service
    networks:
      - taskflow-network

networks:
  taskflow-network:
    driver: bridge
```

Then run:

```bash
cd d:\G Drive\task-management-system-jira\backend
docker-compose up -d
```

---

## Useful Docker Commands

### View Running Containers
```bash
docker ps
```

### View Logs
```bash
docker logs project-service
docker logs -f project-service  # Follow logs in real-time
```

### Stop Container
```bash
docker stop project-service
```

### Remove Container
```bash
docker rm project-service
```

### Remove Image
```bash
docker rmi project-service:latest
```

### Access Container Shell
```bash
docker exec -it project-service sh
```

### Rebuild Without Cache
```bash
docker build --no-cache -t project-service:latest .
```

---

## Environment Variables

You can override application.properties values using environment variables:

| Property | Environment Variable | Example |
|----------|---------------------|---------|
| `server.port` | `SERVER_PORT` | `8082` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://...` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `neondb_owner` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `npg_ldDN8gsV3aBv` |
| `eureka.client.service-url.defaultZone` | `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | `http://eureka:8761/eureka/` |

---

## Troubleshooting

### Issue: "Cannot connect to database"
**Solution:** Use `host.docker.internal` instead of `localhost` in connection strings when connecting from Docker to host services.

### Issue: "Cannot connect to Eureka"
**Solution:** Ensure Eureka server is running and accessible. Use service names in Docker Compose network.

### Issue: "Port already in use"
**Solution:** Stop the service running on that port or change the port mapping:
```bash
docker run -p 9082:8082 project-service:latest  # Maps host 9082 to container 8082
```

### Issue: "Build fails with dependency errors"
**Solution:** Clear Maven cache and rebuild:
```bash
docker build --no-cache -t project-service:latest .
```

---

## Production Considerations

### 1. Use External Configuration
Mount config files instead of baking them into the image:

```bash
docker run -v /path/to/application.properties:/app/config/application.properties \
  -p 8082:8082 project-service:latest
```

### 2. Health Checks
Add to Dockerfile:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1
```

### 3. Resource Limits
```bash
docker run --memory="512m" --cpus="1.0" -p 8082:8082 project-service:latest
```

### 4. Use Docker Secrets for Passwords
Instead of environment variables, use Docker secrets for sensitive data.

---

## Next Steps

1. ✅ Create Dockerfiles for other services (ums-service, api-gateway, etc.)
2. ✅ Set up docker-compose.yml for the entire backend
3. ✅ Configure persistent volumes for databases
4. ✅ Set up Docker networks for service isolation
5. ✅ Add health checks and monitoring
