-- Миграция для удаления таблицы users_uuid_codes
-- Удаляем данную таблицу, так как мы используем связь прямо в таблице uuid_codes, явно указывая user_id

DROP TABLE IF EXISTS users_uuid_codes;
