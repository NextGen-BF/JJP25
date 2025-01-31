create table if not exists rsvp_invitations (
    id integer not null auto_increment,
    expiration_date datetime(6) not null,
    sent_at datetime(6) not null,
    status enum ('ACCEPTED','DISMISSED','SENT') not null,
    event_id integer not null,
    receiver_id integer not null,
    sender_id integer not null,
    primary key (id)
);