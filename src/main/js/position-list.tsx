import React from "react";
import { ColumnLayout } from "@cloudscape-design/components";
import PositionContainer from "./position-container";
import { useQuery } from "@tanstack/react-query";
import { getAllPositions } from "./accessors";
import { BulletList } from "react-content-loader";
import { Position } from "./Position";

const PositionList: React.FC = () => {
	const { data: positions } = useQuery({
		queryKey: ["AllPositions"],
		queryFn: () => getAllPositions(),
	});

	if (positions) {
		return (
			<ColumnLayout columns={1}>
				{(positions as Position[]).map((position: Position) => (
					<PositionContainer
						position={position}
						key={position.positionId}
					/>
				))}
			</ColumnLayout>
		);
	}
	return <BulletList />;
};

export default PositionList;
