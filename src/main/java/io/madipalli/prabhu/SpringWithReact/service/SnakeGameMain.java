package io.madipalli.prabhu.SpringWithReact.service;

import io.madipalli.prabhu.SpringWithReact.Direction;
import io.madipalli.prabhu.SpringWithReact.RegularlyGrowingSnakeGame;
import java.util.stream.IntStream;

public class SnakeGameMain {

	public static void main(String[] args) {
		final var game = new RegularlyGrowingSnakeGame(20, 5, 10);
		IntStream.range(0, 50).forEach(i -> {
				game.nextInstant();
				gap();
			});
		game.changeDirection(Direction.UP);
		IntStream.range(0, 50).forEach(i -> {
				game.nextInstant();
				gap();
			});
		game.changeDirection(Direction.LEFT);
		IntStream.range(0, 50).forEach(i -> {
				game.nextInstant();
				gap();
			});
		game.changeDirection(Direction.UP);
		game.nextInstant();
		gap();
		game.changeDirection(Direction.RIGHT);
		game.nextInstant();
		gap();
		game.changeDirection(Direction.UP);
	}

	private static void gap() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
