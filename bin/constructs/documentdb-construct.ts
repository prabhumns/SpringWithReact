import { Construct } from "constructs";
import { Duration, RemovalPolicy } from "aws-cdk-lib";
import {
  Vpc,
  SecurityGroup,
  SubnetType,
  InstanceType,
  InstanceClass,
  InstanceSize,
} from "aws-cdk-lib/aws-ec2";
import { DatabaseCluster } from "aws-cdk-lib/aws-docdb";
import { Secret as SecretsManagerSecret } from "aws-cdk-lib/aws-secretsmanager";

export interface DocumentDbConstructProps {
  vpc: Vpc;
  securityGroup: SecurityGroup;
}

export class DocumentDbConstruct extends Construct {
  public readonly cluster: DatabaseCluster;
  public readonly credentials: SecretsManagerSecret;

  constructor(scope: Construct, id: string, props: DocumentDbConstructProps) {
    super(scope, id);

    // Create secrets for MongoDB credentials
    this.credentials = new SecretsManagerSecret(
      this,
      "MongoCredentials",
      {
        description: "MongoDB credentials for Spring application",
        generateSecretString: {
          secretStringTemplate: JSON.stringify({
            username: "spring_mongo_user",
          }),
          generateStringKey: "password",
          excludeCharacters: '"@/\\',
          passwordLength: 32,
        },
      },
    );

    // Create DocumentDB cluster
    this.cluster = new DatabaseCluster(this, "DocumentDBCluster", {
      masterUser: {
        username: "spring_mongo_user",
        password: this.credentials.secretValueFromJson("password"),
      },
      instanceType: InstanceType.of(InstanceClass.T3, InstanceSize.MEDIUM),
      instances: 1,
      vpc: props.vpc,
      vpcSubnets: {
        subnetType: SubnetType.PRIVATE_ISOLATED,
      },
      securityGroup: props.securityGroup,
      storageEncrypted: true,
      backup: {
        retention: Duration.days(7),
      },
      removalPolicy: RemovalPolicy.DESTROY,
    });
  }
}