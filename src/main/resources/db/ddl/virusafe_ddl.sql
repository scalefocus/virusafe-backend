
-- Dumping database structure for virusafedb
USE `virusafedb`;

-- Dumping structure for table virusafedb.user_details
DROP TABLE IF EXISTS `user_details`;
CREATE TABLE IF NOT EXISTS `user_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(50) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Dumping structure for table virusafedb.user_registration_tokens
DROP TABLE IF EXISTS `user_registration_tokens`;
CREATE TABLE IF NOT EXISTS `user_registration_tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pin` varchar(50) NOT NULL,
  `valid_until` datetime(6) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `user_details_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd4w9ffxyyh2je992hns1lsh8b` (`user_details_id`),
  CONSTRAINT `FKd4w9ffxyyh2je992hns1lsh8b` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

