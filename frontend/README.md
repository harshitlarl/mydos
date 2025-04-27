# MyDOS Frontend

This is the frontend for MyDOS (My Daily Organization System), a comprehensive daily organization system built with React, TypeScript, and Material UI.

## Features

- 📊 Dashboard with task and expense overviews
- ✅ Task management with priority and due dates
- 💸 Expense tracking with categories and reports
- 📱 Responsive design for desktop and mobile
- 🌙 Material UI components for modern UI/UX

## Project Structure

```
frontend/
├── public/              # Public assets
├── src/
│   ├── components/      # Reusable UI components
│   │   └── Layout.tsx   # Main layout component with navigation
│   ├── pages/           # React pages/views
│   │   ├── Dashboard.tsx # Main dashboard view
│   │   ├── Tasks.tsx    # Task management view
│   │   └── Expenses.tsx # Expense tracking view  
│   ├── services/        # API services
│   │   └── api.ts       # API client for backend communication
│   ├── App.tsx          # Main App component with routing
│   └── index.tsx        # Application entry point
├── Dockerfile           # Production Docker configuration
├── Dockerfile.dev       # Development Docker configuration
├── nginx.conf           # NGINX configuration for production
└── package.json         # NPM dependencies and scripts
```

## Tech Stack

- **React 18**: Modern UI library
- **TypeScript**: Type-safe JavaScript
- **Material UI**: Component library for consistent design
- **React Router**: Client-side routing
- **Axios**: HTTP client for API communication
- **Chart.js**: Interactive charts and visualizations
- **Formik & Yup**: Form handling and validation

## Running the Frontend

### Local Development

#### Option 1: Using Docker (Recommended)

The simplest way to run the frontend in development mode is using Docker Compose from the root directory:

```bash
# From the root directory
./start-local.sh
```

This will start the frontend with hot reloading at http://localhost:3000

#### Option 2: Running Directly

If you prefer to run the frontend directly:

```bash
cd frontend
npm install
npm start
```

The app will be available at http://localhost:3000

### Production Deployment

#### Building for Production

```bash
# Build the production bundle
npm run build

# This creates a build/ folder with optimized production files
```

#### Using Docker for Production

```bash
# Build the production Docker image
docker build -t mydos-frontend:latest .

# Run the production container
docker run -p 80:80 mydos-frontend:latest
```

The production build will be available at http://localhost

## Environment Variables

The following environment variables can be set:

- `REACT_APP_API_URL`: Backend API URL (defaults to http://localhost:8080/api)
- `NODE_ENV`: Set to 'development' or 'production'

## Available Scripts

- `npm start`: Run the development server
- `npm run build`: Build for production
- `npm test`: Run tests
- `npm run eject`: Eject from create-react-app

## API Integration

The frontend communicates with the backend using the API service located in `src/services/api.ts`. This service provides methods for:

- Task management (create, read, update, delete)
- Expense tracking (create, read, update, delete)
- Analytics data retrieval
- User activity logging

## Contributing

1. Make sure code follows the existing style
2. Add appropriate tests for new features
3. Ensure all tests pass before submitting changes
4. Document new components and features