CREATE DATABASE IF NOT EXISTS se_publicityplatform
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE se_publicityplatform;

CREATE TABLE department (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL UNIQUE,
    leader_id INT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_department
        FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE department
ADD CONSTRAINT fk_department_leader
FOREIGN KEY (leader_id) REFERENCES `user`(user_id);

CREATE TABLE task_type (
    task_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    default_department_id INT NULL,
    description VARCHAR(255),
    CONSTRAINT fk_task_type_department
        FOREIGN KEY (default_department_id) REFERENCES department(department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE publicity_request (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    applicant_id INT NOT NULL,
    activity_name VARCHAR(100) NOT NULL,
    activity_time DATETIME NOT NULL,
    activity_location VARCHAR(100) NOT NULL,
    activity_content TEXT,
    publicity_types VARCHAR(200) NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    deadline DATETIME NOT NULL,
    attachment_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'pending_review',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    CONSTRAINT fk_request_applicant
        FOREIGN KEY (applicant_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE publicity_task (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    task_type_id INT NOT NULL,
    department_id INT NULL,
    assignee_id INT NULL,
    task_status VARCHAR(30) NOT NULL DEFAULT 'waiting_assignment',
    deadline DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    CONSTRAINT fk_task_request
        FOREIGN KEY (request_id) REFERENCES publicity_request(request_id),
    CONSTRAINT fk_task_type
        FOREIGN KEY (task_type_id) REFERENCES task_type(task_type_id),
    CONSTRAINT fk_task_department
        FOREIGN KEY (department_id) REFERENCES department(department_id),
    CONSTRAINT fk_task_assignee
        FOREIGN KEY (assignee_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE assignment (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    assigner_id INT NOT NULL,
    assignee_id INT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(255),
    CONSTRAINT fk_assignment_task
        FOREIGN KEY (task_id) REFERENCES publicity_task(task_id),
    CONSTRAINT fk_assignment_assigner
        FOREIGN KEY (assigner_id) REFERENCES `user`(user_id),
    CONSTRAINT fk_assignment_assignee
        FOREIGN KEY (assignee_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE submission (
    submission_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    submitter_id INT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    description TEXT,
    version_no INT NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'submitted',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_task
        FOREIGN KEY (task_id) REFERENCES publicity_task(task_id),
    CONSTRAINT fk_submission_submitter
        FOREIGN KEY (submitter_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE review_record (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    target_type VARCHAR(30) NOT NULL,
    target_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    review_result VARCHAR(30) NOT NULL,
    review_comment TEXT,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user
        FOREIGN KEY (reviewer_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reminder_record (
    reminder_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    receiver_id INT NOT NULL,
    reminder_type VARCHAR(30) NOT NULL,
    reminder_content VARCHAR(255) NOT NULL,
    send_status VARCHAR(30) NOT NULL DEFAULT 'waiting',
    planned_time DATETIME NOT NULL,
    sent_at DATETIME NULL,
    CONSTRAINT fk_reminder_task
        FOREIGN KEY (task_id) REFERENCES publicity_task(task_id),
    CONSTRAINT fk_reminder_receiver
        FOREIGN KEY (receiver_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE publicity_asset (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    task_id INT NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    archived_by INT NOT NULL,
    archived_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    CONSTRAINT fk_asset_request
        FOREIGN KEY (request_id) REFERENCES publicity_request(request_id),
    CONSTRAINT fk_asset_task
        FOREIGN KEY (task_id) REFERENCES publicity_task(task_id),
    CONSTRAINT fk_asset_user
        FOREIGN KEY (archived_by) REFERENCES `user`(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE system_param (
    param_id INT PRIMARY KEY AUTO_INCREMENT,
    param_key VARCHAR(50) NOT NULL UNIQUE,
    param_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE se_publicityplatform;

INSERT INTO department (department_name, description) VALUES
('摄影部', '负责活动拍照和图片整理'),
('新媒体部', '负责公众号推送和排版'),
('新闻部', '负责新闻稿撰写'),
('美工部', '负责海报和视觉设计');

INSERT INTO `user` (username, password, real_name, role, department_id, phone, email) VALUES
('applicant01', 'encrypted_123456', '张同学', 'applicant', NULL, '13800000001', 'applicant01@example.com'),
('teacher01', 'encrypted_123456', '李老师', 'teacher', NULL, '13800000002', 'teacher01@example.com'),
('director01', 'encrypted_123456', '王主任', 'director', NULL, '13800000003', 'director01@example.com'),
('photo_leader', 'encrypted_123456', '赵部长', 'leader', 1, '13800000004', 'photo_leader@example.com'),
('photo_member', 'encrypted_123456', '钱干事', 'member', 1, '13800000007', 'photo_member@example.com'),
('media_leader', 'encrypted_123456', '周部长', 'leader', 2, '13800000008', 'media_leader@example.com'),
('media_member', 'encrypted_123456', '陈干事', 'member', 2, '13800000005', 'media_member@example.com'),
('news_leader', 'encrypted_123456', '吴部长', 'leader', 3, '13800000009', 'news_leader@example.com'),
('news_member', 'encrypted_123456', '郑干事', 'member', 3, '13800000010', 'news_member@example.com'),
('art_leader', 'encrypted_123456', '冯部长', 'leader', 4, '13800000011', 'art_leader@example.com'),
('art_member', 'encrypted_123456', '王干事', 'member', 4, '13800000012', 'art_member@example.com'),
('admin01', 'encrypted_123456', '系统管理员', 'admin', NULL, '13800000006', 'admin01@example.com');

UPDATE department SET leader_id = 4 WHERE department_id = 1;
UPDATE department SET leader_id = 6 WHERE department_id = 2;
UPDATE department SET leader_id = 8 WHERE department_id = 3;
UPDATE department SET leader_id = 10 WHERE department_id = 4;

INSERT INTO task_type (type_name, default_department_id, description) VALUES
('拍照', 1, '活动现场拍照'),
('推送', 2, '公众号推送制作'),
('新闻稿', 3, '新闻稿撰写'),
('海报', 4, '活动海报设计');

INSERT INTO publicity_request (
    applicant_id,
    activity_name,
    activity_time,
    activity_location,
    activity_content,
    publicity_types,
    contact_name,
    contact_phone,
    deadline,
    attachment_url,
    status
) VALUES (
    1,
    '学院志愿服务活动',
    '2026-05-20 09:00:00',
    '学院报告厅',
    '学院组织志愿服务主题活动，需要进行现场拍摄和推送宣传',
    '拍照,推送',
    '张同学',
    '13800000001',
    '2026-05-22 18:00:00',
    '/upload/activity_plan.docx',
    'pending_review'
);

USE se_publicityplatform;

SELECT * FROM department;

SELECT * FROM `user`;

SELECT * FROM task_type;

SELECT * FROM publicity_request;
