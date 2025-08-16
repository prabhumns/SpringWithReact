package io.madipalli.prabhu.SpringWithReact;

public final class RegularlyGrowingSnakeGame extends BaseSnakeGame {

	private int instantCounter;
	private final int growthFrequency;

	public RegularlyGrowingSnakeGame(
		final int boardLength,
		final int initialSnakeLength,
		final int growthFrequency
	) {
		super(boardLength, initialSnakeLength);
		this.growthFrequency = growthFrequency;
		this.instantCounter = 0;
	}

	@Override
	protected boolean shouldIncreaseSnakeSize() {
		this.instantCounter = this.instantCounter + 1;
		if (this.instantCounter == this.growthFrequency) {
			this.instantCounter = 0;
			return true;
		}
		return false;
	}
}
