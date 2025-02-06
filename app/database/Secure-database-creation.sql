use ems_auth_db;

create table if not exists secure_user_info (
	id primary key,
	username varchar(255) unique not null,
	password varchar(255) not null,
	birth_date timestamp not null,
	email varchar(255) unique not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null
);