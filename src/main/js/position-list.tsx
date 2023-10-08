import React from "react";
import { ColumnLayout } from "@cloudscape-design/components";
import PositionContainer from "./position-container";
import { useQuery } from "@tanstack/react-query";
import { getAllPositions } from "./accessors";
import { BulletList } from "react-content-loader";

const PositionList: React.FC = () => {
  const { data: positions } = useQuery(["AllPositions"], () =>
    getAllPositions(),
  );
  if (positions) {
    return (
      <ColumnLayout columns={1}>
        {positions.map((position) => (
          <PositionContainer position={position} key={position.positionId} />
        ))}
      </ColumnLayout>
    );
  }
  return <BulletList />;
};

export default PositionList;
