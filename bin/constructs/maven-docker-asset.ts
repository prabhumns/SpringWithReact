import { Construct } from "constructs";
import { DockerImageAsset, Platform } from "aws-cdk-lib/aws-ecr-assets";

export interface MavenDockerAssetProps {
  directory: string;
  exclude?: string[];
}

export class MavenDockerAsset extends Construct {
  public readonly dockerImageAsset: DockerImageAsset;

  constructor(scope: Construct, id: string, props: MavenDockerAssetProps) {
    super(scope, id);

    // Create Docker image asset with automatic Maven build
    this.dockerImageAsset = new DockerImageAsset(this, "SpringAppImage", {
      directory: props.directory,
      exclude: props.exclude || [
        "node_modules",
        "bin",
        "cdk.out",
        ".git",
        "*.md",
        "deploy.sh",
      ],
      platform: Platform.LINUX_AMD64,
      buildArgs: {
        JAR_FILE: "target/*.jar",
      },
      file: "Dockerfile.cdk", // We'll create a multi-stage Dockerfile
    });
  }
}