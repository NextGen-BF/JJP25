create table if not exists event_statistics (
    id integer not null auto_increment,
    average_rating decimal(1,1) not null,
    feedback_count integer not null,
    last_updated datetime(6),
    total_revenue decimal(6,2) not null,
    total_tickets_sold integer not null,
    event_id integer not null,
    primary key (id)
);