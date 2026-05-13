
# Online Sales Ticket

A full-stack web application and mobile that allows users to browse events or movie screenings, select seats in real-time, and purchase tickets through secure online payments.
The system includes both user and admin roles, supports JWT-based authentication, and integrates PayOS for seamless payment processing. Admins can manage movies, showtimes, theaters, orders, and monitor sales performance through a centralized dashboard.

## Key Features
 Authentication & Authorization
JWT-based login/register system with role-based access (Admin & User)

### Movie / Event Management
Admins can create, update, and delete movies, events, showtimes, and screening rooms

### Online Ticket Booking
Users can view showtimes, select available seats in real-time, and book tickets

### Payment Integration
Secure online payments via ZaloPay

### Email Notifications
Automatic booking confirmation emails sent to users

### Admin Dashboard
Real-time statistics on sales, ticket revenue, and event performance


## Installation & Setup


#### Frontend (React + Vite)
```bash
npm install 
npm run dev
```
#### Backend (Spring Boot)
1. System Requirements
Java 17 or higher

Maven 

MySQL 

2. Database Configuration
Create a new database in MySQL:    
```bash
CREATE DATABASE ticket_sales;
```
Update your application.yml with your database
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/ticket_sales
spring.datasource.username=root
spring.datasource.password=your_password
server.port=8080
```
## Quick Test
Frontend: http://localhost:5173

Backend (API): http://localhost:8080/api
## Screenshots

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)


## Tech Stack

**Client:**
 React (Vite), Axios, Animate.css, Boostrap.css 

**Server:** 
- Java 17
- Spring Boot (Spring Web, Spring Security, JPA)
- MySQL
- PayOS / VNPay integration
- JWT Authentication
- Email Service (JavaMailSender)
**Tools:**
- Postman (for API testing)
- Docker (optional)




## API Reference

#### Get all items

### 🔐 Authentication & Account

| Method | Endpoint               | Description                              | Role Access |
|--------|------------------------|------------------------------------------|-------------|
| `POST` | `/api/auth/register`   | Register a new account                   | Guest       |
| `POST` | `/api/auth/login`      | Log in with email and password           | Guest       |
| `GET`  | `/api/account/profile` | Get current user's account information   | User/Admin  |
| `PUT`  | `/api/account/profile` | Update personal information              | User/Admin  |
| `PUT`  | `/api/account/password`| Change password                          | User/Admin  |
| `GET`  | `/api/account`         | Get a paginated list of all accounts     | Admin       |
| `GET`  | `/api/account/{id}`    | Get account details by ID                | Admin       |
| `POST` | `/api/account`         | 	Create a new account (typically for staff users)| Admin       |
| `PUT`  | `/api/account/{id}`    | 	Update account information              | Admin       |
| `DELETE`| `/api/account/{id}`   | Delete an account                        | Admin       |
| `PUT`  | `/api/account/role/{id}`| Update account role                     | Admin       |

### Movie & Category
| Method | Endpoint                         | Description                     |
|--------|----------------------------------|---------------------------------|
| GET    | `/api/movies`                    | Get all movies                  |
| GET    | `/api/movies/{id}`               | Get a movie by ID               |
| POST   | `/api/movies`                    | Create a new movie              |
| PUT    | `/api/movies/{id}`               | Update a movie                  |
| DELETE | `/api/movies/{id}`               | Delete a movie                  |
| POST   | `/api/movies/upload`             | Import movies from Excel file   |
| GET    | `/api/movies/search`             | Search movies by keyword        |
| POST   | `/api/movies/{movieId}/category/{categoryId}` | Assign category to movie |
| GET    | `/api/category`                  | Get all categories              |
| POST   | `/api/category`                  | Create a new category           |
| PUT    | `/api/category/{id}`             | Update a category               |
| DELETE | `/api/category/{id}`             | Delete a category               |

### Showtime
| Method | Endpoint                  | Description                                   | Role Access |
|--------|---------------------------|-----------------------------------------------|-------------|
| `GET`  | `/api/showtimes`          | Get all showtimes (with pagination)           | All         |
| `GET`  | `/api/showtimes/{id}`     | Get showtime details by ID                    | All         |
| `POST` | `/api/showtimes`          | Create a new showtime                         | Admin       |
| `PUT`  | `/api/showtimes/{id}`     | Update a showtime                             | Admin       |
| `DELETE`| `/api/showtimes/{id}`    | Delete a showtime                             | Admin       |

### Booking & Process Payment
| Method  | Endpoint                                      | Description                                                              | Role Access |
|---------|-----------------------------------------------|--------------------------------------------------------------------------|-------------|
| `POST`  | `/api/bookings`                               | Create a new booking                                                     | User        |
| `POST`  | `/api/bookings/{bookingId}/payment-link`      | Generate PayOS payment link for the booking                              | User        |
| `POST`  | `/api/bookings/webhook/payos`                 | Zalopay payment webhook callback                                         | Zalopay     |
| `POST`  | `/api/bookings/hold`                          | Temporarily hold seats before payment                                    | User        |
| `DELETE`| `/api/bookings/hold`                          | Release held seats                                                       | User        |
| `PUT`   | `/api/bookings/{bookingId}/confirm`           | Confirm a booking after successful payment                               | User/Admin  |
| `PUT`   | `/api/bookings/{bookingId}/cancel`            | Cancel a booking                                                         | User/Admin  |
| `GET`   | `/api/bookings/{bookingId}`                   | Get booking details by ID                                                | User/Admin  |
| `GET`   | `/api/bookings/history/{userId}`              | Get all booking history of a user                                        | User/Admin  |
## 🎥 Demo Video

