ALTER TABLE `user_details`
    ADD COLUMN `questionnaire_update_bucket_count` bigint(20) NULL AFTER `last_questionnaire_submit_time`;