package io.madipalli.prabhu.SpringWithReact;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SnakeBodySection {

	SnakeBodySection nodeTowardsTail;
	SnakeBodySection nodeTowardsHead;
	final int x;
	final int y;

	public String toString() {
		return "SnakeBodySection(x=" + this.x + ", y=" + this.y + ")";
	}
}
