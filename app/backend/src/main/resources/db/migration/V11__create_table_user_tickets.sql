create table if not exists user_tickets (
    id integer not null auto_increment,
    bought_at datetime(6) not null,
    ticket_template_id integer not null,
    user_id integer not null,
    primary key (id)
);