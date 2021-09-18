-- --------------------------------------------------------
-- Host:                         database-2.cd3qhxwxyvzj.eu-west-2.rds.amazonaws.com
-- Server version:               8.0.23 - Source distribution
-- Server OS:                    Linux
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for chat-app-test
CREATE DATABASE IF NOT EXISTS `chat-app-test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `chat-app-test`;

-- Dumping structure for table chat-app-test.messages
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  `time` time DEFAULT NULL,
  `receiver` bigint DEFAULT NULL,
  `chat` bigint DEFAULT NULL,
  `session_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpmq5ifuu12ky74d5hbouj2gxn` (`receiver`),
  KEY `FK308xy9e9twocfxxphbkgjk20b` (`chat`,`session_date`),
  CONSTRAINT `FK308xy9e9twocfxxphbkgjk20b` FOREIGN KEY (`chat`, `session_date`) REFERENCES `sessions` (`chat`, `session_date`),
  CONSTRAINT `FKpmq5ifuu12ky74d5hbouj2gxn` FOREIGN KEY (`receiver`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table chat-app-test.messages: ~0 rows (approximately)
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` (`id`, `message`, `time`, `receiver`, `chat`, `session_date`) VALUES
	(1, 'testMessage', '22:03:16', 1, 1, '2021-09-13'),
	(2, 'testMessage', '22:33:16', 2, 1, '2021-09-13'),
	(3, 'testMessage', '22:43:16', 1, 1, '2021-09-13'),
	(4, 'testMessage', '22:53:16', 2, 1, '2021-09-14'),
	(5, 'testMessage', '23:03:16', 1, 1, '2021-09-14'),
	(6, 'testMessage', '23:43:16', 2, 1, '2021-09-15'),
	(7, 'testMessage', '23:53:16', 1, 1, '2021-09-15'),
	(8, 'testMessage2', '23:54:16', 2, 1, '2021-09-16'),
	(9, 'testMessage1', '23:56:16', 1, 1, '2021-09-17'),
	(10, 'testMessage', '23:57:16', 1, 1, '2021-09-18'),
	(11, 'testMessage', '23:58:16', 2, 2, '2021-09-19'),
	(12, 'testMessage', '22:03:16', 2, 2, '2021-09-17'),
	(13, 'testMessage', '22:03:16', 3, 2, '2021-09-17'),
	(14, 'testMessage', '22:03:16', 2, 2, '2021-09-18'),
	(15, 'testMessage', '22:03:16', 3, 2, '2021-09-19'),
	(16, 'testMessage', '22:03:16', 3, 2, '2021-09-20');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
