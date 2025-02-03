create table if not exists payments (
    id integer not null auto_increment,
    amount decimal(6,2) not null,
    created_at datetime(6) not null,
    currency varchar(3) not null,
    external_id varchar(64) not null,
    is_disputed bit,
    payment_processor enum ('STRIPE') not null,
    payment_provider enum ('APPLE_PAY','GOOGLE_PAY','REVOLUT') not null,
    payment_status enum ('CANCELLED','COMPLETED','FAILED','PENDING') not null,
    updated_at datetime(6),
    user_id integer not null,
    primary key (id)
);