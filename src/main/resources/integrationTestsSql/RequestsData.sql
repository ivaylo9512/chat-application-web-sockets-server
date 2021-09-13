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

-- Dumping structure for table chat-app-test.requests
DELETE FROM `requests`;
ALTER TABLE `requests` AUTO_INCREMENT = 1;
CREATE TABLE IF NOT EXISTS `requests` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `receiver` bigint(20) DEFAULT NULL,
  `sender` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiqupi3vk3th6q9htaydj7aebv` (`receiver`),
  KEY `FKh5beal9i8f8smhul2iytw5o5t` (`sender`),
  CONSTRAINT `FKh5beal9i8f8smhul2iytw5o5t` FOREIGN KEY (`sender`) REFERENCES `users` (`id`),
  CONSTRAINT `FKiqupi3vk3th6q9htaydj7aebv` FOREIGN KEY (`receiver`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- Dumping data for table chat-app-test.requests: ~0 rows (approximately)
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` (`id`, `created_at`, `updated_at`, `receiver`, `sender`) VALUES
	(1, '2021-08-26 18:17:44.000000', '2021-08-26 18:17:44.000000', 1, 3),
	(2, '2021-08-26 23:17:52.000000', '2021-08-26 23:17:52.000000', 1, 4),
	(3, '2021-08-24 23:18:09.000000', '2021-08-24 23:18:09.000000', 1, 5),
	(4, '2021-08-25 23:18:17.000000', '2021-08-25 23:18:17.000000', 5, 2),
	(5, '2021-08-25 23:18:37.000000', '2021-08-25 23:18:37.000000', 2, 6),
	(6, '2021-08-26 00:48:26.000000', '2021-08-26 00:48:26.000000', 1, 6),
	(7, '2021-08-26 00:48:40.000000', '2021-08-26 00:48:40.000000', 1, 7),
	(8, '2021-08-26 00:49:40.000000', '2021-08-26 00:48:40.000000', 9, 1);
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 1;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
