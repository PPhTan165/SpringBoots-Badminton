# Spring Boot Badminton Management

Backend REST API cho he thong quan ly ban hang/san pham cau long. Du an cung cap cac chuc nang xac thuc nguoi dung, quan ly san pham, danh muc, gio hang, don hang, thanh toan, hoa don va cac API quan tri.

## Cong Nghe Su Dung

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Security
- Spring OAuth2 Client
- Spring Data JPA
- PostgreSQL
- JWT voi JJWT
- Maven Wrapper

## Chuc Nang Chinh

- Dang ky, dang nhap bang username/password
- Dang nhap Google OAuth2
- Cap access token bang refresh token
- Dang xuat va xoa refresh token cookie
- Quan ly thong tin nguoi dung hien tai
- Doi mat khau
- Quan ly danh muc san pham
- Quan ly san pham va trang thai san pham
- Quan ly gio hang va cart item
- Tao va xem don hang
- Quan ly thanh toan don hang
- Quan ly hoa don don hang
- API quan tri cho don hang, thanh toan va hoa don

## Cau Truc Du An

```text
src/main/java/com/example/badminton_management
|-- config        # Cau hinh Spring Security, JWT filter, OAuth2 success handler
|-- controller    # REST controller
|-- dto           # Request/response DTO
|-- enums         # Enum trang thai va loai du lieu nghiep vu
|-- exception     # Custom exception va global exception handler
|-- model         # JPA entity
|-- repository    # Spring Data JPA repository
|-- service       # Business logic
```

## Yeu Cau Moi Truong

- JDK 17+
- PostgreSQL
- Maven khong bat buoc cai rieng vi du an co `mvnw.cmd`
- Google OAuth Client neu muon test dang nhap Google

## Cau Hinh

File cau hinh chinh nam tai:

```text
src/main/resources/application.properties
```

Vi du cau hinh local:

```properties
spring.application.name=badminton_management

spring.datasource.url=jdbc:postgresql://localhost:5432/badminton_management
spring.datasource.username=postgres
spring.datasource.password=123456
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,email,profile

api.secret.key=${SECRET_KEY}
```

Khong nen commit `client-secret`, JWT secret hoac password database that len Git. Hay truyen cac gia tri nay qua bien moi truong.

Tren PowerShell co the set bien moi truong tam thoi nhu sau:

```powershell
$env:GOOGLE_CLIENT_ID="your-google-client-id"
$env:GOOGLE_CLIENT_SECRET="your-google-client-secret"
$env:SECRET_KEY="your-jwt-secret"
```

## Cau Hinh Google OAuth2

Trong Google Cloud Console, tao OAuth Client voi application type:

```text
Web application
```

Them Authorized redirect URI:

```text
http://localhost:8080/login/oauth2/code/google
```

Neu ung dung dang o che do Testing, them email cua ban vao danh sach Test users. Neu gap loi `403: org_internal`, can chuyen OAuth consent screen sang `External` hoac dung tai khoan thuoc cung Google Workspace.

URL test dang nhap Google:

```text
http://localhost:8080/oauth2/authorization/google
```

Sau khi dang nhap thanh cong, backend se redirect ve frontend:

```text
http://localhost:3000/oauth2/success?accessToken=...
```

## Chay Du An

Tao database PostgreSQL:

```sql
CREATE DATABASE badminton_management;
```

Compile du an:

```powershell
.\mvnw.cmd -DskipTests compile
```

Chay ung dung:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend mac dinh chay tai:

```text
http://localhost:8080
```

## API Chinh

### Auth

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| POST | `/api/auth/register` | Dang ky tai khoan |
| POST | `/api/auth/login` | Dang nhap username/password |
| POST | `/api/auth/logout` | Dang xuat |
| POST | `/api/auth/refresh-token` | Cap access token moi tu refresh token cookie |
| GET | `/oauth2/authorization/google` | Bat dau Google OAuth2 login |

### User

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/users/me` | Lay thong tin nguoi dung hien tai |
| GET | `/api/users/admin` | API kiem tra quyen admin |
| PATCH | `/api/users/password` | Doi mat khau |

### Categories

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/categories` | Lay danh sach danh muc |
| GET | `/api/categories/{id}` | Lay chi tiet danh muc |
| POST | `/api/categories` | Tao danh muc |
| PUT | `/api/categories/{id}` | Cap nhat danh muc |

### Products

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/products` | Lay danh sach san pham |
| GET | `/api/products/{id}` | Lay chi tiet san pham |
| POST | `/api/products` | Tao san pham |
| PUT | `/api/products/{id}` | Cap nhat san pham |
| PATCH | `/api/products/{id}` | Cap nhat trang thai san pham |

### Cart

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/carts` | Lay gio hang |
| POST | `/api/carts/items` | Them san pham vao gio hang |
| PATCH | `/api/carts/items/{cartItemId}` | Cap nhat so luong item |
| DELETE | `/api/carts/items/{cartItemId}` | Xoa item khoi gio hang |

### Orders

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/orders` | Lay danh sach don hang cua nguoi dung |
| GET | `/api/orders/{orderId}` | Lay chi tiet don hang |
| POST | `/api/orders` | Tao don hang |

### Payments

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/orders/{orderId}/payment` | Lay thanh toan theo don hang |
| GET | `/api/order-payments` | Lay danh sach thanh toan |
| GET | `/api/order-payments/{orderPaymentId}` | Lay chi tiet thanh toan |
| POST | `/api/orders/{orderId}/payment` | Tao thanh toan cho don hang |

### Invoices

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/order-invoices` | Lay danh sach hoa don |
| GET | `/api/order-invoices/{orderInvoiceId}` | Lay chi tiet hoa don |

### Admin

| Method | Endpoint | Mo ta |
| --- | --- | --- |
| GET | `/api/admin/orders` | Lay tat ca don hang |
| GET | `/api/admin/orders/{orderId}` | Lay chi tiet don hang |
| PATCH | `/api/admin/orders/{orderId}/status` | Cap nhat trang thai don hang |
| GET | `/api/admin/order-payments` | Lay tat ca thanh toan |
| PATCH | `/api/admin/order-payments/{orderPaymentId}/status` | Cap nhat trang thai thanh toan |
| GET | `/api/admin/order-invoices` | Lay tat ca hoa don |
| GET | `/api/admin/order-invoices/{orderInvoiceId}` | Lay chi tiet hoa don |
| POST | `/api/admin/order-invoices/{orderPaymentId}/invoice` | Tao hoa don tu thanh toan |

## Bao Mat

- API su dung JWT Bearer token cho cac endpoint can xac thuc.
- Refresh token duoc luu trong HTTP-only cookie ten `refreshToken`.
- Google OAuth2 duoc xu ly qua Spring Security `oauth2Login`.
- Cac endpoint auth va OAuth2 duoc permit public.
- Cac endpoint con lai yeu cau authentication theo cau hinh Spring Security.

Vi du goi API co token:

```http
Authorization: Bearer <access-token>
```

## Kiem Thu Nhanh

Dang ky tai khoan:

```http
POST /api/auth/register
```

Dang nhap:

```http
POST /api/auth/login
```

Test Google OAuth2:

```text
http://localhost:8080/oauth2/authorization/google
```

Compile:

```powershell
.\mvnw.cmd -DskipTests compile
```

