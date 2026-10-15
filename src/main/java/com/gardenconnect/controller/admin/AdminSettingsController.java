package com.gardenconnect.controller.admin;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.controller.BaseController;
import com.gardenconnect.model.SystemSetting;
import com.gardenconnect.model.User;
import com.gardenconnect.service.SystemSettingService;
import com.gardenconnect.service.impl.SystemSettingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Controller for managing system runtime configurations stored in MySQL.
 */
@WebServlet(name = "AdminSettingsController", urlPatterns = "/admin/settings")
public class AdminSettingsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminSettingsController.class);

    private final SystemSettingService settingService;

    public AdminSettingsController() {
        this.settingService = new SystemSettingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Map<String, SystemSetting> settings = settingService.getAllSettings();
            request.setAttribute("settings", settings);
            request.setAttribute("pageTitle", "System Settings - GardenConnect Admin");
            request.setAttribute("activeTab", "settings");

            forward(request, response, "admin/settings");
        } catch (Exception e) {
            logger.error("Error loading system settings: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load system settings.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User adminUser = getAuthenticatedUser(request);
        if (adminUser == null || !adminUser.isAdmin()) {
            sendForbidden(request, response, "Admin authentication required.");
            return;
        }

        try {
            // Update individual settings submitted by form
            String[] settingKeys = {
                    AppConfig.SETTING_COMMUNITY_NAME,
                    "community_description",
                    AppConfig.SETTING_MAINTENANCE_MODE,
                    AppConfig.SETTING_REGISTRATION_OPEN,
                    AppConfig.SETTING_MAX_UPLOAD_MB,
                    AppConfig.SETTING_DEFAULT_PAGE_SIZE,
                    AppConfig.SETTING_MODERATION_POLICY,
                    "contact_email"
            };

            for (String key : settingKeys) {
                String val = request.getParameter(key);
                if (val != null) {
                    settingService.updateSetting(adminUser.getId(), adminUser.getUsername(), key, val);
                }
            }

            // Update application context with latest community name
            String newCommunityName = request.getParameter(AppConfig.SETTING_COMMUNITY_NAME);
            if (newCommunityName != null && !newCommunityName.trim().isEmpty()) {
                getServletContext().setAttribute(AppConfig.APP_COMMUNITY_NAME, newCommunityName.trim());
            }

            setFlashSuccess(request, "System settings updated and persisted successfully to the database.");
            redirect(request, response, "/admin/settings");
        } catch (Exception e) {
            logger.error("Error saving system settings: {}", e.getMessage(), e);
            setFlashError(request, "Failed to update system settings: " + e.getMessage());
            redirect(request, response, "/admin/settings");
        }
    }
}
