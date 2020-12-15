ALTER TABLE `uploadservice`.`uploadbyuser`
ADD COLUMN `session_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL AFTER `user_id`;
