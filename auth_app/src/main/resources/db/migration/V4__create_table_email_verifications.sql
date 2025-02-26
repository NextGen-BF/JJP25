create table email_verifications (
    code_expiration_date datetime(6),
    user_id bigint unique,
    uuid binary(16) not null,
    code varchar(255),
    primary key (uuid)
);