FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=target/*.jar

WORKDIR /app
COPY ${JAR_FILE} /app/app.jar

CMD ["java", "--jar", "app.jar"]