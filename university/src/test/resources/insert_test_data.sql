-- Generate test data for university.admin
INSERT INTO university.admin (id, first_name, last_name, active, email, password)
VALUES
    (1, 'John', 'Doe', true, 'john.doe@example.com', 'password1'),
    (2, 'Jane', 'Smith', true, 'jane.smith@example.com', 'password2'),
    (3, 'Michael', 'Johnson', true, 'michael.johnson@example.com', 'password3');

-- Generate test data for university.classroom
INSERT INTO university.classroom (classroom_id, build_no, room_no, street)
VALUES
    (1, 1, 101, 'Main Street'),
    (2, 2, 202, 'Oak Avenue'),
    (3, 3, 303, 'Elm Road');

-- Generate test data for university.courses
INSERT INTO university.courses (course_id, course_name, course_description)
VALUES
    (1, 'Mathematics', 'Advanced mathematics course'),
    (2, 'Physics', 'Introduction to physics'),
    (3, 'Chemistry', 'Chemical principles and reactions');

-- Generate test data for university.faculties
INSERT INTO university.faculties (faculty_id, faculty_name)
VALUES
    (1, 'Faculty of Science'),
    (2, 'Faculty of Engineering'),
    (3, 'Faculty of Arts');

-- Generate test data for university.departments
INSERT INTO university.departments (department_id, name, faculty_id)
VALUES
    (1, 'Mathematics Department', 1),
    (2, 'Physics Department', 2),
    (3, 'Department of Human Resources', 3);

-- Generate test data for university.groups
INSERT INTO university.groups (group_id, group_name, faculty_id)
VALUES
    (1, 'Group A', 1),
    (2, 'Group B', 2),
    (3, 'Group C', 3);

-- Generate test data for university.staff
INSERT INTO university.staff (id, first_name, last_name, active, email, password, position)
VALUES
    (1, 'Mark', 'Wilson', true, 'mark.wilson@example.com', 'password4', 'Utility'),
    (2, 'Sarah', 'Taylor', true, 'sarah.taylor@example.com', 'password5', 'Security'),
    (3, 'David', 'Anderson', true, 'david.anderson@example.com', 'password6', 'Assistant Teacher');

-- Generate test data for university.students
INSERT INTO university.students (id, first_name, last_name, active, email, password, group_id)
VALUES
    (1, 'Emily', 'Brown', true, 'emily.brown@example.com', 'password7', 1),
    (2, 'Daniel', 'Davis', true, 'daniel.davis@example.com', 'password8', 2),
    (3, 'Olivia', 'Johnson', true, 'olivia.johnson@example.com', 'password9', 3);

-- Generate test data for university.teachers
INSERT INTO university.teachers (id, first_name, last_name, active, email, password, course_id, department_id)
VALUES
    (1, 'Jennifer', 'Smith', true, 'jennifer.smith@example.com', 'password10', 1, 1),
    (2, 'James', 'Wilson', true, 'james.wilson@example.com', 'password11', 2, 2),
    (3, 'Sophia', 'Anderson', true, 'sophia.anderson@example.com', 'password12', 3, 3);

INSERT INTO university.timetable (timetable_id, date, time_start, time_end, teacher_id, student_id, course_id, group_id, classroom_id)
VALUES 
    (1, '2023-06-12', '08:00', '10:00', 1, 1, 1, 1, 3),
    (2, '2023-06-12', '10:30', '12:00', 2, 2, 2, 2, 2),
    (3, '2023-06-13', '12:00', '14:00', 3, 3, 3, 3, 1);
