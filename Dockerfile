FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

WORKDIR /app
COPY ${JAR_FILE} /app/app.jar

CMD ["java", "--jar", "app.jar"]