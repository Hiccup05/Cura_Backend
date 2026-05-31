Cura — Clinic Management System
Cura is a full-stack clinic management platform built with Spring Boot and React. It handles the complete workflow of a clinic — from doctor scheduling and patient booking to real-time payment processing and automated notifications — across four distinct user roles: Admin, Doctor, Patient, and Receptionist.
This is a long-term project I'm actively developing. I use it as a learning ground — each new concept (Redis caching, Kafka, multi-tenancy, microservices) gets implemented here on a real domain with real complexity.

Tech stack
Backend

Java 21, Spring Boot 4.0
Spring Security — JWT (cookie-based) + Google OAuth2
Spring Data JPA + PostgreSQL
Spring WebFlux (WebClient for external API calls)
Spring Mail (JavaMailSender + Gmail SMTP)
Spring Validation, Lombok, ModelMapper
Khalti payment gateway integration
Cloudinary (image uploads)

Frontend

React + TypeScript
Ant Design, React Router, Axios


Architecture overview
Cura has four roles, each with its own access scope:
RoleCan doAdminManage doctors, receptionists, services, specializations. View stats and revenue.DoctorView own schedule, manage appointments, write and update prescriptions.PatientBook appointments, pay online via Khalti, cancel within the 5-hour window, view history.ReceptionistBook walk-in appointments, manage all clinic appointments, process in-person payments.
Authentication uses JWT stored in an HTTP-only cookie. Google OAuth2 is supported for patients. Admins use username/password. All role-based routing is enforced at the Spring Security layer.
com.hiccup.Cura
 ├── controller        # AuthController, AppointmentController, PaymentController, ...
 ├── service           # Business logic per domain
 ├── repository        # Spring Data JPA repositories
 ├── model             # JPA entities (User, DoctorProfile, Appointment, Payment, ...)
 ├── dto               # Request/Response DTOs
 ├── security          # JWT filter, OAuth2 config, SecurityConfig
 ├── exception         # GlobalExceptionHandler, custom exceptions
 └── scheduler         # @Scheduled jobs (auto-complete appointments)

Key features
Authentication & authorization

JWT cookie-based stateless auth
Google OAuth2 login (patients)
Admin login via username/password (secret endpoint)
@AuthenticationPrincipal with CustomUser
Role-based endpoint protection: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT, ROLE_RECEPTIONIST

Doctor management

Full CRUD by admin
DoctorProfile with @MapsId on User (shared primary key)
Status: ACTIVE, INACTIVE, ON_LEAVE, PENDING
Specializations (ManyToMany)
Schedule management: day of week, time slots, max appointments, toggle availability
Leave management: leave dates used in appointment validation

Appointment booking
Appointment creation runs 10 validations before confirming a slot:

Specialization match between doctor and service
Slot availability check
Doctor leave check
Max appointments per slot check
Past date/time check
(and more)

Two booking flows:

Patient flow: status PENDING, isPaid=false, online payment required
Receptionist flow: status CONFIRMED, isPaid=true, walk-in patient name/phone

Cancellation enforces a 5-hour window rule. On cancel: prescription is deleted, cancellation email is sent.
Prescription

One prescription per appointment (@MapsId on Appointment)
Created with the appointment, deleted on cancellation
Doctor can update description — ownership validated

Payment (Khalti)

Initiate: POST /payment/{appointmentId} — returns Khalti payment URL
Verify: GET /payment/verify?pidx=... (public) — backend verifies with Khalti, then redirects to frontend
On success: sets PaymentStatus.COMPLETE, AppointmentStatus.CONFIRMED, isPaid=true, sends confirmation email
Duplicate payment check
syncPendingPaymentStatus scheduler checks expired pending payments against Khalti lookup

Email notifications
JavaMailSender with Gmail SMTP. All email calls are wrapped in try-catch so failures never crash the main flow.
Templates sent for:

Welcome / registration
Doctor promotion
Receptionist promotion
Appointment confirmation
Appointment cancellation
Payment success

Scheduler
@Scheduled(fixedRate = 60000) — automatically marks CONFIRMED appointments as COMPLETED once their slot time has passed.
Admin dashboard stats
GET /admin/stats returns: totalDoctors, totalPatients, totalAppointments, pendingDoctorApprovals, totalRevenue.

