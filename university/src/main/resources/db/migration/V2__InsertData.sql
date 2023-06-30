-- Insert sample data for university.admin
INSERT INTO university.admin (first_name, last_name, active, email, password)
VALUES ('Admin', 'User', true, 'admin@example.com', 'adminpassword');

-- Insert sample data for university.classroom
INSERT INTO university.classroom (build_no, room_no, street)
VALUES
    (1, 101, 'Main Street'),
    (2, 202, 'Park Avenue'),
    (3, 303, 'Elm Street'),
    (4, 404, 'Oak Avenue'),
    (5, 505, 'Maple Street');

-- Insert sample data for university.courses
INSERT INTO university.courses (course_name, course_description)
VALUES
    ('Course 1', 'Course 1 description'),
    ('Course 2', 'Course 2 description'),
    ('Course 3', 'Course 3 description'),
    ('Course 4', 'Course 4 description'),
    ('Course 5', 'Course 5 description'),
    ('Course 6', 'Course 6 description'),
    ('Course 7', 'Course 7 description'),
    ('Course 8', 'Course 8 description'),
    ('Course 9', 'Course 9 description'),
    ('Course 10', 'Course 10 description');

-- Insert sample data for university.faculties
INSERT INTO university.faculties (faculty_name)
VALUES
    ('Faculty 1'),
    ('Faculty 2'),
    ('Faculty 3'),
    ('Faculty 4'),
    ('Faculty 5');

-- Insert sample data for university.departments
INSERT INTO university.departments (name, faculty_id)
VALUES
    ('Department 1', 1),
    ('Department 2', 2),
    ('Department 3', 3),
    ('Department 4', 4),
    ('Department 5', 5),
    ('Department 6', 1),
    ('Department 7', 2),
    ('Department 8', 3),
    ('Department 9', 4),
    ('Department 10', 5);

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
INSERT INTO university.staff (first_name, last_name, active, email, password, position, "function")
VALUES
    ('Staff 1', 'User', true, 'staff1@example.com', 'staff1password', 'Position 1', 'Function 1'),
    ('Staff 2', 'User', true, 'staff2@example.com', 'staff2password', 'Position 2', 'Function 2'),
    ('Staff 3', 'User', true, 'staff3@example.com', 'staff3password', 'Position 3', 'Function 3'),
    ('Staff 4', 'User', true, 'staff4@example.com', 'staff4password', 'Position 4', 'Function 4'),
    ('Staff 5', 'User', true, 'staff5@example.com', 'staff5password', 'Position 5', 'Function 5');

-- Insert sample data for university.students
INSERT INTO university.students (first_name, last_name, active, email, password, group_id)
SELECT
    CONCAT('Studen Name', id),
    CONCAT('Student Surname', id),
    true,
    CONCAT('student', id, '@example.com'),
    CONCAT('student', id, 'password'),
    FLOOR((id - 1) / 10) + 1
FROM generate_series(1, 100) id;

-- Insert sample data for university.teachers
INSERT INTO university.teachers (first_name, last_name, active, email, password, course_id, department_id)
SELECT
    CONCAT('Teacher Name', id),
    CONCAT('Teacher Surname', id),
    true,
    CONCAT('teacher', id, '@example.com'),
    CONCAT('teacher', id, 'password'),
    (id - 1) % 10 + 1, 
    FLOOR((id - 1) / 2) + 1
FROM generate_series(1, 20) id;

-- Insert sample data for university.students_courses
INSERT INTO university.students_courses (student_id, course_id)
SELECT
    s.id,
    c.course_id
FROM
    university.students AS s
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
    university.teachers AS t
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

