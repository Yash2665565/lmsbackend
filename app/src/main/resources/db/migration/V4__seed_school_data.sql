-- ============================================================
-- V4: Full school seed data
-- ============================================================

-- ── Academic Year ──────────────────────────────────────────
UPDATE academic_years SET is_current = 1, name = '2025-26', start_date = '2025-04-01', end_date = '2026-03-31' WHERE id = 1;

-- Disable FK checks for all cleanup
SET FOREIGN_KEY_CHECKS = 0;

-- ── Class Grades 1–12 ──────────────────────────────────────
DELETE FROM class_grades;
INSERT INTO class_grades (id, name, created_at, updated_at) VALUES
  (1,  'Class 1',  NOW(), NOW()),
  (2,  'Class 2',  NOW(), NOW()),
  (3,  'Class 3',  NOW(), NOW()),
  (4,  'Class 4',  NOW(), NOW()),
  (5,  'Class 5',  NOW(), NOW()),
  (6,  'Class 6',  NOW(), NOW()),
  (7,  'Class 7',  NOW(), NOW()),
  (8,  'Class 8',  NOW(), NOW()),
  (9,  'Class 9',  NOW(), NOW()),
  (10, 'Class 10', NOW(), NOW()),
  (11, 'Class 11', NOW(), NOW()),
  (12, 'Class 12', NOW(), NOW());

-- ── Periods (7 per day) ────────────────────────────────────
DELETE FROM periods;
INSERT INTO periods (id, name, start_time, end_time, sort_order, created_at, updated_at) VALUES
  (1, 'Period 1', '08:00:00', '08:45:00', 1, NOW(), NOW()),
  (2, 'Period 2', '08:45:00', '09:30:00', 2, NOW(), NOW()),
  (3, 'Period 3', '09:30:00', '10:15:00', 3, NOW(), NOW()),
  (4, 'Period 4', '10:30:00', '11:15:00', 4, NOW(), NOW()),
  (5, 'Period 5', '11:15:00', '12:00:00', 5, NOW(), NOW()),
  (6, 'Period 6', '13:00:00', '13:45:00', 6, NOW(), NOW()),
  (7, 'Period 7', '13:45:00', '14:30:00', 7, NOW(), NOW());

-- ── Subjects (topics table) ────────────────────────────────
DELETE FROM timetable_slots;
DELETE FROM subject_teachers;
DELETE FROM class_subjects;
UPDATE topics SET is_hidden = 1;

-- Lower school subjects (Classes 1–5)  IDs 100–104
INSERT INTO topics (id, name, code, description, mandatory, is_hidden, position, created_at, updated_at) VALUES
  (100, 'English Language',       'ENG',   'Reading, writing, grammar, and comprehension',         '1', 0, 1, NOW(), NOW()),
  (101, 'Mathematics',            'MATH',  'Arithmetic, geometry, algebra foundations',            '1', 0, 2, NOW(), NOW()),
  (102, 'Environmental Science',  'EVS',   'Nature, environment, and basic science',               '1', 0, 3, NOW(), NOW()),
  (103, 'Hindi',                  'HIN',   'Hindi language and literature',                        '1', 0, 4, NOW(), NOW()),
  (104, 'Art & Craft',            'ART',   'Creative arts, drawing, and crafts',                   '1', 0, 5, NOW(), NOW());

-- Middle school subjects (Classes 6–8) IDs 105–109
INSERT INTO topics (id, name, code, description, mandatory, is_hidden, position, created_at, updated_at) VALUES
  (105, 'Science',                'SCI',   'Physics, Chemistry, Biology integrated',               '1', 0, 6, NOW(), NOW()),
  (106, 'Social Studies',         'SST',   'History, Geography, Civics',                           '1', 0, 7, NOW(), NOW()),
  (107, 'Computer Science',       'CS',    'Programming, digital literacy, and IT',                '1', 0, 8, NOW(), NOW()),
  (108, 'Sanskrit',               'SKT',   'Sanskrit language and classical literature',           '0', 0, 9, NOW(), NOW()),
  (109, 'Physical Education',     'PE',    'Sports, fitness, and health education',                '1', 0, 10, NOW(), NOW());

-- High school subjects (Classes 9–10) IDs 110–114
INSERT INTO topics (id, name, code, description, mandatory, is_hidden, position, created_at, updated_at) VALUES
  (110, 'Physics',                'PHY',   'Mechanics, electricity, optics, and waves',            '1', 0, 11, NOW(), NOW()),
  (111, 'Chemistry',              'CHEM',  'Atomic structure, reactions, and organic chemistry',   '1', 0, 12, NOW(), NOW()),
  (112, 'Biology',                'BIO',   'Cell biology, genetics, ecology, and human body',      '1', 0, 13, NOW(), NOW()),
  (113, 'History',                'HIST',  'Indian and world history',                             '1', 0, 14, NOW(), NOW()),
  (114, 'Geography',              'GEO',   'Physical and human geography',                         '1', 0, 15, NOW(), NOW());

