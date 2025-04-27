package com.mydos.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * UserActivityLog represents a log of user activities in the system.
 * This document is stored in MongoDB for analytical purposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityLog {
    
    private ObjectId id;
    
    private Long userId;
    
    private String username;
    
    private String activityType;
    
    private String description;
    
    private LocalDateTime timestamp;
    
    private String ipAddress;
    
    private String userAgent;
    
    private Map<String, Object> additionalDetails;
}