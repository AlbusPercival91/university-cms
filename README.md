# university-cms

## The application is built using the following technologies:

- Java 17
- Spring Boot
- Spring Security 
- Spring Data JPA
- Flyway migration
- Tomcat 13
- PostgreSQL
- HTML, CSS, JS with Thymeleaf templates
- Lombok
- JUnit 5
- DataJpaTest
- WebMvcTest
- Mockito
- Testcontainers

## Required components for launching:
- Java 17
- PostgreSQL
- Maven


## Installation
- Download the source code
- Import as Maven Project to your IDE
- Change username and password for pgAdmin in class UniversityApplication, method initializeDatabase():
```java
@SpringBootApplication
public class UniversityApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        initializeDatabase();
        SpringApplication.run(UniversityApplication.class, args);
    }

    private static void initializeDatabase() {
        String initialScriptFileName = "initDatabase.sql";

        try (InputStream is = UniversityApplication.class.getResourceAsStream("/" + initialScriptFileName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] initialScriptBytes = baos.toByteArray();
            File initialScriptTempFile = File.createTempFile(initialScriptFileName, ".tmp");
            initialScriptTempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(initialScriptTempFile)) {
                out.write(initialScriptBytes);
            }

            String cmdQuery = String.format("psql -U postgres -h localhost -p 5432 -f %s",
                    initialScriptTempFile.getAbsolutePath());
            String[] envVars = { "PGPASSWORD=1234" };
            Process runInitScript = Runtime.getRuntime().exec(cmdQuery, envVars);
            runInitScript.waitFor();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
``` 
- Run in IDE or package as executable .jar using mvn package
- Visit http://localhost:8080

## Authorization, use one of the following credentials: 
- Admin: login=admin@example.com password=1234
- Teacher: teacher1@example.com password=1234
- Student: student1@example.com password=1234
- Staff: staff1@example.com password=1234


## User Stories

### Admin User Stories

- As an admin, they should be able to create, update, and delete users (admin, teacher, student, staff) in the system.
- As an admin, they should have CRUD (Create, Read, Update, Delete) operations for managing courses, groups, faculties, departments, classrooms.
- As an admin, they should have CRUD (Create, Read, Update, Delete) operations for managing timetable schedule.
- As an admin, they should be able to view and edit the timetable of a particular teacher, all teachers, a particular student, a particular group, and all groups.
- As an admin, they should be able to assign or reassign student and teacher to/from their courses.
- As an admin, they should have access to view and edit their personal data.
- As an admin, they should be able to receive related alerts and send alert messages to particular teacher, particular student, particular staff, particular group, particular department, particular faculty, all teachers and students that is related to particular course, particular admin. 

### Teacher User Stories
- As a teacher, they should be able to view their own timetable.
- As a teacher, they should be able to view the timetable of a particular teacher, all teachers, a particular group, and all groups.
- As a teacher, they should be able to view the timetable of a particular student.
- As a teacher, they should have access to view all departments, faculties, teachers under departments, all groups, students in groups, courses and all staff data.
- As a teacher, they should have access to view and edit their personal data.
- As a teacher, they should be able to receive related alerts and send alert messages to particular teacher, particular student, particular staff, particular group, all teachers and students that is related to particular course.

### Student User Stories

- As a student, they should be able to view their own timetable.
- As a student, they should be able to view the timetable of their group, a particular group, and all groups.
- As a student, they should have access to view all departments, faculties, teachers under departments, all groups, students in groups, courses and staff.
- As a student, they should have access to view and edit their personal data.
- As a student, they should be able to receive related alerts and send alert messages to particular group.

### Staff User Stories

- As a staff, they should have CRUD (Create, Read, Update, Delete) operations for managing timetable schedule.
- As a staff, they should be able to view and edit the timetable of a particular teacher, all teachers, a particular student, a particular group, and all groups.
- As a staff, they should be able to assign or reassign student and teacher to/from their courses.
- As a staff, they should have access to view all departments, faculties, teachers under departments, all groups, students in groups, and all staff data.
- As a staff, they should have access to view and edit their personal data.
- As a staff, they should be able to receive related alerts and send alert messages to particular teacher, particular student, particular staff, particular group, particular department, particular faculty, all teachers and students that is related to particular course, particular admin.
