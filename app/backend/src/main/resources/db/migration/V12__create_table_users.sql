create table if not exists users (
    id integer not null auto_increment,
    created_at datetime(6) not null,
    phone varchar(15) not null,
    profile_pic varchar(255),
    type enum ('ATTENDEE','ORGANISER') not null,
    updated_at datetime(6) not null,
    primary key (id)
);