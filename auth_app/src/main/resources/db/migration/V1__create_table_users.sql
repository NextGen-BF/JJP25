create table users (
     id bigint not null auto_increment,
     birth_date datetime(6) not null,
     email varchar(255) not null unique,
     is_enabled bit not null,
     fist_name varchar(255) not null,
     last_name varchar(255) not null,
     password varchar(255) not null,
     username varchar(255) not null unique,
     verification_code varchar(255),
     verification_expiration_date datetime(6),
     primary key (id)
);