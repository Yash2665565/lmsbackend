-- Fees module: fee heads (categories), per-section fee structure, per-student status.

CREATE TABLE IF NOT EXISTS fee_heads (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(300),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fee_structures (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    section_id  BIGINT UNSIGNED NOT NULL,
    fee_head_id BIGINT UNSIGNED NOT NULL,
    amount      DECIMAL(10,2)   NOT NULL DEFAULT 0,
    frequency   VARCHAR(30)     DEFAULT 'Annual',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fs_head FOREIGN KEY (fee_head_id) REFERENCES fee_heads (id) ON DELETE CASCADE,
    CONSTRAINT uq_section_head UNIQUE (section_id, fee_head_id),
    INDEX idx_fs_section (section_id)
);

CREATE TABLE IF NOT EXISTS student_fees (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id       BIGINT UNSIGNED NOT NULL,
    fee_structure_id BIGINT UNSIGNED NOT NULL,
    status           VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    amount_paid      DECIMAL(10,2)   DEFAULT 0,
    remarks          VARCHAR(300),
    paid_at          DATETIME,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sfee_struct FOREIGN KEY (fee_structure_id) REFERENCES fee_structures (id) ON DELETE CASCADE,
    CONSTRAINT uq_student_struct UNIQUE (student_id, fee_structure_id),
    INDEX idx_sfee_student (student_id)
);
