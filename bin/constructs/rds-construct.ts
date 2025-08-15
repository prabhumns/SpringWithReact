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
import {
  DatabaseInstance,
  DatabaseInstanceEngine,
  PostgresEngineVersion,
  Credentials,
} from "aws-cdk-lib/aws-rds";
import { Secret as SecretsManagerSecret } from "aws-cdk-lib/aws-secretsmanager";

export interface RdsConstructProps {
  vpc: Vpc;
  securityGroup: SecurityGroup;
}

export class RdsConstruct extends Construct {
  public readonly database: DatabaseInstance;
  public readonly credentials: SecretsManagerSecret;

  constructor(scope: Construct, id: string, props: RdsConstructProps) {
    super(scope, id);

    // Create secrets for database credentials
    this.credentials = new SecretsManagerSecret(
      this,
      "DatabaseCredentials",
      {
        description: "Database credentials for Spring application",
        generateSecretString: {
          secretStringTemplate: JSON.stringify({ username: "spring_user" }),
          generateStringKey: "password",
          excludeCharacters: '"@/\\',
          passwordLength: 32,
        },
      },
    );

    // Create RDS PostgreSQL database
    this.database = new DatabaseInstance(this, "PostgreSQLDatabase", {
      engine: DatabaseInstanceEngine.postgres({
        version: PostgresEngineVersion.VER_15,
      }),
      instanceType: InstanceType.of(InstanceClass.T3, InstanceSize.MICRO),
      vpc: props.vpc,
      vpcSubnets: {
        subnetType: SubnetType.PRIVATE_ISOLATED,
      },
      securityGroups: [props.securityGroup],
      credentials: Credentials.fromSecret(this.credentials),
      databaseName: "first_db",
      allocatedStorage: 20,
      storageEncrypted: true,
      backupRetention: Duration.days(7),
      deletionProtection: false,
      removalPolicy: RemovalPolicy.DESTROY,
    });
  }
}