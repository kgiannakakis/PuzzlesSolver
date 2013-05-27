package gr.sullenart.games.puzzles.gameengine.lights;

public class LightsOutSolver {

	private LightsOutBoard lightsOutBoard;
	private int colorsCount;
	private int endColor;

	public LightsOutSolver(LightsOutBoard lightsOutBoard,
						   int colorsCount,
						   int endColor) {
		this.lightsOutBoard = lightsOutBoard;
		this.colorsCount = colorsCount;
		this.endColor = endColor;
	}
	
	public boolean solve() {
		for(int i=0; i<lightsOutBoard.getSolution().length;i++) {
			lightsOutBoard.getSolution()[i] = i % 2;
		}
		return true;
	}
}
