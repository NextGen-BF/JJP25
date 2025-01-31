use event_manager_system;

create table if not exists users (
	id int primary key,
	type enum('Attendee', 'Organiser') not null,
	phone varchar(15) unique,
	username varchar(255) unique not null,
	profile_pic varchar(255),
	created_at timestamp not null,
	updated_at timestamp not null
);
 
create table if not exists notifications (
	id int primary key,
	user_id int,
	type enum(
		'EventCancel', 
		'EventDonation', 
		'RSVP', 
		'UserPaymentStatus', 
		'AdminPaymentPeriod'
	) not null,
	description text not null,
	sent_at timestamp not null,
	constraint fk_notifications_users foreign key (user_id) 
	references users(id) on delete cascade
);

create table if not exists venues (
	id int primary key,
	country varchar(255) not null,
	city varchar(255) not null,
	address varchar(255) not null,
	name varchar(255) not null
);

create table if not exists event_dates (
	id int primary key,
	event_id int not null
	date timestamp not null,
	constraint fk_dates_events foreign key (event_id) references events(id) on delete cascade;
);

create table if not exists events (
	id int primary key,
	admin_id int not null,
	venue_id int not null,
	title varchar(255) not null, 
	description text not null, 
	category enum(
		'Concert',
		'Sport',
		'Seminar',
		'Theater',
		'Comedy Show',
		'Film Screening',
		'Workshop'
	),
	age_restriction tinyint,
	created_at timestamp not null,
	updated_at timestamp not null,
	charity_iban varchar(34),
	status enum(
		'Incoming', 
		'Ongoing', 
		'Completed', 
		'Cancelled'
	),
	constraint fk_events_users foreign key (admin_id) 
	references users(id) on delete cascade,
);

create table venues_events (
	venue_id int not null,
	event_id int not null,
	primary key (venue_id, event_id),
	constraint fk_venues foreign key (venue_id) references venues(id) on delete cascade,
	constraint fk_events foreign key (event_id) references events(id) on delete cascade
);


create table if not exists rsvp_invitations (
	id int primary key,
	sender_id int not null,
	receiver_id int not null,
	event_id int not null,
	status enum('Sent', 'Accepted', 'Dismissed') not null,
	sent_at timestamp not null,
	expiration_date timestamp not null,
	constraint fk_invitations_users_senders foreign key (sender_id) 
	references users(id) on delete cascade,
	constraint fk_invitations_users_receivers foreign key (receiver_id)
	references users(id) on delete cascade,
	constraint fk_invitaions_events foreign key (event_id)
	references events(id) on delete cascade
);

create table if not exists event_statistics (
	id int primary key,
	event_id int not null,
	total_tickets_sold int default 0,
	total_revenue decimal(6, 2) default 0.00,
	average_rating decimal(1, 1) default 0.0,
	feedback_count int default 0,
	last_updated timestamp,
	constraint fk_statistics_events foreign key (event_id)
	references events(id) on delete cascade
);

create table if not exists feedbacks (
	id int primary key,
	user_id int not null,
	event_id int not null,
	rating tinyint not null,
	comment text,
	created_at timestamp not null,
	updated_at timestamp not null,
	constraint fk_feedbacks_users foreign key (user_id) 
	references users(id) on delete cascade,
	constraint fk_feedbacks_events foreign key (event_id)
	references events(id) on delete cascade,
	constraint chk_rating check(rating between 1 and 5)
);

create table if not exists ticket_templates (
	id int primary key,
	event_id int not null,
	ticket_type enum(
		'General Admission',
		'Seat',
		'VIP'
	),
	venue_type enum(
		'Stadium',
		'Conference',
		'Centre',
		'Cinema Hall',
		'Theatre'
	),
	price decimal(4, 2) not null,
	event_date timestamp not null,
	description text,
	available_quantity int not null,
	total_quantity int not null,
	constraint chk_available_quantity check (available_quantity >= 0),
	constraint chk_total_quantity check (total_quantity >= 0),
	constraint fk_templates_events foreign key (event_id) references events(id) on delete cascade
);


create table if not exists user_tickets (
	id int primary key,
	ticket_template_id int not null,
	user_id int not null,
	bought_at timestamp not null,
	constraint fk_tickets_templates foreign key (ticket_template_id)
	references ticket_templates(id) on delete cascade,
	constraint fk_tickets_users foreign key (user_id)
	references users(id) on delete cascade
);

create table if not exists payments (
	id int primary key,
	user_id int not null,
	external_id varchar(64) not null,
	amount decimal(4, 2) not null,
	currency varchar(3) not null,
	payment_processor enum('Stripe'),
	payment_provider enum(
		'Revolut',
		'GooglePay',
		'ApplePay'
	) not null,
	payment_status enum(
		'Pending',
		'Completed',
		'Failed',
		'Canceled'
	) not null,
	created_at timestamp not null,
	updated_at timestamp,
	is_disputed boolean,
	constraint fk_payments_users foreign key (user_id)
	references users(id) on delete cascade
);

create table if not exists payment_executions (
	id int primary key,
	payment_id int not null,
	user_ticket_id int not null,
	external_payment_status varchar(50) not null,
	action_type enum(
		'Payment',
		'Refund',
		'Cancellation',
		'Chargeback'
	),
	description text,
	error_code varchar(40),
	created_at timestamp not null,
	updated_at timestamp,
	refund_expiration_date timestamp not null,
	refunded_amount decimal(4, 2),
	refunc_reason text,
	constraint fk_executions_payments foreign key (payment_id)
	references payments(id) on delete cascade,
	constraint fk_executions_tickets foreign key (user_ticket_id)
	references user_tickets(id) on delete cascade
);
