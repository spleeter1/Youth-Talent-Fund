# Giai đoạn lấy image nền của java để build. Đặt tên cho giai đoạn này là build viết code là AS build
FROM maven:3.9.6-eclipse-temurin-21 AS build

# tạo thư mục tên là build viết là /build
#Tất cả các lệnh sau dòng này sẽ làm việc với thư mục /build
WORKDIR /build
COPY pom.xml .

# cài dependency
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests
# Build xong ứng dụng thành .jar trong /build/target
# Giai đoạn 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /build/target/youthtalentfund-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]




