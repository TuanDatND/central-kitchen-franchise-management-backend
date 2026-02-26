# Giai đoạn 1: Build file JAR bằng Maven (Dùng JDK 21 ổn định để build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng (Dùng JDK 25 bản chính thức của Temurin)
# Lưu ý: Nếu 25 vẫn báo lỗi 'not found', hãy tạm dùng 23 hoặc 21 để nộp bài vì chúng rất ổn định.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]