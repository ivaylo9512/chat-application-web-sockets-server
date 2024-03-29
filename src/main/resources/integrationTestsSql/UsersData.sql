-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.3.8-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
SET FOREIGN_KEY_CHECKS = 0;


-- Dumping database structure for chat-app-test
CREATE DATABASE IF NOT EXISTS `chat-app-test` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `chat-app-test`;

-- Dumping structure for table chat-app-test.users
DELETE FROM `users`;
ALTER TABLE `users` AUTO_INCREMENT = 1;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) NOT NULL,
  `country` varchar(255) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `profile_image` bigint(20) DEFAULT NULL,
  `is_enabled` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc7w5h6b6d74bu9o1yulvrbh6c` (`profile_image`),
  CONSTRAINT `FKc7w5h6b6d74bu9o1yulvrbh6c` FOREIGN KEY (`profile_image`) REFERENCES `files` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

-- Dumping data for table chat-app-test.users: ~3 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `age`, `country`, `firstName`, `lastName`, `password`, `role`, `username`, `email`, `profile_image`, `is_enabled`) VALUES
	(1, 25, 'Bulgaria', 'firstName', 'lastName', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'ROLE_ADMIN', 'adminUser', 'adminUser@gmail.com', 1, true),
	(2, 30, 'Bulgaria', 'First', 'testA', '$2a$04$MRJSj9OWmmaWeDLPIHxU6.en5D8n10XTpFvgPQY5g.r61z0SFRkJW', 'ROLE_ADMIN', 'testUser', 'testUser@gmail.com', 2, true),
	(3, 25, 'Bulgaria', 'FirstD', 'test', '$2a$04$JrrsioMxE3HE7bJ/sXLWD.0Ty8iJB1W4zgxumoFmn9rWl5a0vATa6', 'ROLE_USER', 'testUser1', 'testUser1@gmail.com', 3, true),
	(4, 24, 'Spain', 'First', 'testC', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'user1', 'user1@gmail.com', 4, true),
	(5, 32, 'Italy', 'test', 'testLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'firstTest', 'firstTest@gmail.com', 5, true),
	(6, 24, 'Spain', 'First', 'testB', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'secondTest', 'secondTest@gmail.com', 6, false),
	(7, 29, 'Italy', 'test', 'testThirdLast', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'ROLE_USER', 'testThird', 'testThird@gmail.com', 7, false),
	(8, 29, 'Italy', 'test', 'testForthLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'testForth', 'testForth@gmail.com', 8, true),
	(9, 40, 'Spain', 'test', 'testFifthLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'testFifth', 'testFifth@gmail.com', 9, true);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 1;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
