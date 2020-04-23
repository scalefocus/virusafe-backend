ALTER TABLE `user_details`
    CHANGE COLUMN `personal_number` `identification_number` varchar(20) NULL AFTER `phone_number`,
    ADD COLUMN `age` integer(4) NULL AFTER `identification_number`,
    ADD COLUMN `gender` varchar(10) NULL AFTER `age`,
    ADD COLUMN `pre_existing_conditions` varchar(100) NULL AFTER `gender`,
    CHANGE COLUMN `last_personal_number_update_time` `last_personal_info_update_bucket_time` DATETIME(6) NULL AFTER `pin_request_bucket_count`,
    ADD COLUMN `personal_info_update_bucket_count` bigint(20) NULL AFTER `last_personal_info_update_bucket_time`,
    CHANGE COLUMN `last_pin_request_time_bucket` `last_pin_request_bucket_time` DATETIME(6) NULL AFTER `last_proximity_update_time`;