package com.mydos.db.mongo.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mydos.db.mongo.MongoDbService;
import com.mydos.document.UserActivityLog;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for UserActivityLog documents in MongoDB.
 * Handles CRUD operations for user activity logging.
 */
@Slf4j
public class UserActivityLogDAO {
    
    private final MongoDbService mongoDbService;
    
    @Inject
    public UserActivityLogDAO(MongoDbService mongoDbService) {
        this.mongoDbService = mongoDbService;
    }
    
    /**
     * Insert a new user activity log entry
     * 
     * @param activityLog The user activity to log
     * @return The inserted document with generated ID
     */
    public UserActivityLog insert(UserActivityLog activityLog) {
        try {
            // Set timestamp if not already set
            if (activityLog.getTimestamp() == null) {
                activityLog.setTimestamp(LocalDateTime.now());
            }
            
            MongoCollection<UserActivityLog> collection = mongoDbService.getUserActivityCollection();
            InsertOneResult result = collection.insertOne(activityLog);
            
            if (result.getInsertedId() != null) {
                activityLog.setId(result.getInsertedId().asObjectId().getValue());
            }
            
            return activityLog;
        } catch (Exception e) {
            log.error("Error inserting user activity log", e);
            throw e;
        }
    }
    
    /**
     * Find activity logs for a specific user
     * 
     * @param userId The user ID to find activity for
     * @return List of activity logs
     */
    public List<UserActivityLog> findByUserId(Long userId) {
        try {
            MongoCollection<UserActivityLog> collection = mongoDbService.getUserActivityCollection();
            return collection.find(Filters.eq("userId", userId))
                           .sort(Sorts.descending("timestamp"))
                           .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Error finding user activity logs by userId: {}", userId, e);
            throw e;
        }
    }
    
    /**
     * Find activity logs by activity type
     * 
     * @param activityType The type of activity to filter by
     * @return List of activity logs
     */
    public List<UserActivityLog> findByActivityType(String activityType) {
        try {
            MongoCollection<UserActivityLog> collection = mongoDbService.getUserActivityCollection();
            return collection.find(Filters.eq("activityType", activityType))
                           .sort(Sorts.descending("timestamp"))
                           .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Error finding user activity logs by activityType: {}", activityType, e);
            throw e;
        }
    }
    
    /**
     * Find activity logs between two dates
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (exclusive)
     * @return List of activity logs
     */
    public List<UserActivityLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            MongoCollection<UserActivityLog> collection = mongoDbService.getUserActivityCollection();
            Bson dateFilter = Filters.and(
                Filters.gte("timestamp", startDate),
                Filters.lt("timestamp", endDate)
            );
            
            return collection.find(dateFilter)
                           .sort(Sorts.descending("timestamp"))
                           .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Error finding user activity logs by date range", e);
            throw e;
        }
    }
    
    /**
     * Delete activity logs older than specified date
     * 
     * @param olderThan Date threshold for deletion
     * @return Number of deleted logs
     */
    public long deleteOlderThan(LocalDateTime olderThan) {
        try {
            MongoCollection<UserActivityLog> collection = mongoDbService.getUserActivityCollection();
            DeleteResult result = collection.deleteMany(
                Filters.lt("timestamp", olderThan)
            );
            
            return result.getDeletedCount();
        } catch (Exception e) {
            log.error("Error deleting old user activity logs", e);
            throw e;
        }
    }
}package com.mydos.db.mongo.dao;

public class UserActivityLogDAO {
    
}
