# Youth Talent Fund - Nền tảng Gây quỹ cho Tài năng trẻ

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Infrastructure-Docker%20Compose-blue.svg)](https://www.docker.com/)
[![Spring Security](https://img.shields.io/badge/Security-Spring%20Security%20%26%20JWT-purple.svg)](https://spring.io/projects/spring-security)
[![JPA/Hibernate](https://img.shields.io/badge/ORM-JPA%20%2F%20Hibernate-orange.svg)](https://hibernate.org/)
[![Maven](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)

**Youth Talent Fund** là một dự án API backend được xây dựng bằng Spring Boot, với sứ mệnh tạo ra một nền tảng kết nối các nhà hảo tâm với những tài năng trẻ đang cần nguồn lực để phát triển. Dự án được thiết kế với kiến trúc hiện đại, dễ dàng triển khai và bảo trì nhờ vào việc quản lý các dịch vụ hạ tầng bằng Docker.

## ✨ Tính Năng Cốt Lõi

*   **🔐 Xác thực & Phân quyền an toàn**: Sử dụng Spring Security kết hợp với JSON Web Tokens (JWT) để bảo vệ các API.
*   **📢 Quản lý Chiến dịch Toàn diện**: Cho phép tạo, cập nhật, và theo dõi trạng thái các chiến dịch gây quỹ.
*   **💳 Quyên góp & Báo cáo Minh bạch**: Cung cấp các API để xử lý quyên góp và gửi báo cáo sử dụng quỹ.
*   **🗄️ Lưu trữ File Linh hoạt**: Tích hợp với **MinIO** (một S3-compatible object storage) để lưu trữ các file media như ảnh bìa chiến dịch, avatar người dùng, và bằng chứng báo cáo.
*   **🏗️ Kiến trúc Vững chắc & Dễ mở rộng**: Áp dụng kiến trúc phân lớp rõ ràng (Controllers, Services, Repositories).
*   **🐳 Môi trường Phát triển Nhất quán**: Sử dụng **Docker Compose** để quản lý các dịch vụ nền tảng (MySQL, MinIO), đảm bảo mọi thành viên trong nhóm đều có một môi trường cài đặt giống hệt nhau.
*   **🔧 Xử lý Lỗi Tập trung & Thân thiện**: Hệ thống xử lý ngoại lệ toàn cục (`GlobalExceptionHandler`) cung cấp các thông báo lỗi có cấu trúc.
*   **🚀 Khởi tạo Dữ liệu Tự động**: Tích hợp `DataInitializer` để tự động tạo vai trò và tài khoản admin mặc định.

## 🚀 Hướng Dẫn Cài Đặt và Chạy Dự Án

Dự án này áp dụng phương pháp "Infrastructure as Code" bằng Docker. Bạn chỉ cần khởi chạy các dịch vụ nền tảng bằng một lệnh duy nhất, sau đó chạy ứng dụng Spring Boot như bình thường.

### **Yêu cầu hệ thống**

*   **Git**: Để clone mã nguồn.
*   **JDK 17** hoặc phiên bản mới hơn.
*   **Maven 3.8+**: Để build và quản lý project.
*   **Docker và Docker Compose**: **BẮT BUỘC**. Đây là công cụ để chạy các dịch vụ nền tảng.

### **Các bước cài đặt**

#### **Bước 1: Clone mã nguồn**
```bash
git clone https://your-repository-url/youthtalentfund.git
cd youthtalentfund
```

#### **Bước 2: Cấu hình Biến Môi trường**
Dự án sử dụng file `.env` để quản lý các thông tin nhạy cảm. Hãy tạo một file tên là `.env` ở thư mục gốc của dự án bằng cách sao chép từ file ví dụ:

```bash
# Trên Linux/macOS
cp .env.example .env

# Trên Windows
copy .env.example .env
```

Bây giờ, mở file `.env` và tùy chỉnh các giá trị cho phù hợp với môi trường của bạn.

```dotenv
# .env file

# Cấu hình Database MySQL
DB_PASSWORD=your_strong_mysql_password
DB_PORT=3306

# Cấu hình MinIO Object Storage
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_strong_minio_password
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001```

#### **Bước 3: Khởi chạy Hạ tầng với Docker Compose**
Mở terminal tại thư mục gốc của dự án và chạy lệnh sau:

```bash
docker-compose up -d
```
*   **`docker-compose up`**: Đọc file `docker-compose.yml`, tải các image cần thiết (MySQL, Minio) và tạo các container.
*   **`-d` (detached mode)**: Chạy các container ở chế độ nền.

Lệnh này sẽ khởi tạo:
1.  Một container MySQL đang chạy trên cổng `3306` (hoặc cổng bạn đã định nghĩa trong `.env`).
2.  Một container MinIO đang chạy, với API trên cổng `9000` và giao diện quản lý trên cổng `9001`.

**Kiểm tra các container đang chạy:**
```bash
docker ps
```
Bạn sẽ thấy hai container `fund_mysql_container` và `youthtalentfund_minio_container` đang ở trạng thái `Up`.

#### **Bước 4: Cấu hình Ứng dụng Spring Boot**
Mở file `src/main/resources/application.properties` và đảm bảo nó được cấu hình để kết nối tới các dịch vụ trong Docker. Các giá trị này nên khớp với những gì bạn đã đặt trong file `.env`.

```properties
# --- Cấu hình Cơ sở dữ liệu (Kết nối tới MySQL trong Docker) ---
spring.datasource.url=jdbc:mysql://localhost:3306/youthtalentfund_db
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD} # Spring sẽ tự động đọc biến môi trường này

# --- Cấu hình JPA/Hibernate ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# --- Cấu hình JWT (BẮT BUỘC) ---
jwt.secret=Q2h1b2lCaU1hdFNpZXVEYWlTaWV1QW5Ub2FuQ3VhVG9pMTIzNDU2
jwt.expiration=86400000

# --- Cấu hình Tài khoản Admin Mặc định ---
admin.email=admin@youthtalent.com
admin.password=SecureAdminPassword123
admin.fullname=Quản Trị Viên

# --- Cấu hình MinIO (Kết nối tới MinIO trong Docker) ---
minio.endpoint=http://localhost:9000
minio.access.key=minioadmin
minio.secret.key=${MINIO_ROOT_PASSWORD} # Đọc từ biến môi trường
minio.bucket.name=youthtalent
```
*Lưu ý: Spring Boot có khả năng đọc các biến môi trường trực tiếp, nên `password=${DB_PASSWORD}` sẽ hoạt động nếu bạn đã export biến môi trường. Tuy nhiên, việc tham chiếu trực tiếp trong file properties thường dễ hiểu hơn cho người mới.*

#### **Bước 5: Chạy Ứng dụng Spring Boot**
Bây giờ, hạ tầng đã sẵn sàng. Bạn có thể chạy ứng dụng Java như một dự án Spring Boot thông thường.

**Sử dụng Maven:**
```bash
mvn spring-boot:run
```

**Hoặc chạy từ IDE:** Mở file `YouthtalentfundApplication.java` và chạy nó.

Khi ứng dụng khởi động thành công, API sẽ hoạt động tại `http://localhost:8080`.

### **Quản lý & Kiểm tra Dịch vụ**
*   **Truy cập MinIO Console**: Mở trình duyệt và truy cập `http://localhost:9001`. Đăng nhập với `MINIO_ROOT_USER` và `MINIO_ROOT_PASSWORD` bạn đã cấu hình trong file `.env`.
*   **Kết nối tới MySQL**: Sử dụng một công cụ quản lý CSDL như DBeaver, DataGrip, hoặc MySQL Workbench để kết nối tới `localhost:3306` với user `root` và mật khẩu trong file `.env`.
*   **Dừng các dịch vụ Docker**:
    ```bash
    docker-compose down
    ```

## 🏛️ Kiến Trúc và Luồng Hoạt Động

Dự án được xây dựng trên kiến trúc phân lớp vững chắc, tận dụng các pattern tốt nhất của Spring Boot và được hỗ trợ bởi một môi trường phát triển được đóng gói bằng Docker.

<details>
<summary><strong>Nhấn để xem chi tiết Kiến trúc & Luồng hoạt động</strong></summary>

### **Sơ đồ kiến trúc tổng quan**
```
+-------------+      HTTP Request      +-------------------+      JDBC      +----------------------+
|             |  ------------------>   |                   |  ---------->   |                      |
|   Client    |                        |  Spring Boot App  |                |   MySQL Container    |
| (Postman/FE)|  <------------------   | (Running on Host) |  <----------   |  (Dockerized)        |
|             |      JSON Response     |                   |      Data      |                      |
+-------------+                        +-------------------+                +----------------------+
                                               |
                                               | S3 API Call
                                               |
                                               v
                                     +----------------------+
                                     |                      |
                                     |    MinIO Container   |
                                     |    (Dockerized)      |
                                     |                      |
                                     +----------------------+
```

### **Luồng Hoạt Động Của Một Request (Ví dụ: Đăng ký)**
1.  **Client** gửi `POST` request đến `/api/auth/register` với `RegisterDTO`.
2.  `AuthController` (`controllers`) tiếp nhận request và xác thực (`@Valid`) DTO.
3.  `AuthController` gọi `AuthService` (`services`).
4.  `AuthServiceImpl` thực hiện logic:
    *   Kiểm tra email đã tồn tại chưa bằng cách gọi `UserRepository`.
    *   Mã hóa mật khẩu bằng `PasswordEncoder`.
    *   Tạo `User` entity mới và liên kết với `Role` mặc định.
    *   `UserRepository` (`repositories`) sử dụng Spring Data JPA để lưu `User` entity vào CSDL MySQL đang chạy trong container Docker.
5.  `AuthController` trả về `ResponseEntity` với mã trạng thái `201 CREATED`.
6.  Nếu có bất kỳ lỗi nào, `GlobalExceptionHandler` (`exceptions`) sẽ bắt lại và trả về một response lỗi có cấu trúc.

</details>

<details>
<summary><strong>Nhấn để xem chi tiết Cấu trúc Thư mục & Sơ đồ CSDL</strong></summary>

### **Cấu Trúc Thư Mục**
```
└── youthtalentfund/
    ├── .env                    # (Bạn tự tạo) Chứa các biến môi trường
    ├── .env.example            # File mẫu cho biến môi trường
    ├── docker-compose.yml      # Định nghĩa các dịch vụ MySQL và MinIO
    └── src/
        └── main/
            ├── java/           # Mã nguồn Java
            |    ├── YouthtalentfundApplication.java # Điểm khởi chạy ứng dụng Spring Boot
            |    ├── config/                 # Các lớp cấu hình (Bảo mật, Bean, Khởi tạo dữ liệu ban đầu)
            |    ├── constants/              # Chứa các hằng số dùng chung trong toàn bộ ứng dụng
            |    ├── controllers/            # Tầng API: Tiếp nhận HTTP request và trả về HTTP response
            |    ├── dtos/                   # Data Transfer Objects: Các đối tượng để truyền dữ liệu giữa các lớp
            |    ├── entities/               # Tầng Persistence: Các lớp ánh xạ tới các bảng trong CSDL (JPA Entities)
            |    ├── enums/                  # Các kiểu dữ liệu liệt kê (VD: Trạng thái chiến dịch, Vai trò người dùng)
            |    ├── exceptions/             # Xử lý ngoại lệ tập trung, định nghĩa các lỗi custom
            |    ├── repositories/           # Tầng Data Access: Giao tiếp với CSDL thông qua Spring Data JPA
            |    ├── security/               # Xử lý bảo mật: JWT Filter, JWT Provider
            |    ├── services/               # Tầng Business Logic: Nơi chứa toàn bộ logic nghiệp vụ cốt lõi
            |    └── utils/                  # Các lớp tiện ích (VD: tạo mã ngẫu nhiên)
            └── resources/
                └── application.properties # Cấu hình ứng dụng Spring
```

### **Sơ Đồ Cơ Sở Dữ Liệu (Entities)**
*   **`BaseEntity`**: Lớp cha chứa `id`, `createdAt`, `updatedAt`.
*   **`User`, `Role`, `UserRole`**: Triển khai mối quan hệ nhiều-nhiều giữa người dùng và vai trò.
*   **`Campaign`**: Entity trung tâm của ứng dụng.
*   **`Donation`**: Ghi lại các lần quyên góp.
*   **`ProofReport`**: Các báo cáo minh bạch cho chiến dịch.
*   **`Attachment`**: Entity linh hoạt để lưu trữ metadata của file. Path của file sẽ trỏ đến một object trong bucket của MinIO.

</details>

## 🤝 Đóng Góp
Mọi sự đóng góp để cải thiện dự án đều được hoan nghênh. Vui lòng tạo một `Pull Request` với mô tả chi tiết về những thay đổi của bạn.