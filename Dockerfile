FROM eclipse-temurin:17-alpine AS build

WORKDIR /app
COPY mvnw pom.xml .
COPY src ./src
COPY .mvn .mvn

RUN ./mvnw dependency:resolve
RUN ./mvnw clean package

FROM eclipse-temurin:17-alpine

WORKDIR /app
COPY --from=build app/target/*.jar ./demo.jar

ENTRYPOINT ["java", "-jar", "demo.jar", "send", "-s", "John", "-r", "Jane", "-a", "100"]