alter table users
add constraint UKob8vaqnvmkoxxfx10tphyv1hi unique (email_verification_id);

alter table users
add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table users
add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table user_roles
add constraint FKhfh9dx7w3ubf1co1vdev94g3f
foreign key (user_id) references users (id);

alter table users
add constraint FKmphla94gah714990r9apprr2p
foreign key (email_verification_id) references email_verifications (id);