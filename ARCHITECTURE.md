# CDK Architecture Documentation

This document explains the modular CDK architecture for deploying the Spring Boot application to AWS.

## Project Structure

```
bin/
├── app.ts                           # CDK app entry point
├── backend-stack.ts                 # Main stack orchestrating all constructs
└── constructs/
    ├── index.ts                     # Construct exports
    ├── documentdb-construct.ts      # DocumentDB (MongoDB) infrastructure
    ├── rds-construct.ts            # RDS PostgreSQL infrastructure
    └── app-construct.ts            # Application infrastructure (ECS, ALB, ECR)
```

## Architecture Overview

The CDK infrastructure is organized into three main constructs, each responsible for a specific domain:

### 1. RDS Construct (`rds-construct.ts`)

**Purpose**: Manages PostgreSQL database infrastructure

**Resources Created**:
- AWS Secrets Manager secret for database credentials
- RDS PostgreSQL instance (t3.micro)
- Database configuration with encryption and backups

**Key Features**:
- Automatic credential generation and rotation
- Encryption at rest
- 7-day backup retention
- Isolated subnet placement for security

**Exports**:
- `database`: DatabaseInstance - The RDS PostgreSQL instance
- `credentials`: SecretsManagerSecret - Database credentials

### 2. DocumentDB Construct (`documentdb-construct.ts`)

**Purpose**: Manages MongoDB-compatible DocumentDB infrastructure

**Resources Created**:
- AWS Secrets Manager secret for MongoDB credentials
- DocumentDB cluster (t3.medium)
- Cluster configuration with encryption and backups

**Key Features**:
- MongoDB-compatible API
- Automatic credential generation
- Encryption at rest and in transit
- 7-day backup retention
- Single instance cluster (cost-optimized)

**Exports**:
- `cluster`: DatabaseCluster - The DocumentDB cluster
- `credentials`: SecretsManagerSecret - MongoDB credentials

### 3. App Construct (`app-construct.ts`)

**Purpose**: Manages application infrastructure (containers, load balancing, repositories)

**Resources Created**:
- ECR repository for Docker images
- ECS Fargate cluster
- ECS task definition with Spring Boot container
- Application Load Balancer (ALB)
- CloudWatch log groups
- ECS Fargate service with auto-scaling

**Key Features**:
- Containerized Spring Boot application
- Auto-scaling ECS service (2 instances)
- Health checks on `/actuator/health`
- Integration with database credentials from Secrets Manager
- CloudWatch logging with 1-week retention

**Exports**:
- `cluster`: Cluster - ECS cluster
- `service`: FargateService - ECS service
- `loadBalancer`: ApplicationLoadBalancer - ALB instance
- `ecrRepository`: Repository - ECR repository

## Main Stack (`backend-stack.ts`)

The main stack orchestrates all constructs and handles:

### Networking Infrastructure
- **VPC**: 3-tier architecture (public, private, isolated subnets)
- **Security Groups**: Least-privilege access between components
- **NAT Gateway**: Outbound internet access for private subnets

### Security Configuration
```typescript
// Database access from ECS only
rdsSecurityGroup.addIngressRule(ecsSecurityGroup, Port.tcp(5432));
docdbSecurityGroup.addIngressRule(ecsSecurityGroup, Port.tcp(27017));

// ALB access from internet
albSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(80));

// ECS access from ALB only
ecsSecurityGroup.addIngressRule(albSecurityGroup, Port.tcp(8080));
```

### Construct Integration
```typescript
// Create database constructs
const rdsConstruct = new RdsConstruct(this, "RdsConstruct", { vpc, securityGroup });
const documentDbConstruct = new DocumentDbConstruct(this, "DocumentDbConstruct", { vpc, securityGroup });

// Create application construct with database dependencies
const appConstruct = new AppConstruct(this, "AppConstruct", {
  vpc,
  ecsSecurityGroup,
  albSecurityGroup,
  database: rdsConstruct.database,
  docdbCluster: documentDbConstruct.cluster,
  dbCredentials: rdsConstruct.credentials,
  mongoCredentials: documentDbConstruct.credentials,
});
```

## Benefits of This Architecture

### 1. **Separation of Concerns**
- Each construct handles a specific domain (database, application, etc.)
- Clear boundaries and responsibilities
- Easier to understand and maintain

### 2. **Reusability**
- Constructs can be reused across different stacks
- Parameterized for different environments (dev, staging, prod)
- Easy to extend with additional features

### 3. **Testability**
- Individual constructs can be unit tested
- Mock dependencies for isolated testing
- Clear interfaces between components

### 4. **Maintainability**
- Changes to database infrastructure don't affect application code
- Easier to upgrade individual components
- Clear dependency management

### 5. **Security**
- Least-privilege security groups
- Secrets managed centrally
- Network isolation between tiers

## Environment Variables Flow

```
Spring Application Container
├── Environment Variables (non-sensitive)
│   ├── SPRING_PROFILES_ACTIVE=prod
│   ├── SPRING_DATASOURCE_URL=jdbc:postgresql://...
│   ├── SPRING_DATA_MONGODB_HOST=docdb-endpoint
│   └── SPRING_DATA_MONGODB_DATABASE=spring_with_react
└── Secrets (from AWS Secrets Manager)
    ├── SPRING_DATASOURCE_USERNAME
    ├── SPRING_DATASOURCE_PASSWORD
    ├── SPRING_DATA_MONGODB_USERNAME
    └── SPRING_DATA_MONGODB_PASSWORD
```

## Deployment Flow

1. **Infrastructure Deployment**: `cdk deploy` creates all AWS resources
2. **Image Build**: Docker image built from Spring Boot JAR
3. **Image Push**: Image pushed to ECR repository
4. **Service Update**: ECS service updated to use new image
5. **Health Checks**: ALB verifies application health before routing traffic

## Monitoring and Observability

### CloudWatch Integration
- **Application Logs**: `/ecs/spring-with-react`
- **ECS Metrics**: CPU, memory, network utilization
- **ALB Metrics**: Request count, latency, error rates
- **Database Metrics**: Connection count, CPU, storage

### Health Checks
- **ALB Health Check**: `GET /actuator/health` every 30 seconds
- **ECS Health Check**: Container-level health monitoring
- **Database Health**: Automated monitoring and alerting

## Cost Optimization

### Resource Sizing
- **RDS**: t3.micro (free tier eligible)
- **DocumentDB**: t3.medium (smallest available)
- **ECS**: 0.5 vCPU, 1GB RAM per task
- **Backup Retention**: 7 days (balance between cost and recovery)

### Auto Scaling
- ECS service can scale based on CPU/memory metrics
- Database read replicas can be added for read-heavy workloads
- ALB automatically distributes load across healthy targets

## Future Enhancements

### Potential Improvements
1. **Multi-AZ Database Deployment** for high availability
2. **Auto Scaling Policies** based on application metrics
3. **CloudFront Distribution** for static content caching
4. **Route 53** for custom domain management
5. **AWS WAF** for application-level security
6. **ElastiCache** for application caching layer
7. **Parameter Store** for application configuration
8. **X-Ray Tracing** for distributed tracing

### Environment-Specific Configurations
```typescript
// Example: Environment-specific sizing
const instanceType = props.environment === 'prod' 
  ? InstanceType.of(InstanceClass.T3, InstanceSize.MEDIUM)
  : InstanceType.of(InstanceClass.T3, InstanceSize.MICRO);
```

This modular architecture provides a solid foundation for deploying and scaling the Spring Boot application in AWS while maintaining security, cost-effectiveness, and operational excellence.