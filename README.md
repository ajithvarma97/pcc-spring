# Spring Boot Application with CI/CD on GCP

This project demonstrates a Spring Boot application integrated with CI/CD using GitHub Actions. The application is automatically built, tested, and deployed to an Ubuntu server on Google Cloud Platform (GCP).

## Features
- **Spring Boot**: A simple “Hello World” application with database integration.
- **Maven Build**: Automated build and test pipeline.
- **CI/CD Pipeline**: GitHub Actions for continuous integration and deployment.
- **Secure Deployment**: Database credentials and sensitive information are securely managed using GitHub Secrets and environment variables.

## Requirements

### Local Development
- **Java**: OpenJDK 17 or higher
- **Maven**: 3.x or higher
- **Database**: MySQL, PostgreSQL, or any database compatible with Spring Boot

### CI/CD
- **GitHub Repository**: For source code and workflows.
- **Ubuntu Server on GCP**: Running a Linux-based OS with SSH access.
- **GitHub Secrets**: For storing sensitive data like database credentials and SSH keys.

## Running the Application Locally

1. **Set Up Your Environment Variables**:
    Export the required database credentials and URL as environment variables. Replace the placeholder values with your actual database details:
    ```sh
    export DB_URL=jdbc:mysql://localhost:5432/your_database
    export DB_USERNAME=your_username
    export DB_PASSWORD=your_password
    ```

2. **Clone the Repository**:
    ```sh
    git clone https://github.com/your-username/your-repo.git
    cd your-repo
    ```

3. **Run the Application with Maven**:
    Use Maven to run the Spring Boot application while passing the environment variables:
    ```sh
    mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=$DB_URL --spring.datasource.username=$DB_USERNAME --spring.datasource.password=$DB_PASSWORD"
    ```

4. **Verify the Application**:
    The application should now be running on [http://localhost:3000](http://localhost:3000).

5. **Access the Logs**:
    If you need to view application logs, they will be displayed in the terminal.

## CI/CD Pipeline

### Workflow Overview

The GitHub Actions workflow (`.github/workflows/maven.yml`) automates:
1. **Building and Testing**:
    - Checks out the repository code.
    - Builds the application with Maven.
2. **Deployment**:
    - Deploys the application JAR to the Ubuntu server on GCP.
    - Starts the application on the server.

### Configure GitHub Secrets

Add the following secrets to your GitHub repository under **Settings > Secrets and Variables > Actions**:
- `DB_URL`: Database connection string (e.g., `jdbc:mysql://<host>:<port>/<database>`).
- `DB_USERNAME`: Database username.
- `DB_PASSWORD`: Database password.
- `GCP_SERVER_IP`: Public IP of your Ubuntu server.
- `GCP_SERVER_USER`: SSH username for the server.
- `GCP_SERVER_SSH_KEY`: Private SSH key for SSH access to the server.

### Triggering the Workflow

The workflow is triggered automatically on:
- Pushes to the `main` branch.
- Pull requests targeting the `main` branch.

### Deployment to GCP

1. **Ensure Your Server is Configured**:
    - Java (JDK 17 or higher) is installed.
    - Firewall allows traffic on port 8080.

2. **Deployment Process**:
    The workflow will:
    - Transfer the JAR file to the Ubuntu server via SCP.
    - Start the application using the `java -jar` command.

3. **Monitor Application Logs**:
    Logs are stored in `/home/<user>/app.log` on the server. View logs using:
    ```sh
    tail -f /home/<user>/app.log
    ```

## Workflow File

```yaml
name: Java CI/CD with Maven

on:
  push:
     branches: [ "main" ]
  pull_request:
     branches: [ "main" ]

jobs:
  build-and-deploy:
     runs-on: ubuntu-latest

     steps:
     - name: Checkout repository
        uses: actions/checkout@v4

     - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

     - name: Build with Maven
        run: mvn -B package --file pom.xml

     - name: Deploy to Ubuntu Server
        env:
          DB_URL: ${{ secrets.DB_URL }}
          DB_USERNAME: ${{ secrets.DB_USERNAME }}
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
          GCP_SERVER_IP: ${{ secrets.GCP_SERVER_IP }}
          GCP_SERVER_USER: ${{ secrets.GCP_SERVER_USER }}
          GCP_SERVER_SSH_KEY: ${{ secrets.GCP_SERVER_SSH_KEY }}
        run: |
          echo "${GCP_SERVER_SSH_KEY}" > ~/.ssh/id_rsa
          chmod 600 ~/.ssh/id_rsa
          scp -o StrictHostKeyChecking=no target/*.jar ${GCP_SERVER_USER}@${GCP_SERVER_IP}:/home/${GCP_SERVER_USER}/app.jar
          ssh -o StrictHostKeyChecking=no ${GCP_SERVER_USER}@${GCP_SERVER_IP} << EOF
             export DB_URL=${DB_URL}
             export DB_USERNAME=${DB_USERNAME}
             export DB_PASSWORD=${DB_PASSWORD}
             pkill -f 'java -jar' || true
             nohup java -jar /home/${GCP_SERVER_USER}/app.jar > app.log 2>&1 &
          EOF
```

## Best Practices

1. **Keep Sensitive Data Secure**:
    - Use environment variables locally.
    - Use GitHub Secrets for CI/CD pipelines.

2. **Sanitize Git History**:
    - If sensitive data like credentials have been committed, scrub your Git history with tools like BFG Repo-Cleaner.

3. **Test Before Deployment**:
    - Test the application locally before triggering the CI/CD pipeline.

## License

This project is licensed under the MIT License.
