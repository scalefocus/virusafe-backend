ALTER TABLE `user_details`
    ADD COLUMN `token_secret` varchar(255) NULL AFTER `pre_existing_conditions`,
    ADD COLUMN `refresh_token` longblob NULL AFTER `token_secret`;