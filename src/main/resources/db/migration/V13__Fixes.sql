
ALTER TABLE `person`       CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `books`        CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `person_books` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `person_books_backup`;
CREATE TABLE `person_books_backup` AS
SELECT `person_id`, `book_id` FROM `person_books`;


DROP TABLE `person_books`;

ALTER TABLE `books`
    MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN `launch_date` DATE NOT NULL;


CREATE TABLE `person_books` (
                                `person_id` BIGINT NOT NULL,
                                `book_id`   BIGINT NOT NULL,
                                PRIMARY KEY (`person_id`, `book_id`),
                                CONSTRAINT `fk_person_books_person`
                                    FOREIGN KEY (`person_id`) REFERENCES `person`(`id`) ON DELETE CASCADE,
                                CONSTRAINT `fk_person_books_book`
                                    FOREIGN KEY (`book_id`)   REFERENCES `books`(`id`)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `person_books` (`person_id`, `book_id`)
SELECT `person_id`, `book_id` FROM `person_books_backup`;

DROP TABLE `person_books_backup`;

ALTER TABLE `books`
    MODIFY COLUMN `price` DOUBLE NOT NULL;