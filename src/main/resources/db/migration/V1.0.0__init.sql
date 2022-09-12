DROP TABLE IF EXISTS artist;
DROP TABLE IF EXISTS album;

CREATE TABLE artist
(
    artist_unique_id   BIGINT NOT NULL,
    artist_id          INT,
    artist_name        VARCHAR(255),
    primary_genre_name VARCHAR(255),
    CONSTRAINT pk_artist PRIMARY KEY (artist_unique_id)
);

CREATE TABLE album
(
    album_id           BIGINT NOT NULL,
    artist_id          INT,
    collection_id      INT,
    artist_name        VARCHAR(255),
    collection_name    VARCHAR(255),
    collection_price   DOUBLE,
    currency           VARCHAR(255),
    primary_genre_name VARCHAR(255),
    copyright          VARCHAR(255),
    CONSTRAINT pk_album PRIMARY KEY (album_id)
);