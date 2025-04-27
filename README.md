
# MyDOS (My Daily OS)

A comprehensive daily organization system with task management and expense tracking capabilities.

---

## 🌟 What is `mydos`?

A minimalist yet powerful app where you:
- ✅ Build and check off daily routines
- 💰 Track expenses and categorize spending
- 📈 Get insights into your savings and investments
- 🎂 Stay on top of birthdays, anniversaries, and key events
- 📸 Store your memories in a secure personal vault
- 📊 View dashboards that help you reflect, improve, and stay motivated

It’s not just a productivity tool — it’s a life tracker.

---

## Project Structure

```
mydos/
├── backend/ - Java/Maven/Dropwizard backend
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── mydos/
│   │                   ├── core/ - Domain models
│   │                   ├── health/ - Health checks
│   │                   └── resources/ - API resources
│   ├── config.yml - Dropwizard configuration
│   ├── Dockerfile - Backend Docker setup
│   └── pom.xml - Maven dependencies
├── frontend/ - React TypeScript frontend
│   ├── src/
│   │   ├── components/ - Reusable UI components
│   │   └── pages/ - React pages
│   ├── Dockerfile - Frontend Docker setup
│   ├── nginx.conf - NGINX configuration
│   └── package.json - NPM dependencies
├── k8s/ - Kubernetes configurations for GCP deployment
│   ├── backend.yaml - Backend deployment & service
│   ├── frontend.yaml - Frontend deployment, service & ingress
│   ├── namespace.yaml - Kubernetes namespace
│   └── postgres.yaml - Database deployment & service
└── docker-compose.yml - Local development setup
```

---

## 🧠 Core Features (WIP - Work In Progress)

### 📋 Daily Routine & Checklist
- Create custom routines like:
  - Did I go to the gym?
  - Did I read today?
  - Did I walk for 1 hour?
- Push notifications / reminders
- Daily check-in, history tracking, and habit streaks

### 💸 Expense Tracker
- Add expenses with categories (food, transport, etc.)
- Daily/Monthly dashboards
- Export to CSV/Excel

### 📊 Investment & Savings Monitor
- Track balances across multiple accounts
- View consolidated reports of net worth
- Get reminders to save or invest

### 🎉 Personal Calendar
- Track birthdays, events, and recurring personal dates
- Get smart reminders

### 📷 Memory Vault
- Upload photos, notes, journal entries
- Searchable, secure, and private

### 📈 Dashboards & Analytics
- Visualize your life with charts & stats
- Daily/weekly/monthly reports

---

## Technology Stack

### Backend
- Java 11
- Maven
- Dropwizard framework
- PostgreSQL database
- Hibernate ORM
- RESTful API

### Frontend
- React 18
- TypeScript
- Material UI components
- React Router
- Formik & Yup for form handling
- Chart.js for data visualization

### DevOps
- Docker & Docker Compose
- Kubernetes
- Google Cloud Platform deployment
- NGINX for frontend serving and routing

---

## 🛠 Getting Started

### Prerequisites
- Java 11+
- Maven 3.8+
- Node.js 16+
- Docker and Docker Compose
- kubectl (for Kubernetes deployment)
- Google Cloud SDK (for GCP deployment)

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mydos.git
   cd mydos
   ```

2. Run with Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - Frontend: http://localhost
   - Backend API: http://localhost:8080/api
   - Backend Admin: http://localhost:8081

### Backend Development

To work on the backend separately:

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the application:
   ```bash
   mvn clean install
   java -jar target/mydos-backend-1.0-SNAPSHOT.jar server config.yml
   ```

### Frontend Development

To work on the frontend separately:

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies and start the development server:
   ```bash
   npm install
   npm start
   ```

---

## Deployment to Google Cloud Platform (GCP)

### Prerequisites
- Google Cloud SDK installed and configured
- GKE (Google Kubernetes Engine) cluster created
- Docker images pushed to Google Container Registry (GCR)

### Steps to Deploy

1. Build and push Docker images:
   ```bash
   # Set your GCP project ID
   export PROJECT_ID=your-project-id
   
   # Build and push backend image
   cd backend
   docker build -t gcr.io/${PROJECT_ID}/mydos-backend:latest .
   docker push gcr.io/${PROJECT_ID}/mydos-backend:latest
   
   # Build and push frontend image
   cd ../frontend
   docker build -t gcr.io/${PROJECT_ID}/mydos-frontend:latest .
   docker push gcr.io/${PROJECT_ID}/mydos-frontend:latest
   ```

2. Update Kubernetes manifests with your project ID:
   ```bash
   sed -i "s/PROJECT_ID/${PROJECT_ID}/g" k8s/backend.yaml
   sed -i "s/PROJECT_ID/${PROJECT_ID}/g" k8s/frontend.yaml
   ```

3. Deploy to GKE:
   ```bash
   # Create namespace
   kubectl apply -f k8s/namespace.yaml
   
   # Deploy database
   kubectl apply -f k8s/postgres.yaml
   
   # Deploy backend
   kubectl apply -f k8s/backend.yaml
   
   # Deploy frontend and ingress
   kubectl apply -f k8s/frontend.yaml
   ```

4. Get the external IP:
   ```bash
   kubectl get ingress mydos-ingress -n mydos
   ```

5. Configure your DNS to point to the ingress IP address.

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.
