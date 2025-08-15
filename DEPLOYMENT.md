# AWS Deployment Guide - Automated CDK Deployment

This guide explains the fully automated AWS deployment process using CDK with integrated Docker build.

## Prerequisites

1. **AWS CLI** configured with appropriate permissions
2. **Node.js** v18.16.0+ and npm installed
3. **AWS CDK CLI** installed (`npm install -g aws-cdk`)
4. **Docker** installed and running

## Infrastructure Components

The CDK stack creates the following AWS resources:

### Networking
- **VPC** with public, private, and isolated subnets across 2 AZs
- **NAT Gateway** for outbound internet access from private subnets
- **Security Groups** for ALB, ECS, RDS, and DocumentDB

### Databases
- **RDS PostgreSQL** (t3.micro) in isolated subnets with encryption
- **DocumentDB** (t3.medium) cluster in isolated subnets with encryption
- **AWS Secrets Manager** for database credentials

### Compute & Load Balancing
- **ECS Fargate** cluster with auto-scaling capabilities
- **Application Load Balancer** (ALB) for traffic distribution
- **CloudWatch Logs** for application logging

### Automated Build Process
- **Multi-stage Docker build** with Maven and Node.js
- **Automatic ECR push** during CDK deployment
- **No manual Docker commands required**

## Automated Deployment Process

### One-Command Deployment

```bash
# Install CDK dependencies
npm install

# Bootstrap CDK (first time only per AWS account/region)
cdk bootstrap

# Deploy everything automatically
npm run deploy
```

### What Happens During Deployment

1. **CDK Synthesis**: Generates CloudFormation templates
2. **Docker Build Process**:
   - Stage 1: Maven build container
     - Downloads Maven dependencies
     - Installs Node.js and builds React frontend
     - Compiles Spring Boot application to JAR
   - Stage 2: Runtime container
     - Uses Amazon Corretto 21 Alpine
     - Copies built JAR from build stage
     - Sets up application user and entrypoint
3. **ECR Push**: Automatically pushes Docker image to ECR
4. **Infrastructure Creation**: Creates all AWS resources
5. **Service Deployment**: Deploys application to ECS Fargate

### Build Architecture

The deployment uses a multi-stage Dockerfile (`Dockerfile.cdk`):

```dockerfile
# Stage 1: Build (Maven + Node.js)
FROM maven:3.9.6-amazoncorretto-21 AS builder
# - Downloads dependencies
# - Builds React frontend
# - Compiles Spring Boot JAR

# Stage 2: Runtime
FROM amazoncorretto:21-alpine-jdk
# - Lightweight runtime image
# - Copies only the built JAR
# - Runs as non-root user
```

## Environment Variables

The application automatically receives these environment variables:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL` - PostgreSQL connection string
- `SPRING_DATA_MONGODB_HOST` - DocumentDB endpoint
- `SPRING_DATA_MONGODB_PORT=27017`
- `SPRING_DATA_MONGODB_DATABASE=spring_with_react`

Database credentials are securely injected from AWS Secrets Manager:
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_MONGODB_USERNAME`
- `SPRING_DATA_MONGODB_PASSWORD`

## CDK Construct Architecture

The deployment is organized into modular constructs:

### [`bin/constructs/rds-construct.ts`](bin/constructs/rds-construct.ts)
- Manages PostgreSQL database infrastructure
- Creates Secrets Manager credentials
- Configures encryption and backups

### [`bin/constructs/documentdb-construct.ts`](bin/constructs/documentdb-construct.ts)
- Manages DocumentDB cluster infrastructure
- Creates MongoDB credentials
- Configures cluster settings

### [`bin/constructs/maven-docker-asset.ts`](bin/constructs/maven-docker-asset.ts)
- Handles automated Docker build process
- Uses multi-stage Dockerfile for optimization
- Automatically pushes to ECR

### [`bin/constructs/app-construct.ts`](bin/constructs/app-construct.ts)
- Manages ECS, ALB, and application infrastructure
- Integrates with database constructs
- Configures health checks and scaling

## Accessing the Application

After deployment, the application will be available at the ALB DNS name provided in the CloudFormation outputs:

```bash
# Get the application URL
aws cloudformation describe-stacks \
  --stack-name Backend-Stack \
  --query 'Stacks[0].Outputs[?OutputKey==`LoadBalancerDNS`].OutputValue' \
  --output text
```

Health check endpoint: `http://<alb-dns-name>/actuator/health`

## Monitoring

- **CloudWatch Logs**: `/ecs/spring-with-react`
- **ECS Console**: Monitor service health and scaling
- **ALB Target Groups**: Monitor health check status
- **Docker Image**: Available in ECR with automatic versioning

## Development Workflow

### Local Development
```bash
npm start  # Run locally with hot reload
```

### Deploy Changes
```bash
npm run deploy  # Automatically builds and deploys
```

### View Changes Before Deploy
```bash
npm run cdk:diff  # Show what will change
```

### Rollback
```bash
# Deploy previous version by reverting code and running:
npm run deploy
```

## Security Features

- All databases are in isolated subnets (no internet access)
- Encryption at rest for both RDS and DocumentDB
- Security groups with minimal required access
- Secrets managed by AWS Secrets Manager
- ECS tasks run in private subnets
- Docker images scanned for vulnerabilities in ECR

## Performance Optimizations

### Docker Build Optimization
- **Multi-stage build** reduces final image size
- **Layer caching** speeds up subsequent builds
- **Dependency caching** in Maven build stage

### CDK Deployment Optimization
- **Parallel resource creation** where possible
- **Incremental deployments** only update changed resources
- **Asset caching** reuses unchanged Docker images

## Troubleshooting

### Common Issues

1. **Docker Build Fails**
   - Ensure Docker is running locally
   - Check Maven dependencies in `pom.xml`
   - Verify Node.js dependencies in `package.json`

2. **CDK Bootstrap Required**
   ```bash
   cdk bootstrap  # Run once per AWS account/region
   ```

3. **Permission Issues**
   - Ensure AWS CLI has sufficient permissions
   - Check IAM roles for ECS task execution

4. **Build Timeout**
   - Maven build may take time on first run
   - Subsequent builds are faster due to caching

### Logs and Debugging

```bash
# View CDK deployment logs
cdk deploy --verbose

# Check ECS service logs
aws logs tail /ecs/spring-with-react --follow

# View ECS service status
aws ecs describe-services \
  --cluster spring-with-react-cluster \
  --services spring-with-react-service
```

## Cost Optimization

### Resource Sizing
- **RDS**: t3.micro (free tier eligible)
- **DocumentDB**: t3.medium (smallest available)
- **ECS**: 0.5 vCPU, 1GB RAM per task
- **Build Process**: Only runs during deployment

### Cost Monitoring
- Monitor ECR storage costs for Docker images
- Set up CloudWatch billing alerts
- Use AWS Cost Explorer for detailed analysis

## Cleanup

To destroy all resources:
```bash
npm run cdk:destroy
```

**Warning**: This will delete all data in the databases. Ensure you have backups if needed.

## Advanced Configuration

### Environment-Specific Deployments
```bash
# Deploy to different environments
cdk deploy --context environment=staging
cdk deploy --context environment=production
```

### Custom Build Arguments
Modify `Dockerfile.cdk` to add custom build arguments or environment-specific configurations.

### Scaling Configuration
Update ECS service desired count in `app-construct.ts` for different scaling requirements.

This automated deployment process eliminates manual steps and ensures consistent, repeatable deployments while maintaining security and performance best practices.