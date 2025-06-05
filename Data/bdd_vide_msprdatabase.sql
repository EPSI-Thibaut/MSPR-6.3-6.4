-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           11.4.5-MariaDB - mariadb.org binary distribution
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Listage de la structure de table msprdatabase. continents
CREATE TABLE IF NOT EXISTS `continents` (
  `id_continents` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `id_regions` int(11) DEFAULT NULL,
  `id_countries` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_continents`),
  KEY `id_regions` (`id_regions`),
  KEY `id_countries` (`id_countries`),
  CONSTRAINT `continents_ibfk_1` FOREIGN KEY (`id_regions`) REFERENCES `regions` (`id_regions`),
  CONSTRAINT `continents_ibfk_2` FOREIGN KEY (`id_countries`) REFERENCES `countries` (`id_countries`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Les données exportées n'étaient pas sélectionnées.

-- Listage de la structure de table msprdatabase. countries
CREATE TABLE IF NOT EXISTS `countries` (
  `id_countries` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_countries`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Les données exportées n'étaient pas sélectionnées.

-- Listage de la structure de table msprdatabase. pandemics
CREATE TABLE IF NOT EXISTS `pandemics` (
  `id_pandemics` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_pandemics`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Les données exportées n'étaient pas sélectionnées.

-- Listage de la structure de table msprdatabase. regions
CREATE TABLE IF NOT EXISTS `regions` (
  `id_regions` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_regions`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Les données exportées n'étaient pas sélectionnées.

-- Listage de la structure de table msprdatabase. total_by_day
CREATE TABLE IF NOT EXISTS `total_by_day` (
  `id_pandemics` int(11) NOT NULL,
  `id_regions` int(11) NOT NULL,
  `case` int(11) DEFAULT NULL,
  `death` int(11) DEFAULT NULL,
  `recovery` int(11) DEFAULT NULL,
  `date_by_day` date NOT NULL,
  PRIMARY KEY (`id_pandemics`,`id_regions`,`date_by_day`),
  KEY `id_regions` (`id_regions`),
  CONSTRAINT `total_by_day_ibfk_1` FOREIGN KEY (`id_pandemics`) REFERENCES `pandemics` (`id_pandemics`),
  CONSTRAINT `total_by_day_ibfk_2` FOREIGN KEY (`id_regions`) REFERENCES `regions` (`id_regions`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Les données exportées n'étaient pas sélectionnées.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
