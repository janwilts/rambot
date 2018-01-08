\c bigmovie

-- Reset queries;
DROP TABLE IF EXISTS movie_country;
DROP TABLE IF EXISTS gross;
DROP SEQUENCE IF EXISTS gross_id_seq;
DROP TABLE IF EXISTS country;
DROP SEQUENCE IF EXISTS country_id_seq;
DROP TABLE IF EXISTS movie_genre;
DROP TABLE IF EXISTS genre;
DROP SEQUENCE IF EXISTS genre_id_seq;
DROP TABLE IF EXISTS soundtrack;
DROP SEQUENCE IF EXISTS soundtrack_id_seq;
DROP TABLE IF EXISTS actor_movie;
DROP TABLE IF EXISTS movie;
DROP SEQUENCE IF EXISTS movie_id_seq;
DROP TABLE IF EXISTS actor;
DROP SEQUENCE IF EXISTS actor_id_seq;

\c postgres;
DROP DATABASE IF EXISTS bigmovie;
REASSIGN OWNED BY bigmovie_admin TO postgres;
REASSIGN OWNED BY bigmovie_ro TO postgres;
DROP OWNED BY bigmovie_admin;
DROP OWNED BY bigmovie_ro;
DROP USER IF EXISTS bigmovie_admin;
DROP USER IF EXISTS bigmovie_ro;

-- Create superuser and read only user
CREATE USER bigmovie_admin WITH PASSWORD '***';
CREATE USER bigmovie_ro WITH PASSWORD '***';

CREATE DATABASE bigmovie;

\c bigmovie;

-- Revoke all permissions to be reset later
REVOKE ALL ON DATABASE bigmovie FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;

-- Make users able to connect
GRANT CONNECT ON DATABASE bigmovie TO bigmovie_admin;
GRANT CONNECT ON DATABASE bigmovie TO bigmovie_ro;

GRANT USAGE ON SCHEMA public TO bigmovie_admin;
GRANT USAGE ON SCHEMA public TO bigmovie_ro;

-- Give superuser access to everything
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLES TO bigmovie_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, UPDATE ON SEQUENCES TO bigmovie_admin;

-- Give read only user only access to selecting
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT ON TABLES TO bigmovie_ro;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT ON SEQUENCES TO bigmovie_ro;

CREATE SEQUENCE movie_id_seq;
CREATE TABLE movie (
  movie_id         BIGINT       NOT NULL DEFAULT nextval('movie_id_seq'),
  -- From movies
  title            VARCHAR(255) NOT NULL,
  release_year     INTEGER,
  occurance        INTEGER      NOT NULL,
  -- From MPAA
  mpaa_rating      VARCHAR(5),
  mpaa_reason      TEXT,
  -- From ratings
  rating           INTEGER,
  rating_votes     INTEGER,
  budget           MONEY,
  -- From business
  production_costs MONEY,
  CONSTRAINT movie_pkey
  PRIMARY KEY (movie_id),
  CONSTRAINT movie_uniq
  UNIQUE (title, release_year, occurance)
);

CREATE SEQUENCE actor_id_seq;
CREATE TABLE actor (
  actor_id   BIGINT       NOT NULL DEFAULT nextval('actor_id_seq'),
  -- From actors & actresses
  name       VARCHAR(255) NOT NULL,
  occurance  INTEGER      NOT NULL,
  gender     CHAR(1)      NOT NULL,
  -- From biographies
  birth_date DATE,
  CONSTRAINT actor_pkey
  PRIMARY KEY (actor_id),
  CONSTRAINT actor_uniq
  UNIQUE (name, occurance)
);

CREATE TABLE actor_movie (
  movie_id BIGINT NOT NULL,
  actor_id BIGINT NOT NULL,
  role     VARCHAR(255),
  CONSTRAINT actor_movie_pkey
  PRIMARY KEY (movie_id, actor_id),
  CONSTRAINT actor_movie_movie_id_fkey
  FOREIGN KEY (movie_id)
  REFERENCES movie (movie_id),
  CONSTRAINT actor_movie_actor_id_fkey
  FOREIGN KEY (actor_id)
  REFERENCES actor (actor_id)
);

CREATE SEQUENCE country_id_seq;
CREATE TABLE country (
  country_id INTEGER      NOT NULL DEFAULT nextval('country_id_seq'),
  country    VARCHAR(255) NOT NULL,
  CONSTRAINT country_pkey
  PRIMARY KEY (country_id)
);

CREATE TABLE movie_country (
  movie_id   BIGINT  NOT NULL,
  country_id INTEGER NOT NULL,
  CONSTRAINT movie_country_pkey
  PRIMARY KEY (movie_id, country_id),
  CONSTRAINT movie_country_movie_id_fkey
  FOREIGN KEY (movie_id)
  REFERENCES movie (movie_id),
  CONSTRAINT movie_country_country_id_fkey
  FOREIGN KEY (country_id)
  REFERENCES country (country_id)
);

CREATE SEQUENCE genre_id_seq;
CREATE TABLE genre (
  -- From genres
  genre_id INTEGER      NOT NULL DEFAULT nextval('genre_id_seq'),
  genre    VARCHAR(255) NOT NULL,
  CONSTRAINT genre_pkey
  PRIMARY KEY (genre_id)
);

CREATE TABLE movie_genre (
  movie_id BIGINT  NOT NULL,
  genre_id INTEGER NOT NULL,
  CONSTRAINT movie_genre_pkey
  PRIMARY KEY (movie_id, genre_id),
  CONSTRAINT movie_genre_movie_id_fkey
  FOREIGN KEY (movie_id)
  REFERENCES movie (movie_id),
  CONSTRAINT movie_genre_genre_id
  FOREIGN KEY (genre_id)
  REFERENCES genre (genre_id)
);

CREATE SEQUENCE soundtrack_id_seq;
CREATE TABLE soundtrack (
  -- From soundtracks
  soundtrack_id BIGINT       NOT NULL DEFAULT nextval('soundtrack_id_seq'),
  movie_id      BIGINT       NOT NULL,
  song          VARCHAR(255) NOT NULL,
  CONSTRAINT soundtrack_pkey
  PRIMARY KEY (soundtrack_id),
  CONSTRAINT soundtrack_movie_id_fkey
  FOREIGN KEY (movie_id)
  REFERENCES movie (movie_id)
);

CREATE SEQUENCE gross_id_seq;
CREATE TABLE gross (
  -- From business
  gross_id         BIGINT NOT NULL DEFAULT nextval('gross_id_seq'),
  movie_id         BIGINT NOT NULL,
  country_id       INTEGER,
  amount           MONEY,
  transaction_date DATE,
  CONSTRAINT gross_pkey
  PRIMARY KEY (gross_id),
  CONSTRAINT gross_movie_id_fkey
  FOREIGN KEY (movie_id)
  REFERENCES movie (movie_id),
  CONSTRAINT gross_country_id_fkey
  FOREIGN KEY (country_id)
  REFERENCES country (country_id)
);