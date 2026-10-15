package com.gardenconnect.config;

/**
 * System-wide constants, configuration keys, and session attribute identifiers.
 */
public final class AppConfig {

    private AppConfig() {}

    // Session Attribute Keys
    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_CSRF_TOKEN = "csrfToken";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";
    public static final String FLASH_WARNING = "flashWarning";
    public static final String FLASH_INFO = "flashInfo";

    // Application Scope Keys
    public static final String APP_SETTINGS = "systemSettings";
    public static final String APP_COMMUNITY_NAME = "communityName";

    // Request Attribute Keys
    public static final String REQ_ERROR_MAP = "fieldErrors";
    public static final String REQ_PAGE_TITLE = "pageTitle";
    public static final String REQ_ACTIVE_TAB = "activeTab";

    // Defaults
    public static final int DEFAULT_PAGE_SIZE = 9;
    public static final int DEFAULT_MAX_UPLOAD_MB = 5;

    // Setting Keys (matching system_settings table)
    public static final String SETTING_COMMUNITY_NAME = "community_name";
    public static final String SETTING_MAINTENANCE_MODE = "maintenance_mode";
    public static final String SETTING_REGISTRATION_OPEN = "registration_open";
    public static final String SETTING_MAX_UPLOAD_MB = "max_upload_size_mb";
    public static final String SETTING_DEFAULT_PAGE_SIZE = "default_page_size";
    public static final String SETTING_MODERATION_POLICY = "default_moderation_policy";
}
