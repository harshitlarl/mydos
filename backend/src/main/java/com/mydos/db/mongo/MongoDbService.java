package com.mydos.db.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mydos.MyDOSConfiguration;
import com.mydos.document.AnalyticsData;
import com.mydos.document.UserActivityLog;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing MongoDB connections and operations.
 * This service creates and manages MongoDB clients, databases, and collections.
 */
@Slf4j
public class MongoDbService {
    
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MyDOSConfiguration.MongoDbConfig config;
    
    // Collection names
    private static final String USER_ACTIVITY_COLLECTION = "user_activities";
    private static final String ANALYTICS_COLLECTION = "analytics";
    
    public MongoDbService(MyDOSConfiguration configuration) {
        this.config = configuration.getMongoDbConfig();
        
        // Create codec registry for POJO mapping
        CodecRegistry pojoCodecRegistry = fromRegistries(
                org.bson.codecs.configuration.CodecRegistries.fromCodecs(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        
        // Create MongoDB client
        log.info("Initializing MongoDB connection to: {}", maskCredentials(config.getUri()));
        this.mongoClient = MongoClients.create(config.getUri());
        this.database = mongoClient.getDatabase(config.getDatabase())
                                  .withCodecRegistry(pojoCodecRegistry);
        
        log.info("Connected to MongoDB database: {}", config.getDatabase());
    }
    
    /**
     * Get the MongoDB collection for user activity logs
     * @return MongoCollection for UserActivityLog documents
     */
    public MongoCollection<UserActivityLog> getUserActivityCollection() {
        return database.getCollection(USER_ACTIVITY_COLLECTION, UserActivityLog.class);
    }
    
    /**
     * Get the MongoDB collection for analytics data
     * @return MongoCollection for AnalyticsData documents
     */
    public MongoCollection<AnalyticsData> getAnalyticsCollection() {
        return database.getCollection(ANALYTICS_COLLECTION, AnalyticsData.class);
    }
    
    /**
     * Close the MongoDB client connection
     */
    public void close() {
        if (mongoClient != null) {
            log.info("Closing MongoDB connection");
            mongoClient.close();
        }
    }
    
    /**
     * Masks credentials in the MongoDB URI for logging purposes
     */
    private String maskCredentials(String uri) {
        return uri.replaceAll("(mongodb://[^:]*:)[^@]*(@.*)", "$1****$2");
    }
}