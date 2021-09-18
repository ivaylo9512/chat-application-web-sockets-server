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
SET FOREIGN_KEY_CHECKS = 1;


-- Dumping database structure for chat-app-test
CREATE DATABASE IF NOT EXISTS `chat-app-test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `chat-app-test`;

-- Dumping structure for table chat-app-test.sessions
DELETE FROM `sessions`;
ALTER TABLE `sessions` AUTO_INCREMENT = 1;
CREATE TABLE IF NOT EXISTS `sessions` (
  `session_date` date NOT NULL,
  `chat` bigint NOT NULL,
  PRIMARY KEY (`chat`,`session_date`),
  CONSTRAINT `FKi239krmv92y7mk4hp7d5dccj7` FOREIGN KEY (`chat`) REFERENCES `chats` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table chat-app-test.sessions: ~0 rows (approximately)
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
INSERT INTO `sessions` (`session_date`, `chat`) VALUES
	('2021-09-18', 1),
	('2021-09-15', 1),
	('2021-09-13', 1),
	('2021-09-16', 1),
	('2021-09-17', 1),
	('2021-09-12', 1),
	('2021-09-17', 2),
	('2021-09-18', 2),
	('2021-09-19', 2),
	('2021-09-20', 2),
	('2021-09-15', 2);
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 0;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
