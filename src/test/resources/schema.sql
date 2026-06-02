CREATE TABLE department (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL UNIQUE,
    leader_id INT NULL,
    description VARCHAR(255)
);

CREATE TABLE `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL,
    department_id INT NULL,
    phone VARCHAR(30),
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'enabled',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE task_type (
    task_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    default_department_id INT NULL,
    description VARCHAR(255)
);

CREATE TABLE publicity_request (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    applicant_id INT NOT NULL,
    activity_name VARCHAR(100) NOT NULL,
    activity_time TIMESTAMP NOT NULL,
    activity_location VARCHAR(100) NOT NULL,
    activity_content CLOB,
    publicity_types VARCHAR(200) NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    deadline TIMESTAMP NOT NULL,
    attachment_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'pending_review',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

CREATE TABLE publicity_task (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    task_type_id INT NOT NULL,
    department_id INT NULL,
    assignee_id INT NULL,
    task_status VARCHAR(30) NOT NULL DEFAULT 'waiting_assignment',
    deadline TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

CREATE TABLE assignment (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    assigner_id INT NOT NULL,
    assignee_id INT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(255)
);

CREATE TABLE submission (
    submission_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    submitter_id INT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    description CLOB,
    version_no INT NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'submitted',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review_record (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    target_type VARCHAR(30) NOT NULL,
    target_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    review_result VARCHAR(30) NOT NULL,
    review_comment CLOB,
    reviewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reminder_record (
    reminder_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    receiver_id INT NOT NULL,
    reminder_type VARCHAR(30) NOT NULL,
    reminder_content VARCHAR(255) NOT NULL,
    send_status VARCHAR(30) NOT NULL DEFAULT 'waiting',
    planned_time TIMESTAMP NOT NULL,
    sent_at TIMESTAMP NULL
);

CREATE TABLE publicity_asset (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    task_id INT NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    archived_by INT NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description CLOB
);

CREATE TABLE system_param (
    param_id INT PRIMARY KEY AUTO_INCREMENT,
    param_key VARCHAR(50) NOT NULL UNIQUE,
    param_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
