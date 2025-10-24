FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dskiptest

#Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/cinema-booking-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.war"]