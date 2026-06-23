CREATE TABLE units (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    subject_id  BIGINT UNSIGNED  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    order_no    INT          NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_unit_subject FOREIGN KEY (subject_id) REFERENCES topics (id) ON DELETE CASCADE
);

CREATE TABLE course_notes (
    id         BIGINT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,
    unit_id    BIGINT UNSIGNED  NOT NULL,
    title      VARCHAR(255)  NOT NULL,
    content    TEXT,
    file_url   VARCHAR(1000),
    note_type  ENUM('TEXT','PDF','VIDEO','LINK') NOT NULL DEFAULT 'TEXT',
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_note_unit FOREIGN KEY (unit_id) REFERENCES units (id) ON DELETE CASCADE
);

CREATE TABLE assignments (
    id          BIGINT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,
    unit_id     BIGINT UNSIGNED  NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    due_date    DATE,
    max_marks   INT           NOT NULL DEFAULT 100,
    created_by  BIGINT UNSIGNED,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_unit FOREIGN KEY (unit_id) REFERENCES units (id) ON DELETE CASCADE
);

CREATE TABLE assignment_submissions (
    id              BIGINT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,
    assignment_id   BIGINT UNSIGNED  NOT NULL,
    student_id      BIGINT UNSIGNED  NOT NULL,
    content         TEXT,
    submitted_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    marks_obtained  DECIMAL(5,2),
    feedback        TEXT,
    status          ENUM('SUBMITTED','GRADED','LATE') NOT NULL DEFAULT 'SUBMITTED',
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES assignments (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_student    FOREIGN KEY (student_id)    REFERENCES students (id)    ON DELETE CASCADE,
    CONSTRAINT uq_assignment_student    UNIQUE (assignment_id, student_id)
);
