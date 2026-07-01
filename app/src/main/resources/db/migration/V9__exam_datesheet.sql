-- Datesheet: each exam paper (exam_subject) gets a date and start time.
ALTER TABLE exam_subjects ADD COLUMN exam_date  DATE         NULL;
ALTER TABLE exam_subjects ADD COLUMN start_time VARCHAR(20)  NULL;
