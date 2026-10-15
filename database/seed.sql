-- ========================================================
-- GardenConnect - Initial Seed Data
-- Database: gardenconnect
-- Note: Default demo passwords:
--   admin -> Admin@123456
--   gardeners -> Gardener@123
-- ========================================================

USE `gardenconnect`;

-- --------------------------------------------------------
-- 1. Seed System Settings
-- --------------------------------------------------------
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `description`, `data_type`) VALUES
('community_name', 'GardenConnect Community', 'Public platform name displayed in navigation and headers', 'STRING'),
('community_description', 'Connecting plant lovers, urban growers, and master gardeners worldwide.', 'Short platform description', 'STRING'),
('max_upload_size_mb', '5', 'Maximum allowed image upload size in Megabytes', 'INTEGER'),
('default_moderation_policy', 'MANUAL', 'Content moderation policy: MANUAL (requires approval) or AUTOMATIC', 'STRING'),
('maintenance_mode', 'false', 'Enable maintenance mode to restrict non-admin access', 'BOOLEAN'),
('registration_open', 'true', 'Allow new gardeners to register accounts', 'BOOLEAN'),
('default_page_size', '9', 'Default number of items per page for tips and discussions', 'INTEGER'),
('contact_email', 'support@gardenconnect.org', 'Community support and inquiry contact address', 'STRING')
ON DUPLICATE KEY UPDATE `setting_value` = VALUES(`setting_value`);

