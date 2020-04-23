ALTER TABLE `user_details`
    CHANGE COLUMN `refresh_token` `refresh_token` varchar(255) NULL AFTER `token_secret`;