CREATE DATABASE users;

CREATE SCHEMA IF NOT EXISTS users_scheme;

CREATE TABLE IF NOT EXISTS users_scheme.genders (
                                                    gender_id SERIAL PRIMARY KEY,
                                                    gender_description VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS users_scheme.users (
                                                  user_id SERIAL PRIMARY KEY,
                                                  first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    gender_id INTEGER REFERENCES users_scheme.genders(gender_id),
    date_of_birth DATE,
    city VARCHAR(255),
    avatar_url VARCHAR,
    user_info VARCHAR,
    user_nickname VARCHAR NOT NULL UNIQUE,
    email VARCHAR NOT NULL UNIQUE,
    phone_number VARCHAR UNIQUE,
    hard_skills VARCHAR,
    deleted boolean
    );

CREATE TABLE IF NOT EXISTS users_scheme.subscriptions (
                                                          id SERIAL PRIMARY KEY,
                                                          user_id1 INTEGER REFERENCES users_scheme.users(user_id) ON DELETE SET NULL,
    user_id2 INTEGER REFERENCES users_scheme.users(user_id) ON DELETE SET NULL
    );