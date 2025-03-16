# Expense Tracker

**Expense Tracker** is a finance management application. It was created as a
learning project to improve my skills with Backend development and CI/CD.

## Frameworks and Tools

- Spring Boot 3.3
- PostgreSQL 14 and PgAdmin 4

## Required dependencies

- Java 17
- Maven 
- Docker/Podman
- GNU Make

## Usage

### Start the development server
```bash
./mvnw spring-boot:run
```

### Run test suites
```bash
./mvnw test
```

### Build the application's container image
```bash
make container-build # uses Podman by default
make CONTAINER_TOOL=docker container-build # uses Docker for building the container
```

### Start the app container
```bash
make container-run # uses Podman by default
make CONTAINER_TOOL=docker container-run # uses Docker to start the container
```

## API Resources

The application comes with **Swagger UI**, **PgAdmin** and **H2 Console** for
easy, hands-on testing and verification.

Once the application server has started, you can access them through the following
links:

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [H2 Console](http://localhost:8080/h2-console/) (for development server)
- [PgAdmin](http://localhost:5050) (for production server)