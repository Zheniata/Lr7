BEGIN;

CREATE TYPE IF NOT EXISTS organization_type AS ENUM (
    'TRUST',
    'PRIVATE_LIMITED_COMPANY',
    'OPEN_JOINT_STOCK_COMPANY');

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    salt VARCHAR(30) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE IF NOT EXISTS organization (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    coordinates_x DOUBLE PRECISION,
    coordinates_y BIGINT,
    annual_turnover FLOAT CHECK (annual_turnover >= 0),
    type organization_type NOT NULL,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

END



