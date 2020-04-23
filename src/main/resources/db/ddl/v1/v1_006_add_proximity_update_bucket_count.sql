ALTER TABLE `user_details`
    ADD COLUMN `proximity_update_bucket_count` bigint(20) NULL AFTER `last_proximity_update_time`;