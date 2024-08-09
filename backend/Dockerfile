# Stage 1: Build the application
FROM maven:3.9.8-amazoncorretto-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and the source code
COPY pom.xml .
COPY src ./src

# Build the source code with Maven
RUN mvn package -DskipTests

# Stage 2: Create the final image
FROM amazoncorretto:21.0.4

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the resources folder (if needed)
COPY --from=build /app/src/main/resources /app/src/main/resources

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

