CREATE TABLE uploadservice.`uploadbyuser` (
  `id` bigint(21) NOT NULL,
  `user_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `create_date` datetime NOT NULL DEFAULT UTC_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
CREATE SEQUENCE uploadservice.sequence_uploadbyuser
INCREMENT BY 1
MINVALUE = 0
NOMAXVALUE
START WITH 0
CACHE 0;
