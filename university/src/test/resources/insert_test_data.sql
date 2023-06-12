-- Generate test data for university.admin
INSERT INTO university.admin (first_name, last_name, active, email, password)
VALUES
    ('John', 'Doe', true, 'john.doe@example.com', 'password1'),
    ('Jane', 'Smith', true, 'jane.smith@example.com', 'password2'),
    ('Michael', 'Johnson', true, 'michael.johnson@example.com', 'password3');

-- Generate test data for university.classroom
INSERT INTO university.classroom (build_no, room_no, street)
VALUES
    (1, 101, 'Main Street'),
    (2, 202, 'Oak Avenue'),
    (3, 303, 'Elm Road');

-- Generate test data for university.courses
INSERT INTO university.courses (course_name, course_description)
VALUES
    ('Mathematics', 'Advanced mathematics course'),
    ('Physics', 'Introduction to physics'),
    ('Chemistry', 'Chemical principles and reactions');

-- Generate test data for university.faculties
INSERT INTO university.faculties (faculty_name)
VALUES
    ('Faculty of Science'),
    ('Faculty of Engineering'),
    ('Faculty of Arts');

-- Generate test data for university.departments
INSERT INTO university.departments (name, faculty_id)
VALUES
    ('Mathematics Department', 1),
    ('Physics Department', 2),
    ('Chemistry Department', 1);

-- Generate test data for university.groups
INSERT INTO university.groups (group_name, faculty_id)
VALUES
    ('Group A', 1),
    ('Group B', 2),
    ('Group C', 3);

-- Generate test data for university.staff
INSERT INTO university.staff (first_name, last_name, active, email, password, position)
VALUES
    ('Mark', 'Wilson', true, 'mark.wilson@example.com', 'password4', 'Utility'),
    ('Sarah', 'Taylor', true, 'sarah.taylor@example.com', 'password5', 'Security'),
    ('David', 'Anderson', true, 'david.anderson@example.com', 'password6', 'Assistant Teacher');

-- Generate test data for university.students
INSERT INTO university.students (first_name, last_name, active, email, password, group_id)
VALUES
    ('Emily', 'Brown', true, 'emily.brown@example.com', 'password7', 1),
    ('Daniel', 'Davis', true, 'daniel.davis@example.com', 'password8', 2),
    ('Olivia', 'Johnson', true, 'olivia.johnson@example.com', 'password9', 3);

-- Generate test data for university.teachers
INSERT INTO university.teachers (first_name, last_name, active, email, password, course_id, department_id)
VALUES
    ('Jennifer', 'Smith', true, 'jennifer.smith@example.com', 'password10', 1, 1),
    ('James', 'Wilson', true, 'james.wilson@example.com', 'password11', 2, 2),
    ('Sophia', 'Anderson', true, 'sophia.anderson@example.com', 'password12', 3, 3);

INSERT INTO university.timetable (date, time_start, time_end, teacher_id, student_id, course_id, group_id, classroom_id)
VALUES 
    ('2023-06-12', '08:00', '10:00', 1, 1, 1, 1, 3),
    ('2023-06-12', '10:30', '12:00', 2, 2, 2, 2, 2),
    ('2023-06-13', '12:00', '14:00', 3, 3, 3, 3, 1);

