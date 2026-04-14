FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app

RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1-mesa-glx \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src

ENTRYPOINT ["mvn", "javafx:run"]