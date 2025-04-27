package com.mydos.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

/**
 * AnalyticsData document stores aggregated metrics and analytics for the application.
 * This document is stored in MongoDB for analytics and reporting purposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsData {
    
    private ObjectId id;
    
    private String metricType;
    
    private LocalDate date;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Generic field to store different types of metrics
    private Map<String, Object> metrics;
    
    // Time-series data points for charts and graphs
    private List<TimeSeriesDataPoint> timeSeriesData;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSeriesDataPoint {
        private LocalDateTime timestamp;
        private Map<String, Object> values;
    }
}package com.mydos.document;

public class AnalyticsData {
    
}
