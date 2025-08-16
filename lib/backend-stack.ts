import { App, Stack, StackProps } from "aws-cdk-lib";
import {
  Peer,
  Port,
  SecurityGroup,
  SubnetType,
  Vpc,
} from "aws-cdk-lib/aws-ec2";
import { AppConstruct, DocumentDbConstruct, RdsConstruct } from "./constructs";

export class BackendStack extends Stack {
  constructor(app: App, props?: StackProps) {
    super(app, "Backend-Stack", props);

    // Create VPC with public and private subnets
    const vpc = new Vpc(this, "SpringAppVpc", {
      maxAzs: 2,
      natGateways: 1,
      subnetConfiguration: [
        {
          cidrMask: 24,
          name: "Public",
          subnetType: SubnetType.PUBLIC,
        },
        {
          cidrMask: 24,
          name: "Private",
          subnetType: SubnetType.PRIVATE_WITH_EGRESS,
        },
        {
          cidrMask: 24,
          name: "Isolated",
          subnetType: SubnetType.PRIVATE_ISOLATED,
        },
      ],
    });

    // Security Groups
    const rdsSecurityGroup = new SecurityGroup(this, "RdsSecurityGroup", {
      vpc,
      description: "Security group for RDS PostgreSQL",
      allowAllOutbound: false,
    });

    const docdbSecurityGroup = new SecurityGroup(this, "DocdbSecurityGroup", {
      vpc,
      description: "Security group for DocumentDB",
      allowAllOutbound: false,
    });

    const ecsSecurityGroup = new SecurityGroup(this, "EcsSecurityGroup", {
      vpc,
      description: "Security group for ECS tasks",
      allowAllOutbound: true,
    });

    const albSecurityGroup = new SecurityGroup(this, "AlbSecurityGroup", {
      vpc,
      description: "Security group for Application Load Balancer",
      allowAllOutbound: true,
    });
    albSecurityGroup.addIngressRule(
      Peer.anyIpv4(),
      Port.tcp(443),
      "Allow HTTPS traffic from internet",
    );

    // Allow ECS tasks to connect to databases
    rdsSecurityGroup.addIngressRule(
      ecsSecurityGroup,
      Port.tcp(5432),
      "Allow ECS tasks to connect to PostgreSQL",
    );

    docdbSecurityGroup.addIngressRule(
      ecsSecurityGroup,
      Port.tcp(27017),
      "Allow ECS tasks to connect to DocumentDB",
    );

    // Allow ALB to connect to ECS tasks
    ecsSecurityGroup.addIngressRule(
      albSecurityGroup,
      Port.tcp(8080),
      "Allow ALB to connect to ECS tasks",
    );

    // Create RDS PostgreSQL database
    const rdsConstruct = new RdsConstruct(this, "RdsConstruct", {
      vpc,
      securityGroup: rdsSecurityGroup,
    });

    // Create DocumentDB cluster
    const documentDbConstruct = new DocumentDbConstruct(
      this,
      "DocumentDbConstruct",
      {
        vpc,
        securityGroup: docdbSecurityGroup,
      },
    );

    // Create main application (ECS, ALB, ECR)
    new AppConstruct(this, "AppConstruct", {
      vpc,
      ecsSecurityGroup,
      albSecurityGroup,
      database: rdsConstruct.database,
      docdbCluster: documentDbConstruct.cluster,
      dbCredentials: rdsConstruct.credentials,
      mongoCredentials: documentDbConstruct.credentials,
    });
  }
}
