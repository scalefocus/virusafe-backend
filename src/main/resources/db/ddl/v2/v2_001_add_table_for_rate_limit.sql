CREATE TABLE IF NOT EXISTS `rate_limits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) DEFAULT NULL,
  `user_details_id` bigint(20) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `last_update_time` DATETIME(6) NULL,
  `bucket_count` bigint(20) NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rate_limit_to_user_details_id` (`user_details_id`),
  CONSTRAINT `fk_rate_limit_to_user_details_id` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX IF NOT EXISTS `user_details_user_guid_indx` on user_details(user_guid);
