# Transport Company API Gateway

A robust and scalable API Gateway service built with Java that serves as the central entry point for the Transport Company microservices ecosystem. This gateway orchestrates communication between client applications and multiple backend services including the core Transport Company project and specialized Email Service microservice.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Routing](#api-routing)
- [Microservices Integration](#microservices-integration)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🌐 Overview

The Transport Company API Gateway is a central hub that manages all incoming API requests and routes them to the appropriate microservices. It provides a unified interface for clients while decoupling the client applications from the individual microservices, enabling independent scaling and deployment of backend services.

### Key Responsibilities

- **Request Routing**: Intelligently routes requests to appropriate microservices based on URL paths and request characteristics
- **Load Balancing**: Distributes traffic across multiple instances of microservices
- **Authentication & Authorization**: Centralized security management for all incoming requests
- **Rate Limiting**: Protects backend services from being overwhelmed with requests
- **Request/Response Transformation**: Normalizes and transforms data between clients and microservices
- **Monitoring & Logging**: Comprehensive logging and monitoring of all gateway operations
- **Circuit Breaking**: Prevents cascading failures when downstream services are unavailable

## 🏗️ Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
│                    (Mobile, Web, Desktop, etc.)                 │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────────────────────────┐
        │   Transport Company API Gateway (This Service) │
        │  ────────────────────────────────────────────  │
        │  • Request Routing                              │
        │  • Authentication                               │
        │  • Rate Limiting                                │
        │  • Load Balancing                               │
        │  • Circuit Breaking                             │
        │  • Monitoring & Logging                         │
        └──────┬──────────────────────────┬──────────────┘
               │                          │
               ▼                          ▼
      ┌────────────────────┐    ┌─────────────────────┐
      │ Transport Company  │    │ Email Service       │
      │ Main Project       │    │ (Microservice)      │
      │                    │    │                     │
      │ • Booking Logic    │    │ • Email Templates   │
      │ • Trip Management  │    │ • Email Sending     │
      │ • User Management  │    │ • Notification Log  │
      │ • Data Persistence │    │ • Async Processing  │
      └────────────────────┘    └─────────────────────┘
```

## ✨ Features

### Core Gateway Features

- **🚀 High Performance**: Built with Java for optimal performance and reliability
- **🔐 Security First**: Implements authentication, authorization, and request validation
- **📊 Scalability**: Designed to handle growing traffic with load balancing capabilities
- **🔄 Service Discovery**: Automatically discovers and routes to available microservices
- **⚡ Rate Limiting**: Protects backend services from abuse and overload
- **🛡️ Circuit Breaker Pattern**: Prevents failures in one service from affecting others
- **📝 Comprehensive Logging**: Detailed logs for debugging and monitoring
- **🔌 Middleware Support**: Extensible middleware pipeline for custom processing
- **✅ Health Checks**: Regular health checks for all connected microservices
- **📊 Metrics & Monitoring**: Built-in metrics collection and monitoring endpoints

### Integrated Microservices

#### Transport Company Main Project
- Complete booking and trip management
- User profile management
- Payment processing
- Route optimization
- Real-time tracking capabilities

#### Email Service Microservice
- Asynchronous email sending
- Customizable email templates
- Bulk email campaigns
- Notification logging and tracking
- Delivery status monitoring

## 🛠️ Tech Stack

- **Language**: Java 11+
- **Build Tool**: Maven / Gradle
- **Framework**: Spring Boot (recommended)
- **API Gateway**: Spring Cloud Gateway / Netflix Zuul
- **Service Discovery**: Eureka / Consul (optional)
- **Load Balancing**: Ribbon / Spring Cloud LoadBalancer
- **Authentication**: JWT / OAuth2
- **Logging**: SLF4J, Logback
- **Monitoring**: Micrometer, Spring Boot Actuator
- **Testing**: JUnit 5, Mockito
- **Container**: Docker (optional)

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher
- **Maven**: Version 3.6+ or Gradle 6.0+
- **Git**: For version control
- **Docker** (optional): For containerization
- **Docker Compose** (optional): For running microservices locally

### System Requirements

- Minimum 4GB RAM
- 2GB disk space for the application
- Network access to all microservices

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Gideon0123/transport-company-api-gateway.git
cd transport-company-api-gateway
```

### 2. Install Dependencies

Using Maven:
```bash
mvn clean install
```

Using Gradle:
```bash
gradle clean build
```

### 3. Build the Project

```bash
mvn clean package
# or
gradle clean build
```

### 4. (Optional) Build Docker Image

```bash
docker build -t transport-company-api-gateway:latest .
```

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in the root directory:

```env
# Server Configuration
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api

# Transport Company Service
TRANSPORT_SERVICE_URL=http://localhost:8081
TRANSPORT_SERVICE_TIMEOUT=30s

# Email Service
EMAIL_SERVICE_URL=http://localhost:8082
EMAIL_SERVICE_TIMEOUT=30s

# Security
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=3600000

# Rate Limiting
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=100

# Logging
LOGGING_LEVEL=INFO
```

### Application Properties

Edit `application.yml` or `application.properties`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: transport-company-api-gateway
  
  cloud:
    gateway:
      routes:
        - id: transport-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/transport/**
          filters:
            - StripPrefix=2
        
        - id: email-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/email/**
          filters:
            - StripPrefix=2

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always
```

## 📖 Usage

### Starting the Application

```bash
# Using Maven
mvn spring-boot:run

# Using Gradle
gradle bootRun

# Using Docker
docker run -p 8080:8080 transport-company-api-gateway:latest
```

The API Gateway will be available at `http://localhost:8080/api`

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "transportService": {
      "status": "UP"
    },
    "emailService": {
      "status": "UP"
    }
  }
}
```

## 🔀 API Routing

### Transport Service Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/transport/bookings` | Get all bookings |
| POST | `/api/transport/bookings` | Create a new booking |
| GET | `/api/transport/bookings/{id}` | Get booking details |
| PUT | `/api/transport/bookings/{id}` | Update booking |
| DELETE | `/api/transport/bookings/{id}` | Cancel booking |
| GET | `/api/transport/trips` | Get all trips |
| POST | `/api/transport/trips` | Create new trip |
| GET | `/api/transport/users/{id}` | Get user profile |

### Email Service Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/email/send` | Send an email |
| POST | `/api/email/send-bulk` | Send bulk emails |
| GET | `/api/email/templates` | Get email templates |
| GET | `/api/email/status/{id}` | Check email delivery status |
| GET | `/api/email/logs` | Get email activity logs |

### Gateway Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/actuator/health` | Detailed health check |
| GET | `/actuator/metrics` | Application metrics |
| GET | `/api/gateway/status` | Gateway status |

## 🔗 Microservices Integration

### Transport Company Service Integration

The Transport Company Service (`transport-company-project`) is the core service handling:

- **Booking Management**: Create, update, and track bookings
- **Trip Scheduling**: Manage trip routes and schedules
- **User Management**: Handle driver and passenger profiles
- **Payment Processing**: Process transportation payments

**Service URL**: `http://localhost:8081` (configurable)

**Communication Protocol**: REST API over HTTP

### Email Service Integration

The Email Service (`transport-company-email-service`) is a dedicated microservice for:

- **Email Delivery**: Asynchronous email sending
- **Template Management**: Customizable email templates for different scenarios
- **Notifications**: Send booking confirmations, trip updates, payment receipts
- **Logging**: Track all email communications

**Service URL**: `http://localhost:8082` (configurable)

**Communication Protocol**: REST API with message queuing (optional)

### Inter-Service Communication Flow

```
Client Request
    ↓
API Gateway (Authentication, Rate Limiting)
    ↓
Route to Appropriate Service
    ├→ Transport Service (Request Processing)
    │   └→ Triggers Email Service
    │       (e.g., Send booking confirmation)
    │
    └→ Email Service (Direct Request)
        └→ Async Email Processing
            └→ Response to Client
```

## 👨‍💻 Development

### Project Structure

```
transport-company-api-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/transportcompany/gateway/
│   │   │       ├── controller/       # API endpoints
│   │   │       ├── service/          # Business logic
│   │   │       ├── filter/           # Gateway filters
│   │   │       ├── exception/        # Exception handling
│   │   │       ├── config/           # Configuration classes
│   │   │       └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.yml       # Main config
│   │       └── logback.xml           # Logging config
│   └── test/
│       └── java/
│           └── com/transportcompany/gateway/
│               ├── controller/       # Controller tests
│               ├── service/          # Service tests
│               └── integration/      # Integration tests
├── pom.xml                          # Maven dependencies
├── Dockerfile                       # Docker image
├── docker-compose.yml               # Local development setup
└── README.md
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=YourTestClass

# Run with coverage
mvn test jacoco:report
```

### Code Standards

- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Write unit tests for business logic
- Use SLF4J for logging

## 🧪 Testing

### Unit Testing

```bash
mvn test -Dtest=*ServiceTest
```

### Integration Testing

```bash
mvn test -Dtest=*IntegrationTest
```

### Running All Tests with Coverage

```bash
mvn clean test jacoco:report
```

### Manual Testing with cURL

```bash
# Test health endpoint
curl -X GET http://localhost:8080/api/health

# Test transport service through gateway
curl -X GET http://localhost:8080/api/transport/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Test email service through gateway
curl -X POST http://localhost:8080/api/email/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "to": "user@example.com",
    "subject": "Test Email",
    "body": "This is a test email"
  }'
```

## 🚀 Deployment

### Using Docker

```bash
# Build image
docker build -t transport-company-api-gateway:latest .

# Run container
docker run -d \
  -p 8080:8080 \
  -e TRANSPORT_SERVICE_URL=http://transport-service:8081 \
  -e EMAIL_SERVICE_URL=http://email-service:8082 \
  --name api-gateway \
  transport-company-api-gateway:latest
```

### Using Docker Compose (Local Development)

```bash
docker-compose up -d
```

### Production Deployment

For production deployment, consider:

1. **Use a reverse proxy** (Nginx, HAProxy) in front of the gateway
2. **Enable HTTPS/TLS** for secure communication
3. **Configure load balancing** for high availability
4. **Set up monitoring and alerting** (Prometheus, Grafana)
5. **Implement circuit breakers** for fault tolerance
6. **Use environment-specific configurations**
7. **Enable rate limiting and DDoS protection**

## 🔧 Troubleshooting

### Common Issues

#### Issue: Gateway cannot connect to microservices

**Solution:**
- Verify microservices are running and accessible
- Check service URLs in configuration
- Review gateway logs: `tail -f logs/gateway.log`
- Test connectivity: `curl http://localhost:8081/health`

#### Issue: High latency or timeouts

**Solution:**
- Check microservice performance
- Increase timeout values if needed
- Enable caching where applicable
- Monitor resource usage (CPU, memory)

#### Issue: Authentication failures

**Solution:**
- Verify JWT token is valid and not expired
- Check JWT secret configuration matches across services
- Review authentication logs
- Test with a fresh token

#### Issue: Rate limiting rejecting valid requests

**Solution:**
- Increase rate limiting thresholds
- Implement request queuing
- Check for request spike patterns
- Consider using API keys for higher limits

### Debugging

Enable debug logging:

```yaml
logging:
  level:
    com.transportcompany.gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

### Health Check Endpoints

```bash
# Gateway health
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/http.server.requests
```

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'Add your feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Submit a pull request

Please ensure:
- Code follows the project's style guide
- Tests are included for new features
- Documentation is updated accordingly
- All tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📞 Support & Contact

For issues, questions, or suggestions, please:

1. Check existing [GitHub Issues](https://github.com/Gideon0123/transport-company-api-gateway/issues)
2. Create a new issue with detailed description
3. Contact the maintainers directly

## 🔗 Related Projects

- [Transport Company Main Project](https://github.com/Gideon0123/Transport-company-project)
- [Email Service Microservice](https://github.com/Gideon0123/Transport-company-email-service)

---

**Last Updated**: May 2026
