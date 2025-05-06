# URL Shortener API

A URL shortener API built with **Spring Boot**, **PostgreSQL**, **Redis**, and **Docker**. The project demonstrates the use of various backend technologies including RESTful API design, caching with Redis, and persistent storage with PostgreSQL. The API accepts requests to shorten URLs and retrieve them via short codes.

## Features
- **Shorten URLs**: Generate short codes using BASE62 encoding for any given original URL.
- **Redirect**: Retrieve the original URL using a short code with HTTP 302 redirects.
- **Caching**: Fast URL lookup through Redis cache, falling back to PostgreSQL if not found in Redis.
- **Dockerized**: Full containerization with Docker and Docker Compose.
- **Demo Available**: Deployed on AWS EC2 for public access.
- **Tests**: Tested with `JUnit` and `Mockito`.

### API Endpoints

- **POST `/shorten`**: Accepts a JSON body with the original URL and returns a shortened URL.
- **GET `/{shortCode}`**: Redirects to the original URL for a given short code.

## Tech Stack
- **Backend**: Spring Boot
- **Database**: PostgreSQL
- **Cache**: Redis
- **Web Server**: Nginx (Reverse Proxy & Rate Limiting)
- **Containerization**: Docker & Docker Compose
- **Cloud**: AWS EC2 (with public demo URL)

## Prerequisites

- Java 21 or higher
- Docker & Docker Compose
- PostgreSQL (if running locally without Docker)
- Redis (if running locally without Docker)

## Running Locally with Docker

1. Clone the repository:
   ```bash
   git clone https://github.com/stichj/urlshortener.git
   cd urlshortener
   
2. Create a new `.env` file in the root of the project:
    ```dotenv
   # PostgreSQL settings
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=your-postgres-password
    POSTGRES_DB=urlshortener
    
    # Redis settings
    REDIS_HOST=cache
    REDIS_PORT=6379
    
    # Spring Boot application settings
    SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/urlshortener
    SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
    SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
    
    # Spring Redis settings
    SPRING_REDIS_HOST=${REDIS_HOST}
    SPRING_REDIS_PORT=${REDIS_PORT}
   
3. Run Docker-Compose:
   ```bash
   docker-compose up --build
   ```

4. The API should now be accessible at http://localhost:8080.
   Try it out with Postman or cURL:
    ```bash
    curl -X POST http://localhost:8080/shorten -H "Content-Type: application/json" -d '{"originalUrl":"http://example.com"}'
    ```
    You should get a full URL mapping returned like this:
    
    ```json
    {
    "id": 1,
    "shortCode": "b",
    "originalUrl": "http://example.com",
    "createdAt": "<creation date and time>"
    }
    ```
   
    Now, you can use the shortCode to `GET`-request the original URL:
    ```bash
    curl -X GET http://localhost:8080/b 
   ```
   
## Demo on AWS EC2
I have deployed a demo on AWS EC2. Check it out using Postman or `cURL`:

1. `POST` new URL mapping:
    ```bash
    curl -X POST http://ec2-18-192-66-154.eu-central-1.compute.amazonaws.com/shorten -H "Content-Type: application/json" -d '{"originalUrl":"http://example.com"}'
    ```
2. `GET` redirect to original URL from valid short code:
    ```bash
    curl -v -X GET http://ec2-18-192-66-154.eu-central-1.compute.amazonaws.com/{shortCode}
    ```
   Make sure you replace `{shortCode}` with a valid short code. E.g. the short code you were given from the `Post`request.
    If the short code is valid you will be able to see the `302` redirect status code and the location you would be routed to. If the short code is invalid you will get a `404 Not Found` status.


3. Open the URL (http://ec2-18-192-66-154.eu-central-1.compute.amazonaws.com/b) with your browser and see the redirect live in action.

## Deploy it on AWS EC2 yourself
1. Start an AWS EC2 instance. The Docker file for the Spring Boot app is optimized for small instances like `t2.micro`.
1. Clone this repository on your EC2 instance.

2. Set up the `.env` file as mentioned above.

3. Install Docker and Docker Compose on the EC2 instance if they are not already installed.

4. Build and start the Docker containers:
   ```bash
   docker-compose up --build -d
   ```
5. Set up NGINX as a reverse proxy: NGINX is configured to reverse proxy requests to the Spring Boot application running inside Docker. It also enforces rate limiting to prevent abuse.
   Example NGINX config:
    ```nginx
    server {
          listen 80;
          server_name _;

          location / {
              proxy_pass http://localhost:8080;
              proxy_http_version 1.1;
              proxy_set_header Upgrade $http_upgrade;
              proxy_set_header Connection keep-alive;
              proxy_set_header Host $host;
              proxy_set_header X-Real-IP $remote_addr;
              proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          }

          location = /shorten {
              limit_req zone=api_limit burst=20 nodelay;
              proxy_pass http://localhost:8080;
              proxy_http_version 1.1;
              proxy_set_header Host $host;
              proxy_set_header X-Real-IP $remote_addr;
              proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          }
    }
    ```
6. Be sure to only allow inbound `TCP` traffic on port `80` in your AWS instances security group settings.

## Next Steps
1. Implement user interface
2. Add user authentication
3. Make short codes customizable 
4. Orchestrate containers with Kubernetes
