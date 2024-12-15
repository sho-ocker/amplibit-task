Here's the revised README.md file with additional details about the Docker Compose setup and the LDAP initialization files:

---

# **Spring Boot LDAP-Inventory Project**

This project is a Spring Boot-based application that integrates with **OpenLDAP** to simulate Active Directory (AD). It provides user authentication, periodic user synchronization, and a role-based access control (RBAC) system. Additionally, it manages an **Inventory** system via RESTful APIs with OpenAPI documentation.

---

## **Table of Contents**
1. [Overview](#overview)
2. [Technologies](#technologies)
3. [Features](#features)
4. [Database Schema](#database-schema)
5. [Folder Structure](#folder-structure)
6. [Setup Instructions](#setup-instructions)
7. [API Documentation](#api-documentation)
8. [Testing](#testing)
9. [Usage](#usage)

---

## **1. Overview**

The project integrates with OpenLDAP as a mock Active Directory to handle user authentication and synchronization. Postgres is used as the primary database to store users and inventory data. APIs are documented using **OpenAPI** for clarity and ease of testing.

---

## **2. Technologies**

- **Spring Boot** - Backend framework.
- **Postgres** - Relational database for inventory and user data.
- **OpenLDAP** - LDAP server for authentication and mock AD integration.
- **JWT** - JSON Web Token for secure authentication.
- **Docker Compose** - Simplifies the setup of Postgres and OpenLDAP containers.
- **Liquibase** - Database migration tool.
- **Swagger UI** - API documentation via OpenAPI.
- **JUnit** - Unit testing framework.

---

## **3. Features**

- **LDAP Integration**: User authentication via OpenLDAP.
- **User Synchronization**: Users are synchronized every 5 minutes from LDAP to the database.
- **RBAC**: Role-based access control with three roles:
    - **User**: Can view only their assigned inventory.
    - **Viewer**: Can view all inventory.
    - **Admin**: Full access to all users and inventory.
- **Inventory Management**:
    - Assign inventory items to users.
    - Retrieve inventory items.
- **OpenAPI Documentation**: Explore and test the APIs via Swagger UI.

---

## **4. Database Schema**

### **User Table**

| Column Name | Data Type      | Constraints          |
|-------------|----------------|----------------------|
| id          | UUID           | Primary Key          |
| username    | VARCHAR(255)   | Unique, Not Null     |
| role        | VARCHAR(50)    | Not Null             |

### **Inventory Table**

| Column Name   | Data Type      | Constraints          |
|---------------|----------------|----------------------|
| id            | UUID           | Primary Key          |
| name          | VARCHAR(255)   | Not Null             |
| assigned_to   | UUID           | Foreign Key (User.id)|

---

## **5. Folder Structure**

```
amplibit-task/
│
├── ldifs/                        # LDAP initialization files
│   └── roles_and_users.ldif
├── src/main/java/org/example/
│   ├── config/                   # Application configurations
│   │   └── AppConfig.java
│   ├── inventory/                # Inventory module
│   │   ├── Inventory.java
│   │   ├── InventoryController.java
│   │   ├── InventoryRepository.java
│   │   ├── InventoryService.java
│   │   └── InventoryServiceImpl.java
│   ├── ldap/                     # LDAP Integration
│   │   ├── CustomLdapUserDetailsMapper.java
│   │   ├── LdapService.java
│   │   ├── LdapServiceImpl.java
│   │   └── LdapUser.java
│   ├── security/                 # Security and JWT
│   │   ├── dto/
│   │   ├── filter/
│   │   └── AuthenticationController.java
│   └── users/                    # User management module
│       ├── User.java
│       ├── UserController.java
│       ├── UserRepository.java
│       └── UserSynchronizationService.java
│
├── src/test/java/org/example/    # Tests
│   ├── inventory/
│   │   └── InventoryControllerTest.java
│   └── users/
│       └── UserControllerTest.java
│
├── .env                          # Environment variables
├── docker-compose.yml            # Docker Compose setup
└── pom.xml                       # Maven dependencies
```

---

## **6. Setup Instructions**

### **Prerequisites**

1. **Docker** and **Docker Compose** installed.
2. **Java 17+** and **Maven** installed.

### **Steps to Run the Project**

1. **Clone the repository**:
   ```bash
   git clone https://github.com/sho-ocker/amplibit-task.git
   cd amplibit-task
   ```

2. **Start Postgres and OpenLDAP**:
   ```bash
   docker-compose up -d
   ```

3. **Start the Spring Boot Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access Swagger UI**:
   Open `http://localhost:8080/openapi` to explore and test the APIs.

---

## **7. API Documentation**

The project uses OpenAPI to document all endpoints. Use Swagger UI to access API specs:

- **Base URL**: `http://localhost:8080`

### **Key Endpoints**

#### **Authentication**

| Method | Endpoint          | Description                 |
|--------|-------------------|-----------------------------|
| POST   | /api/login        | Authenticate and get JWT    |

#### **User Management**

| Method | Endpoint          | Description                 |
|--------|-------------------|-----------------------------|
| GET    | /api/users        | Get all users (**Admin**)   |

#### **Inventory Management**

| Method | Endpoint                   | Description                          |
|--------|----------------------------|--------------------------------------|
| GET    | /api/inventory             | View inventory (RBAC applied)        |
| POST   | /api/inventory/assign      | Assign inventory to a user (**Admin**) |

---

## **8. Testing**

Unit tests are located under `src/test/java`.

To run tests, execute:

```bash
mvn test
```
- **Basic Auth** is the **primary** method for which unit tests have been written and validated.
- JWT Token authentication has been implemented as an **alternative** method for additional security.

---

## **9. Usage**

### **1. Basic Authentication (Primary Method)**

The application uses Basic Authentication by default for user authentication and API testing.

1. **Login Credentials**:
   Use LDAP credentials to authenticate, for example:
    - **Username**: `admin_person` `user_person` `viewer_person`
    - **Password**: `pass`

2. **Access API Endpoints**:
    - **Using Postman**:
        - Open Postman and select the **Authorization** tab.
        - Set the **Type** to **Basic Auth**.
        - Enter your username (`admin`) and password (`admin`).
        - Postman will automatically generate the correct `Authorization` header.

---

### **2. JWT Token Authentication**

JWT Token authentication can also be used as an alternative method for secure API requests.

1. **Authenticate to Get JWT Token**:

   Send a **POST** request to the `/api/login` endpoint with LDAP credentials:


**Response**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBZG1pbiIsIml...",
     "expiresIn": 1000000
   }
   ```


2. **Access API Endpoints**:
   Use the JWT token received in the response for authentication in subsequent requests.

   Add the token to the **Authorization** header as `Bearer <token>`:

   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/users
   ```

### **3. Switching between auths (Uncomment code)**

1. **SecurityConfig.java**:
    - Uncomment JwtAuthenticationFilter and configure it to be used before the UsernamePasswordAuthenticationFilter.
    - Update the securityFilterChain method to enforce JWT authentication for all endpoints except those specified (like /auth/** for login and signup endpoints).
2. **AppConfig.java**:
    - Include JwtService for handling JWT-related tasks (like extracting the username from a JWT token and validating the JWT).
    - Adjust the AuthenticationManager to use JwtService for user validation.
    - Define a bean for JwtService to handle token validation.
    - Define a bean for a password encoder
3. **JwtAuthenticationFilter**:
    - Uncomment the whole class so that doFilterInternal method can extract the JWT from the Authorization header, validate it, and set the authentication context accordingly.

---

## **10. Docker Compose Services**

### **Postgres**:
- Service Name: `postgres`
- Port: `5432`

### **OpenLDAP**:
- Service Name: `openldap`
- Port: `1389`

### **LDAP Initialization Files (ldifs/roles_and_users.ldif)**

The `roles_and_users.ldif` file is used for initializing users and roles in the OpenLDAP container. It contains the following entries:
```ldif
dn: cn=ADMIN,ou=Roles,dc=task,dc=com
cn: ADMIN
description: Admin role
objectClass: groupOfNames
member: cn=admin_person,dc=task,dc=com
member: cn=admin_person_2,dc=task,dc=com

dn: cn=USER,ou=Roles,dc=task,dc=com
cn: USER
description: Regular user role
objectClass: groupOfNames
member: cn=user_person,dc=task,dc=com
member: cn=user_person_2,dc=task,dc=com

dn: cn=VIEWER,ou=Roles,dc=task,dc=com
cn: VIEWER
description: Viewer role
objectClass: groupOfNames
member: cn=viewer_person,dc=task,dc=com
member: cn=viewer_person_2,dc=task,dc=com
```

This file initializes the roles and users in LDAP and assigns roles to users, making it possible to manage access control within the system.