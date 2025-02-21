create table if not exists users (
    id integer not null,
    created_at datetime(6) not null,
    phone varchar(15),
    profile_picture varchar(255),
    type enum ('ATTENDEE','ORGANISER') not null,
    updated_at datetime(6) not null,
    primary key (id)
);