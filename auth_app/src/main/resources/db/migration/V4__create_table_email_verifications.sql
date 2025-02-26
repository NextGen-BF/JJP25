create table email_verifications (
    code_expiration_date datetime(6),
    uuid binary(16) not null,
    code varchar(255),
    primary key (uuid)
);