package com.mydos.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mydos.db.mongo.MongoDbService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

/**
 * Health check for MongoDB connectivity.
 * Verifies that the MongoDB connection is active and working.
 */
@Slf4j
public class MongoDbHealthCheck extends HealthCheck {
    
    private final MongoDbService mongoDbService;
    
    public MongoDbHealthCheck(MongoDbService mongoDbService) {
        this.mongoDbService = mongoDbService;
    }
    
    @Override
    protected Result check() {
        try {
            // Try to execute a simple query against MongoDB
            MongoCollection<Document> collection = mongoDbService
                .getUserActivityCollection()
                .withDocumentClass(Document.class);
            
            // Simple count operation to verify connectivity
            collection.countDocuments(Filters.eq("_id", "health-check"));
            
            return Result.healthy();
        } catch (Exception e) {
            log.error("MongoDB health check failed", e);
            return Result.unhealthy("MongoDB connection failure: " + e.getMessage());
        }
    }
}