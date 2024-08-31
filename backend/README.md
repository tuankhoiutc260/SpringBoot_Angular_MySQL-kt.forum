# Spring Boot Application with Docker - Installation and Running Guide

## 1. System Requirements

- Docker
- Docker Compose

## 2. Project Structure

```
/your-project
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 3. Installation and Running Instructions

### 3.1 Using Docker Compose

1. Open a terminal and navigate to the directory containing the `docker-compose.yml` file.

2. Run the following command to start all services:
   ```
   docker-compose up -d
   ```

   This command will create and run containers for MySQL, Redis, Elasticsearch, and your Spring Boot application.

3. To stop and remove the containers:
   ```
   docker-compose down
   ```

### 3.2 Using Dockerfile (to run Spring Boot application separately)

1. Build Docker image:
   ```
   docker build -t springboot-kt-forum .
   ```

2. Run container:
   ```
   docker run -p 8080:8080 springboot-kt-forum
   ```

## 4. Configuration

### 4.1 Docker Compose

The `docker-compose.yml` file defines the following services:

- MySQL (port 3307)
- Redis (port 6379)
- Elasticsearch (port 9200)
- Spring Boot application (port 8080)

### 4.2 Environment Variables

Important environment variables defined in `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/springboot-ktforum?createDatabaseIfNotExist=true
  SPRING_DATASOURCE_USERNAME: <database_username>
  SPRING_DATASOURCE_PASSWORD: <database_password>
  SPRING_DATA_REDIS_HOST: redis
  SPRING_ELASTICSEARCH_URIS: http://elasticsearch:9200
  SPRING_ELASTICSEARCH_USERNAME: <elasticsearch_username>
  SPRING_ELASTICSEARCH_PASSWORD: <elasticsearch_password>
  CLOUDINARY_APIKEY: <cloudinary_api_key>
  CLOUDINARY_APISECRET: <cloudinary_api_secret>
  CLOUDINARY_CLOUDNAME: <cloudinary_cloud_name>
  JWT_SIGNERKEY: <jwt_signer_key>
```

Replace `<placeholders>` with your actual values.

## 5. Accessing the Application

After the containers are running:

- Spring Boot application: `http://localhost:8080`
- MySQL: localhost:3307
- Redis: localhost:6379
- Elasticsearch: `http://localhost:9200`

## 6. Troubleshooting

- Check container logs:
  ```
  docker-compose logs [service_name]
  ```
- Ensure all required ports are not being used by other applications.
- If experiencing connection issues, verify the environment variables and network configuration in `docker-compose.yml`.

## 7. Security Notes

- Change default passwords in production environment.
- Restrict access to internal service ports (MySQL, Redis, Elasticsearch) from outside.

## 8. Development

For local development, you can use `docker-compose` to run dependent services (MySQL, Redis, Elasticsearch) and run your Spring Boot application directly on the host machine.

---

If you have any questions or encounter any issues, please create an issue in the project's GitHub repository.

---

# Hướng dẫn Cài đặt và Chạy Ứng dụng Spring Boot với Docker

## 1. Yêu cầu Hệ thống

- Docker
- Docker Compose

## 2. Cấu trúc Dự án

```
/your-project
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 3. Hướng Dẫn Cài đặt và Chạy

### 3.1 Sử dụng Docker Compose

1. Mở terminal và di chuyển đến thư mục chứa file `docker-compose.yml`.

2. Chạy lệnh sau để khởi động tất cả các services:
   ```
   docker-compose up -d
   ```

   Lệnh này sẽ tạo và chạy các containers cho MySQL, Redis, Elasticsearch, và ứng dụng Spring Boot của bạn.

3. Để dừng và xóa các containers:
   ```
   docker-compose down
   ```

### 3.2 Sử dụng Dockerfile (nếu muốn chạy riêng ứng dụng Spring Boot)

1. Build Docker image:
   ```
   docker build -t springboot-kt-forum .
   ```

2. Chạy container:
   ```
   docker run -p 8080:8080 springboot-kt-forum
   ```

## 4. Cấu hình

### 4.1 Docker Compose

File `docker-compose.yml` định nghĩa các services sau:

- MySQL (cổng 3307)
- Redis (cổng 6379)
- Elasticsearch (cổng 9200)
- Ứng dụng Spring Boot (cổng 8080)

### 4.2 Biến Môi trường

Các biến môi trường quan trọng được định nghĩa trong `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/springboot-ktforum?createDatabaseIfNotExist=true
  SPRING_DATASOURCE_USERNAME: <database_username>
  SPRING_DATASOURCE_PASSWORD: <database_password>
  SPRING_DATA_REDIS_HOST: redis
  SPRING_ELASTICSEARCH_URIS: http://elasticsearch:9200
  SPRING_ELASTICSEARCH_USERNAME: <elasticsearch_username>
  SPRING_ELASTICSEARCH_PASSWORD: <elasticsearch_password>
  CLOUDINARY_APIKEY: <cloudinary_api_key>
  CLOUDINARY_APISECRET: <cloudinary_api_secret>
  CLOUDINARY_CLOUDNAME: <cloudinary_cloud_name>
  JWT_SIGNERKEY: <jwt_signer_key>
```

Thay thế `<placeholders>` bằng giá trị thực tế của bạn.

## 5. Truy cập Ứng dụng

Sau khi các containers đã chạy:

- Ứng dụng Spring Boot: `http://localhost:8080`
- MySQL: localhost:3307
- Redis: localhost:6379
- Elasticsearch: `http://localhost:9200`

## 6. Xử lý Sự cố

- Kiểm tra logs của các containers:
  ```
  docker-compose logs [service_name]
  ```
- Đảm bảo rằng tất cả các ports cần thiết không bị sử dụng bởi các ứng dụng khác.
- Nếu gặp vấn đề kết nối, hãy kiểm tra các biến môi trường và cấu hình mạng trong `docker-compose.yml`.

## 7. Lưu ý Bảo mật

- Thay đổi các mật khẩu mặc định trong môi trường production.
- Hạn chế truy cập vào các ports của services nội bộ (MySQL, Redis, Elasticsearch) từ bên ngoài.

## 8. Phát triển

Để phát triển locally, bạn có thể sử dụng `docker-compose` để chạy các services phụ thuộc (MySQL, Redis, Elasticsearch) và chạy ứng dụng Spring Boot của bạn trực tiếp trên máy host.

---

Nếu bạn có bất kỳ câu hỏi hoặc gặp vấn đề nào, vui lòng tạo một issue trong repository GitHub của dự án.