package io.madipalli.prabhu.SpringWithReact;

import java.util.StringJoiner;
import java.util.stream.IntStream;
import lombok.Getter;

abstract class BaseSnakeGame implements SnakeGame {

	private final int boardLength;
	private SnakeBodySection head;
	private SnakeBodySection tail;
	protected boolean isGameOver = false;

	@Getter
	private Direction currentDirection;

	protected final CellContent[][] board;

	public BaseSnakeGame(final int boardLength, final int initialSnakeLength) {
		this.boardLength = boardLength;
		this.currentDirection = Direction.RIGHT;

		this.tail = new SnakeBodySection(0, 0);
		var next = this.tail;
		for (int i : IntStream.range(1, initialSnakeLength).toArray()) {
			this.head = new SnakeBodySection(i, 0);
			this.head.nodeTowardsTail = next;
			next.nodeTowardsHead = this.head;
			next = this.head;
		}
		if (head == null) this.head = this.tail;

		this.board = new CellContent[boardLength][boardLength];

		var current = this.tail;
		while (current != null) {
			this.board[current.x][current.y] = new CellContent.SnakeBody();
			current = current.nodeTowardsHead;
		}
		this.board[this.head.x][this.head.y] = CellContent.SnakeHead.INSTANCE;
	}

	public void nextInstant() {
		if (this.isGameOver) {
			return;
		}
		this.printBoard();
		moveSnakeHead();

		if (!this.shouldIncreaseSnakeSize()) {
			updateBoardForTail();
			moveSnakeTail();
		}
		if (
			this.board[this.head.x][this.head.y] != null &&
			this.board[this.head.x][this.head.y].isSnakeBody()
		) {
			this.isGameOver = true;
			System.out.println("Game over");
			return;
		}
		this.board[this.head.x][this.head.y] = CellContent.SnakeHead.INSTANCE;
	}

	private void printBoard() {
		System.out.println("Head: " + this.head);
		System.out.println("Tail: " + this.tail);

		final var boardString = new StringJoiner("\n");
		for (int i = 0; i < this.boardLength; i++) {
			final var sj = new StringJoiner("|", "|", "|");
			for (int j = 0; j < this.boardLength; j++) {
				final var cellContent = this.board[i][j];
				if (cellContent == null) {
					sj.add(" ");
				} else {
					sj.add(cellContent.toString());
				}
			}
			boardString.add(sj.toString());
		}
		System.out.println("*".repeat(41));
		System.out.println("*".repeat(41));
		System.out.println(boardString);
		System.out.println("*".repeat(41));
		System.out.println("*".repeat(41));
	}

	private void moveSnakeTail() {
		final var newTail = this.tail.nodeTowardsHead;
		newTail.nodeTowardsTail = null;
		this.tail = newTail;
	}

	private void updateBoardForTail() {
		this.board[this.tail.x][this.tail.y] = null;
	}

	protected abstract boolean shouldIncreaseSnakeSize();

	private void moveSnakeHead() {
		final var oldHead = this.head;
		int x = oldHead.x;
		int y = oldHead.y;
		switch (this.currentDirection) {
			case UP -> y++;
			case DOWN -> y--;
			case RIGHT -> x++;
			case LEFT -> x--;
			default -> throw new IllegalStateException("Cannot reach here");
		}
		final var newHead = new SnakeBodySection(
			this.cleanCoordinate(x),
			this.cleanCoordinate(y)
		);
		newHead.nodeTowardsTail = oldHead;
		oldHead.nodeTowardsHead = newHead;
		this.head = newHead;
		this.board[oldHead.x][oldHead.y] = new CellContent.SnakeBody();
	}

	private int cleanCoordinate(final int x) {
		if (x == -1) return this.boardLength - 1;
		if (x == this.boardLength) return 0;
		if (x >= 0 && x < this.boardLength) return x;
		throw new IllegalStateException("Cannot reach this state");
	}

	public void changeDirection(Direction direction) {
		this.currentDirection = direction;
	}

	public final boolean isGameOver() {
		return this.isGameOver;
	}
}
