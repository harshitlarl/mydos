package com.mydos.resources;

import com.mydos.db.mongo.dao.AnalyticsDataDAO;
import com.mydos.db.mongo.dao.UserActivityLogDAO;
import com.mydos.document.AnalyticsData;
import com.mydos.document.UserActivityLog;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API resource for accessing analytics data stored in MongoDB.
 * Provides endpoints for user activity logs and aggregated metrics.
 */
@Path("/api/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class AnalyticsResource {

    private final UserActivityLogDAO userActivityLogDAO;
    private final AnalyticsDataDAO analyticsDataDAO;

    public AnalyticsResource(UserActivityLogDAO userActivityLogDAO, AnalyticsDataDAO analyticsDataDAO) {
        this.userActivityLogDAO = userActivityLogDAO;
        this.analyticsDataDAO = analyticsDataDAO;
    }

    /**
     * Record user activity
     */
    @POST
    @Path("/activity")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recordActivity(UserActivityLog activityLog) {
        try {
            log.debug("Recording user activity for userId: {}, type: {}", 
                activityLog.getUserId(), activityLog.getActivityType());
            
            // Set timestamp if not provided
            if (activityLog.getTimestamp() == null) {
                activityLog.setTimestamp(LocalDateTime.now());
            }
            
            UserActivityLog saved = userActivityLogDAO.insert(activityLog);
            return Response.status(Response.Status.CREATED).entity(saved).build();
        } catch (Exception e) {
            log.error("Error recording user activity", e);
            return Response.serverError().entity("Failed to record activity").build();
        }
    }

    /**
     * Get user activity logs
     */
    @GET
    @Path("/activity/user/{userId}")
    public Response getUserActivityLogs(@PathParam("userId") Long userId,
                                      @QueryParam("startDate") String startDateStr,
                                      @QueryParam("endDate") String endDateStr) {
        try {
            log.debug("Fetching activity logs for userId: {}", userId);
            
            if (startDateStr != null && endDateStr != null) {
                try {
                    LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
                    LocalDateTime endDate = LocalDate.parse(endDateStr).plusDays(1).atStartOfDay();
                    
                    List<UserActivityLog> logs = userActivityLogDAO.findByDateRange(startDate, endDate);
                    return Response.ok(logs).build();
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid date format. Use yyyy-MM-dd").build();
                }
            } else {
                List<UserActivityLog> logs = userActivityLogDAO.findByUserId(userId);
                return Response.ok(logs).build();
            }
        } catch (Exception e) {
            log.error("Error fetching user activity logs", e);
            return Response.serverError().entity("Failed to retrieve activity logs").build();
        }
    }

    /**
     * Get analytics data for a specific metric and date range
     */
    @GET
    @Path("/metrics/{metricType}")
    public Response getAnalyticsData(@PathParam("metricType") String metricType,
                                  @QueryParam("startDate") String startDateStr,
                                  @QueryParam("endDate") String endDateStr) {
        try {
            log.debug("Fetching analytics data for metricType: {}", metricType);
            
            if (startDateStr == null || endDateStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both startDate and endDate are required").build();
            }

            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);
                
                List<AnalyticsData> data = analyticsDataDAO.findByDateRange(metricType, startDate, endDate);
                return Response.ok(data).build();
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format. Use yyyy-MM-dd").build();
            }
        } catch (Exception e) {
            log.error("Error fetching analytics data", e);
            return Response.serverError().entity("Failed to retrieve analytics data").build();
        }
    }

    /**
     * Save or update analytics data for a specific date
     */
    @PUT
    @Path("/metrics/{metricType}/{date}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMetrics(@PathParam("metricType") String metricType,
                               @PathParam("date") String dateStr,
                               Map<String, Object> metrics) {
        try {
            log.debug("Updating metrics for type: {} and date: {}", metricType, dateStr);
            
            try {
                LocalDate date = LocalDate.parse(dateStr);
                
                // Find or create analytics entry
                AnalyticsData data = analyticsDataDAO.findOrCreate(metricType, date);
                
                // Update metrics
                data.setMetrics(metrics);
                
                // Save changes
                AnalyticsData updated = analyticsDataDAO.save(data);
                
                return Response.ok(updated).build();
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format. Use yyyy-MM-dd").build();
            }
        } catch (Exception e) {
            log.error("Error updating metrics", e);
            return Response.serverError().entity("Failed to update metrics").build();
        }
    }

    /**
     * Get system stats
     */
    @GET
    @Path("/system/stats")
    public Response getSystemStats() {
        try {
            log.debug("Fetching system stats");
            
            // This would be expanded with actual calculations from both databases
            Map<String, Object> stats = new HashMap<>();
            
            // Sample stats that would be computed from actual data
            stats.put("activeUsers", 42);
            stats.put("totalTasks", 156);
            stats.put("completedTasks", 87);
            stats.put("pendingTasks", 69);
            stats.put("totalExpenses", 3456.78);
            
            return Response.ok(stats).build();
        } catch (Exception e) {
            log.error("Error fetching system stats", e);
            return Response.serverError().entity("Failed to retrieve system stats").build();
        }
    }
}