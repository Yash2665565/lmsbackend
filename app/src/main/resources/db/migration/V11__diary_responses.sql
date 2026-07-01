-- Student/parent responses to diary (homework) entries: mark done + leave a note.
-- New table in the lms DB only; never touches corporate tables.
CREATE TABLE IF NOT EXISTS diary_responses (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  diary_id    BIGINT NOT NULL,
  student_id  BIGINT UNSIGNED NOT NULL,
  status      VARCHAR(20) NOT NULL DEFAULT 'DONE',
  note        TEXT,
  created_at  DATETIME(6),
  updated_at  DATETIME(6),
  UNIQUE KEY uq_diary_student (diary_id, student_id),
  KEY idx_dr_student (student_id),
  CONSTRAINT fk_dr_diary   FOREIGN KEY (diary_id)   REFERENCES diary(id)    ON DELETE CASCADE,
  CONSTRAINT fk_dr_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);
