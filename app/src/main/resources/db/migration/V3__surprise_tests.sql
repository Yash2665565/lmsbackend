CREATE TABLE IF NOT EXISTS surprise_tests (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    unit_id     BIGINT UNSIGNED  NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    test_date   DATE,
    duration_minutes INT      NOT NULL DEFAULT 30,
    max_marks   INT           NOT NULL DEFAULT 20,
    created_by  BIGINT UNSIGNED,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_st_unit FOREIGN KEY (unit_id) REFERENCES units (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS surprise_test_results (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    test_id         BIGINT UNSIGNED  NOT NULL,
    student_id      BIGINT UNSIGNED  NOT NULL,
    marks_obtained  DECIMAL(5,2),
    remarks         TEXT,
    graded_at       DATETIME,
    CONSTRAINT fk_str_test    FOREIGN KEY (test_id)    REFERENCES surprise_tests (id) ON DELETE CASCADE,
    CONSTRAINT fk_str_student FOREIGN KEY (student_id) REFERENCES students (id)       ON DELETE CASCADE,
    CONSTRAINT uq_test_student UNIQUE (test_id, student_id)
);
