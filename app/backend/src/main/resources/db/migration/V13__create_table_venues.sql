create table if not exists venues (
    id integer not null auto_increment,
    phone varchar(15),
    country varchar(255) not null,
    city varchar(255) not null,
    address varchar(255) not null,
    name varchar(255) not null,
    website varchar(255),
    primary key (id)
);