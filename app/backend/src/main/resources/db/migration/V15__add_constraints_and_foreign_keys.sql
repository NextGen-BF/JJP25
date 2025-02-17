alter table event_statistics add constraint UKj4v94ymapgf9d18jy1vl0rxre unique (event_id);
alter table payment_executions add constraint UK5lhi08k1ssj2qk5505gl3w8c7 unique (user_ticket_id);
alter table users add constraint UKdu5v5sr43g5bfnji4vb8hg5s3 unique (phone);

alter table event_dates
add constraint FKt6eutgjmpfnfjlnc1j94wif88
foreign key (event_id)
references events (id);

alter table event_statistics
add constraint FK74wfcqq5hmtp6kdy36hjh7ch8
foreign key (event_id)
references events (id);

alter table events
add constraint FK3gfhv8wa98swm87l76b322mmb
foreign key (admin_id)
references users (id);

alter table feedbacks
add constraint FKfr1mdb1ux17qw5k0xobnckww0
foreign key (event_id)
references events (id);

alter table feedbacks
add constraint FK312drfl5lquu37mu4trk8jkwx
foreign key (user_id)
references users (id);

alter table notifications
add constraint FK9y21adhxn0ayjhfocscqox7bh
foreign key (user_id)
references users (id);

alter table payment_executions
add constraint FKhv413nd2l0kewgh8sjgrf0kce
foreign key (payment_id)
references payments (id);

alter table payment_executions
add constraint FK6lxtl6e08kqn661qp17rp32tg
foreign key (user_ticket_id)
references user_tickets (id);

alter table payments
add constraint FKs3ufuyecnucmu8dbeodxq7l80
foreign key (receiver_id)
references users (id);

alter table payments
add constraint FK6i0puhywkb4x91syggfrdegev
foreign key (sender_id)
references users (id);

alter table rsvp_invitations
add constraint FK7k7chx6ajtxgk3adcjs94d8s8
foreign key (event_id)
references events (id);

alter table rsvp_invitations
add constraint FKeb4xun63jobbpq6v2r26km7yy
foreign key (receiver_id)
references users (id);

alter table rsvp_invitations
add constraint FK1mj9p94xkmt0uyenfgcau94ly
foreign key (sender_id)
references users (id);

alter table ticket_templates
add constraint FKoo3h3ej26r2s5c5nqlpkehxe1
foreign key (event_id)
references events (id);

alter table user_tickets
add constraint FKqme1d4hgvbto9vd4faoxc2fq9
foreign key (ticket_template_id)
references ticket_templates (id);

alter table user_tickets
add constraint FK9ok1dlkt49it7wyeh529wy315
foreign key (user_id)
references users (id);

alter table venues_events
add constraint FKsijnu5vq3xnwpjhnsy3qgqdc7
foreign key (venue_id)
references venues (id);

alter table venues_events
add constraint FKg0rafnj3gmshfmsbeq35yy5xd
foreign key (event_id)
references events (id);