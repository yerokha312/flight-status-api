# Flight Status API

Welcome to the Flight Status API repository. This project provides an API for tracking and moderation of the flight status.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)


## Introduction

The Flight Status API is designed to provide comprehensive flight tracking capabilities. It allows users to query flight status, track flight movements, and receive updates on flight schedules and statuses.

## Features

- Historical flight data
- Flight status updates
- Secure authentication using JWT (RSA algorithm)


### Prerequisites

- Java 17 or higher
- PostgreSQL
- Redis
- Gradle

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/yerokha312/flight-status-api.git
   ```

2. Navigate to the project directory:

   ```bash
   cd flight-status-api
   ```

3. Configure the database settings in `src/main/resources/application.yml`:

   ```yaml
    spring:
        application:
            name: flight-status-api
        datasource:
            url: ${DATABASE_URL}
            username: ${DATABASE_USERNAME}
            password: ${DATABASE_PASSWORD}
            driver-class-name: org.postgresql.Driver
        jpa:
            hibernate:
                ddl-auto: validate
            show-sql: true
        data:
            redis:
                password: ${REDIS_PASSWORD}
                username: ${REDIS_USER}
                port: ${REDIS_PORT}
                host: ${REDIS_HOST}
        cache:
            type: caffeine
        sql:
            init:
            mode: always
   ```
**Also you have to set ENCRYPTION_KEY key with 32-character long string (secret)**

**In classpath you can find 2 predefined SQL-scripts those will be executed on every startup**

**There will be a 'moderator' user with password 'password' that has all permissions**

**Tests require the same environmental variables to run. Just run the common controller folder of tests**


## Usage

### Authentication

The API uses JWT for authentication. Obtain a token by authenticating with your credentials.

### Example Request

To get the status of a flight, send a GET request to the `/flights` endpoint:

```bash
curl -H "Authorization: Bearer <your-token>" -X GET "http://localhost:8080/api/v1/flights?filter=origin&origin=<IATA code>"
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/login` - Authenticate and get a token.

### Flights

- `GET /api/v1/flights` - Retrieve flight status.
- `POST /api/v1/flights` - Create a new flight (requires MODERATOR role).

## Configuration

Configuration settings can be found in the `application.yml` file located in `src/main/resources`.

### Database Configuration

Configure the database connection settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yourdatabase
    username: yourusername
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Redis Configuration

Configure the Redis settings:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: yourpassword
```
