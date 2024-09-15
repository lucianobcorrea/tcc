CREATE TABLE roles (
   id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   role TEXT NOT NULL UNIQUE
);

CREATE TABLE users (
   id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   username TEXT NOT NULL,
   email TEXT NOT NULL UNIQUE,
   password TEXT NOT NULL,
   active BOOLEAN NOT NULL,
   role_id BIGINT NOT NULL,
   CONSTRAINT fk_role_user FOREIGN KEY(role_id) REFERENCES roles
);

CREATE TABLE revoked_tokens (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    token TEXT NOT NULL UNIQUE,
    revoked_at TIMESTAMP NOT NULL
);

CREATE TABLE areas (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    slug TEXT NOT NULL UNIQUE,
    image TEXT NOT NULL,
    active BOOLEAN NOT NULL
);

ALTER TABLE areas
DROP COLUMN active;

CREATE TABLE user_areas (
    user_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    CONSTRAINT fk_user_areas_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_areas_area FOREIGN KEY(area_id) REFERENCES areas(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, area_id)
);

CREATE TABLE reasons (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title TEXT NOT NULL,
    slug TEXT NOT NULL UNIQUE,
    image TEXT NOT NULL,
    active BOOLEAN NOT NULL
);

ALTER TABLE reasons
DROP COLUMN active;

CREATE TABLE user_reasons (
    user_id BIGINT NOT NULL,
    reason_id BIGINT NOT NULL,
    CONSTRAINT fk_user_areas_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_reasons_reason FOREIGN KEY(reason_id) REFERENCES reasons(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, reason_id)
);

CREATE TABLE study_days (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    week_day VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE user_days (
    user_id BIGINT NOT NULL,
    week_day_id BIGINT NOT NULL,
    CONSTRAINT fk_user_areas_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_study_days FOREIGN KEY(week_day_id) REFERENCES study_days(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, week_day_id)
);

CREATE TABLE study_durations (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    duration VARCHAR(50) NOT NULL UNIQUE,
    image TEXT NOT NULL
);

ALTER TABLE users
ADD initial_form BOOLEAN NOT NULL DEFAULT(false);

ALTER TABLE users
ADD study_durations_id BIGINT;

ALTER TABLE users
ADD CONSTRAINT fk_user_study_durations FOREIGN KEY(study_durations_id) REFERENCES study_durations(id) ON DELETE CASCADE;
