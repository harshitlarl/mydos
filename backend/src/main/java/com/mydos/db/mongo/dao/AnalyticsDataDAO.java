package com.mydos.db.mongo.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mydos.db.mongo.MongoDbService;
import com.mydos.document.AnalyticsData;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AnalyticsData documents in MongoDB.
 * Provides CRUD operations for analytics metrics and aggregated data.
 */
@Slf4j
public class AnalyticsDataDAO {
    
    private final MongoDbService mongoDbService;
    
    @Inject
    public AnalyticsDataDAO(MongoDbService mongoDbService) {
        this.mongoDbService = mongoDbService;
    }
    
    /**
     * Save analytics data - either inserts new or updates existing
     * 
     * @param analyticsData The analytics data to save
     * @return The saved document with generated ID
     */
    public AnalyticsData save(AnalyticsData analyticsData) {
        try {
            MongoCollection<AnalyticsData> collection = mongoDbService.getAnalyticsCollection();
            
            // Set creation/update timestamps
            LocalDateTime now = LocalDateTime.now();
            if (analyticsData.getCreatedAt() == null) {
                analyticsData.setCreatedAt(now);
            }
            analyticsData.setUpdatedAt(now);
            
            // Insert if new, otherwise update based on metric type and date
            if (analyticsData.getId() == null) {
                collection.insertOne(analyticsData);
            } else {
                collection.replaceOne(
                    Filters.eq("_id", analyticsData.getId()), 
                    analyticsData
                );
            }
            
            return analyticsData;
        } catch (Exception e) {
            log.error("Error saving analytics data: {}", analyticsData.getMetricType(), e);
            throw e;
        }
    }
    
    /**
     * Find or create analytics data for a specific metric type and date
     * 
     * @param metricType The type of metric
     * @param date The date for the metrics
     * @return The found or newly created analytics data
     */
    public AnalyticsData findOrCreate(String metricType, LocalDate date) {
        try {
            MongoCollection<AnalyticsData> collection = mongoDbService.getAnalyticsCollection();
            
            Bson filter = Filters.and(
                Filters.eq("metricType", metricType),
                Filters.eq("date", date)
            );
            
            AnalyticsData existing = collection.find(filter).first();
            
            if (existing != null) {
                return existing;
            }
            
            // Create new analytics data
            AnalyticsData newData = AnalyticsData.builder()
                .metricType(metricType)
                .date(date)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
            collection.insertOne(newData);
            return newData;
        } catch (Exception e) {
            log.error("Error finding/creating analytics data for metricType: {} and date: {}", 
                metricType, date, e);
            throw e;
        }
    }
    
    /**
     * Get analytics data for a specific date range
     * 
     * @param metricType The type of metric to filter by
     * @param startDate The start date (inclusive)
     * @param endDate The end date (exclusive)
     * @return List of analytics data entries
     */
    public List<AnalyticsData> findByDateRange(String metricType, LocalDate startDate, LocalDate endDate) {
        try {
            MongoCollection<AnalyticsData> collection = mongoDbService.getAnalyticsCollection();
            
            Bson filter = Filters.and(
                Filters.eq("metricType", metricType),
                Filters.gte("date", startDate),
                Filters.lt("date", endDate)
            );
            
            return collection.find(filter)
                .sort(Sorts.ascending("date"))
                .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Error finding analytics data for date range", e);
            throw e;
        }
    }
    
    /**
     * Update metrics in an analytics document
     * 
     * @param id The document ID
     * @param metrics The metrics to update/replace
     * @return The updated document
     */
    public AnalyticsData updateMetrics(ObjectId id, java.util.Map<String, Object> metrics) {
        try {
            MongoCollection<AnalyticsData> collection = mongoDbService.getAnalyticsCollection();
            
            // Get the current document
            AnalyticsData existing = collection.find(Filters.eq("_id", id)).first();
            
            if (existing == null) {
                throw new IllegalArgumentException("Analytics data not found with id: " + id);
            }
            
            // Update metrics
            existing.setMetrics(metrics);
            existing.setUpdatedAt(LocalDateTime.now());
            
            // Replace the document
            return collection.findOneAndReplace(
                Filters.eq("_id", id),
                existing,
                new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER)
            );
        } catch (Exception e) {
            log.error("Error updating metrics for analytics data with id: {}", id, e);
            throw e;
        }
    }
    
    /**
     * Delete analytics data older than specified date
     * 
     * @param metricType The type of metric
     * @param olderThan Date threshold for deletion
     * @return Number of deleted documents
     */
    public long deleteOlderThan(String metricType, LocalDate olderThan) {
        try {
            MongoCollection<AnalyticsData> collection = mongoDbService.getAnalyticsCollection();
            
            Bson filter = Filters.and(
                Filters.eq("metricType", metricType),
                Filters.lt("date", olderThan)
            );
            
            DeleteResult result = collection.deleteMany(filter);
            return result.getDeletedCount();
        } catch (Exception e) {
            log.error("Error deleting old analytics data", e);
            throw e;
        }
    }
}package com.mydos.db.mongo.dao;

public class AnalyticsDataDAO {
    
}
