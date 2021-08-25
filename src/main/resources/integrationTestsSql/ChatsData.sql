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


-- Dumping database structure for chat-app-test
CREATE DATABASE IF NOT EXISTS `chat-app-test` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `chat-app-test`;

DELETE FROM `chats`;
ALTER TABLE `chats` AUTO_INCREMENT = 1;
CREATE TABLE IF NOT EXISTS `chats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `first_user` bigint(20) DEFAULT NULL,
  `second_user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjkdygx0osyuythgf37fy512f8` (`first_user`),
  KEY `FK59axryomh25kdkerna0hwm7ps` (`second_user`),
  CONSTRAINT `FK59axryomh25kdkerna0hwm7ps` FOREIGN KEY (`second_user`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjkdygx0osyuythgf37fy512f8` FOREIGN KEY (`first_user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Dumping data for table chat-app-test.chats: ~0 rows (approximately)
/*!40000 ALTER TABLE `chats` DISABLE KEYS */;
INSERT INTO `chats` (`id`, `created_at`, `updated_at`, `first_user`, `second_user`) VALUES
	(1, '2021-08-25 23:15:18.000000', '2021-08-25 23:15:19.000000', 1, 2),
	(2, '2021-08-25 23:15:29.000000', '2021-08-25 23:15:30.000000', 2, 3),
	(3, '2021-08-25 23:15:41.000000', '2021-08-25 23:15:42.000000', 3, 4);
/*!40000 ALTER TABLE `chats` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
