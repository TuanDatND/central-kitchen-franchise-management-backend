# Giai đoạn 1: Build file JAR bằng Maven
FROM maven:3.9.6-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng với JDK 25
FROM openjdk:25-slim
WORKDIR /app
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]