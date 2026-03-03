CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE,
                       deleted boolean not null default false,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX unique_email_active
    ON users(email)
    WHERE deleted = false;

CREATE UNIQUE INDEX unique_username_active
    ON users(username)
    WHERE deleted = false;

create table categories (
                            id bigserial primary key,
                            name varchar(255) not null,
                            slug varchar(255) not null unique,
                            description text,
                            parent_id bigint,
                            deleted boolean not null default false,
                            created_at timestamp,
                            updated_at timestamp,
                            constraint fk_parent foreign key (parent_id) references categories(id)
);

CREATE INDEX idx_categories_slug ON categories(slug);

create table products (
                          id bigserial primary key,
                          version bigint not null default 0,
                          name varchar(255) not null,
                          slug varchar(255) not null,
                          description text,
                          price numeric(19,2) not null,
                          stock_quantity integer not null,
                          status varchar(50) not null,
                          category_id bigint not null,
                          deleted boolean not null default false,
                          created_at timestamp,
                          updated_at timestamp,
                          constraint fk_product_category foreign key (category_id)
                              references categories(id)
);

create index idx_product_category_status_price
    on products(category_id, status, price);

create index idx_product_created_at
    on products(created_at);

create index idx_product_name_trgm
    on products
    using gin (lower(name) gin_trgm_ops);

create unique index idx_product_slug_active
    on products(slug)
    where deleted = false;

CREATE TABLE product_images (
                                id BIGSERIAL PRIMARY KEY,
                                product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                image_url TEXT NOT NULL,
                                is_main BOOLEAN DEFAULT FALSE
);

create table carts (
                       id bigserial primary key,
                       user_id bigint not null unique,
                       created_at timestamp,
                       updated_at timestamp,
                       constraint fk_cart_user foreign key (user_id)
                           references users(id)
);

create table cart_items (
                            id bigserial primary key,
                            cart_id bigint not null,
                            product_id bigint not null,
                            quantity integer not null,
                            version bigint not null default 0,
                            constraint fk_cart_item_cart foreign key (cart_id)
                                references carts(id),
                            constraint fk_cart_item_product foreign key (product_id)
                                references products(id),
                            constraint uq_cart_product unique (cart_id, product_id)
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        status VARCHAR(30) NOT NULL,
                        total_amount BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id BIGINT,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INTEGER NOT NULL,
                             price BIGINT NOT NULL
);

CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL REFERENCES orders(id),
                          provider VARCHAR(50),
                          status VARCHAR(30),
                          transaction_id VARCHAR(255),
                          amount BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                token VARCHAR(512) NOT NULL UNIQUE,
                                user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                expiry_date TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);