-- Senior secondary (Classes 11–12) IDs 115–119
INSERT INTO topics (id, name, code, description, mandatory, is_hidden, position, created_at, updated_at) VALUES
  (115, 'Advanced Physics',       'APHY',  'Waves, thermodynamics, modern physics',                '1', 0, 16, NOW(), NOW()),
  (116, 'Advanced Chemistry',     'ACHE',  'Coordination, electrochemistry, polymers',             '1', 0, 17, NOW(), NOW()),
  (117, 'Advanced Biology',       'ABIO',  'Genetics, biotechnology, human physiology',            '1', 0, 18, NOW(), NOW()),
  (118, 'Advanced Mathematics',   'AMATH', 'Calculus, vectors, probability, statistics',           '1', 0, 19, NOW(), NOW()),
  (119, 'English Core',           'ENGC',  'Advanced reading, writing, and literature',            '1', 0, 20, NOW(), NOW());

-- ── class_subjects ─────────────────────────────────────────
-- Classes 1–5: ENG(100), MATH(101), EVS(102), HIN(103), ART(104)
INSERT INTO class_subjects (class_grade_id, topic_id, created_at, updated_at)
SELECT g.id, t.id, NOW(), NOW()
FROM class_grades g CROSS JOIN topics t
WHERE g.id BETWEEN 1 AND 5 AND t.id BETWEEN 100 AND 104;

-- Classes 6–8: ENG(100), MATH(101), SCI(105), SST(106), HIN(103)
INSERT INTO class_subjects (class_grade_id, topic_id, created_at, updated_at)
SELECT g.id, t.id, NOW(), NOW()
FROM class_grades g CROSS JOIN topics t
WHERE g.id BETWEEN 6 AND 8 AND t.id IN (100, 101, 103, 105, 106);

-- Classes 9–10: ENG(100), MATH(101), PHY(110), CHEM(111), BIO(112)
INSERT INTO class_subjects (class_grade_id, topic_id, created_at, updated_at)
SELECT g.id, t.id, NOW(), NOW()
FROM class_grades g CROSS JOIN topics t
WHERE g.id BETWEEN 9 AND 10 AND t.id IN (100, 101, 110, 111, 112);

-- Classes 11–12: ENGC(119), AMATH(118), APHY(115), ACHE(116), ABIO(117)
INSERT INTO class_subjects (class_grade_id, topic_id, created_at, updated_at)
SELECT g.id, t.id, NOW(), NOW()
FROM class_grades g CROSS JOIN topics t
WHERE g.id BETWEEN 11 AND 12 AND t.id BETWEEN 115 AND 119;

-- ── Teacher user accounts ──────────────────────────────────
-- Password: Teacher@123
SET @tpw = '$2a$10$h9XJXji/5HjRh8v9oQkaZuXIdna.j312mt6bxP88By..MbpLnhpyq';

