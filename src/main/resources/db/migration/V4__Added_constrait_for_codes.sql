-- Удален столбец isActive и добавлен lastSendedCode
ALTER TABLE users
DROP COLUMN isactive;

ALTER TABLE users
    ADD COLUMN lastSendedCode TIMESTAMP WITH TIME ZONE;

ALTER TABLE users_codes
DROP CONSTRAINT fklx0qjphbg72u0vgqonu3khk5d