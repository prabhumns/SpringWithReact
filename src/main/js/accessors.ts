import axios from "axios";
import { Position } from "./Position";

interface PositionResponse {
	name: string;
	creationTime: string;
	updateTime: string;
	positionId: string;
}

export const getAllPositions = async (): Promise<Position[]> => {
	const responses = await axios.get<PositionResponse[]>("/api/v1/positions");
	return responses.data.map((response) => ({
		...response,
		creationTime: new Date(response.creationTime),
		updateTime: new Date(response.updateTime),
	}));
};
