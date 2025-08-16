import { DockerImageAsset, Platform } from "aws-cdk-lib/aws-ecr-assets";
import { Construct } from "constructs";

export class DockerAsset extends Construct {
  public readonly dockerImageAsset: DockerImageAsset;

  constructor(scope: Construct, id: string) {
    super(scope, id);

    // Create Docker image asset with automatic Maven build
    this.dockerImageAsset = new DockerImageAsset(this, "SpringAppImage", {
      directory: ".",
      exclude: [
        "*",
        "!target",
      ],
      platform: Platform.LINUX_AMD64,
      file: "Dockerfile",
    });
  }
}
