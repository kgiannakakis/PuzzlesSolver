package gr.sullenart.games.puzzles.gameengine.lights;
import java.util.Random;

public class LightsOutBoardFactory {

    private Random rnd = new Random();
    
    public LightsOutBoardFactory() {

    }
    
    public int [] getBoard(int sizeX, int sizeY) {
        int [] board = new int[sizeX * sizeY];
        boolean isSolved = false;
        
        while(!isSolved) {
        	int lightsCount = 0;
	        for (int i=0;i<board.length;i++) {
	            board[i] = rnd.nextInt(2);
	            lightsCount++;
	        }
	        if (lightsCount == 0) {
	        	continue;
	        }
	        LightsOutBoard lightsOutBoard = 
	        	new LightsOutBoard(sizeX, sizeY, board);
	        LightsOutSolver solver = new LightsOutSolver(lightsOutBoard, 2, 0);
	        isSolved = solver.solve();
        }
        
        return board;
    }
}