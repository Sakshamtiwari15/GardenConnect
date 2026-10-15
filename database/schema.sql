-- ========================================================
-- GardenConnect - Relational Database Schema
-- Database Management System: MySQL 8.0+
-- Storage Engine: InnoDB | Character Set: utf8mb4
-- ========================================================

CREATE DATABASE IF NOT EXISTS `gardenconnect`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `gardenconnect`;

-- --------------------------------------------------------
-- 1. Table: users
-- Core entity storing credentials, profile details, and role
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `bio` TEXT DEFAULT NULL,
    `location` VARCHAR(100) DEFAULT NULL,
    `gardening_interests` VARCHAR(255) DEFAULT NULL,
    `role` ENUM('ADMIN', 'GARDENER') NOT NULL DEFAULT 'GARDENER',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_active` (`is_active`),
    INDEX `idx_users_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 2. Table: tip_categories
-- Normalized categories for gardening tips
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tip_categories` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `slug` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL,
    `icon` VARCHAR(50) DEFAULT 'bi-flower1',
    `display_order` INT NOT NULL DEFAULT 0,
    INDEX `idx_tip_cat_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 3. Table: tips
-- Gardening tips shared by gardeners with moderation status
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tips` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `category_id` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `summary` VARCHAR(350) DEFAULT NULL,
    `content` MEDIUMTEXT NOT NULL,
    `image_url` VARCHAR(255) DEFAULT NULL,
    `moderation_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    `rejection_reason` VARCHAR(500) DEFAULT NULL,
    `view_count` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_tips_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_tips_category` FOREIGN KEY (`category_id`)
        REFERENCES `tip_categories` (`id`) ON DELETE RESTRICT,
    INDEX `idx_tips_status_date` (`moderation_status`, `created_at` DESC),
    INDEX `idx_tips_user` (`user_id`),
    INDEX `idx_tips_category` (`category_id`),
    INDEX `idx_tips_views` (`view_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 4. Table: discussion_categories
-- Topic taxonomy for community discussions
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `discussion_categories` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `slug` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL,
    `icon` VARCHAR(50) DEFAULT 'bi-chat-dots',
    `display_order` INT NOT NULL DEFAULT 0,
    INDEX `idx_disc_cat_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 5. Table: discussions
-- Community discussion threads
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `discussions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `category_id` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` MEDIUMTEXT NOT NULL,
    `moderation_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'APPROVED',
    `rejection_reason` VARCHAR(500) DEFAULT NULL,
    `view_count` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_discussions_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_discussions_category` FOREIGN KEY (`category_id`)
        REFERENCES `discussion_categories` (`id`) ON DELETE RESTRICT,
    INDEX `idx_discussions_status_date` (`moderation_status`, `created_at` DESC),
    INDEX `idx_discussions_user` (`user_id`),
    INDEX `idx_discussions_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 6. Table: comments
-- Multi-user replies within discussion threads
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `discussion_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_comments_discussion` FOREIGN KEY (`discussion_id`)
        REFERENCES `discussions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comments_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_comments_discussion_date` (`discussion_id`, `created_at` ASC),
    INDEX `idx_comments_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 7. Table: gardening_projects
-- Personal projects tracked by individual gardeners
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gardening_projects` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `plant_crop_type` VARCHAR(100) DEFAULT NULL,
    `start_date` DATE DEFAULT NULL,
    `target_completion_date` DATE DEFAULT NULL,
    `status` ENUM('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD') NOT NULL DEFAULT 'PLANNED',
    `progress_percentage` INT NOT NULL DEFAULT 0 CHECK (`progress_percentage` >= 0 AND `progress_percentage` <= 100),
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_projects_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_projects_user_status` (`user_id`, `status`),
    INDEX `idx_projects_updated_at` (`updated_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 8. Table: system_settings
-- Key-value administrative runtime configurations
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_settings` (
    `setting_key` VARCHAR(50) PRIMARY KEY,
    `setting_value` TEXT NOT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `data_type` VARCHAR(20) NOT NULL DEFAULT 'STRING',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 9. Table: activity_logs
-- Audit trail for security, user, and administrative actions
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `activity_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `username` VARCHAR(50) DEFAULT NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_type` VARCHAR(50) DEFAULT NULL,
    `entity_id` BIGINT DEFAULT NULL,
    `details` TEXT DEFAULT NULL,
    `ip_address` VARCHAR(45) DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_logs_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_logs_action` (`action`),
    INDEX `idx_logs_created_at` (`created_at` DESC),
    INDEX `idx_logs_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
