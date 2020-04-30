CREATE TABLE IF NOT EXISTS `personal_information_consent_audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) DEFAULT NULL,
  `changed_on` DATETIME(6) NULL,
  `action` varchar(32) NOT NULL,
  `user_guid` varchar(40) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `personal_information_consent_audit` (id, version, changed_on, action, user_guid)
    SELECT null, null, NOW(), "GRANTED", `user_details`.`user_guid`
    FROM `user_details` WHERE `identification_number` is not null;

INSERT INTO `personal_information_consent_audit` (id, version, changed_on, action, user_guid)
    SELECT null, null, NOW(), "REVOKED", `user_details`.`user_guid`
    FROM `user_details` WHERE `identification_number` is null;