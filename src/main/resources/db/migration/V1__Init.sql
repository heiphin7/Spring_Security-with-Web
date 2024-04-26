create table roles
(
    id   bigint not null
        primary key,
    name varchar(255)
);


create table users
(
    id       bigserial
        primary key,
    email    varchar(255),
    isActive BOOLEAN,
    password varchar(255),
    username varchar(255)
);


create table blog
(
    id        bigint not null
        primary key,
    anons     text,
    fulltext  text,
    image     text,
    title     varchar(255),
    author_id bigint
        constraint fk4v4ymc0irlkpq9aowljw4mdrs
            references users
);


create table users_roles
(
    user_id bigint not null
        constraint fk2o0jvgh89lemvvo17cbqvdxaa
            references users,

    role_id bigint not null
        constraint fkj6m8fwv7oqv74fcehir1a9ffy
            references roles
);


create table user_favorite_blogs
(
    user_id bigint not null
        constraint fk7sd8u1se3b9n598m27vadcr9y
            references users,
    blog_id bigint
);

insert into roles (id, name) VALUES (1, 'ROLE_USER')


