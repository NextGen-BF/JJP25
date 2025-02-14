create table if not exists user_roles (
    user_id bigint not null,
    role enum ('ROLE_ADMIN','ROLE_USER')
);

alter table user_roles
add constraint FKhfh9dx7w3ubf1co1vdev94g3f
foreign key (user_id)
references users (id);