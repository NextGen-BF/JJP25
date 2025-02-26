alter table email_verifications
add constraint UKeeueurj7th7upmx2wakspc8qr unique (user_id);

alter table users
add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table users
add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table email_verifications
add constraint FKou1xuy3rdeao75p2x1v4v3xs
foreign key (user_id) references users (id);

alter table user_roles
add constraint FKhfh9dx7w3ubf1co1vdev94g3f
foreign key (user_id) references users (id);