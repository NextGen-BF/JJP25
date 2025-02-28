create table if not exists notifications (
    id integer not null auto_increment,
    description TEXT not null,
    sent_at datetime(6) not null,
    type enum ('ADMIN_PAYMENT_PERIOD','EVENT_CANCEL','EVENT_DONATION','RSVP','USER_PAYMENTS_STATUS') not null,
    user_id integer not null,
    primary key (id)
);