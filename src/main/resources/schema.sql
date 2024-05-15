CREATE TABLE Address (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    premises VARCHAR(255),
    locality VARCHAR(255),
    postal_code VARCHAR(20),
    address_line_1 VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE Company (
    company_number VARCHAR(50) PRIMARY KEY,
    company_type VARCHAR(50),
    title VARCHAR(255),
    company_status VARCHAR(50),
    date_of_creation DATE,
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES Address(address_id)
);

CREATE TABLE Officer (
    officer_id INT PRIMARY KEY AUTO_INCREMENT,
    company_number VARCHAR(50),
    name VARCHAR(255),
    officer_role VARCHAR(50),
    appointed_on DATE,
    officer_address_id INT,
    FOREIGN KEY (company_number) REFERENCES Company(company_number),
    FOREIGN KEY (officer_address_id) REFERENCES Address(address_id)
);
