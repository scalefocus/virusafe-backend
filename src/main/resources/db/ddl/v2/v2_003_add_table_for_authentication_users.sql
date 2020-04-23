CREATE TABLE IF NOT EXISTS `authentication_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `active` boolean NULL DEFAULT false,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `authentication_users` (`version`, `username`, `password`, `active`) VALUES ('0', 'test', '1blsl0FNITKt9UhAoix+meJF7++5VsigenambfCzv04xFck22v30t9ncxRzy58BsJ38W4cofAy7GAeuTM/nw/A==', '0');