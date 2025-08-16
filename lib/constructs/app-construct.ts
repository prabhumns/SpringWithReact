import { Duration, RemovalPolicy } from "aws-cdk-lib";
import { DatabaseCluster } from "aws-cdk-lib/aws-docdb";
import { SecurityGroup, SubnetType, Vpc } from "aws-cdk-lib/aws-ec2";
import {
  Cluster,
  Compatibility,
  ContainerImage,
  FargateService,
  LogDrivers,
  NetworkMode,
  Protocol as EcsProtocol,
  Secret,
  TaskDefinition
} from "aws-cdk-lib/aws-ecs";
import { ApplicationLoadBalancer, ApplicationProtocol, ListenerAction } from "aws-cdk-lib/aws-elasticloadbalancingv2";
import { LogGroup, RetentionDays } from "aws-cdk-lib/aws-logs";
import { DatabaseInstance } from "aws-cdk-lib/aws-rds";
import { Secret as SecretsManagerSecret } from "aws-cdk-lib/aws-secretsmanager";
import { Construct } from "constructs";
import { DockerAsset } from "./maven-docker-asset";

export interface AppConstructProps {
  vpc: Vpc;
  ecsSecurityGroup: SecurityGroup;
  albSecurityGroup: SecurityGroup;
  database: DatabaseInstance;
  docdbCluster: DatabaseCluster;
  dbCredentials: SecretsManagerSecret;
  mongoCredentials: SecretsManagerSecret;
}

export class AppConstruct extends Construct {
  public readonly cluster: Cluster;
  public readonly service: FargateService;
  public readonly loadBalancer: ApplicationLoadBalancer;
  public readonly mavenDockerAsset: DockerAsset;

  constructor(scope: Construct, id: string, props: AppConstructProps) {
    super(scope, id);

    // Create Maven Docker asset that automatically builds Spring Boot app and Docker image
    this.mavenDockerAsset = new DockerAsset(this, "MavenDockerAsset");

    // Create ECS Cluster
    this.cluster = new Cluster(this, "SpringAppCluster", {
      vpc: props.vpc,
      clusterName: "spring-with-react-cluster",
    });

    // Create CloudWatch Log Group
    const logGroup = new LogGroup(this, "SpringAppLogGroup", {
      logGroupName: "/ecs/spring-with-react",
      retention: RetentionDays.ONE_WEEK,
      removalPolicy: RemovalPolicy.DESTROY,
    });

    // Create ECS Task Definition
    const taskDefinition = new TaskDefinition(this, "SpringAppTaskDef", {
      family: "spring-with-react-task",
      compatibility: Compatibility.FARGATE,
      cpu: "512",
      memoryMiB: "1024",
      networkMode: NetworkMode.AWS_VPC,
    });

    // Add container to task definition
    const container = taskDefinition.addContainer("SpringAppContainer", {
      image: ContainerImage.fromDockerImageAsset(
        this.mavenDockerAsset.dockerImageAsset,
      ),
      memoryLimitMiB: 1024,
      cpu: 512,
      logging: LogDrivers.awsLogs({
        streamPrefix: "spring-app",
        logGroup,
      }),
      environment: {
        SPRING_PROFILES_ACTIVE: "prod",
        SPRING_DATASOURCE_URL: `jdbc:postgresql://${props.database.instanceEndpoint.hostname}:${props.database.instanceEndpoint.port}/first_db?currentSchema=first_schema_in_first_db`,
        SPRING_DATA_MONGODB_HOST: props.docdbCluster.clusterEndpoint.hostname,
        SPRING_DATA_MONGODB_PORT: "27017",
        SPRING_DATA_MONGODB_DATABASE: "spring_with_react",
      },
      secrets: {
        SPRING_DATASOURCE_USERNAME: Secret.fromSecretsManager(
          props.dbCredentials,
          "username",
        ),
        SPRING_DATASOURCE_PASSWORD: Secret.fromSecretsManager(
          props.dbCredentials,
          "password",
        ),
        SPRING_DATA_MONGODB_USERNAME: Secret.fromSecretsManager(
          props.mongoCredentials,
          "username",
        ),
        SPRING_DATA_MONGODB_PASSWORD: Secret.fromSecretsManager(
          props.mongoCredentials,
          "password",
        ),
      },
    });

    // Add port mapping
    container.addPortMappings({
      containerPort: 8080,
      protocol: EcsProtocol.TCP,
    });

    // Grant task definition access to secrets
    props.dbCredentials.grantRead(taskDefinition.taskRole);
    props.mongoCredentials.grantRead(taskDefinition.taskRole);

    // Create Application Load Balancer
    this.loadBalancer = new ApplicationLoadBalancer(this, "SpringAppALB", {
      vpc: props.vpc,
      internetFacing: true,
      securityGroup: props.albSecurityGroup,
      vpcSubnets: {
        subnetType: SubnetType.PUBLIC,
      },
    });

    // Create ALB listener
    const listener = this.loadBalancer.addListener("SpringAppListener", {
      port: 80,
      protocol: ApplicationProtocol.HTTP,
      defaultAction: ListenerAction.fixedResponse(404, {
        contentType: "text/plain",
        messageBody: "Not Found",
      }),
    });

    // Create ECS Fargate Service
    this.service = new FargateService(this, "SpringAppService", {
      cluster: this.cluster,
      taskDefinition,
      desiredCount: 2,
      assignPublicIp: false,
      securityGroups: [props.ecsSecurityGroup],
      vpcSubnets: {
        subnetType: SubnetType.PRIVATE_WITH_EGRESS,
      },
      serviceName: "spring-with-react-service",
    });

    // Add service as target to ALB
    listener.addTargets("SpringAppTargets", {
      port: 8080,
      protocol: ApplicationProtocol.HTTP,
      targets: [this.service],
      healthCheck: {
        path: "/actuator/health",
        interval: Duration.seconds(30),
        timeout: Duration.seconds(5),
        healthyThresholdCount: 2,
        unhealthyThresholdCount: 3,
      },
    });
  }
}
