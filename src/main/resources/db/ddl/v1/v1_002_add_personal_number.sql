ALTER TABLE `user_details`
    ADD COLUMN `personal_number` varchar(50) NULL AFTER `phone_number`,
    ADD COLUMN `last_personal_number_update_time` DATETIME(6) NULL AFTER `personal_number`;