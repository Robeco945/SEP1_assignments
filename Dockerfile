FROM eclipse-temurin:21-jdk
COPY target/shopping-cart-localized-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]