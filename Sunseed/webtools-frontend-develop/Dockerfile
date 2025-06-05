# Step 1: Build the app using Node
FROM node:20-alpine AS builder 

# Set working directory
WORKDIR /app

# Copy dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy app source
COPY . .

# Build the app
RUN npm run build

# Step 2: Serve using Nginx
FROM nginx:stable-alpine 

# Copy built files from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Replace default Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]

