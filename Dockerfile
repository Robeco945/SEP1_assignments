FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compile and run via Maven
ENTRYPOINT ["mvn", "javafx:run"]