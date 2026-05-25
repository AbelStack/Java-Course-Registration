# Course Registration System

A JavaFX desktop application for managing university course registrations.

## Tech Stack
- Java 17+
- JavaFX 21
- Maven 3.9+
- MySQL 8.0

## Setup

### 1. Clone the repository
```bash
git clone <repo-url>
cd course-reg-system
```

### 2. Create the MySQL database
```sql
CREATE DATABASE course_reg_db;
```

### 3. Configure database credentials
Copy the template and fill in your password:
```bash
cp src/main/resources/db.properties.template src/main/resources/db.properties
```
Edit `db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/javapro
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### 4. Run the application
```bash
mvn javafx:run
```

## Default Admin Login
- Username: `admin`
- Password: `admin123`

## Project Structure
```
src/main/java/com/university/crs/
├── App.java                  # JavaFX entry point
├── db/                       # Database connection & initializer
├── model/                    # Data models (User, Student, Course)
├── dao/                      # Database access objects
├── gui/                      # JavaFX screens and pages
└── ui/                       # Console UI (legacy)
```

## Features
- Admin login & account creation
- Admin dashboard with sidebar navigation
- Student management (add, edit, delete)
- Course management (add, edit, delete)
- Enrollment management (enroll, drop, view)
- Reports (enrollment summary, totals)
- Student portal (in progress)