-- --------------------------------------------------------
-- 2. Seed Tip Categories
-- --------------------------------------------------------
INSERT INTO `tip_categories` (`id`, `name`, `slug`, `description`, `icon`, `display_order`) VALUES
(1, 'Vegetables', 'vegetables', 'Growing vibrant edible crops, tomatoes, peppers, and root veggies', 'bi-egg-fried', 1),
(2, 'Flowers & Ornamentals', 'flowers', 'Annuals, perennials, bulbs, and pollinator gardens', 'bi-flower1', 2),
(3, 'Herbs', 'herbs', 'Culinary and medicinal herbs for pots, beds, and windowsills', 'bi-tree', 3),
(4, 'Composting & Soil', 'composting-soil', 'Vermicomposting, organic amendments, and soil biome health', 'bi-recycle', 4),
(5, 'Indoor & Houseplants', 'indoor-plants', 'Tropicals, succulents, ferns, and indoor lighting techniques', 'bi-house-heart', 5),
(6, 'Watering & Irrigation', 'watering-irrigation', 'Drip systems, rainwater collection, and moisture management', 'bi-droplet-half', 6),
(7, 'Pest & Disease Control', 'pest-control', 'Integrated pest management and organic fungicides', 'bi-bug', 7),
(8, 'Organic Gardening', 'organic-gardening', 'Permaculture, companion planting, and chemical-free cultivation', 'bi-sun', 8)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- --------------------------------------------------------
-- 3. Seed Discussion Categories
-- --------------------------------------------------------
INSERT INTO `discussion_categories` (`id`, `name`, `slug`, `description`, `icon`, `display_order`) VALUES
(1, 'General Gardening', 'general-gardening', 'Everyday garden musings, season kickoffs, and general chit-chat', 'bi-chat-dots', 1),
(2, 'Troubleshooting & Plant Help', 'plant-help', 'Diagnose wilting leaves, yellowing foliage, and mysterious pests', 'bi-bandaid', 2),
(3, 'DIY & Garden Projects', 'diy-projects', 'Raised beds, greenhouses, potting benches, and trellis builds', 'bi-tools', 3),
(4, 'Tools & Equipment', 'tools-equipment', 'Pruner recommendations, soil meters, and watering systems', 'bi-wrench', 4),
(5, 'Seed Swapping & Propagation', 'seeds-propagation', 'Cuttings, seed saving, stratification, and division', 'bi-flower2', 5),
(6, 'Urban & Balcony Gardening', 'urban-balcony', 'Small space container gardening, vertical planters, and shade', 'bi-building', 6)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- --------------------------------------------------------
-- 4. Seed Users
-- Passwords:
-- admin: Admin@123456 -> $2a$12$Hfd3/rJUB4ypxxkjJ14dVu0kuvMTMnZc2Ud3ZuO4C1RLr/.wVqTQG
-- gardeners: Gardener@123 -> $2a$12$XcMMMOR0.f/3e4uXdc2qROtCcJWrJLBrwq8qxfmqpK.EcSOhuHSMu
-- (BCrypt verified standard test hashes, cost factor 12)
-- --------------------------------------------------------
INSERT INTO `users` (`id`, `username`, `email`, `password_hash`, `full_name`, `bio`, `location`, `gardening_interests`, `role`, `is_active`) VALUES
(1, 'admin', 'admin@gardenconnect.com', '$2a$12$Hfd3/rJUB4ypxxkjJ14dVu0kuvMTMnZc2Ud3ZuO4C1RLr/.wVqTQG', 'GardenConnect Administrator', 'Platform maintainer and chief horticulture coordinator.', 'Seattle, WA', 'System Administration, Permaculture', 'ADMIN', TRUE),
(2, 'sarah_green', 'sarah.gardens@example.com', '$2a$12$XcMMMOR0.f/3e4uXdc2qROtCcJWrJLBrwq8qxfmqpK.EcSOhuHSMu', 'Sarah Jenkins', 'Master Gardener with 12 years of experience in heirloom tomatoes and pollinator borders.', 'Portland, OR', 'Heirloom Tomatoes, Native Plants, Composting', 'GARDENER', TRUE),
(3, 'marcus_urban', 'marcus.plants@example.com', '$2a$12$XcMMMOR0.f/3e4uXdc2qROtCcJWrJLBrwq8qxfmqpK.EcSOhuHSMu', 'Marcus Chen', 'Balcony farmer experimenting with sub-irrigated planters and hydroponic leafy greens.', 'Chicago, IL', 'Container Gardening, Hydroponics, Herbs', 'GARDENER', TRUE),
(4, 'elena_botanist', 'elena.bloom@example.com', '$2a$12$XcMMMOR0.f/3e4uXdc2qROtCcJWrJLBrwq8qxfmqpK.EcSOhuHSMu', 'Dr. Elena Rostova', 'Botanist studying soil microbiology, mycorrhizal fungi, and organic pest mitigation.', 'Denver, CO', 'Soil Biome, Rare Tropicals, Organic Farming', 'GARDENER', TRUE),
(5, 'david_veggies', 'david.organic@example.com', '$2a$12$XcMMMOR0.f/3e4uXdc2qROtCcJWrJLBrwq8qxfmqpK.EcSOhuHSMu', 'David Miller', 'Homestead enthusiast growing 70% of family produce through succession planting.', 'Austin, TX', 'Succession Planting, Drip Irrigation, Root Crops', 'GARDENER', TRUE)
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`);

-- --------------------------------------------------------
-- 5. Seed Gardening Tips
-- Mix of APPROVED and PENDING moderation statuses
-- --------------------------------------------------------
INSERT INTO `tips` (`id`, `user_id`, `category_id`, `title`, `summary`, `content`, `image_url`, `moderation_status`, `view_count`, `created_at`) VALUES
(1, 2, 1, 'Deep Root Watering for Giant Heirloom Tomatoes', 'Learn how bottom watering prevents blossom end rot and fungal splashback.', 'Heirloom tomatoes thrive when their root systems reach deep into the soil profile. Instead of surface sprinkling every day, give your tomatoes a deep soak once or twice a week. \n\nPro-Tip: Bury a perforated 2-liter bottle or terra cotta olla next to each tomato transplant. Pour water directly into the vessel; water will diffuse slowly straight to the lower root zone without wetting delicate foliage!', 'https://images.unsplash.com/photo-1592841200221-a6898f307baa?auto=format&fit=crop&w=800&q=80', 'APPROVED', 142, DATE_SUB(NOW(), INTERVAL 5 DAY)),

(2, 3, 3, 'Kitchen Herb Windowsill Regeneration', 'Turn supermarket culinary scraps into infinite basil, mint, and scallions.', 'Never throw away spring onion roots or mature basil cuttings! Place the root base of green onions in a small ramekin with clean water on a sunny sill; within 48 hours new shoots will surge upward. \n\nFor basil: cut a 4-inch stem right below a node, strip bottom leaves, and place in water. In 7 days, vigorous roots will appear, ready for potting soil!', 'https://images.unsplash.com/photo-1466692476868-aef1dfb1e735?auto=format&fit=crop&w=800&q=80', 'APPROVED', 98, DATE_SUB(NOW(), INTERVAL 4 DAY)),

(3, 4, 4, 'Mastering the 30:1 Carbon-to-Nitrogen Compost Ratio', 'Accelerate hot composting without foul odors or sluggish breakdown.', 'Composting is biochemical poetry. Fast hot decomposition requires balancing brown (carbon-rich: straw, cardboard, dried leaves) and green (nitrogen-rich: vegetable scraps, fresh grass clippings, coffee grounds) materials. \n\nTurn the pile every 4-5 days to inject oxygen for aerobic bacteria. The core temperature should reach 135°F to 155°F, effectively killing weed seeds and pathogen spores.', 'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=800&q=80', 'APPROVED', 215, DATE_SUB(NOW(), INTERVAL 3 DAY)),

(4, 5, 8, 'Companion Planting: The Ancient Three Sisters Guild', 'Corn, beans, and squash grown in symbiotic harmony.', 'The traditional Three Sisters polyculture is the ultimate companion guild: \n1. Sweet Corn provides sturdy living poles for climbing pole beans. \n2. Pole Beans fix atmospheric nitrogen into the soil to nourish heavy-feeding corn. \n3. Winter Squash covers the ground with broad prickly leaves, shading weeds and trapping soil moisture.', 'https://images.unsplash.com/photo-1523348837708-15d4a09cfac2?auto=format&fit=crop&w=800&q=80', 'APPROVED', 167, DATE_SUB(NOW(), INTERVAL 2 DAY)),

(5, 3, 6, 'DIY Gravity Drip System for Vacation Watering', 'Keep balcony containers alive while you are away using zero electricity.', 'Elevate a 5-gallon food-grade bucket 3 feet above your plant containers. Connect 1/4-inch drip irrigation tubing with adjustable micro-drippers set to 0.5 gallons per hour. Run tests 2 days before leaving to calibrate flow rates to match plant transpiration.', 'https://images.unsplash.com/photo-1584467735871-8e85353a8413?auto=format&fit=crop&w=800&q=80', 'PENDING', 12, DATE_SUB(NOW(), INTERVAL 1 HOUR))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- --------------------------------------------------------
-- 6. Seed Discussions
-- --------------------------------------------------------
INSERT INTO `discussions` (`id`, `user_id`, `category_id`, `title`, `content`, `moderation_status`, `view_count`, `created_at`) VALUES
(1, 2, 2, 'Curling Leaves on Young Pepper Plants: Aphids or Overwatering?', 'My habanero and bell pepper seedlings are showing upward leaf cupping on the newest growth. The soil feels slightly damp 1 inch down. Has anyone dealt with this early in the season? Could it be calcium deficiency or broad mites?', 'APPROVED', 84, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 3, 6, 'Best Heavy-Duty Trellising for Windy Balconies?', 'I live on the 8th floor and wind gusts can be brutal. Standard bamboo stakes blow over once the tomato plants get top-heavy. What do other high-rise gardeners use to secure tall climbing plants?', 'APPROVED', 61, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 4, 3, 'Pallet Herb Planters: Safe Wood Treatment Options', 'When building vertical planters from reclaimed wooden shipping pallets, ensure you only use pallets stamped HT (Heat Treated) instead of MB (Methyl Bromide). Here are my favorite non-toxic food-safe wood sealants: pure linseed oil and beeswax blends.', 'APPROVED', 119, DATE_SUB(NOW(), INTERVAL 1 DAY))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- --------------------------------------------------------
-- 7. Seed Comments
-- --------------------------------------------------------
INSERT INTO `comments` (`id`, `discussion_id`, `user_id`, `content`, `created_at`) VALUES
(1, 1, 4, 'Check the underside of the curled leaves with a 10x jeweler loupe. Broad mites cause upward cupping and brittle leaf texture, whereas aphids leave sticky honeydew secretions.', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 5, 'Also check your watering frequency! Solanaceous crops hate "wet feet". Let the top 2 inches dry out completely between waterings before adding moisture.', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 2, 2, 'Try heavy-gauge EMT conduit secured to the balcony railing with stainless steel U-bolts. String durable nylon trellis netting between the vertical posts.', DATE_SUB(NOW(), INTERVAL 1 DAY))
ON DUPLICATE KEY UPDATE `content` = VALUES(`content`);

-- --------------------------------------------------------
-- 8. Seed Gardening Projects
-- Demonstrating status lifecycles and 0-100% progress
-- --------------------------------------------------------
INSERT INTO `gardening_projects` (`id`, `user_id`, `title`, `description`, `plant_crop_type`, `start_date`, `target_completion_date`, `status`, `progress_percentage`, `notes`) VALUES
(1, 2, 'Spring Heirloom Tomato Sanctuary', 'Raised bed cultivation of Cherokee Purple, Brandywine, and Black Krim varieties.', 'Solanum lycopersicum (Heirloom)', '2026-03-01', '2026-08-30', 'IN_PROGRESS', 65, 'Pruned lower suckers; trellis support installed; early fruit cluster developing.'),
(2, 2, 'Native Pollinator Flower Border', 'Perennial border designed to support native bumblebees and monarch butterflies.', 'Echinacea, Milkweed, Salvia', '2026-04-10', '2026-09-15', 'IN_PROGRESS', 40, 'Seeds stratified and germinated. Planting out seedlings after last frost date.'),
(3, 3, 'Balcony Kitchen Salad Wall', 'Vertical pocket system for continuous butterhead lettuce and arugula harvests.', 'Lettuce & Greens', '2026-02-15', '2026-05-15', 'COMPLETED', 100, 'All pockets fully mature; harvested three full bowls of crisp greens this week!'),
(4, 3, 'Sub-Irrigated Dwarf Blueberry Container', 'Acidic soil container setup for Patriot and Top Hat highbush blueberries.', 'Vaccinium corymbosum', '2026-05-01', '2026-10-30', 'PLANNED', 10, 'Acquired peat moss and sulfur; awaiting shipment of 2-year potted bush.'),
(5, 5, 'Autumn Brassica Succession', 'Cold-hardy broccoli, kale, and cabbage for late fall and winter pantry storage.', 'Brassica oleracea', '2026-07-01', '2026-11-20', 'PLANNED', 0, 'Seed flats prepared under grow lights; awaiting summer harvest clearance.')
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- --------------------------------------------------------
-- 9. Seed Activity Logs
-- --------------------------------------------------------
INSERT INTO `activity_logs` (`user_id`, `username`, `action`, `entity_type`, `entity_id`, `details`, `ip_address`, `created_at`) VALUES
(1, 'admin', 'SYSTEM_INITIALIZATION', 'SYSTEM', 1, 'GardenConnect platform successfully seeded and configured.', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 'sarah_green', 'USER_REGISTRATION', 'USER', 2, 'New gardener registration completed.', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 'sarah_green', 'TIP_CREATED', 'TIP', 1, 'Created tip: Deep Root Watering for Giant Heirloom Tomatoes', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'admin', 'TIP_MODERATED', 'TIP', 1, 'Tip approved by administrator.', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 'marcus_urban', 'PROJECT_CREATED', 'PROJECT', 3, 'Created project: Balcony Kitchen Salad Wall', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 4 DAY));
