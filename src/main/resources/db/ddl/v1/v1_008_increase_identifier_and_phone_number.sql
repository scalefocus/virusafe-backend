ALTER TABLE `user_details`
    CHANGE COLUMN `phone_number` `phone_number` varchar(100) NULL AFTER `id`,
    CHANGE COLUMN `identification_number` `identification_number` varchar(100) NULL AFTER `user_guid`;