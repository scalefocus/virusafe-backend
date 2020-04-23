ALTER TABLE `user_details`
    ADD COLUMN `created_date` bigint(20) NULL AFTER `version`,
	ADD COLUMN `last_questionnaire_submit_time` DATETIME(6) NULL AFTER `created_date`,
	ADD COLUMN `last_position_update_time` DATETIME(6) NULL AFTER `last_questionnaire_submit_time`,
	ADD COLUMN `last_proximity_update_time` DATETIME(6) NULL AFTER `last_position_update_time`,
	ADD COLUMN `last_pin_request_time_bucket` DATETIME(6) NULL AFTER `last_proximity_update_time`,
	ADD COLUMN `pin_request_bucket_count` bigint(20) NULL AFTER `last_pin_request_time_bucket`;

CREATE UNIQUE INDEX IF NOT EXISTS `user_details_phone_number_indx` on user_details(phone_number);