API overview
All endpoints are prefixed with /api/v1.
AreaEndpointsAuthPOST /auth/login, POST /auth/logout, GET /auth/me, OAuth2DoctorGET/POST/PUT/DELETE /admin/doctors/**ScheduleGET/POST/PUT/DELETE /admin/doctors/{id}/schedules/**LeaveGET/POST/DELETE /admin/doctors/{id}/leaves/**PatientGET/PUT /patient/**, GET/DELETE /admin/patients/**ReceptionistGET/POST/PUT/DELETE /admin/receptionists/**AppointmentPOST /appointment, GET /appointment/**, DELETE /appointment/{id}PrescriptionPATCH /appointment/prescription/{id}PaymentPOST /payment/{appointmentId}, GET /payment/verifyServicesGET/POST/PUT/DELETE /admin/services/**, GET /public/services/**SpecializationsGET/POST/DELETE /admin/specialization/**, GET /public/specializationAdminGET /admin/profile, GET /admin/stats
Full interactive API docs available via Swagger UI at /swagger-ui.html when running locally.

Running locally
Prerequisites

Java 21
PostgreSQL
Redis (optional for now, required in future)
A Khalti developer account (for payment features)
A Cloudinary account (for image uploads)
Gmail app password (for email)

Setup
1. Clone the repo
bashgit clone https://github.com/Hiccup05/Cura_Backend.git
cd Cura_Backend
2. Create the database
sqlCREATE DATABASE cura;
3. Configure application.properties
properties# Server
server.port=8080
api.prefix=/api/v1

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/cura
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=your_google_client_id
spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Khalti
khalti.secret.key=your_khalti_secret_key
khalti.verify.url=https://khalti.com/api/v2/payment/verify/

# Cloudinary
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret

# Admin (secret login)
admin.username=your_admin_username
admin.password=your_admin_password

Never commit your actual credentials. Use environment variables in production.

4. Run
bash./mvnw spring-boot:run
The API will be available at http://localhost:8080/api/v1.
Swagger UI at http://localhost:8080/swagger-ui.html.

Security design
Request
  └── JwtAuthFilter (extracts + validates JWT from cookie)
      └── SecurityConfig
            ├── /auth/**              → permitAll
            ├── /public/**            → permitAll
            ├── /api/v1/payment/verify → permitAll
            ├── /admin/**             → ROLE_ADMIN
            ├── /doctor/**            → ROLE_DOCTOR
            ├── /patient/**           → ROLE_PATIENT
            └── /appointment/**       → ROLE_PATIENT or ROLE_RECEPTIONIST
CORS is configured to allow localhost:3000 and the ngrok frontend URL during development.

Frontend
The React + TypeScript frontend is in a separate repository: Cura_Frontend (link coming soon)
Built with Ant Design, React Router, and Axios. Features role-based dashboards for all four user types, a 4-step appointment booking flow for patients, a 5-step walk-in booking flow for receptionists, and Khalti payment integration.

Roadmap
This project is under active development. Planned next:

 Redis caching for public doctor/service endpoints
 Pagination and search across all listing endpoints
 Spring ApplicationEventPublisher to decouple email side effects
 @Async threading for notifications
 Kafka for event-driven email and notification processing
 Docker + Docker Compose (app + PostgreSQL + Redis)
 Deployment to a live server
 Architecture Decision Records (ADR) documenting design choices
 Multi-vendor payment support (eSewa alongside Khalti)
 Multi-tenancy (multiple clinics on one Cura instance)
 Microservices migration


Design decisions worth noting
@MapsId for profile entities — DoctorProfile, PatientProfile, and ReceptionistProfile all use @MapsId on the User relationship instead of a separate foreign key column. This means each profile shares its primary key with the User entity — a cleaner one-to-one mapping with no extra join column.
10-step appointment validation — appointment creation runs through a chain of checks before confirming. This was deliberately kept in the service layer rather than database constraints, so each failure returns a meaningful error message to the client.
Email wrapped in try-catch — all email sending is wrapped so a failed email never rolls back or crashes the main appointment/payment transaction. This is a temporary pattern; the goal is to move email to an async event system (Spring Events → Kafka) so the main flow doesn't depend on it at all.
@JsonProperty("isPaid") on Boolean DTOs — Jackson strips the is prefix from Boolean getters by default. Using @JsonProperty("isPaid") ensures the field serializes with the correct name in the API response.

About
Built by Bipin Paudel — software engineering graduate from Nepal, actively looking for backend / full-stack opportunities.

LinkedIn: linkedin.com/in/beepin-paudel
LeetCode: leetcode.com/u/GfJOGeDWry
Email: paudelbipin05@gmail.com
