-- ========================================================
-- GardenConnect - Quick Setup Database Script
-- This script safely drops existing tables (if present),
-- recreates the entire schema, and loads the seed data.
-- ========================================================

CREATE DATABASE IF NOT EXISTS `gardenconnect`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `gardenconnect`;

-- Drop existing tables in reverse dependency order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `system_settings`;
DROP TABLE IF EXISTS `gardening_projects`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `discussions`;
DROP TABLE IF EXISTS `discussion_categories`;
DROP TABLE IF EXISTS `tips`;
DROP TABLE IF EXISTS `tip_categories`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- Now source schema.sql and seed.sql or execute complete definitions
SOURCE database/schema.sql;
SOURCE database/seed.sql;
