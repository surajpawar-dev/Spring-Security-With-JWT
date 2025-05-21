package com.suraj.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/welcome")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<Map<String, String>> welcome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Auth Service");
        response.put("user", auth.getName());
        response.put("role", auth.getAuthorities().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint accessible to all authenticated users (ROLE_USER and above)
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "User Endpoint");
        response.put("access_level", "All authenticated users");
        response.put("description", "This endpoint is accessible to all authenticated users (ROLE_USER and above)");
        response.put("permissions", "Basic user permissions");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint accessible to moderators and above (ROLE_MODERATOR, ROLE_SUPERVISOR, ROLE_MANAGER, ROLE_ADMIN)
     */
    @GetMapping("/moderator")
    public ResponseEntity<Map<String, Object>> moderatorEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "Moderator Endpoint");
        response.put("access_level", "Moderators and above");
        response.put("description", "This endpoint is accessible to moderators and higher roles");
        response.put("permissions", "Content moderation capabilities");
        response.put("features", new String[]{"User management", "Content moderation", "Report handling"});
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint accessible to supervisors and above (ROLE_SUPERVISOR, ROLE_MANAGER, ROLE_ADMIN)
     */
    @GetMapping("/supervisor")
    public ResponseEntity<Map<String, Object>> supervisorEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "Supervisor Endpoint");
        response.put("access_level", "Supervisors and above");
        response.put("description", "This endpoint is accessible to supervisors and higher roles");
        response.put("permissions", "Advanced supervision capabilities");
        response.put("features", new String[]{"User management", "Content moderation", "Report handling", 
                                            "Moderator oversight", "Analytics access"});
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint accessible to managers and above (ROLE_MANAGER, ROLE_ADMIN)
     */
    @GetMapping("/manager")
    public ResponseEntity<Map<String, Object>> managerEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "Manager Endpoint");
        response.put("access_level", "Managers and above");
        response.put("description", "This endpoint is accessible to managers and higher roles");
        response.put("permissions", "Management capabilities");
        response.put("features", new String[]{"User management", "Content moderation", "Report handling",
                                           "Moderator oversight", "Analytics access", "Staff management",
                                           "Financial reports", "Performance metrics"});
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint accessible only to administrators (ROLE_ADMIN)
     */
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> adminEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "Admin Endpoint");
        response.put("access_level", "Administrators only");
        response.put("description", "This endpoint is accessible only to administrators");
        response.put("permissions", "Full system access");
        response.put("features", new String[]{"User management", "Content moderation", "Report handling",
                                           "Moderator oversight", "Analytics access", "Staff management",
                                           "Financial reports", "Performance metrics", "System configuration",
                                           "Role management", "Security settings", "Database access"});
        return ResponseEntity.ok(response);
    }
}
