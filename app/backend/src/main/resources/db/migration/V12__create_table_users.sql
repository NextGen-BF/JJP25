create table if not exists users (
    id integer not null,
    created_at datetime(6) not null default current_timestamp(6),
    phone varchar(15) default null,
    profile_picture varchar(255) default null,
    type enum ('ATTENDEE','ORGANISER') not null,
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    primary key (id)
);