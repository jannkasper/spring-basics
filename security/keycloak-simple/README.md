# Spring Boot with Keycloak OAuth2 Authentication

This project demonstrates how to implement OAuth2 authentication in a Spring Boot application using Keycloak as the identity provider.

## Features

- Spring Boot 3.x
- Spring Security with OAuth2 Resource Server
- Role-based access control (RBAC)
- Method-level security with @PreAuthorize
- Keycloak as the OAuth2/OpenID Connect provider
- Docker Compose for easy Keycloak setup

## Requirements

- Java 17+
- Maven
- Docker and Docker Compose

## Getting Started

### 1. Start Keycloak

```bash
docker-compose up -d
```

This will start Keycloak on port 8090 with the admin username `admin` and password `admin`.

### 2. Configure Keycloak

1. Access the Keycloak Admin Console at http://localhost:8090/admin
2. Log in with username `admin` and password `admin`
3. Create a new realm named `spring-boot-realm`:
   - Click on the dropdown in the top-left corner
   - Click "Create Realm"
   - Enter "spring-boot-realm" and click "Create"

4. Create a new client:
   - Go to "Clients" → "Create client"
   - Client ID: `spring-boot-client`
   - Client type: `OpenID Connect`
   - Click "Next"
   - Enable "Client authentication" 
   - Click "Next" and then "Save"

5. Configure client settings:
   - Go to the "Settings" tab
   - Set "Valid redirect URIs" to `http://localhost:8080/*`
   - Click "Save"

6. Get client credentials:
   - Go to the "Credentials" tab
   - Copy the "Client secret" for later use

7. Create roles:
   - Go to "Realm roles"
   - Click "Create role"
   - Create roles named `USER` and `ADMIN`

8. Create a test user:
   - Go to "Users" → "Add user"
   - Fill in the form and click "Create"
   - Go to the "Credentials" tab and set a password (disable temporary password)
   - Go to the "Role mapping" tab
   - Click "Assign role" and assign the `USER` and/or `ADMIN` roles

9. Add client roles mapping (important for JWT claims):
   - Go to "Clients" → Select "spring-boot-client"
   - Go to the "Client scopes" tab → Select "spring-boot-client-dedicated"
   - Go to "Mappers" tab → Click "Add mapper" → "By configuration"
   - Create a new mapper with the following settings:
     - Name: `client roles`
     - Mapper type: `User Client Role`
     - Client ID: `spring-boot-client`
     - Token Claim Name: `roles`
     - Claim JSON Type: `String`
     - Add to ID token: `ON`
     - Add to access token: `ON`
     - Add to userinfo: `ON`
     - Multivalued: `ON`
   - Click "Save"

### 3. Run the Spring Boot Application

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

- Public (no auth required): 
  - `GET /api/public/hello`

- User Role Required:
  - `GET /api/user/info` - Basic user info
  - `GET /api/user/claims` - View all JWT claims
  - `GET /api/user/roles` - View user roles

- Admin Role Required:
  - `GET /api/admin/info` - Basic admin info
  - `GET /api/admin/details` - Protected endpoint using method security
  - `GET /api/admin/claims` - View all JWT claims
  - `GET /api/admin/roles` - View admin roles

## Understanding Keycloak JWT Claims

Keycloak structures roles in the JWT token in two main ways:

1. **Realm roles**: Stored under `realm_access.roles`
2. **Client roles**: Stored under `resource_access.[client-id].roles`

Our application is configured to read roles from both locations. You can view the raw JWT claims by accessing:
- `/api/user/claims` (for authenticated users)
- `/api/admin/claims` (for admin users)

## Testing the Application

1. Access the public endpoint:
   ```
   curl http://localhost:8080/api/public/hello
   ```

2. To access protected endpoints, you need to get a token from Keycloak:
   ```
   curl -X POST \
     http://localhost:8090/realms/spring-boot-realm/protocol/openid-connect/token \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -d 'client_id=spring-boot-client&client_secret=h9CgyFHhK0E0gAzmZc6BcPF7mLIZLl8M&grant_type=password&username=YOUR_USERNAME&password=YOUR_PASSWORD'
   ```

3. Use the token to access protected endpoints:
   ```
   curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/user/info
   ```

4. View your roles:
   ```
   curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/user/roles
   ```

## Security Configuration

The security configuration is defined in `SecurityConfig.java`. It sets up:
- JWT-based authentication with custom role converter
- Role-based authorization
- Method-level security with @PreAuthorize

The `KeycloakRoleConverter` class is responsible for extracting role information from the JWT token's claims, handling both realm roles and client roles.

## Troubleshooting

- If roles are not being recognized, check:
  1. The role mapping in Keycloak (both realm roles and client roles)
  2. The JWT token claims structure using the `/api/user/claims` endpoint
  3. That the client ID in the `KeycloakRoleConverter` matches your actual client ID

- Debug logs are enabled for Spring Security to help with troubleshooting.

## Reference Documentation

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security OAuth2 Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Keycloak Documentation](https://www.keycloak.org/documentation) 