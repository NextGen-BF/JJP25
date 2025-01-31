create table if not exists venues (
    id integer not null auto_increment,
    country varchar(255) not null,
    city varchar(255) not null,
    address varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
);