package gr.sullenart.games.puzzles.gameengine.lights;


public class LightsOutBoard {

    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;

    private int sizeX;
    
    private int sizeY;

    private int [] board;
    
    private int [] solution;
    
    public int getSizeX() {
        return sizeX;
    }
    
    public int getSizeY() {
        return sizeY;
    }    
    
    public int [] getBoard() {
        return board;
    }
    
    public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
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
        
        solution = new int [board.length];
        
        while (lightsOnCount == 0) {
	        for(int i=0; i<board.length; i++) {
	            lightsOnCount += board[i];
	            solution[i] = 0;
	        }
        }
    }
    
    public void toggle(int r, int c) {
        int i = r*sizeX + c;
        
        board[i] ^= 1;
        if (solution[i] > 0) {
        	solution[i]--;
        }
        
        lightsOnCount += board[i] == 1 ? 1 : -1;
        if (r > 0) {
            board[i - sizeX] ^= 1;    
            lightsOnCount += board[i - sizeX] == 1 ? 1 : -1;
        }
        if (r < sizeY - 1) {
            board[i + sizeX] ^= 1;    
            lightsOnCount += board[i + sizeX] == 1 ? 1 : -1;
        }
        if (c > 0) {
            board[i - 1] ^= 1;    
            lightsOnCount += board[i - 1] == 1 ? 1 : -1;
        }
        if (c < sizeX - 1) {
            board[i + 1] ^= 1;    
            lightsOnCount += board[i + 1] == 1 ? 1 : -1;
        }        
    }
    
    public boolean isSolved() {
        return lightsOnCount == 0;
    }
    
    public boolean isSolutionVisible() {
    	if (solution == null) {
    		return false;
    	}
    	for(int i=0; i<solution.length;i++) {
    		if (solution[i] > 0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public int getLightState(int r, int c) {
        return board[r*sizeX + c];
    }
    
    public String serialize() {
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append(String.format("%02d%02d", sizeX, sizeY));
    	for(int i=0;i<board.length;i++) {
    		builder.append(String.valueOf(board[i]));
    	}
    	
    	return builder.toString();
    }
    
    public static LightsOutBoard deserialize(String lightsOutBoardStr) {
    	int sizeX = 0;
    	int sizeY = 0;
    	
    	if (lightsOutBoardStr == null || lightsOutBoardStr.length() < 4) {
    		return null;
    	}
    	
    	try {
    		sizeX = Integer.parseInt(lightsOutBoardStr.substring(0, 2));
    		sizeY = Integer.parseInt(lightsOutBoardStr.substring(2, 4));
    	}
		catch(NumberFormatException e) {
			return null;
		}    	
    	
		int boardSize = sizeX*sizeY;
		if (lightsOutBoardStr.length() != boardSize + 4) {
			return null;
		}
		
		int [] board = new int[boardSize];
		
		int start = 4;
		for(int i=0; i< boardSize; i++) {
	    	try {
	    		board[i] = Integer.parseInt(lightsOutBoardStr.substring(start, start+1));
	    		start++;
	    	}
			catch(NumberFormatException e) {
				return null;
			}  			
			if (board[i] > 1) {
				return null;
			}
		}
		
    	return new LightsOutBoard(sizeX, sizeY, board);
    }

}