INSERT INTO users (id, name, email, password, created_at, updated_at) VALUES
  (20001,'Amit Sharma',    'amit.sharma@school.edu',   @tpw, NOW(), NOW()),
  (20002,'Priya Singh',    'priya.singh@school.edu',   @tpw, NOW(), NOW()),
  (20003,'Rahul Verma',    'rahul.verma@school.edu',   @tpw, NOW(), NOW()),
  (20004,'Sunita Patel',   'sunita.patel@school.edu',  @tpw, NOW(), NOW()),
  (20005,'Vikram Rao',     'vikram.rao@school.edu',    @tpw, NOW(), NOW()),
  (20006,'Meena Joshi',    'meena.joshi@school.edu',   @tpw, NOW(), NOW()),
  (20007,'Arun Kumar',     'arun.kumar@school.edu',    @tpw, NOW(), NOW()),
  (20008,'Deepa Nair',     'deepa.nair@school.edu',    @tpw, NOW(), NOW()),
  (20009,'Suresh Gupta',   'suresh.gupta@school.edu',  @tpw, NOW(), NOW()),
  (20010,'Anita Mehta',    'anita.mehta@school.edu',   @tpw, NOW(), NOW()),
  (20011,'Rajesh Iyer',    'rajesh.iyer@school.edu',   @tpw, NOW(), NOW()),
  (20012,'Kavita Desai',   'kavita.desai@school.edu',  @tpw, NOW(), NOW()),
  (20013,'Mohan Das',      'mohan.das@school.edu',     @tpw, NOW(), NOW()),
  (20014,'Neha Agarwal',   'neha.agarwal@school.edu',  @tpw, NOW(), NOW()),
  (20015,'Ramesh Pandey',  'ramesh.pandey@school.edu', @tpw, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO user_roles (user_id, role, created_at, updated_at) VALUES
  (20001,'TEACHER',NOW(),NOW()),(20002,'TEACHER',NOW(),NOW()),(20003,'TEACHER',NOW(),NOW()),
  (20004,'TEACHER',NOW(),NOW()),(20005,'TEACHER',NOW(),NOW()),(20006,'TEACHER',NOW(),NOW()),
  (20007,'TEACHER',NOW(),NOW()),(20008,'TEACHER',NOW(),NOW()),(20009,'TEACHER',NOW(),NOW()),
  (20010,'TEACHER',NOW(),NOW()),(20011,'TEACHER',NOW(),NOW()),(20012,'TEACHER',NOW(),NOW()),
  (20013,'TEACHER',NOW(),NOW()),(20014,'TEACHER',NOW(),NOW()),(20015,'TEACHER',NOW(),NOW())
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO teachers (user_id, employee_no, qualification, joining_date, created_at, updated_at) VALUES
  (20001,'EMP001','B.Ed, M.A. English',     '2018-06-01', NOW(), NOW()),
  (20002,'EMP002','B.Ed, M.Sc. Maths',      '2019-07-15', NOW(), NOW()),
  (20003,'EMP003','B.Ed, M.Sc. Science',    '2017-04-10', NOW(), NOW()),
  (20004,'EMP004','B.Ed, M.A. Hindi',       '2020-06-01', NOW(), NOW()),
  (20005,'EMP005','B.Ed, M.A. Social St.',  '2016-07-20', NOW(), NOW()),
  (20006,'EMP006','B.Ed, MCA',              '2021-06-01', NOW(), NOW()),
  (20007,'EMP007','B.Ed, M.Sc. Physics',    '2015-08-15', NOW(), NOW()),
  (20008,'EMP008','B.Ed, M.Sc. Chemistry',  '2018-06-10', NOW(), NOW()),
  (20009,'EMP009','B.Ed, M.Sc. Biology',    '2019-06-01', NOW(), NOW()),
  (20010,'EMP010','B.Ed, M.A. History',     '2020-07-01', NOW(), NOW()),
  (20011,'EMP011','B.Ed, M.Sc. Geography',  '2017-06-15', NOW(), NOW()),
  (20012,'EMP012','B.Ed, M.A. Sanskrit',    '2022-06-01', NOW(), NOW()),
  (20013,'EMP013','B.Ed, M.Sc. Adv.Phy',   '2014-08-01', NOW(), NOW()),
  (20014,'EMP014','B.Ed, M.Sc. Adv.Chem',  '2016-06-10', NOW(), NOW()),
  (20015,'EMP015','B.Ed, M.Sc. Adv.Bio',   '2018-07-15', NOW(), NOW())
ON DUPLICATE KEY UPDATE employee_no = VALUES(employee_no);

-- ── Sections (3 per class, fixed IDs) ─────────────────────
DELETE FROM enrollments;
DELETE FROM sections;

INSERT INTO sections (id, class_grade_id, name, created_at, updated_at) VALUES
  ( 1, 1,'A',NOW(),NOW()),( 2, 1,'B',NOW(),NOW()),( 3, 1,'C',NOW(),NOW()),
  ( 4, 2,'A',NOW(),NOW()),( 5, 2,'B',NOW(),NOW()),( 6, 2,'C',NOW(),NOW()),
  ( 7, 3,'A',NOW(),NOW()),( 8, 3,'B',NOW(),NOW()),( 9, 3,'C',NOW(),NOW()),
  (10, 4,'A',NOW(),NOW()),(11, 4,'B',NOW(),NOW()),(12, 4,'C',NOW(),NOW()),
  (13, 5,'A',NOW(),NOW()),(14, 5,'B',NOW(),NOW()),(15, 5,'C',NOW(),NOW()),
  (16, 6,'A',NOW(),NOW()),(17, 6,'B',NOW(),NOW()),(18, 6,'C',NOW(),NOW()),
  (19, 7,'A',NOW(),NOW()),(20, 7,'B',NOW(),NOW()),(21, 7,'C',NOW(),NOW()),
  (22, 8,'A',NOW(),NOW()),(23, 8,'B',NOW(),NOW()),(24, 8,'C',NOW(),NOW()),
  (25, 9,'A',NOW(),NOW()),(26, 9,'B',NOW(),NOW()),(27, 9,'C',NOW(),NOW()),
  (28,10,'A',NOW(),NOW()),(29,10,'B',NOW(),NOW()),(30,10,'C',NOW(),NOW()),
  (31,11,'A',NOW(),NOW()),(32,11,'B',NOW(),NOW()),(33,11,'C',NOW(),NOW()),
  (34,12,'A',NOW(),NOW()),(35,12,'B',NOW(),NOW()),(36,12,'C',NOW(),NOW());

-- ── Student user accounts ──────────────────────────────────
-- Password: Student@123
SET @spw = '$2a$10$McJaENFha6fdZRFBWwiP2.IBzrx6HG44TQZ78vw2uk4YM9dc9wdca';

INSERT INTO users (id,name,email,password,created_at,updated_at) VALUES
(30001,'Aarav Sharma','aarav.sharma@student.edu',@spw,NOW(),NOW()),
(30002,'Ananya Singh','ananya.singh@student.edu',@spw,NOW(),NOW()),
(30003,'Arjun Patel','arjun.patel@student.edu',@spw,NOW(),NOW()),
(30004,'Bhavya Verma','bhavya.verma@student.edu',@spw,NOW(),NOW()),
(30005,'Chaitali Rao','chaitali.rao@student.edu',@spw,NOW(),NOW()),
(30006,'Dev Kumar','dev.kumar@student.edu',@spw,NOW(),NOW()),
(30007,'Divya Joshi','divya.joshi@student.edu',@spw,NOW(),NOW()),
(30008,'Eshan Nair','eshan.nair@student.edu',@spw,NOW(),NOW()),
(30009,'Farhan Gupta','farhan.gupta@student.edu',@spw,NOW(),NOW()),
(30010,'Gauri Mehta','gauri.mehta@student.edu',@spw,NOW(),NOW()),
(30011,'Harsh Iyer','harsh.iyer@student.edu',@spw,NOW(),NOW()),
(30012,'Ishaan Desai','ishaan.desai@student.edu',@spw,NOW(),NOW()),
(30013,'Jaya Das','jaya.das@student.edu',@spw,NOW(),NOW()),
(30014,'Kabir Agarwal','kabir.agarwal@student.edu',@spw,NOW(),NOW()),
(30015,'Lakshmi Pandey','lakshmi.pandey@student.edu',@spw,NOW(),NOW()),
(30016,'Manav Sharma','manav.sharma@student.edu',@spw,NOW(),NOW()),
(30017,'Nidhi Singh','nidhi.singh@student.edu',@spw,NOW(),NOW()),
(30018,'Om Patel','om.patel@student.edu',@spw,NOW(),NOW()),
(30019,'Payal Verma','payal.verma@student.edu',@spw,NOW(),NOW()),
(30020,'Qais Rao','qais.rao@student.edu',@spw,NOW(),NOW()),
(30021,'Riya Kumar','riya.kumar@student.edu',@spw,NOW(),NOW()),
(30022,'Sagar Joshi','sagar.joshi@student.edu',@spw,NOW(),NOW()),
(30023,'Tara Nair','tara.nair@student.edu',@spw,NOW(),NOW()),
(30024,'Udit Gupta','udit.gupta@student.edu',@spw,NOW(),NOW()),
(30025,'Vandana Mehta','vandana.mehta@student.edu',@spw,NOW(),NOW()),
(30026,'Vivek Iyer','vivek.iyer@student.edu',@spw,NOW(),NOW()),
(30027,'Wren Desai','wren.desai@student.edu',@spw,NOW(),NOW()),
(30028,'Xena Das','xena.das@student.edu',@spw,NOW(),NOW()),
(30029,'Yash Agarwal','yash.agarwal@student.edu',@spw,NOW(),NOW()),
(30030,'Zara Pandey','zara.pandey@student.edu',@spw,NOW(),NOW()),
(30031,'Aditya Sharma','aditya.sharma@student.edu',@spw,NOW(),NOW()),
(30032,'Bhumi Singh','bhumi.singh@student.edu',@spw,NOW(),NOW()),
(30033,'Chandan Patel','chandan.patel@student.edu',@spw,NOW(),NOW()),
(30034,'Disha Verma','disha.verma@student.edu',@spw,NOW(),NOW()),
(30035,'Elan Rao','elan.rao@student.edu',@spw,NOW(),NOW()),
(30036,'Faizan Kumar','faizan.kumar@student.edu',@spw,NOW(),NOW()),
(30037,'Gitanjali Joshi','gitanjali.joshi@student.edu',@spw,NOW(),NOW()),
(30038,'Hemant Nair','hemant.nair@student.edu',@spw,NOW(),NOW()),
(30039,'Ira Gupta','ira.gupta@student.edu',@spw,NOW(),NOW()),
(30040,'Jai Mehta','jai.mehta@student.edu',@spw,NOW(),NOW()),
(30041,'Kavya Iyer','kavya.iyer@student.edu',@spw,NOW(),NOW()),
(30042,'Lokesh Desai','lokesh.desai@student.edu',@spw,NOW(),NOW()),
(30043,'Mahi Das','mahi.das@student.edu',@spw,NOW(),NOW()),
(30044,'Neel Agarwal','neel.agarwal@student.edu',@spw,NOW(),NOW()),
(30045,'Ora Pandey','ora.pandey@student.edu',@spw,NOW(),NOW()),
(30046,'Parth Sharma','parth.sharma@student.edu',@spw,NOW(),NOW()),
(30047,'Qiran Singh','qiran.singh@student.edu',@spw,NOW(),NOW()),
(30048,'Ridhi Patel','ridhi.patel@student.edu',@spw,NOW(),NOW()),
(30049,'Samir Verma','samir.verma@student.edu',@spw,NOW(),NOW()),
(30050,'Tia Rao','tia.rao@student.edu',@spw,NOW(),NOW()),
(30051,'Ujjwal Kumar','ujjwal.kumar@student.edu',@spw,NOW(),NOW()),
(30052,'Vidhi Joshi','vidhi.joshi@student.edu',@spw,NOW(),NOW()),
(30053,'Waqar Nair','waqar.nair@student.edu',@spw,NOW(),NOW()),
(30054,'Xara Gupta','xara.gupta@student.edu',@spw,NOW(),NOW()),
(30055,'Yogesh Mehta','yogesh.mehta@student.edu',@spw,NOW(),NOW()),
(30056,'Zainab Iyer','zainab.iyer@student.edu',@spw,NOW(),NOW()),
(30057,'Akash Desai','akash.desai@student.edu',@spw,NOW(),NOW()),
(30058,'Bindiya Das','bindiya.das@student.edu',@spw,NOW(),NOW()),
(30059,'Chirag Agarwal','chirag.agarwal@student.edu',@spw,NOW(),NOW()),
(30060,'Daksh Pandey','daksh.pandey@student.edu',@spw,NOW(),NOW()),
(30061,'Ekta Sharma','ekta.sharma@student.edu',@spw,NOW(),NOW()),
(30062,'Farida Singh','farida.singh@student.edu',@spw,NOW(),NOW()),
(30063,'Girish Patel','girish.patel@student.edu',@spw,NOW(),NOW()),
(30064,'Harini Verma','harini.verma@student.edu',@spw,NOW(),NOW()),
(30065,'Ishan Rao','ishan.rao@student.edu',@spw,NOW(),NOW()),
(30066,'Jatin Kumar','jatin.kumar@student.edu',@spw,NOW(),NOW()),
(30067,'Keerti Joshi','keerti.joshi@student.edu',@spw,NOW(),NOW()),
(30068,'Liam Nair','liam.nair@student.edu',@spw,NOW(),NOW()),
(30069,'Maya Gupta','maya.gupta@student.edu',@spw,NOW(),NOW()),
(30070,'Nikhil Mehta','nikhil.mehta@student.edu',@spw,NOW(),NOW()),
(30071,'Oshin Iyer','oshin.iyer@student.edu',@spw,NOW(),NOW()),
(30072,'Prabhat Desai','prabhat.desai@student.edu',@spw,NOW(),NOW()),
(30073,'Qureshi Das','qureshi.das@student.edu',@spw,NOW(),NOW()),
(30074,'Roshni Agarwal','roshni.agarwal@student.edu',@spw,NOW(),NOW()),
(30075,'Shivam Pandey','shivam.pandey@student.edu',@spw,NOW(),NOW()),
(30076,'Tanvi Sharma','tanvi.sharma@student.edu',@spw,NOW(),NOW()),
(30077,'Umar Singh','umar.singh@student.edu',@spw,NOW(),NOW()),
(30078,'Varsha Patel','varsha.patel@student.edu',@spw,NOW(),NOW()),
(30079,'Wasim Verma','wasim.verma@student.edu',@spw,NOW(),NOW()),
(30080,'Yamini Rao','yamini.rao@student.edu',@spw,NOW(),NOW()),
(30081,'Zoya Kumar','zoya.kumar@student.edu',@spw,NOW(),NOW()),
(30082,'Abhay Joshi','abhay.joshi@student.edu',@spw,NOW(),NOW()),
(30083,'Bhavana Nair','bhavana.nair@student.edu',@spw,NOW(),NOW()),
(30084,'Chinmay Gupta','chinmay.gupta@student.edu',@spw,NOW(),NOW()),
(30085,'Damini Mehta','damini.mehta@student.edu',@spw,NOW(),NOW()),
(30086,'Eklavya Iyer','eklavya.iyer@student.edu',@spw,NOW(),NOW()),
(30087,'Farrukh Desai','farrukh.desai@student.edu',@spw,NOW(),NOW()),
(30088,'Geetika Das','geetika.das@student.edu',@spw,NOW(),NOW()),
(30089,'Hardik Agarwal','hardik.agarwal@student.edu',@spw,NOW(),NOW()),
(30090,'Isha Pandey','isha.pandey@student.edu',@spw,NOW(),NOW()),
(30091,'Jayant Sharma','jayant.sharma@student.edu',@spw,NOW(),NOW()),
(30092,'Kiran Singh','kiran.singh@student.edu',@spw,NOW(),NOW()),
(30093,'Lalita Patel','lalita.patel@student.edu',@spw,NOW(),NOW()),
(30094,'Manish Verma','manish.verma@student.edu',@spw,NOW(),NOW()),
(30095,'Naira Rao','naira.rao@student.edu',@spw,NOW(),NOW()),
(30096,'Ojasvi Kumar','ojasvi.kumar@student.edu',@spw,NOW(),NOW()),
(30097,'Pranav Joshi','pranav.joshi@student.edu',@spw,NOW(),NOW()),
(30098,'Qamar Nair','qamar.nair@student.edu',@spw,NOW(),NOW()),
(30099,'Radha Gupta','radha.gupta@student.edu',@spw,NOW(),NOW()),
(30100,'Siddharth Mehta','siddharth.mehta@student.edu',@spw,NOW(),NOW()),
(30101,'Tarini Iyer','tarini.iyer@student.edu',@spw,NOW(),NOW()),
(30102,'Utkarsh Desai','utkarsh.desai@student.edu',@spw,NOW(),NOW()),
(30103,'Vanya Das','vanya.das@student.edu',@spw,NOW(),NOW()),
(30104,'Waris Agarwal','waris.agarwal@student.edu',@spw,NOW(),NOW()),
(30105,'Yana Pandey','yana.pandey@student.edu',@spw,NOW(),NOW()),
(30106,'Zayn Sharma','zayn.sharma@student.edu',@spw,NOW(),NOW()),
(30107,'Aishwarya Singh','aishwarya.singh@student.edu',@spw,NOW(),NOW()),
(30108,'Baldev Patel','baldev.patel@student.edu',@spw,NOW(),NOW()),
(30109,'Chandrika Verma','chandrika.verma@student.edu',@spw,NOW(),NOW()),
(30110,'Deepesh Rao','deepesh.rao@student.edu',@spw,NOW(),NOW()),
(30111,'Esha Kumar','esha.kumar@student.edu',@spw,NOW(),NOW()),
(30112,'Firoz Joshi','firoz.joshi@student.edu',@spw,NOW(),NOW()),
(30113,'Gayatri Nair','gayatri.nair@student.edu',@spw,NOW(),NOW()),
(30114,'Hitesh Gupta','hitesh.gupta@student.edu',@spw,NOW(),NOW()),
(30115,'Indira Mehta','indira.mehta@student.edu',@spw,NOW(),NOW()),
(30116,'Jagdish Iyer','jagdish.iyer@student.edu',@spw,NOW(),NOW()),
(30117,'Komal Desai','komal.desai@student.edu',@spw,NOW(),NOW()),
(30118,'Lalit Das','lalit.das@student.edu',@spw,NOW(),NOW()),
(30119,'Madhuri Agarwal','madhuri.agarwal@student.edu',@spw,NOW(),NOW()),
(30120,'Navneet Pandey','navneet.pandey@student.edu',@spw,NOW(),NOW()),
(30121,'Omkar Sharma','omkar.sharma@student.edu',@spw,NOW(),NOW()),
(30122,'Poonam Singh','poonam.singh@student.edu',@spw,NOW(),NOW()),
(30123,'Rishabh Patel','rishabh.patel@student.edu',@spw,NOW(),NOW()),
(30124,'Simran Verma','simran.verma@student.edu',@spw,NOW(),NOW()),
(30125,'Tushar Rao','tushar.rao@student.edu',@spw,NOW(),NOW()),
(30126,'Urvi Kumar','urvi.kumar@student.edu',@spw,NOW(),NOW()),
(30127,'Vedant Joshi','vedant.joshi@student.edu',@spw,NOW(),NOW()),
(30128,'Wahida Nair','wahida.nair@student.edu',@spw,NOW(),NOW()),
(30129,'Xander Gupta','xander.gupta@student.edu',@spw,NOW(),NOW()),
(30130,'Yasmin Mehta','yasmin.mehta@student.edu',@spw,NOW(),NOW()),
(30131,'Zaheer Iyer','zaheer.iyer@student.edu',@spw,NOW(),NOW()),
(30132,'Aditi Desai','aditi.desai@student.edu',@spw,NOW(),NOW()),
(30133,'Bharat Das','bharat.das@student.edu',@spw,NOW(),NOW()),
(30134,'Chesta Agarwal','chesta.agarwal@student.edu',@spw,NOW(),NOW()),
(30135,'Dhruv Pandey','dhruv.pandey@student.edu',@spw,NOW(),NOW()),
(30136,'Erawat Sharma','erawat.sharma@student.edu',@spw,NOW(),NOW()),
(30137,'Farha Singh','farha.singh@student.edu',@spw,NOW(),NOW()),
(30138,'Gaurav Patel','gaurav.patel@student.edu',@spw,NOW(),NOW()),
(30139,'Hiral Verma','hiral.verma@student.edu',@spw,NOW(),NOW()),
(30140,'Iram Rao','iram.rao@student.edu',@spw,NOW(),NOW()),
(30141,'Jeetendra Kumar','jeetendra.kumar@student.edu',@spw,NOW(),NOW()),
(30142,'Kajal Joshi','kajal.joshi@student.edu',@spw,NOW(),NOW()),
(30143,'Laxman Nair','laxman.nair@student.edu',@spw,NOW(),NOW()),
(30144,'Minakshi Gupta','minakshi.gupta@student.edu',@spw,NOW(),NOW()),
(30145,'Nakul Mehta','nakul.mehta@student.edu',@spw,NOW(),NOW()),
(30146,'Ojas Iyer','ojas.iyer@student.edu',@spw,NOW(),NOW()),
(30147,'Preeti Desai','preeti.desai@student.edu',@spw,NOW(),NOW()),
(30148,'Qasim Das','qasim.das@student.edu',@spw,NOW(),NOW()),
(30149,'Rachna Agarwal','rachna.agarwal@student.edu',@spw,NOW(),NOW()),
(30150,'Saurabh Pandey','saurabh.pandey@student.edu',@spw,NOW(),NOW()),
(30151,'Tarun Sharma','tarun.sharma@student.edu',@spw,NOW(),NOW()),
(30152,'Uma Singh','uma.singh@student.edu',@spw,NOW(),NOW()),
(30153,'Varun Patel','varun.patel@student.edu',@spw,NOW(),NOW()),
(30154,'Whiz Verma','whiz.verma@student.edu',@spw,NOW(),NOW()),
(30155,'Yuvraj Rao','yuvraj.rao@student.edu',@spw,NOW(),NOW()),
(30156,'Zehra Kumar','zehra.kumar@student.edu',@spw,NOW(),NOW()),
(30157,'Aman Joshi','aman.joshi@student.edu',@spw,NOW(),NOW()),
(30158,'Bela Nair','bela.nair@student.edu',@spw,NOW(),NOW()),
(30159,'Cyrus Gupta','cyrus.gupta@student.edu',@spw,NOW(),NOW()),
(30160,'Daya Mehta','daya.mehta@student.edu',@spw,NOW(),NOW()),
(30161,'Elina Iyer','elina.iyer@student.edu',@spw,NOW(),NOW()),
(30162,'Faisal Desai','faisal.desai@student.edu',@spw,NOW(),NOW()),
(30163,'Gouri Das','gouri.das@student.edu',@spw,NOW(),NOW()),
(30164,'Hamid Agarwal','hamid.agarwal@student.edu',@spw,NOW(),NOW()),
(30165,'Indu Pandey','indu.pandey@student.edu',@spw,NOW(),NOW()),
(30166,'Jayesh Sharma','jayesh.sharma@student.edu',@spw,NOW(),NOW()),
(30167,'Kamini Singh','kamini.singh@student.edu',@spw,NOW(),NOW()),
(30168,'Lalit Patel','lalit.patel@student.edu',@spw,NOW(),NOW()),
(30169,'Meena Verma','meena.verma@student.edu',@spw,NOW(),NOW()),
(30170,'Naman Rao','naman.rao@student.edu',@spw,NOW(),NOW()),
(30171,'Ojasvini Kumar','ojasvini.kumar@student.edu',@spw,NOW(),NOW()),
(30172,'Palak Joshi','palak.joshi@student.edu',@spw,NOW(),NOW()),
(30173,'Rajan Nair','rajan.nair@student.edu',@spw,NOW(),NOW()),
(30174,'Shalini Gupta','shalini.gupta@student.edu',@spw,NOW(),NOW()),
(30175,'Tapan Mehta','tapan.mehta@student.edu',@spw,NOW(),NOW()),
(30176,'Urmila Iyer','urmila.iyer@student.edu',@spw,NOW(),NOW()),
(30177,'Vikas Desai','vikas.desai@student.edu',@spw,NOW(),NOW()),
(30178,'Wahab Das','wahab.das@student.edu',@spw,NOW(),NOW()),
(30179,'Ximena Agarwal','ximena.agarwal@student.edu',@spw,NOW(),NOW()),
(30180,'Yusuf Pandey','yusuf.pandey@student.edu',@spw,NOW(),NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- User roles for students
INSERT INTO user_roles (user_id, role, created_at, updated_at)
SELECT id, 'STUDENT', NOW(), NOW() FROM users WHERE id BETWEEN 30001 AND 30180
ON DUPLICATE KEY UPDATE role = VALUES(role);

-- ── Student records ─────────────────────────────────────────
INSERT INTO students (user_id, admission_no, first_name, last_name, dob, gender, created_at, updated_at)
SELECT
  u.id,
  CONCAT('ADM', LPAD(u.id - 30000, 4, '0')),
  SUBSTRING_INDEX(u.name, ' ', 1),
  SUBSTRING_INDEX(u.name, ' ', -1),
  DATE_SUB('2026-06-23', INTERVAL (6 + FLOOR((u.id - 30001) / 15)) YEAR),
  CASE WHEN MOD(u.id, 2) = 0 THEN 'F' ELSE 'M' END,
  NOW(), NOW()
FROM users u WHERE u.id BETWEEN 30001 AND 30180
ON DUPLICATE KEY UPDATE admission_no = VALUES(admission_no);

-- ── Enrollments ─────────────────────────────────────────────
-- section_id = CEIL((student_user_id - 30000) / 5)
-- students 30001-30005 -> section 1, 30006-30010 -> section 2, etc.
INSERT INTO enrollments (student_id, section_id, academic_year_id, roll_no, status, created_at, updated_at)
SELECT
  st.id,
  CEIL((st.user_id - 30000) / 5) AS section_id,
  1,
  MOD(st.user_id - 30001, 5) + 1 AS roll_no,
  'ACTIVE',
  NOW(), NOW()
FROM students st
WHERE st.user_id BETWEEN 30001 AND 30180
ON DUPLICATE KEY UPDATE roll_no = VALUES(roll_no);

-- ── Timetable slots ────────────────────────────────────────
-- 5 subjects per class, 7 periods per day, 5 days per week
-- Subject for each period slot: cycle through class subjects by (period_id - 1) % 5 + 1
INSERT INTO timetable_slots (section_id, day_of_week, period_id, topic_id, teacher_id, academic_year_id, created_at, updated_at)
SELECT
  sec.id,
  d.day_num,
  p.id,
  cs_ordered.topic_id,
  (SELECT t.id FROM teachers t WHERE t.user_id = 20001 + MOD((sec.id - 1) * 5 + (p.id - 1), 15) LIMIT 1),
  1,
  NOW(), NOW()
FROM sections sec
CROSS JOIN (SELECT 1 AS day_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) d
CROSS JOIN periods p
JOIN (
  SELECT cs.class_grade_id, cs.topic_id,
         ROW_NUMBER() OVER (PARTITION BY cs.class_grade_id ORDER BY cs.topic_id) AS rn
  FROM class_subjects cs
) cs_ordered ON cs_ordered.class_grade_id = sec.class_grade_id
                AND cs_ordered.rn = MOD(p.id - 1, 5) + 1;

-- ── LMS Units (5 per subject) ──────────────────────────────
INSERT INTO units (subject_id, title, description, order_no, created_at, updated_at)
SELECT
  t.id,
  CONCAT('Unit ', u.n, ': ', ut.title),
  CONCAT('This unit covers ', ut.title, ' in the context of ', t.name, '.'),
  u.n,
  NOW(), NOW()
FROM topics t
CROSS JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) u
JOIN (
  SELECT 1 AS n, 'Introduction and Fundamentals' AS title UNION
  SELECT 2, 'Core Concepts and Principles' UNION
  SELECT 3, 'Applications and Practice' UNION
  SELECT 4, 'Advanced Topics and Problem Solving' UNION
  SELECT 5, 'Revision and Assessment Preparation'
) ut ON ut.n = u.n
WHERE t.is_hidden = 0 AND t.id BETWEEN 100 AND 119;

-- ── Assignments (2 per unit) ───────────────────────────────
INSERT INTO assignments (unit_id, title, description, due_date, max_marks, created_by, created_at, updated_at)
SELECT
  un.id,
  CONCAT(ad.prefix, ' — ', un.title),
  CONCAT(ad.desc_text, ' for "', un.title, '".'),
  DATE_ADD('2026-06-23', INTERVAL ad.days DAY),
  20,
  20001,
  NOW(), NOW()
FROM units un
CROSS JOIN (
  SELECT 1 AS seq, 'Written Assignment' AS prefix, 'Submit a written report' AS desc_text, 14 AS days UNION
  SELECT 2,        'Practice Exercise',             'Complete the practice set',             21
) ad;

-- ── Surprise Tests (2 per unit) ────────────────────────────
INSERT INTO surprise_tests (unit_id, title, description, test_date, duration_minutes, max_marks, created_by, created_at, updated_at)
SELECT
  un.id,
  CONCAT(sd.prefix, ' — ', un.title),
  CONCAT(sd.desc_text, ' for "', un.title, '".'),
  DATE_ADD('2026-06-23', INTERVAL sd.days DAY),
  20,
  20,
  20001,
  NOW(), NOW()
FROM units un
CROSS JOIN (
  SELECT 1 AS seq, 'Surprise Test 1' AS prefix, 'Quick assessment 1' AS desc_text, 7 AS days UNION
  SELECT 2,        'Surprise Test 2',            'Quick assessment 2',             28
) sd;

SET FOREIGN_KEY_CHECKS = 1;
