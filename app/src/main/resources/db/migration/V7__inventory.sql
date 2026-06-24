-- Inventory module: categories, items (with stock), stock movement log.

CREATE TABLE IF NOT EXISTS inventory_categories (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(120) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_items (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(180) NOT NULL,
    category_id   BIGINT UNSIGNED,
    sku           VARCHAR(80),
    unit          VARCHAR(30)  DEFAULT 'pcs',
    quantity      INT          NOT NULL DEFAULT 0,
    reorder_level INT          DEFAULT 0,
    location      VARCHAR(150),
    notes         VARCHAR(400),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES inventory_categories (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS inventory_txns (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    item_id     BIGINT UNSIGNED NOT NULL,
    type        VARCHAR(20)     NOT NULL,           -- IN | OUT | SET
    quantity    INT             NOT NULL,           -- signed delta applied
    note        VARCHAR(300),
    created_by  BIGINT UNSIGNED,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_item FOREIGN KEY (item_id) REFERENCES inventory_items (id) ON DELETE CASCADE,
    INDEX idx_txn_item (item_id)
);
