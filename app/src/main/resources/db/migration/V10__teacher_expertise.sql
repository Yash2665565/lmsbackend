-- Subjects a teacher is qualified to teach (expertise), independent of section allocation.
CREATE TABLE IF NOT EXISTS teacher_expertise (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT UNSIGNED NOT NULL,
    topic_id   BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NULL,
    CONSTRAINT uq_teacher_topic UNIQUE (teacher_id, topic_id),
    INDEX idx_te_teacher (teacher_id),
    INDEX idx_te_topic (topic_id)
);
