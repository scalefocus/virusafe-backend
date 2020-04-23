ALTER TABLE `user_details`
    ADD COLUMN `user_guid` varchar(40) NOT NULL AFTER `phone_number`;
