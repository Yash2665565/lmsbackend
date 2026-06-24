-- Transportation module: routes, buses (with driver/conductor), student assignments.

CREATE TABLE IF NOT EXISTS transport_routes (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL,
    description VARCHAR(500),
    fare        DECIMAL(10,2) DEFAULT 0,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS buses (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bus_number      VARCHAR(50)   NOT NULL,
    route_id        BIGINT UNSIGNED,
    capacity        INT,
    driver_name     VARCHAR(120),
    driver_phone    VARCHAR(30),
    conductor_name  VARCHAR(120),
    conductor_phone VARCHAR(30),
    pickup_time     VARCHAR(20),
    drop_time       VARCHAR(20),
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bus_route FOREIGN KEY (route_id) REFERENCES transport_routes (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS student_transport (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT UNSIGNED NOT NULL,
    bus_id      BIGINT UNSIGNED NOT NULL,
    pickup_stop VARCHAR(150),
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_transport UNIQUE (student_id),
    CONSTRAINT fk_st_bus FOREIGN KEY (bus_id) REFERENCES buses (id) ON DELETE CASCADE,
    INDEX idx_st_student (student_id)
);
