create table if not exists ticket_templates (
    id integer not null auto_increment,
    available_quantity integer not null,
    description TEXT,
    event_date datetime(6) not null,
    price decimal(6,2) not null,
    ticket_type enum ('GENERAL_ADMISSION','SEAT','VIP') not null,
    total_quantity integer not null,
    venue_type enum ('CINEMA_HALL','CONFERENCE_CENTRE','STADIUM','THEATRE') not null,
    event_id integer not null,
    primary key (id)
);