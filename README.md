# Course Registration System

A JavaFX desktop application for managing university course registrations with Role-Based Access Control (RBAC).

## Tech Stack
- Java 17+
- JavaFX 21
- Maven 3.9+
- MySQL 8.0

## Features

### 🔐 Role-Based Access Control (RBAC)
Three distinct user roles with unified authentication:

#### 1. **Administrator**
- Full system access
- Approve/reject student registrations
- Manage all courses, instructors, and students
- View system-wide reports and analytics

#### 2. **Department Head**
- Manage courses in their department
- Manage instructors in their department
- View students and enrollments (read-only)
- Generate department reports

#### 3. **Student**
- Browse and enroll in courses
- View personal enrollment history
- Manage profile
- Requires admin approval before access

### 📊 Core Features
- **Unified Login System** - Single login page for all user types
- **Dynamic Dashboard Routing** - Automatic role-based navigation
- **Student Approval Workflow** - Admin approval required for new students
- **Course Management** - Full CRUD operations for courses
- **Instructor Management** - Manage faculty and assignments
- **Enrollment Tracking** - Monitor student registrations
- **Reports & Analytics** - System-wide and department-specific reports
- **Modern UI/UX** - Consistent design with role badges

## Setup

### 1. Clone the repository
```bash
git clone <repo-url>
cd course-registration-system
```

### 2. Create the MySQL database
```sql
CREATE DATABASE course_registration_db;
```

### 3. Configure database credentials
Copy the template and fill in your password:
```bash
cp src/main/resources/db.properties.template src/main/resources/db.properties
```
Edit `db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/course_registration_db
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### 4. Run the application
```bash
mvn clean javafx:run
```

The application will automatically:
- Initialize the database schema
- Create required tables
- Set up the default admin account

## Quick Start

### Default Admin Login
```
Username: admin
Password: admin123
```

### Create Test Users
Run the seed script to create test accounts for all roles:
```bash
mysql -u root -p course_registration_db < docs/seed_test_users.sql
```

**Test Credentials:**
- **Admin:** `admin` / `admin123`
- **Department Head:** `cs_head` / `password123`
- **Student:** `john_doe` / `student123`

See [Quick Start Guide](docs/QUICK_START_RBAC.md) for detailed testing instructions.

## Documentation

- **[RBAC Implementation Guide](docs/RBAC_IMPLEMENTATION.md)** - Complete RBAC documentation
- **[Quick Start Guide](docs/QUICK_START_RBAC.md)** - Step-by-step testing guide
- **[Test Users Seed Script](docs/seed_test_users.sql)** - SQL script for test accounts

## Project Structure
```
src/main/java/com/university/crs/
├── App.java                  # JavaFX entry point
├── Main.java                 # Application launcher
├── db/                       # Database connection & initializer
│   ├── DatabaseConnection.java
│   └── DatabaseInitializer.java
├── model/                    # Data models
│   ├── User.java            # User with role methods
│   ├── Student.java
│   ├── Course.java
│   ├── Instructor.java
│   └── Admin.java
├── dao/                      # Database access objects
│   ├── UserDao.java         # Authentication & user management
│   ├── StudentDao.java
│   ├── CourseDao.java
│   ├── InstructorDao.java
│   └── EnrollmentDao.java
└── gui/                      # JavaFX UI components
    ├── LoginScreen.java              # Unified login
    ├── CreateAccountScreen.java      # Student registration
    ├── AdminDashboard.java           # Admin interface
    ├── DepartmentHeadDashboard.java  # Department head interface
    ├── StudentPortal.java            # Student interface
    ├── StudentApprovalsPage.java     # Approval management
    ├── CoursesPage.java
    ├── InstructorsPage.java
    ├── StudentsPage.java
    ├── EnrollmentsPage.java
    ├── ReportsPage.java
    ├── ProfilePage.java
    ├── OverviewPage.java
    ├── ColorScheme.java              # Color constants
    ├── FontLoader.java               # Font management
    ├── StyleConstants.java           # Style utilities
    └── StyleUtil.java
```

## User Roles & Permissions

| Feature | Admin | Department Head | Student |
|---------|-------|-----------------|---------|
| View Dashboard | ✓ | ✓ | ✓ |
| Approve Students | ✓ | ✗ | ✗ |
| Manage All Courses | ✓ | ✗ | ✗ |
| Manage Dept Courses | ✓ | ✓ | ✗ |
| Manage All Instructors | ✓ | ✗ | ✗ |
| Manage Dept Instructors | ✓ | ✓ | ✗ |
| View All Students | ✓ | ✓ (read-only) | ✗ |
| Manage Students | ✓ | ✗ | ✗ |
| View Enrollments | ✓ | ✓ (read-only) | ✓ (own only) |
| Enroll in Courses | ✗ | ✗ | ✓ |
| Generate Reports | ✓ | ✓ | ✗ |

## Security Notes

⚠️ **Current Implementation:**
- Passwords are stored in **plain text** (for development only)
- Basic role-based UI rendering
- Student approval workflow

⚠️ **Production Requirements:**
- Implement password hashing (BCrypt recommended)
- Add session management with tokens
- Implement server-side permission checks
- Add audit logging
- Enable HTTPS/TLS
- Implement account lockout policies

## Development

### Build
```bash
mvn clean install
```

### Run
```bash
mvn javafx:run
```

### Package
```bash
mvn clean package
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or contributions, please open an issue on GitHub.
