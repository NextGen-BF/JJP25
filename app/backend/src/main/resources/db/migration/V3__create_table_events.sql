create table if not exists events (
    id integer not null auto_increment,
    age_restriction integer,
    category enum ('COMEDY_SHOW','CONCERT','FILM_SCREENING','SEMINAR','SPORT','THEATER','WORKSHOP') not null,
    charity_iban varchar(34),
    created_at datetime(6) not null,
    description TEXT not null,
    status enum ('CANCELLED','COMPLETED','INCOMING','ONGOING') not null,
    title varchar(255) not null,
    updated_at datetime(6) not null,
    admin_id integer not null,
    primary key (id)
);