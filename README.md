# Spring Boot Microservice Starter

A comprehensive Spring Boot starter template for building production-ready microservices with built-in support for multiple databases, messaging systems, caching, security, and monitoring.

## Features

### Core Features
- ✅ Spring Boot 4.0.0 with Java 21
- ✅ Spring Cloud 2025.0.0
- ✅ Comprehensive abstraction layers (Security, Persistence, Service, Web)
- ✅ Profile-based configuration
- ✅ Docker Compose with multiple services
- ✅ JWT Authentication
- ✅ Resilience4j for fault tolerance
- ✅ OpenAPI/Swagger documentation

### Database Support
- **SQL**: PostgreSQL, MariaDB, Oracle XE
- **NoSQL**: MongoDB
- **Cache**: Redis

### Messaging
- Apache Kafka
- RabbitMQ
- ActiveMQ Artemis

### Monitoring & Observability
- Prometheus (metrics collection)
- Grafana (visualization)
- Spring Boot Actuator

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd common-spring-boot-starter
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Start services with Docker Compose**
   ```bash
   # Start all services
   docker-compose --profile full up
   
   # Or start only essential services
   docker-compose --profile app up
   
   # Or start with monitoring
   docker-compose --profile app --profile monitoring up
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator
   - Grafana: http://localhost:3000 (admin/admin)
   - Prometheus: http://localhost:9090

## Docker Compose Profiles

Use profiles to start only the services you need:

| Profile | Services | Use Case |
|---------|----------|----------|
| `app` | PostgreSQL, Redis, Kafka, MongoDB, RabbitMQ | Default development |
| `full` | All services | Full stack testing |
| `postgres` | PostgreSQL only | SQL database testing |
| `mariadb` | MariaDB only | Alternative SQL database |
| `oracle` | Oracle XE only | Oracle compatibility testing |
| `mongodb` | MongoDB only | NoSQL testing |
| `kafka` | Kafka only | Event streaming |
| `rabbitmq` | RabbitMQ only | AMQP messaging |
| `activemq` | ActiveMQ only | JMS messaging |
| `monitoring` | Prometheus + Grafana | Metrics and monitoring |

**Examples:**
```bash
# PostgreSQL + Kafka
docker-compose --profile postgres --profile kafka up

# MariaDB + ActiveMQ + Monitoring
docker-compose --profile mariadb --profile activemq --profile monitoring up
```

See [DOCKER_COMPOSE.md](DOCKER_COMPOSE.md) for detailed usage.

## Abstraction Layers

This starter includes comprehensive abstraction layers for better flexibility and maintainability:

### Security Layer
- `TokenProvider` - Generic token operations (JWT, OAuth2, etc.)
- `AbstractAuthenticationFilter` - Template for authentication filters
- `AuthenticationProvider` - Pluggable authentication strategies

### Persistence Layer
- `BaseEntity` - Base class with auditing and versioning
- `BaseRepository` - Generic repository with common operations
- `CacheOperations` - Generic cache interface (Redis, Caffeine, etc.)

### Service Layer
- `BaseService` - Standard CRUD operations interface
- `AbstractBaseService` - Template implementation with hooks
- `EntityMapper` - Entity-DTO mapping (MapStruct compatible)
- Exception hierarchy: `ServiceException`, `ResourceNotFoundException`, `ValidationException`

### Web Layer
- `BaseController` - Standard REST endpoints
- `ApiResponse` - Standardized response wrapper
- `RestResponseBuilder` - Fluent API for responses
- `PagedResponse` - Pagination support

See [ABSTRACTION_LAYERS.md](ABSTRACTION_LAYERS.md) for detailed documentation and examples.

## Project Structure

```
src/main/java/com/common/starter/
├── config/              # Configuration classes
├── core/                # Core utilities and constants
├── exception/           # Global exception handling
├── persistence/         # Persistence layer abstractions
│   ├── cache/          # Cache operations
│   ├── entity/         # Base entities
│   └── repository/     # Base repositories
├── security/            # Security abstractions and implementations
│   └── abstraction/    # Security interfaces
├── service/             # Service layer abstractions
│   ├── abstraction/    # Service interfaces
│   ├── exception/      # Service exceptions
│   ├── mapper/         # Entity-DTO mappers
│   └── validator/      # Business validators
└── web/                 # Web layer abstractions
    ├── controller/     # Base controllers
    ├── response/       # Response wrappers
    └── validation/     # Request validators
```

## Usage Examples

### Creating a New Entity

```java
@Entity
@Table(name = "products")
public class Product extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private BigDecimal price;
    
    // Getters and setters
}
```

### Creating a Repository

```java
public interface ProductRepository extends BaseRepository<Product, Long> {
    List<Product> findByPriceLessThan(BigDecimal price);
}
```

### Creating a Service

```java
@Service
@Transactional(readOnly = true)
public class ProductService extends AbstractBaseService<Product, Long, ProductRepository> {
    
    public ProductService(ProductRepository repository) {
        super(repository);
    }
    
    @Override
    protected void validate(Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be positive");
        }
    }
    
    @Override
    protected Product mergeForUpdate(Product existing, Product partial) {
        if (partial.getName() != null) existing.setName(partial.getName());
        if (partial.getPrice() != null) existing.setPrice(partial.getPrice());
        return existing;
    }
    
    @Override
    protected String getEntityName() {
        return "Product";
    }
}
```

### Creating a Controller

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController<Product, Long, ProductService> {
    
    public ProductController(ProductService service) {
        super(service);
    }
    
    // All CRUD endpoints inherited:
    // GET    /api/v1/products          - Get all (paginated)
    // GET    /api/v1/products/{id}     - Get by ID
    // POST   /api/v1/products          - Create
    // PUT    /api/v1/products/{id}     - Update
    // PATCH  /api/v1/products/{id}     - Partial update
    // DELETE /api/v1/products/{id}     - Delete
}
```

## Configuration

### Application Profiles

- `default` - Local development
- `docker` - Docker environment
- `test` - Testing

### Environment Variables

Key environment variables for Docker:

```bash
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/starterdb
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/starterdb
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_ACTIVEMQ_BROKER_URL=tcp://activemq:61616
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

Integration tests use Testcontainers for database and messaging services.

## Building for Production

```bash
# Build JAR
mvn clean package

# Build Docker image
docker build -t microservice-starter:latest .

# Run container
docker run -p 8080:8080 microservice-starter:latest
```

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for deployment instructions.

## CI/CD

CI/CD templates are available in the `templates/` directory:
- GitHub Actions
- GitLab CI
- Jenkins Pipeline

## Documentation

- [Abstraction Layers Guide](ABSTRACTION_LAYERS.md) - Detailed guide to abstraction layers
- [Docker Compose Guide](DOCKER_COMPOSE.md) - Docker Compose usage and profiles
- [Deployment Guide](DEPLOYMENT.md) - Deployment instructions

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

[Add your license here]

## Support

[Add support information here]
