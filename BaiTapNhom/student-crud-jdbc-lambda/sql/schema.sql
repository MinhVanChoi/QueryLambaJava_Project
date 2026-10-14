DROP DATABASE IF EXISTS student_db;

CREATE DATABASE student_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE student_db;

CREATE TABLE classes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    class_code VARCHAR(20) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL
);

CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    age INT NOT NULL,
    class_id INT NULL,
    CONSTRAINT chk_student_age CHECK (age BETWEEN 16 AND 100),
    CONSTRAINT fk_students_class
        FOREIGN KEY (class_id)
        REFERENCES classes(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

INSERT INTO classes(class_code, class_name) VALUES
('EC2026A', 'E-Commerce 2026 - A'),
('EC2026B', 'E-Commerce 2026 - B'),
('IT2026A', 'Information Technology 2026 - A');

INSERT INTO students(name, email, age, class_id) VALUES
('Nguyen Van An', 'an@gmail.com', 20, 1),
('Tran Thi Binh', 'binh@yahoo.com', 21, 1),
('Le Minh Cuong', 'cuong@gmail.com', 19, 2),
('Pham Thu Dung', 'dung@outlook.com', 22, 2),
('Hoang Gia Huy', 'huy@gmail.com', 20, 3),
('Do Minh Khoa', 'khoa@yahoo.com', 23, 3);
