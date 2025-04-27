package com.mydos;

import com.mydos.core.Expense;
import com.mydos.core.Task;
import com.mydos.core.User;
import com.mydos.db.mongo.MongoDbService;
import com.mydos.db.mongo.dao.AnalyticsDataDAO;
import com.mydos.db.mongo.dao.UserActivityLogDAO;
import com.mydos.health.MongoDbHealthCheck;
import com.mydos.health.TemplateHealthCheck;
import com.mydos.resources.AnalyticsResource;
import com.mydos.resources.ExpenseResource;
import com.mydos.resources.TaskResource;
import com.mydos.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * Main application class for MyDOS (My Daily Organization System).
 * Initializes and configures the application, databases, and resources.
 */
@Slf4j
public class MyDOSApplication extends Application<MyDOSConfiguration> {
    
    // Hibernate bundle for MySQL/PostgreSQL connectivity
    private final HibernateBundle<MyDOSConfiguration> hibernateBundle =
        new HibernateBundle<MyDOSConfiguration>(User.class, Task.class, Expense.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(MyDOSConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };
    
    // MongoDB service instance
    private MongoDbService mongoDbService;
    
    // MongoDB DAOs
    private UserActivityLogDAO userActivityLogDAO;
    private AnalyticsDataDAO analyticsDataDAO;
    
    public static void main(String[] args) throws Exception {
        new MyDOSApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "mydos";
    }
    
    @Override
    public void initialize(Bootstrap<MyDOSConfiguration> bootstrap) {
        // Add Hibernate bundle
        bootstrap.addBundle(hibernateBundle);
        
        // Add migrations bundle
        bootstrap.addBundle(new MigrationsBundle<MyDOSConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(MyDOSConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }
    
    @Override
    public void run(MyDOSConfiguration configuration, Environment environment) {
        log.info("Starting MyDOS application in {} environment", configuration.getEnvironment());
        
        // Initialize MongoDB service if enabled
        initMongoDbService(configuration, environment);
        
        // Register health checks
        registerHealthChecks(configuration, environment);
        
        // Register resources
        registerResources(configuration, environment);
        
        // Enable CORS
        enableCors(environment);
        
        log.info("MyDOS application initialization complete");
    }
    
    /**
     * Initialize MongoDB service
     */
    private void initMongoDbService(MyDOSConfiguration configuration, Environment environment) {
        try {
            this.mongoDbService = new MongoDbService(configuration);
            
            // Initialize DAOs
            this.userActivityLogDAO = new UserActivityLogDAO(mongoDbService);
            this.analyticsDataDAO = new AnalyticsDataDAO(mongoDbService);
            
            // Add a managed lifecycle for MongoDB to ensure proper shutdown
            environment.lifecycle().manage(new io.dropwizard.lifecycle.Managed() {
                @Override
                public void start() {
                    // MongoDB service is already started in constructor
                    log.info("MongoDB service started");
                }

                @Override
                public void stop() {
                    if (mongoDbService != null) {
                        mongoDbService.close();
                        log.info("MongoDB service stopped");
                    }
                }
            });
            
            log.info("MongoDB service initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize MongoDB service", e);
            if (configuration.isProduction()) {
                throw e;  // Fail fast in production
            }
        }
    }
    
    /**
     * Register health checks
     */
    private void registerHealthChecks(MyDOSConfiguration configuration, Environment environment) {
        // Register template health check
        final TemplateHealthCheck templateHealthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", templateHealthCheck);
        
        // Register MongoDB health check if service is available
        if (mongoDbService != null) {
            final MongoDbHealthCheck mongoHealthCheck = new MongoDbHealthCheck(mongoDbService);
            environment.healthChecks().register("mongodb", mongoHealthCheck);
        }
    }
    
    /**
     * Register REST API resources
     */
    private void registerResources(MyDOSConfiguration configuration, Environment environment) {
        // Get session factory for SQL database resources
        final org.hibernate.SessionFactory sessionFactory = hibernateBundle.getSessionFactory();
        
        // Register SQL database resources
        environment.jersey().register(new UserResource(sessionFactory));
        environment.jersey().register(new TaskResource(sessionFactory));
        environment.jersey().register(new ExpenseResource(sessionFactory));
        
        // Register MongoDB resources if available
        if (mongoDbService != null) {
            environment.jersey().register(new AnalyticsResource(userActivityLogDAO, analyticsDataDAO));
        }
    }
    
    /**
     * Enable CORS for cross-origin requests
     */
    private void enableCors(Environment environment) {
        final org.eclipse.jetty.servlets.CrossOriginFilter filter = 
            new org.eclipse.jetty.servlets.CrossOriginFilter();
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        filter.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        
        environment.servlets().addFilter("CORS", filter)
            .addMappingForUrlPatterns(null, true, "/*");
    }
}