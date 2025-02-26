create table users (
    is_enabled bit not null,
    birth_date datetime(6) not null,
    id bigint not null auto_increment,
    email varchar(255) not null,
    fist_name varchar(255) not null,
    last_name varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);