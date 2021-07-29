DROP TABLE IF EXISTS `tb_nominated_movies`;
DROP TABLE IF EXISTS `tb_movie_rating`;


CREATE TABLE `tb_nominated_movies` (
  `movie_id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(200) NOT NULL,
  `movie_name` varchar(200) NOT NULL,
  `movie_info` varchar(300) DEFAULT NULL,
  `is_winner` varchar(3) DEFAULT NULL,
  `year` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`movie_id`),
  UNIQUE KEY `movie_id_UNIQUE` (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tb_movie_rating` (
  `rating_id` int NOT NULL AUTO_INCREMENT,
  `movie_name` varchar(200) NOT NULL,
  `rating` float DEFAULT NULL,
  `no_of_ratings` int DEFAULT 10,
  `box_office_value` int DEFAULT NULL,
  PRIMARY KEY (`rating_id`),
  UNIQUE KEY `movie_id_UNIQUE` (`rating_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;