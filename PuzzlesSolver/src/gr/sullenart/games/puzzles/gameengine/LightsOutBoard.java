package gr.sullenart.games.puzzles.gameengine;


public class LightsOutBoard {

    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;

    private int sizeX;
    
    private int sizeY;

    private int [] board;
    
    public int getSizeX() {
        return sizeX;
    }
    
    public int getSizeY() {
        return sizeY;
    }    
    
    public int [] getBoard() {
        return board;
    }
    
    private int lightsOnCount;

    /*
      ---------- size x ----------
      |     0   1   2 ... (x-1)
      |     x (x+1)  
    size y  .
      |     .
      |   (y-2)*x ...     (y-1)*x - 1    
    */
    
    
    public LightsOutBoard(int sizeX, int sizeY, int [] board) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.board = board;
        lightsOnCount = 0;
        for(int i=0; i<board.length; i++) {
            lightsOnCount += board[i];
        }
    }
    
    public void toggle(int r, int c) {
        int i = r*sizeX + c;
        
        board[i] ^= 1;
        lightsOnCount += board[i] == 1 ? 1 : -1;
        if (r > 0) {
            board[i - sizeX] ^= 1;    
            lightsOnCount += board[i - sizeX] == 1 ? 1 : -1;
        }
        if (r < sizeY - 2) {
            board[i + sizeX] ^= 1;    
            lightsOnCount += board[i + sizeX] == 1 ? 1 : -1;
        }
        if (c > 0) {
            board[i - 1] ^= 1;    
            lightsOnCount += board[i - 1] == 1 ? 1 : -1;
        }
        if (c < sizeX - 2) {
            board[i + 1] ^= 1;    
            lightsOnCount += board[i + 1] == 1 ? 1 : -1;
        }        
    }
    
    public boolean isSolved() {
        return lightsOnCount == 0;
    }
    
    public int getLightState(int r, int c) {
        return board[r*sizeX + c];
    }

}