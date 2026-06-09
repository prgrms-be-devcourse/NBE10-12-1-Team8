CREATE TABLE product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    create_date DATETIME(6),
    modify_date DATETIME(6),
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    description VARCHAR(1000),
    image_url VARCHAR(500),
    selling BIT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    create_date DATETIME(6),
    modify_date DATETIME(6),
    email VARCHAR(255) NOT NULL,
    shipping_date DATETIME(6) NOT NULL,
    address VARCHAR(500) NOT NULL,
    zipcode VARCHAR(20) NOT NULL,
    order_at DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    create_date DATETIME(6),
    modify_date DATETIME(6),
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_order_item_order_product UNIQUE (order_id, product_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
