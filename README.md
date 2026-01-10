# Project Service

## Overview
The **Project Service** manages all project-related operations including project creation, member management, and workflow status configuration for the TaskFlow application.

## Technology Stack
- **Spring Boot** 3.4.1
- **Spring Data JPA**
- **Spring Cloud OpenFeign** (for inter-service communication)
- **PostgreSQL** Database
- **Java** 17

## Port
- **Default Port**: `8082`

## Features
- ✅ Project CRUD operations
- ✅ Project member management
- ✅ Role-based access control (OWNER, ADMIN, MEMBER, VIEWER)
- ✅ Workflow status management
- ✅ Add members by email
- ✅ Project type categorization
- ✅ Integration with User Management Service

## Database Configuration

### Database Name
```
project_db
```

### Connection Properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/project_db
spring.datasource.username=project_user
spring.datasource.password=project_pass
spring.jpa.hibernate.ddl-auto=update
```

## API Endpoints

### Project Endpoints (`/api/projects`)

#### Get All Projects
```http
GET /api/projects
Authorization: Bearer <token>
X-User-Id: <user-uuid>
```

#### Get Project by ID
```http
GET /api/projects/{id}
Authorization: Bearer <token>
```

#### Create Project
```http
POST /api/projects
Authorization: Bearer <token>
X-User-Id: <user-uuid>
Content-Type: application/json

{
  "name": "TaskFlow Development",
  "projectKey": "TFD",
  "description": "Main development project",
  "type": "SOFTWARE",
  "ownerId": "user-uuid-here"
}
```

#### Update Project
```http
PUT /api/projects/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Project Name",
  "description": "Updated description",
  "type": "SOFTWARE"
}
```

#### Delete Project
```http
DELETE /api/projects/{id}
Authorization: Bearer <token>
```

#### Get Project Workflow Statuses
```http
GET /api/projects/{id}/statuses
Authorization: Bearer <token>
```

### Project Member Endpoints (`/api/projects/{projectId}/members`)

#### Get All Project Members
```http
GET /api/projects/{projectId}/members
Authorization: Bearer <token>
```

#### Add Member by Email
```http
POST /api/projects/{projectId}/members/by-email
Authorization: Bearer <token>
X-User-Id: <user-uuid>
Content-Type: application/json

{
  "email": "member@example.com",
  "role": "MEMBER"
}
```

#### Update Member Role
```http
PUT /api/projects/{projectId}/members/{userId}/role?role=ADMIN
Authorization: Bearer <token>
X-User-Id: <user-uuid>
```

#### Remove Member
```http
DELETE /api/projects/{projectId}/members/{userId}
Authorization: Bearer <token>
X-User-Id: <user-uuid>
```

## Data Models

### Project Entity
```java
{
  "id": "UUID",
  "name": "String",
  "projectKey": "String (unique, max 10 chars)",
  "description": "String",
  "type": "String (SOFTWARE, BUSINESS, MARKETING, DESIGN, OTHER)",
  "ownerId": "UUID",
  "createdAt": "Timestamp"
}
```

### Project Member Entity
```java
{
  "id": "UUID",
  "userId": "UUID",
  "projectId": "UUID",
  "role": "Enum (OWNER, ADMIN, MEMBER, VIEWER)",
  "status": "Enum (ACTIVE, INVITED, REMOVED)",
  "joinedAt": "Timestamp"
}
```

### Workflow Status Entity
```java
{
  "id": "UUID",
  "name": "String",
  "description": "String",
  "position": "Integer",
  "projectId": "UUID"
}
```

## Role-Based Access Control

### Roles
- **OWNER**: Full control, can delete project
- **ADMIN**: Can manage members and settings
- **MEMBER**: Can view and edit project content
- **VIEWER**: Read-only access

### Permissions Matrix
| Action | OWNER | ADMIN | MEMBER | VIEWER |
|--------|-------|-------|--------|--------|
| View Project | ✅ | ✅ | ✅ | ✅ |
| Edit Project | ✅ | ✅ | ❌ | ❌ |
| Delete Project | ✅ | ❌ | ❌ | ❌ |
| Add Members | ✅ | ✅ | ❌ | ❌ |
| Remove Members | ✅ | ✅ | ❌ | ❌ |
| Change Roles | ✅ | ✅ | ❌ | ❌ |

## Inter-Service Communication

### Feign Client - User Service
```java
@FeignClient(name = "UMS-SERVICE")
public interface UserServiceClient {
    @GetMapping("/api/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable String email);
}
```

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Eureka Service Registry running
- User Management Service running

### Database Setup
```sql
CREATE DATABASE project_db;
CREATE USER project_user WITH PASSWORD 'project_pass';
GRANT ALL PRIVILEGES ON DATABASE project_db TO project_user;
```

### Using Maven
```bash
cd project-service
mvn clean install
mvn spring-boot:run
```

## Service Registration
Registers with Eureka as: **PROJECT-SERVICE**

## Security Configuration
All endpoints require authentication via API Gateway's `AuthenticationFilter`.

### Custom Headers
- `X-User-Id`: Current user's UUID (set by API Gateway)
- `X-User-Email`: Current user's email (set by API Gateway)

## Health Check
```bash
curl http://localhost:8082/actuator/health
```

## Testing

### Create a Project
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Project",
    "projectKey": "TEST",
    "description": "A test project",
    "type": "SOFTWARE",
    "ownerId": "user-uuid"
  }'
```

### Add Member by Email
```bash
curl -X POST http://localhost:8080/api/projects/{projectId}/members/by-email \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "member@example.com",
    "role": "MEMBER"
  }'
```

## Troubleshooting

### Project Key Already Exists
- Project keys must be unique
- Use a different key or update existing project

### User Not Found
- Verify email exists in User Management Service
- Check Feign client connectivity

### Insufficient Permissions
- Check user's role in the project
- Only OWNER/ADMIN can add/remove members

### Duplicate Member
- User is already a member of the project
- Returns 409 Conflict status

## Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## Future Enhancements
- [ ] Project templates
- [ ] Project archiving
- [ ] Project analytics
- [ ] Custom workflow configuration
- [ ] Project cloning

---

**Status**: ✅ Production Ready  
**Maintained by**: TaskFlow Team
