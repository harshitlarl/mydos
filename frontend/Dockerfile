# Build stage
FROM node:16-alpine AS build

WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy the source code
COPY . .

# Build the app
RUN npm run build

# Production stage
FROM nginx:1.23-alpine

# Copy built assets from the build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]