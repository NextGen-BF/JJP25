create table user_roles (
    user_id bigint not null,
    role enum ('ROLE_ADMIN','ROLE_USER')
);

