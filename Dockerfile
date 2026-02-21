# 1 Build the JAR...
FROM eclipse-temurin:17-alpine AS build

# 1.1
# Set the Spring application working directory (inside the container)
WORKDIR /app

# 1.2.1
# The state of the following files WILL be used
# to check if the dependencies must be updated (re-downloaded)
COPY mvnw pom.xml .
COPY .mvn .mvn

# 1.2.2
# Update the dependencies (if needed)
# The "go-offline" flag will download every dependency possible (plugins too)
RUN ./mvnw dependency:go-offline

# 1.2.3
# The state of the following files WILL NOT be used
# to check if the dependencies must be updated (re-downloaded)
COPY src ./src

# 1.3
# Build the JAR (the optional "-o"" explicitly specifies "offline")
RUN ./mvnw clean package -o

# 2
# Start the JAR...
FROM eclipse-temurin:17-alpine

WORKDIR /app
COPY --from=build app/target/*.jar ./demo.jar

# Everything after the "*.jar" here is passed to the Spring Shell
ENTRYPOINT ["java", "-jar", "demo.jar", "send", "-s", "John", "-r", "Jane", "-a", "100"]