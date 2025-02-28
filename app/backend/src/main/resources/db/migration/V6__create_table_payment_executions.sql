create table if not exists payment_executions (
    id integer not null auto_increment,
    action_type enum ('CANCELLATION','CHARGEBACK','PAYMENT','REFUND') not null,
    created_at datetime(6) not null,
    description TEXT,
    error_code varchar(40),
    external_payment_status varchar(50) not null,
    refund_expiration_date datetime(6) not null,
    refund_reason varchar(255),
    refunded_amount decimal(6,2),
    updated_at datetime(6),
    payment_id integer not null,
    user_ticket_id integer not null,
    primary key (id)
);