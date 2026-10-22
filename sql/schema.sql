CREATE DATABASE IF NOT EXISTS librarydb CHARACTER SET utf8mb4;
USE librarydb;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','MEMBER') NOT NULL DEFAULT 'MEMBER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(150) NOT NULL,
  category VARCHAR(80) NOT NULL,
  isbn VARCHAR(20),
  total_copies INT NOT NULL CHECK (total_copies >= 0),
  available_copies INT NOT NULL CHECK (available_copies >= 0),
  INDEX idx_title (title), INDEX idx_author (author), INDEX idx_category (category)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS issues (
  id INT AUTO_INCREMENT PRIMARY KEY,
  book_id INT NOT NULL,
  user_id INT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  fine INT NOT NULL DEFAULT 0,
  FOREIGN KEY (book_id) REFERENCES books(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user (user_id), INDEX idx_active (return_date, due_date)
) ENGINE=InnoDB;

INSERT INTO books (title,author,category,isbn,total_copies,available_copies) VALUES
('Clean Code','Robert C. Martin','Programming','9780132350884',3,3),
('Wings of Fire','A.P.J. Abdul Kalam','Biography','9788173711466',2,2),
('Head First Java','Kathy Sierra','Programming','9780596009205',4,4);
