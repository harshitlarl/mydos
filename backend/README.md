# MyDOS Backend

This is the backend service for MyDOS (My Daily Organization System), a comprehensive daily organization system built with Java, Dropwizard, and a dual database architecture using MySQL and MongoDB.

## Features

- 🔄 RESTful API for task and expense management
- 📊 Analytics collection via MongoDB
- 🔒 Dual database architecture: MySQL for transactional data, MongoDB for analytics
- 📝 Comprehensive logging and monitoring
- 🔧 Environment-specific configurations for local development and production

## Project Structure

```
backend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── mydos/
│                   ├── MyDOSApplication.java    # Main application entry point
│                   ├── MyDOSConfiguration.java  # Configuration class
│                   ├── core/                    # Domain models (SQL entities)
│                   │   ├── Expense.java
│                   │   ├── Task.java
│                   │   └── User.java
│                   ├── db/                      # Database access
│                   │   └── mongo/               # MongoDB specific code
│                   │       ├── MongoDbService.java
│                   │       └── dao/             # Data Access Objects
│                   │           ├── AnalyticsDataDAO.java
│                   │           └── UserActivityLogDAO.java
│                   ├── document/                # MongoDB document models
│                   │   ├── AnalyticsData.java
│                   │   └── UserActivityLog.java
│                   ├── health/                  # Health checks
│                   │   ├── MongoDbHealthCheck.java
│                   │   └── TemplateHealthCheck.java
│                   └── resources/               # API Resources/Controllers
│                       ├── AnalyticsResource.java
│                       ├── ExpenseResource.java
│                       ├── TaskResource.java
│                       └── UserResource.java
├── config.yml         # Default configuration
├── config-local.yml   # Local development configuration
├── config-prod.yml    # Production configuration
├── Dockerfile         # Docker configuration
└── pom.xml            # Maven dependencies
```

## Tech Stack

- **Java 11**: Core programming language
- **Dropwizard**: RESTful API framework
- **Hibernate**: ORM for database access
- **MySQL**: Primary relational database (for transactional data)
- **MongoDB**: Secondary NoSQL database (for analytics and logs)
- **Lombok**: Reduces boilerplate code
- **Maven**: Build and dependency management
- **Docker**: Containerization for deployment

## Database Design

### MySQL (Relational)

Stores transactional data with tables:
- Users: User accounts and authentication
- Tasks: User tasks with status and due dates
- Expenses: Financial transactions with categories

### MongoDB (NoSQL)

Stores analytics and log data:
- UserActivityLog: Tracks user actions for analytics
- AnalyticsData: Aggregated metrics and statistics

## Running the Backend

### Local Development

#### Option 1: Using Docker (Recommended)

The simplest way to run the backend in development mode is using Docker Compose from the root directory:

```bash
# From the root directory
./start-local.sh
```

This will start the backend with MySQL and MongoDB at:
- API: http://localhost:8080/api
- Admin: http://localhost:8081

#### Option 2: Running Directly

If you prefer to run the backend directly:

```bash
cd backend
mvn clean install
java -jar target/mydos-backend-service-1.0-SNAPSHOT.jar server config-local.yml
```

This requires MySQL and MongoDB to be running locally with appropriate configurations.

### Production Deployment

#### Building for Production

```bash
# Build the JAR file
mvn clean package

# This creates a JAR file in the target/ directory
```

#### Using Docker for Production

```bash
# Build the production Docker image
docker build -t mydos-backend:latest .

# Run the production container
docker run -p 8080:8080 -p 8081:8081 \
  -e MYSQL_USER=prod_user \
  -e MYSQL_PASSWORD=prod_password \
  -e MONGODB_URI=mongodb://user:pass@host:port/db \
  mydos-backend:latest
```

## Configuration

The application uses YAML configuration with environment-specific files:

- `config.yml`: Default configuration
- `config-local.yml`: Local development settings
- `config-prod.yml`: Production settings

Configuration includes:
- Database connections (MySQL/PostgreSQL and MongoDB)
- Logging settings
- Server ports and settings
- Environment indicators

## API Endpoints

### Tasks

- `GET /api/tasks` - List tasks with optional filters
- `GET /api/tasks/{id}` - Get a specific task
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task
- `PUT /api/tasks/{id}/complete` - Mark task as complete/incomplete

### Expenses

- `GET /api/expenses` - List expenses with optional filters
- `GET /api/expenses/{id}` - Get a specific expense
- `POST /api/expenses` - Create a new expense
- `PUT /api/expenses/{id}` - Update an expense
- `DELETE /api/expenses/{id}` - Delete an expense
- `GET /api/expenses/summary` - Get expense summary statistics

### Analytics

- `POST /api/analytics/activity` - Record user activity
- `GET /api/analytics/activity/user/{userId}` - Get user activity logs
- `GET /api/analytics/metrics/{metricType}` - Get analytics data
- `PUT /api/analytics/metrics/{metricType}/{date}` - Update metrics
- `GET /api/analytics/system/stats` - Get system statistics

## Environment Variables

For production, the following environment variables are used:

- `MYSQL_USER`: MySQL database username
- `MYSQL_PASSWORD`: MySQL database password
- `MONGODB_URI`: MongoDB connection URI
- `PORT`: Application port (default: 8080)
- `ADMIN_PORT`: Admin port (default: 8081)

## Contributing

1. Follow Java coding conventions
2. Add proper JavaDoc documentation
3. Include unit tests for new features
4. Use proper exception handling and logging
5. Update configuration examples as needed