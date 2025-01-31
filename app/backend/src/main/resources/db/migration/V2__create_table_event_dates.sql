create table if not exists event_dates (
    id integer not null auto_increment,
    date datetime(6) not null,
    event_id integer not null,
    primary key (id)
);