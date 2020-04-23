ALTER TABLE `user_details`
    ADD COLUMN `push_token` varchar(255) NULL AFTER `pre_existing_conditions`,
    ADD COLUMN `last_push_token_update_bucket_time` DATETIME(6) NULL AFTER `personal_info_update_bucket_count`,
    ADD COLUMN `push_token_update_bucket_count` bigint(20) NULL AFTER `last_push_token_update_bucket_time`;