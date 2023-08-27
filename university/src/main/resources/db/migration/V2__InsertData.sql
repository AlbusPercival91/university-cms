CREATE EXTENSION pgcrypto;

-- Insert sample data for university.admin
INSERT INTO university.admin (first_name, last_name, active, email, password, role)
VALUES ('Admin', 'Admin', true, 'admin@example.com', crypt('1111', gen_salt('bf')), 'ADMIN');

-- Insert sample data for university.classroom
INSERT INTO university.classroom (build_no, room_no, street)
VALUES
    (1, 101, 'Green Street'),
    (2, 202, 'Yellow Avenue'),
    (3, 303, 'Red Street'),
    (4, 404, 'Pink Avenue'),
    (5, 505, 'Purple Street');

-- Insert sample data for university.courses
INSERT INTO university.courses (course_name, course_description)
VALUES
    ('History', 'Exploring the ancient civilizations and their impact on modern society.'),
    ('Geometry', 'A comprehensive study of calculus and its applications in mathematics.'),
    ('Literature', 'An overview of literary genres, styles, and critical analysis.'),
    ('Economics', 'Understanding the basic principles and concepts of economics.'),
    ('Psychology', 'An introduction to the fundamental concepts and theories of psychology.'),
    ('Political Science', 'Examining the basic concepts and theories of political science.'),
    ('Sociology', 'Understanding the sociological perspectives and theories.'),
    ('Biology', 'An introduction to the basic principles of biology and the diversity of life.'),
    ('Chemistry', 'Exploring the foundational principles and concepts of chemistry.'),
    ('Philosophy', 'Examining major philosophical themes and thinkers throughout history.');

-- Insert sample data for university.faculties
INSERT INTO university.faculties (faculty_name)
VALUES
    ('Faculty of Arts'),
    ('Faculty of Science'),
    ('Faculty of Business'),
    ('Faculty of Engineering'),
    ('Faculty of Medicine');

-- Insert sample data for university.departments
INSERT INTO university.departments (name, faculty_id)
VALUES
    ('Department of History', 1),
    ('Department of Mathematics', 2),
    ('Department of Computer Science', 3),
    ('Department of Physics', 4),
    ('Department of Economics', 5),
    ('Department of English', 1),
    ('Department of Chemistry', 2),
    ('Department of Business Administration', 3),
    ('Department of Mechanical Engineering', 4),
    ('Department of Medicine', 5);

-- Insert sample data for university.groups
INSERT INTO university.groups (group_name, faculty_id)
VALUES
    ('Group 1', 1),
    ('Group 2', 2),
    ('Group 3', 3),
    ('Group 4', 4),
    ('Group 5', 5),
    ('Group 6', 1),
    ('Group 7', 2),
    ('Group 8', 3),
    ('Group 9', 4),
    ('Group 10', 5);

-- Insert sample data for university.staff
INSERT INTO university.staff (first_name, last_name, active, email, password, position, "function", role)
VALUES
    ('John', 'Cole', true, 'johncole@example.com', crypt('1111', gen_salt('bf')), 'Administrator', 'Administrative tasks', 'STAFF'),
    ('Jane', 'Smith', true, 'janesmith@example.com', crypt('1111', gen_salt('bf')), 'Counselor', 'Student guidance', 'STAFF'),
    ('Michael', 'Johnson', true, 'michaeljohnson@example.com', crypt('1111', gen_salt('bf')), 'Librarian', 'Library management', 'STAFF'),
    ('Emily', 'Brown', true, 'emilybrown@example.com', crypt('1111', gen_salt('bf')), 'Advisor', 'Academic advising', 'STAFF'),
    ('David', 'Davis', true, 'daviddavis@example.com', crypt('1111', gen_salt('bf')), 'Coordinator', 'Program coordination', 'STAFF');

-- Insert sample data for university.students
INSERT INTO university.student (first_name, last_name, active, email, password, group_id, role)
SELECT
    CONCAT('Student Name', id),
    CONCAT('Student Surname', id),
    true,
    CONCAT('student', id, '@example.com'),
    crypt('1111', gen_salt('bf')),
    FLOOR((id - 1) / 10) + 1,
    'STUDENT'
FROM generate_series(1, 100) id;

-- Insert sample data for university.teachers
INSERT INTO university.teacher (first_name, last_name, active, email, password, department_id, role)
SELECT
    CONCAT('Teacher Name', id),
    CONCAT('Teacher Surname', id),
    true,
    CONCAT('teacher', id, '@example.com'),
    crypt('1111', gen_salt('bf')),
    FLOOR((id - 1) / 2) + 1,
    'TEACHER'
    FROM generate_series(1, 20) id;

-- Insert sample data for university.students_courses
INSERT INTO university.students_courses (student_id, course_id)
SELECT
    s.id,
    c.course_id
FROM
    university.student AS s
    JOIN LATERAL (
        SELECT course_id
        FROM university.courses
        WHERE course_id NOT IN (
            SELECT course_id
            FROM university.students_courses
            WHERE student_id = s.id
        )
        ORDER BY random()
        LIMIT 5 
    ) AS c ON true;
    
-- Insert sample data for university.teachers_courses
INSERT INTO university.teachers_courses (teacher_id, course_id)
SELECT
    t.id,
    c.course_id
FROM
    university.teacher AS t
    JOIN LATERAL (
        SELECT course_id
        FROM university.courses
        WHERE course_id NOT IN (
            SELECT course_id
            FROM university.teachers_courses
            WHERE teacher_id = t.id
        )
        ORDER BY random()
        LIMIT 2 
    ) AS c ON true;

