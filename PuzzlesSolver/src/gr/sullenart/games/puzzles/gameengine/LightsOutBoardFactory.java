package gr.sullenart.games.puzzles.gameengine;
import java.util.Random;

public class LightsOutBoardFactory {

    private Random rnd = new Random();
    
    public LightsOutBoardFactory() {

    }
    
    public int [] getBoard(int sizeX, int sizeY) {
        int [] board = new int[sizeX * sizeY];
        
        for (int i=0;i<board.length;i++) {
            board[i] = rnd.nextInt(2);
        }
        
        return board;
    }
}