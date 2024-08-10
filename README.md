# API Authentication Service

## Description

This API Authentication service manages users, JWT tokens, and email sending for password resets.  
It is configured to connect to an Eureka server for service registration and discovery, and it uses a MySQL database to store information.

## Prerequisites

Before starting, ensure you have the following installed on your machine:

- **Java 17** or later
- **Maven 3.8.8** or later
- **MySQL** (if you are not using a remote MySQL database)
- **Docker** (optional) if you prefer to run the API in a Docker container
- **An SMTP server** for sending emails

## Configuration

Configurations are managed through environment variables, which can be defined in a `.env` file.  
This file should be placed at the root of the project.

### Example `.env` File

```env
# === Server Configuration ===
SERVER_PORT=8081
APP_NAME=API-Authentication

# === Configuration de la base de données ===
DATA_URL=jdbc:mysql://db_host:db_port/db_name
DATA_USERNAME=Charles
DATA_PASSWORD=your_database_password

# === Configuration du service de messagerie ===
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email_username
MAIL_PASSWORD=your_email_password

# === Configuration du token JWT ===
JWT_KEY=your_jwt_secret_key
JWT_EXPIRATION=86400000

# === Configuration Eureka ===
EUREKA_DEFAULTZONE=http://localhost:8761/eureka/
```
### Explanation of Variables

- **SERVER_PORT**: The port on which the application listens.
- **APP_NAME**: The name under which the application is registered with Eureka.
- **DATA_URL**: The JDBC URL for connecting to the MySQL database.
- **DATA_USERNAME**: The username for database connection.
- **DATA_PASSWORD**: The password for database connection.
- **MAIL_HOST**: The SMTP server used for sending emails.
- **MAIL_PORT**: The port used by the SMTP server.
- **MAIL_USERNAME**: The username for SMTP authentication (usually an email address).
- **MAIL_PASSWORD**: The password for SMTP authentication.
- **JWT_KEY**: The secret key used to sign JWT tokens.
- **JWT_EXPIRATION**: The expiration duration of JWT tokens in milliseconds (e.g., 86400000 ms for 24 hours).
- **EUREKA_DEFAULTZONE**: The URL of the Eureka server for service registration and discovery.

## Installation

### Clone the repository

```bash
git clone https://github.com/your-username/api-authentication.git
cd api-authentication
```

### Configure environment variables

Create a .env file in the root directory of the project using the example provided above.  
Fill in the appropriate values for your environment.

### Build the project with Maven

```bash
mvn clean install
```

## Running the Application

### Standard Execution

1. Start the application

After configuring the .env file and building the project, you can run the application using the following command:
```bash
mvn spring-boot:run
```

2. Access the application

The application will be available at the following address (depending on the port you configured):
```bash
http://localhost:8081
```

### Running with Docker

If you prefer to use Docker, follow these steps:

1. Build the Docker image

Ensure you are in the project directory, then run the following command to build the Docker image:
```bash
docker build -t api-authentication .
```

2. Run the Docker container with the .env file

After building the image, you can start the container using the following command, specifying the .env file:
```bash
docker run --env-file .env -d -p 8081:8081 --name api-authentication api-authentication
```

3. Access the application

As with standard execution, the application will be accessible at the following address (depending on the port you configured):
```bash
http://localhost:8081
```

## Tests and Code Coverage

### Running Tests

Unit tests are executed using Maven. To run all tests, use the following command:
```bash
mvn test
```

### Generating Code Coverage Report with JaCoCo

This project uses JaCoCo to measure code coverage. To generate a code coverage report, run the tests with the following command:
```bash
mvn clean test
```
After running the tests, a JaCoCo coverage report will be generated in the following directory:
```bash
target/site/jacoco/index.html
```

### Viewing the Coverage Report

To view the code coverage report, open the index.html file in a browser:
```bash
open target/site/jacoco/index.html
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.