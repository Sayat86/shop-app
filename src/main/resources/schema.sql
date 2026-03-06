DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(255),
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE,
                       deleted boolean not null default false,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE UNIQUE INDEX IF NOT EXISTS unique_email_active
    ON users(email)
    WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS unique_username_active
    ON users(username)
    WHERE deleted = false;

create table IF NOT EXISTS categories (
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

CREATE INDEX IF NOT EXISTS idx_categories_slug ON categories(slug);

create table IF NOT EXISTS brands (
                                      id bigserial primary key,
                                      name varchar(255) not null,
    slug varchar(255) not null unique,
    created_at timestamp
    );

CREATE TABLE IF NOT EXISTS products (

                                        id BIGSERIAL PRIMARY KEY,
                                        version BIGINT NOT NULL DEFAULT 0,

                                        name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,

    status VARCHAR(50) NOT NULL,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    views BIGINT NOT NULL DEFAULT 0,

    average_rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    review_count INTEGER NOT NULL DEFAULT 0,

    category_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_product_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id),

    CONSTRAINT fk_product_brand
    FOREIGN KEY (brand_id)
    REFERENCES brands(id)
);

-- slug (soft delete)
CREATE UNIQUE INDEX IF NOT EXISTS idx_product_slug_active
    ON products(slug)
    WHERE deleted = false;

-- категория
CREATE INDEX IF NOT EXISTS idx_product_category
    ON products(category_id);

-- бренд
CREATE INDEX IF NOT EXISTS idx_product_brand
    ON products(brand_id);

-- популярные товары
CREATE INDEX IF NOT EXISTS idx_product_views
    ON products(views DESC);

-- сортировка новых товаров
CREATE INDEX IF NOT EXISTS idx_product_created_at
    ON products(created_at DESC);

-- поиск
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_product_name_trgm
    ON products
    USING gin (lower(name) gin_trgm_ops);

create table IF NOT EXISTS product_variants (

                                                id bigserial primary key,
                                                sku varchar(255) not null unique,
    product_id bigint not null,
    price numeric(19,2) not null,
    stock_quantity integer not null,
    deleted boolean not null default false,
    created_at timestamp,
    constraint fk_variant_product
    foreign key (product_id)
    references products(id)
    );

create index IF NOT EXISTS idx_variant_product
    on product_variants(product_id);

create index IF NOT EXISTS idx_variant_price
    on product_variants(price);

create table IF NOT EXISTS variant_attributes (
                                                  id bigserial primary key,
                                                  name varchar(255) not null
    );

create table IF NOT EXISTS product_images (
                                id bigserial primary key,
                                product_id bigint not null,
                                url text not null,
                                main_image boolean not null default false,
                                created_at timestamp,
                                constraint fk_product_image_product
                                    foreign key (product_id) references products(id)
);
create index IF NOT EXISTS idx_product_images_product
    on product_images(product_id);

create table IF NOT EXISTS carts (
                       id bigserial primary key,
                       user_id bigint not null unique,
                       created_at timestamp,
                       updated_at timestamp,
                       constraint fk_cart_user foreign key (user_id)
                           references users(id)
);

create table IF NOT EXISTS cart_items (

                            id bigserial primary key,
                            cart_id bigint not null,
                            variant_id bigint not null,
                            quantity integer not null,
                            version bigint not null default 0,
                            constraint fk_cart_item_cart
                                foreign key (cart_id)
                                    references carts(id),
                            constraint fk_cart_item_variant
                                foreign key (variant_id)
                                    references product_variants(id),
                            constraint uk_cart_variant
                                unique(cart_id, variant_id)
);
create index IF NOT EXISTS idx_cart_item_variant
    on cart_items(variant_id);

create table IF NOT EXISTS orders (
                        id bigserial primary key,
                        order_number varchar(50) UNIQUE,
                        user_id bigint not null,
                        total_amount numeric(19,2) not null,
                        status varchar(50) not null,
                        created_at timestamp not null,
                        email varchar(255),
                        phone varchar(50),
                        address varchar(255),
                        city varchar(100),
                        postal_code varchar(50),
                        constraint fk_order_user foreign key (user_id)
                            references users(id)
);

create table IF NOT EXISTS order_items (

                             id bigserial primary key,
                             order_id bigint not null,
                             variant_id bigint not null,
                             product_name varchar(255) not null,
                             sku varchar(255) not null,
                             price numeric(19,2) not null,
                             quantity integer not null,
                             constraint fk_order_item_order
                                 foreign key (order_id)
                                     references orders(id),
                             constraint fk_order_item_variant
                                 foreign key (variant_id)
                                     references product_variants(id)
);
create index IF NOT EXISTS idx_order_item_order
    on order_items(order_id);

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                token VARCHAR(512) NOT NULL UNIQUE,
                                user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                expiry_date TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table IF NOT EXISTS order_history (
                               id bigserial primary key,
                               order_id bigint not null,
                               event_type varchar(50) not null,
                               created_at timestamp,
                               constraint fk_order_history_order
                                   foreign key (order_id) references orders(id)
);

create table IF NOT EXISTS variant_attribute_values (
                                          id bigserial primary key,
                                          variant_id bigint not null,
                                          attribute_id bigint not null,
                                          value varchar(255) not null,
                                          constraint fk_variant_attr_variant
                                              foreign key (variant_id)
                                                  references product_variants(id),
                                          constraint fk_variant_attr_attribute
                                              foreign key (attribute_id)
                                                  references variant_attributes(id)
);

create index IF NOT EXISTS idx_variant_attr_variant
    on variant_attribute_values(variant_id);

create index IF NOT EXISTS idx_variant_attr_attribute
    on variant_attribute_values(attribute_id);

DROP MATERIALIZED VIEW IF EXISTS product_catalog;
CREATE MATERIALIZED VIEW product_catalog AS

SELECT
    p.id,
    p.name,
    p.slug,
    MIN(v.price) AS price,
    i.url AS image_url,
    p.average_rating,
    p.review_count

FROM products p

         LEFT JOIN product_variants v
                   ON v.product_id = p.id AND v.deleted = false

         LEFT JOIN product_images i
                   ON i.product_id = p.id AND i.main_image = true

WHERE p.deleted = false

GROUP BY
    p.id,
    p.name,
    p.slug,
    i.url,
    p.average_rating,
    p.review_count;

CREATE INDEX IF NOT EXISTS idx_catalog_slug
    ON product_catalog(slug);

CREATE INDEX IF NOT EXISTS idx_catalog_price
    ON product_catalog(price);

create table IF NOT EXISTS payments (

                          id bigserial primary key,
                          order_id bigint not null unique,
                          amount numeric(19,2) not null,
                          status varchar(50) not null,
                          provider varchar(50),
                          created_at timestamp,
                          constraint fk_payment_order
                              foreign key (order_id)
                                  references orders(id)
);

create table IF NOT EXISTS stock_reservations (

                                                  id bigserial primary key,
                                                  variant_id bigint not null,
                                                  quantity integer not null,
                                                  status varchar(50) not null default 'ACTIVE',
    expires_at timestamp not null,
    constraint fk_reservation_variant
    foreign key (variant_id)
    references product_variants(id)
    );

create index IF NOT EXISTS idx_stock_reservation_expiration
    on stock_reservations(expires_at);