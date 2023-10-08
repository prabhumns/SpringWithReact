import React from "react";
import { Position } from "./Position";
import Container from "@cloudscape-design/components/container";
import { Box } from "@cloudscape-design/components";

const ValueWithLabel: React.FC<
  React.PropsWithChildren<{ label: React.ReactNode }>
> = ({ label, children }) => (
  <div>
    <Box variant="awsui-key-label">{label}</Box>
    <div>{children}</div>
  </div>
);

const PositionContainer: React.FC<{ position: Position }> = ({ position }) => {
  console.log("Object is ", position);
  return (
    <Container header={position.name}>
      <ValueWithLabel label="Created at">{`${position.creationTime.getUTCDate()}`}</ValueWithLabel>
      <ValueWithLabel label="Updated at">{`${position.creationTime.getUTCDate()}`}</ValueWithLabel>
      <ValueWithLabel label="Id">{position.positionId}</ValueWithLabel>
    </Container>
  );
};

export default PositionContainer;
