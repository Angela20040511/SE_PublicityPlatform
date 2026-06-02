INSERT INTO department (department_name, description) VALUES
('摄影部', '负责活动拍照和图片整理'),
('新媒体部', '负责公众号推送和排版'),
('新闻部', '负责新闻稿撰写'),
('美工部', '负责海报和视觉设计');

INSERT INTO `user` (username, password, real_name, role, department_id, phone, email) VALUES
('applicant01', 'encrypted_123456', '张同学', 'applicant', NULL, '13800000001', 'applicant01@example.com'),
('teacher01', 'encrypted_123456', '李老师', 'teacher', NULL, '13800000002', 'teacher01@example.com'),
('director01', 'encrypted_123456', '王主任', 'director', NULL, '13800000003', 'director01@example.com'),
('photo_leader', 'encrypted_123456', '赵部长', 'leader', 1, '13800000004', 'photo_leader@example.com');

INSERT INTO task_type (type_name, default_department_id, description) VALUES
('拍照', 1, '活动现场拍照'),
('推送', 2, '公众号推送制作'),
('新闻稿', 3, '新闻稿撰写'),
('海报', 4, '活动海报设计');
