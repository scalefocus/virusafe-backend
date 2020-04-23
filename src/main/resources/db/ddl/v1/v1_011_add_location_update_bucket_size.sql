ALTER TABLE `user_details`
    ADD COLUMN `location_update_bucket_count` bigint(20) NULL AFTER `last_position_update_time`;