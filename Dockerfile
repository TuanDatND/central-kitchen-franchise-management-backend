# Giai đoạn 1: Build ứng dụng
# Sử dụng JDK 25 của Eclipse Temurin (Ubuntu-based) để có trình biên dịch mới nhất
FROM eclipse-temurin:25-jdk AS build

# Cài đặt Maven thủ công vì Image Maven chính thức có thể chưa cập nhật JDK 25
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .

# Biên dịch dự án
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng
# Sử dụng JRE 25 để tối ưu dung lượng (nhẹ và nhanh hơn bản JDK full)
FROM eclipse-temurin:25-jdk
WORKDIR /app

# Copy file jar đã build từ giai đoạn 1
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
# Giới hạn RAM cho Java: Khởi động với 150MB, tối đa chỉ được dùng 300MB
# Tránh việc vượt quá 512MB của Render gói Free
ENTRYPOINT ["java", "-Xmx300m", "-Xms150m", "-jar", "app.jar"]