FROM eclipse-temurin:17-alpine AS build

WORKDIR /app

COPY mvnw pom.xml .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package

FROM eclipse-temurin:17-alpine

WORKDIR /app
COPY --from=build app/target/*.jar ./demo.jar

ENTRYPOINT ["java", "-jar", "demo.jar", "send", "-s", "John", "-r", "Jane", "-a", "100"]