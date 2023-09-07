CREATE SCHEMA IF NOT EXISTS university;

CREATE TABLE IF NOT EXISTS university.admin (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(70) NOT NULL,
    last_name VARCHAR(90) NOT NULL,
    active BOOLEAN NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(60) NOT NULL,
    role VARCHAR(8) NOT NULL
);

CREATE TABLE IF NOT EXISTS university.classroom (
    classroom_id SERIAL PRIMARY KEY,
    build_no INT NOT NULL,
    room_no INT NOT NULL,
    street VARCHAR(255) NOT NULL
);

CREATE TABLE university.courses (
    course_id SERIAL PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    course_description TEXT
);

CREATE TABLE university.faculties (
    faculty_id SERIAL PRIMARY KEY,
    faculty_name VARCHAR(255) NOT NULL
);

CREATE TABLE university.departments (
    department_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    faculty_id INT,
    FOREIGN KEY (faculty_id) REFERENCES university.faculties (faculty_id)
);

CREATE TABLE IF NOT EXISTS university.groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(30) NOT NULL,
    faculty_id INT,
    FOREIGN KEY (faculty_id) REFERENCES university.faculties (faculty_id)
);

CREATE TABLE IF NOT EXISTS university.staff (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(70) NOT NULL,
    last_name VARCHAR(90) NOT NULL,
    active BOOLEAN NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(60) NOT NULL,
    position VARCHAR(150) NOT NULL,
    function TEXT,
    role VARCHAR(8) NOT NULL
);

CREATE TABLE IF NOT EXISTS university.student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(70) NOT NULL,
    last_name VARCHAR(90) NOT NULL,
    active BOOLEAN NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(60) NOT NULL,
    group_id INT,
    role VARCHAR(8) NOT NULL,
    FOREIGN KEY (group_id) REFERENCES university.groups (group_id)
);

CREATE TABLE IF NOT EXISTS university.teacher (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(70) NOT NULL,
    last_name VARCHAR(90) NOT NULL,
    active BOOLEAN NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(60) NOT NULL,
    department_id INT,
    role VARCHAR(8) NOT NULL,
    FOREIGN KEY (department_id) REFERENCES university.departments (department_id)
);

CREATE TABLE IF NOT EXISTS university.students_courses (
    id SERIAL PRIMARY KEY,
    student_id INT,
    course_id INT,
    FOREIGN KEY (student_id) REFERENCES university.student (id),
    FOREIGN KEY (course_id) REFERENCES university.courses (course_id)
);

CREATE TABLE IF NOT EXISTS university.teachers_courses (
    id SERIAL PRIMARY KEY,
    teacher_id INT,
    course_id INT,
    FOREIGN KEY (teacher_id) REFERENCES university.teacher (id),
    FOREIGN KEY (course_id) REFERENCES university.courses (course_id)
);

CREATE TABLE IF NOT EXISTS university.timetable (
    timetable_id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    time_start TIME NOT NULL,
    time_end TIME NOT NULL,
    teacher_id INT,
    course_id INT,
    group_id INT,
    classroom_id INT,
    FOREIGN KEY (teacher_id) REFERENCES university.teacher (id),
    FOREIGN KEY (course_id) REFERENCES university.courses (course_id),
    FOREIGN KEY (group_id) REFERENCES university.groups (group_id),
    FOREIGN KEY (classroom_id) REFERENCES university.classroom (classroom_id)
);

CREATE TABLE alert (
    alert_id SERIAL PRIMARY KEY,
    alert_timestamp TIMESTAMP(0) NOT NULL,
    teacher_id INT,
    student_id INT,
    staff_id INT,
    admin_id INT,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES university.teacher (id),
    FOREIGN KEY (student_id) REFERENCES university.student (id),
    FOREIGN KEY (staff_id) REFERENCES university.staff (id),
    FOREIGN KEY (admin_id) REFERENCES university.admin (id)
);
