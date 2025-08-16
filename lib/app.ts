import { App } from "aws-cdk-lib";
import { BackendStack } from "./backend-stack";

const app = new App();

new BackendStack(app);
