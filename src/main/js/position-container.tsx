import React from "react";
import { Position } from "./Position";
import Container from "@cloudscape-design/components/container";
import KeyValuePairs from "@cloudscape-design/components/key-value-pairs";

const PositionContainer: React.FC<{ position: Position }> = ({ position }) => {
	return (
		<Container header={position.name}>
			<KeyValuePairs
				items={[
					{
						label: "Created at",
						value: <>{`${position.creationTime.toISOString()}`}</>,
					},
					{
						label: "Updated at",
						value: <>{`${position.updateTime.toISOString()}`}</>,
					},
					{
						label: "Id",
						value: <>{`${position.positionId}`}</>,
					},
				]}
			/>
		</Container>
	);
};

export default PositionContainer;
