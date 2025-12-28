# Abstraction Layers Guide

This Spring Boot starter includes comprehensive abstraction layers for security, persistence, service, and web components. These abstractions make the codebase more flexible, testable, and maintainable.

## Table of Contents
- [Security Abstraction Layer](#security-abstraction-layer)
- [Persistence Abstraction Layer](#persistence-abstraction-layer)
- [Service Abstraction Layer](#service-abstraction-layer)
- [Web Abstraction Layer](#web-abstraction-layer)
- [Usage Examples](#usage-examples)

---

## Security Abstraction Layer

### TokenProvider Interface
Generic interface for token operations, supporting different implementations (JWT, OAuth2, custom tokens).

```java
public interface TokenProvider<T> {
    String generateToken(String subject, Map<String, Object> additionalClaims);
    boolean validateToken(String token);
    String extractSubject(String token);
    T extractClaims(String token);
    boolean isTokenExpired(String token);
    long getExpirationTime();
}
```

**Default Implementation**: `JwtTokenProvider` (marked with `@Primary`)

### AbstractAuthenticationFilter
Base class for authentication filters using the template method pattern.

**Key Methods**:
- `extractToken()` - Extracts token from request
- `authenticate()` - Performs authentication (abstract, must be implemented)
- `shouldAuthenticate()` - Determines if authentication should be attempted
- Hook methods: `onSuccessfulAuthentication()`, `onFailedAuthentication()`, `onAuthenticationException()`

**Default Implementation**: `JwtAuthenticationFilter`

### AuthenticationProvider Interface
Allows multiple authentication strategies to be implemented.

```java
public interface AuthenticationProvider {
    Authentication authenticate(Object credentials) throws AuthenticationException;
    boolean supports(Class<?> credentialsType);
}
```

---

## Persistence Abstraction Layer

### BaseEntity
Abstract base class for all JPA entities with auditing support.

**Features**:
- Auto-populated `createdAt` and `updatedAt` timestamps
- Optimistic locking with `@Version`
- Common `equals()`, `hashCode()`, and `toString()` implementations

```java
@Entity
public class User extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
```

### BaseRepository
Generic repository interface with common operations.

**Additional Methods**:
- `findByIdOrThrow()` - Finds entity or throws exception
- `existsByIdOrThrow()` - Checks existence or throws exception
- `findByIdAndNotDeleted()` - Soft delete support

```java
public interface UserRepository extends BaseRepository<User, Long> {
    // Custom query methods
    Optional<User> findByUsername(String username);
}
```

### CacheOperations
Generic interface for cache operations, abstracting over different cache providers.

**Implementations**:
- `RedisCacheOperations` - Redis-based caching

```java
@Service
public class UserService {
    private final CacheOperations<String, Object> cache;
    
    public User getUserWithCache(Long id) {
        String key = "user:" + id;
        return cache.get(key)
                .map(obj -> (User) obj)
                .orElseGet(() -> {
                    User user = userRepository.findById(id).orElseThrow();
                    cache.put(key, user, Duration.ofHours(1));
                    return user;
                });
    }
}
```

---

## Service Abstraction Layer

### BaseService Interface
Generic interface for service layer CRUD operations.

```java
public interface BaseService<T, ID extends Serializable> {
    T create(T entity);
    T update(ID id, T entity);
    T partialUpdate(ID id, T partialEntity);
    void delete(ID id);
    Optional<T> findById(ID id);
    T findByIdOrThrow(ID id);
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    boolean existsById(ID id);
    long count();
}
```

### AbstractBaseService
Abstract implementation with template method pattern and hooks for customization.

**Hook Methods**:
- `beforeCreate()`, `afterCreate()`
- `beforeUpdate()`, `afterUpdate()`
- `beforeDelete()`, `afterDelete()`
- `validate()` - Custom validation logic
- `mergeForUpdate()` - Partial update logic (abstract)

```java
@Service
@Transactional(readOnly = true)
public class UserService extends AbstractBaseService<User, Long, UserRepository> {
    
    public UserService(UserRepository repository) {
        super(repository);
    }
    
    @Override
    protected void beforeCreate(User user) {
        // Hash password, set defaults, etc.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    
    @Override
    protected void validate(User user) {
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
    }
    
    @Override
    protected User mergeForUpdate(User existing, User partial) {
        if (partial.getEmail() != null) {
            existing.setEmail(partial.getEmail());
        }
        // Merge other fields as needed
        return existing;
    }
    
    @Override
    protected String getEntityName() {
        return "User";
    }
}
```

### Exception Hierarchy
- `ServiceException` - Base service exception
- `ResourceNotFoundException` - Resource not found
- `ValidationException` - Validation failures

### EntityMapper
Generic interface for entity-DTO mapping, compatible with MapStruct.

```java
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<User, UserDTO> {
    // MapStruct will generate implementation
}
```

### BusinessValidator
Interface for business rule validation with chain of responsibility support.

```java
@Component
public class UserValidator implements BusinessValidator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        if (user.getAge() < 18) {
            throw new ValidationException("User must be 18 or older");
        }
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return User.class.equals(type);
    }
}
```

---

## Web Abstraction Layer

### ApiResponse
Standard wrapper for all API responses.

```java
{
  "success": true,
  "data": { ... },
  "message": "Success",
  "timestamp": "2025-12-28T21:00:00",
  "errors": null
}
```

### PagedResponse
Extension of `ApiResponse` for paginated data.

```java
{
  "success": true,
  "data": [ ... ],
  "message": "Success",
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### RestResponseBuilder
Utility for building standardized responses.

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.findByIdOrThrow(id);
        return RestResponseBuilder.success(user);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO dto) {
        UserDTO created = userService.create(dto);
        return RestResponseBuilder.created(created, "User created successfully");
    }
}
```

### BaseController
Abstract controller with standard CRUD endpoints.

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends BaseController<UserDTO, Long, UserService> {
    
    public UserController(UserService service) {
        super(service);
    }
    
    // Inherits: create(), update(), partialUpdate(), delete(), getById(), getAll()
    
    // Add custom endpoints
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> search(@RequestParam String query) {
        List<UserDTO> results = service.search(query);
        return RestResponseBuilder.success(results);
    }
}
```

---

## Usage Examples

### Complete CRUD Example

#### 1. Entity
```java
@Entity
@Table(name = "products")
public class Product extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    // Getters and setters
}
```

#### 2. Repository
```java
public interface ProductRepository extends BaseRepository<Product, Long> {
    List<Product> findByPriceLessThan(BigDecimal price);
}
```

#### 3. Service
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
        if (partial.getDescription() != null) existing.setDescription(partial.getDescription());
        if (partial.getPrice() != null) existing.setPrice(partial.getPrice());
        return existing;
    }
    
    @Override
    protected String getEntityName() {
        return "Product";
    }
}
```

#### 4. Controller
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController<Product, Long, ProductService> {
    
    public ProductController(ProductService service) {
        super(service);
    }
    
    // All CRUD endpoints inherited
    // GET    /api/v1/products          - Get all (paginated)
    // GET    /api/v1/products/all      - Get all (unpaged)
    // GET    /api/v1/products/{id}     - Get by ID
    // POST   /api/v1/products          - Create
    // PUT    /api/v1/products/{id}     - Update
    // PATCH  /api/v1/products/{id}     - Partial update
    // DELETE /api/v1/products/{id}     - Delete
}
```

### Custom Token Provider Example

```java
@Component
public class OAuth2TokenProvider implements TokenProvider<OAuth2Claims> {
    
    @Override
    public String generateToken(String subject, Map<String, Object> additionalClaims) {
        // OAuth2 token generation logic
    }
    
    @Override
    public boolean validateToken(String token) {
        // OAuth2 token validation logic
    }
    
    // Implement other methods...
}
```

### Custom Cache Provider Example

```java
@Component
@ConditionalOnProperty(name = "cache.provider", havingValue = "caffeine")
public class CaffeineCacheOperations implements CacheOperations<String, Object> {
    
    private final Cache<String, Object> cache;
    
    public CaffeineCacheOperations() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofHours(1))
                .build();
    }
    
    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }
    
    // Implement other methods...
}
```

---

## Benefits

1. **Flexibility**: Easy to swap implementations without changing business logic
2. **Testability**: Mock interfaces instead of concrete classes
3. **Maintainability**: Clear separation of concerns
4. **Consistency**: Standardized patterns across the codebase
5. **Extensibility**: New features can be added by implementing interfaces
6. **Documentation**: Clear contracts defined by interfaces

## Best Practices

1. **Always extend base classes** when creating new entities, services, or controllers
2. **Use hook methods** in `AbstractBaseService` for custom logic instead of overriding main methods
3. **Leverage `RestResponseBuilder`** for consistent API responses
4. **Implement `EntityMapper`** for all entity-DTO conversions
5. **Use `@Primary`** annotation for default implementations when multiple implementations exist
6. **Document custom implementations** with JavaDoc
