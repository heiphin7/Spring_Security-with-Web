-- Создание таблицы verificationCode сначала
CREATE TABLE verificationCode (
    id BIGINT PRIMARY KEY,
    code BIGINT NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    isUsed BOOLEAN NOT NULL,
    attempts INT NOT NULL
);

-- Затем создание таблицы users_codes
create table users_codes
(
    verification_code_id bigint
        constraint fklx0qjphbg72u0vgqonu3khk5d
            references verificationCode(id),

    user_id              bigint not null,
    primary key (user_id, verification_code_id),

    constraint fk5ojiyxjxgs9re42y2gwfpn91w
        foreign key (user_id) references users
);
