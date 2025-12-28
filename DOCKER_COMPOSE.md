# Docker Compose Usage Guide

This project includes a comprehensive Docker Compose setup with multiple services for different use cases.

## Available Services

### Databases
- **PostgreSQL** (port 5432) - Default SQL database
- **MariaDB** (port 3306) - Alternative SQL database
- **Oracle XE** (port 1521) - Oracle database for testing
- **MongoDB** (port 27017) - NoSQL database

### Cache
- **Redis** (port 6379) - In-memory cache

### Message Brokers
- **Kafka** (port 9092) - Distributed streaming platform
- **RabbitMQ** (ports 5672, 15672) - AMQP message broker
- **ActiveMQ Artemis** (ports 61616, 8161) - JMS message broker

### Monitoring
- **Prometheus** (port 9090) - Metrics collection
- **Grafana** (port 3000) - Metrics visualization (admin/admin)

## Profile-Based Startup

Use Docker Compose profiles to start only the services you need:

### Start all services
```bash
docker-compose --profile full up
```

### Start only the app with essential services (Postgres, Redis, Kafka, MongoDB, RabbitMQ)
```bash
docker-compose --profile app up
```

### Start specific database profiles
```bash
# PostgreSQL only
docker-compose --profile postgres up

# MariaDB only
docker-compose --profile mariadb up

# Oracle only
docker-compose --profile oracle up

# MongoDB only
docker-compose --profile mongodb up
```

### Start specific messaging profiles
```bash
# Kafka only
docker-compose --profile kafka up

# RabbitMQ only
docker-compose --profile rabbitmq up

# ActiveMQ only
docker-compose --profile activemq up
```

### Start monitoring stack
```bash
docker-compose --profile monitoring up
```

### Combine multiple profiles
```bash
# App with PostgreSQL and monitoring
docker-compose --profile app --profile monitoring up

# MariaDB with Kafka
docker-compose --profile mariadb --profile kafka up
```

## Service Credentials

### Databases
- **PostgreSQL**: `postgres/postgres` (database: `starterdb`)
- **MariaDB**: `mariadb/mariadb` (database: `starterdb`, root: `root`)
- **Oracle**: `starteruser/starterpass` (SYS password: `oracle`)
- **MongoDB**: `admin/admin` (database: `starterdb`)

### Message Brokers
- **RabbitMQ**: `guest/guest` (Management UI: http://localhost:15672)
- **ActiveMQ**: `admin/admin` (Console: http://localhost:8161)

### Monitoring
- **Grafana**: `admin/admin` (http://localhost:3000)
- **Prometheus**: http://localhost:9090

## Health Checks

All services include health checks. Use the following to check service status:

```bash
docker-compose ps
```

## Data Persistence

All services use named volumes for data persistence:
- `postgres-data`
- `mariadb-data`
- `oracle-data`
- `mongodb-data`
- `redis-data`
- `kafka-data`
- `rabbitmq-data`
- `activemq-data`
- `prometheus-data`
- `grafana-data`

To remove all data:
```bash
docker-compose down -v
```

## Application Configuration

The Spring Boot application is configured to connect to services via environment variables in `docker-compose.yml`. You can override these in your `application-docker.yml` or `application-docker.properties` file.

## Examples

### Development with PostgreSQL and Redis
```bash
docker-compose --profile postgres --profile redis up
```

### Testing with MariaDB and ActiveMQ
```bash
docker-compose --profile mariadb --profile activemq up
```

### Full stack with monitoring
```bash
docker-compose --profile full --profile monitoring up
```
