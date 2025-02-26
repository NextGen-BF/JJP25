create table email_verifications (
    code_expiration_date datetime(6),
    id bigint not null auto_increment,
    uuid varchar(255),
    code varchar(255),
    primary key (id)
);