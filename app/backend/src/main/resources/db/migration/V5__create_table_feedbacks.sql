create table if not exists feedbacks (
    id integer not null auto_increment,
    comment TEXT,
    created_at datetime(6) not null,
    rating integer not null,
    updated_at datetime(6) not null,
    event_id integer not null,
    user_id integer not null,
    primary key (id)
);