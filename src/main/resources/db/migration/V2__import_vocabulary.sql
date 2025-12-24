-- V2: Import vocabulary CSV into `word` table
-- Place the CSV file `vocabulary_9000_adaptive.csv` in a path accessible to the DB server
-- Recommended location for MySQL when running locally:
--   src/main/resources/data/vocabulary_9000_adaptive.csv

-- Example (MySQL) - adjust path to the actual filesystem location and ensure
-- `local_infile` is enabled in the client and server if using LOAD DATA LOCAL INFILE.

-- Example 1: LOAD DATA LOCAL INFILE (MySQL) with D: drive absolute path
-- Windows example (use double backslashes or forward slashes):
-- LOAD DATA LOCAL INFILE 'D:/vocab/vocabulary_9000_adaptive.csv'
-- INTO TABLE `word`
-- FIELDS TERMINATED BY ',' ENCLOSED BY '"'
-- LINES TERMINATED BY '\n'
-- IGNORE 1 LINES
-- (`word`, `korean`, `detail_section`);

-- Example 2: LOAD DATA LOCAL INFILE using backslashes (escape backslashes)
-- LOAD DATA LOCAL INFILE 'D:\\vocab\\vocabulary_9000_adaptive.csv'
-- INTO TABLE `word` ...

-- If using H2 for local development, use the CSVREAD function in an INSERT:
-- INSERT INTO word (word, korean, detail_section)
-- SELECT * FROM CSVREAD('src/main/resources/data/vocabulary_9000_adaptive.csv');

-- PowerShell copy example (copy from D: to project resources):
-- Copy-Item -Path "D:\vocab\vocabulary_9000_adaptive.csv" -Destination "src\\main\\resources\\data\\vocabulary_9000_adaptive.csv" -Force

-- Alternative: write a small Spring Boot data loader that reads the CSV from
-- classpath and persists Word entities via Spring Data JPA (recommended for portability).

-- End of V2
