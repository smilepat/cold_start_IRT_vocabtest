# DB migration helper files for IRT/CAT

Files created:

- V1__create_irt_tables.sql  — CREATE TABLE statements for IRT/CAT tables
- V2__import_vocabulary.sql  — CSV import examples (MySQL LOAD DATA / H2 CSVREAD)

How to use

1. Place `vocabulary_9000_adaptive.csv` under `src/main/resources/data/` or any
   filesystem path accessible to your DB server.

2. Apply V1 to create tables (use your DB client):

   mysql> SOURCE /absolute/path/to/V1__create_irt_tables.sql;

3. Import CSV (MySQL example):

   mysql> LOAD DATA LOCAL INFILE '/absolute/path/to/vocabulary_9000_adaptive.csv'
     INTO TABLE word
     FIELDS TERMINATED BY ',' ENCLOSED BY '"'
     LINES TERMINATED BY '\n'
     IGNORE 1 LINES
     (word, korean, detail_section);

Notes

- The import step assumes CSV columns: `word`, `korean`, `detail_section` in that order.
- For production migrations, convert these SQL files to your migration tool format (Flyway/Liquibase).
- Consider partitioning `word_response_log` and adding retention policy if logs grow large.
