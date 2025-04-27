package com.mydos;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MyDOSConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    
    @Valid
    @NotNull
    private MongoDbConfig mongoDb = new MongoDbConfig();
    
    @NotNull
    private String environment;
    
    @JsonProperty
    public String getTemplate() {
        return template;
    }
    
    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }
    
    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }
    
    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
    
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }
    
    @JsonProperty("mongodb")
    public MongoDbConfig getMongoDbConfig() {
        return mongoDb;
    }
    
    @JsonProperty("mongodb")
    public void setMongoDbConfig(MongoDbConfig mongoDb) {
        this.mongoDb = mongoDb;
    }
    
    @JsonProperty("environment")
    public String getEnvironment() {
        return environment;
    }
    
    @JsonProperty("environment")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public boolean isProduction() {
        return "prod".equalsIgnoreCase(environment);
    }
    
    public boolean isDevelopment() {
        return "local".equalsIgnoreCase(environment) || "dev".equalsIgnoreCase(environment);
    }
    
    /**
     * MongoDB configuration class
     */
    @Getter
    @Setter
    public static class MongoDbConfig {
        @NotEmpty
        private String host = "localhost";
        
        private int port = 27017;
        
        @NotEmpty
        private String database = "mydos";
        
        private String user;
        
        private String password;
        
        private String authSource = "admin";
        
        private boolean sslEnabled = false;
        
        // Connection URI will be constructed based on properties or can be provided directly
        private String uri;
        
        @JsonProperty
        public String getUri() {
            if (uri != null && !uri.isEmpty()) {
                return uri;
            }
            
            // Build URI from components if direct URI not provided
            StringBuilder uriBuilder = new StringBuilder("mongodb://");
            
            // Add credentials if present
            if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
                uriBuilder.append(user).append(":").append(password).append("@");
            }
            
            // Add host and port
            uriBuilder.append(host).append(":").append(port);
            
            // Add database and auth params
            uriBuilder.append("/").append(database)
                      .append("?authSource=").append(authSource);
            
            if (sslEnabled) {
                uriBuilder.append("&ssl=true");
            }
            
            return uriBuilder.toString();
        }
    }
}