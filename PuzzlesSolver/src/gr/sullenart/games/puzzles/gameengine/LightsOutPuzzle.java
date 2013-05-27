package gr.sullenart.games.puzzles.gameengine;

import gr.sullenart.games.puzzles.R;
import gr.sullenart.games.puzzles.gameengine.lights.LightsOutBoard;
import gr.sullenart.games.puzzles.gameengine.lights.LightsOutBoardFactory;
import gr.sullenart.games.puzzles.gameengine.lights.LightsOutSolver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.preference.PreferenceManager;

/**
 * LightsOutPuzzle Class. The player must switch off all lights.
 * When the user touches on a light, this and all adjacent to it lights
 * change their state.
 */
public class LightsOutPuzzle extends Puzzle
{
    /** Square's width*/
    private int squareWidth = 90;

    /** Lights Out board to play */
    private LightsOutBoard lightsOutBoard;    

    /** Number of moves made so far */
    private int movesMade = 0;
    
    /** List for storing moves made */
    private List<Integer> movesList;

	private int boardRowCount = 5;
	
	private int boardColumnCount = 5;
    
	private String boardKey = "Lights_Board";

	private int[] initialBoard = null;
	
    /** Setter of lightsOutBoard
    * @param lightsOutBoard The board to play
    */
    public void setLightsOutBoard(LightsOutBoard lightsOutBoard) {
        this.lightsOutBoard = lightsOutBoard;
    }
    
	public LightsOutPuzzle(Context context) {
		super(context);
		enableReplay = false;
		family = "Lights";
		
        SharedPreferences preferences =
    		PreferenceManager.getDefaultSharedPreferences(context);		
		String rowsStr = preferences.getString("Lights_Rows", null);
		String columnsStr = preferences.getString("Lights_Columns", null);
		if (rowsStr != null && columnsStr != null) {
			try {
				boardRowCount = Integer.parseInt(rowsStr);
				boardColumnCount = Integer.parseInt(columnsStr);
			}
			catch(NumberFormatException ex) {
				
			}
		}
		
		name = context.getResources().getString(R.string.lights) + " " +
				boardRowCount + "x" + boardColumnCount;
	}

	/**
	 * Configures the options of the puzzle (void).
	 *
	 * @param options Options string.
	 */
	public boolean configure(SharedPreferences preferences) {
		boolean result = false;
		
		String boardStr = preferences.getString(boardKey, null);
		if (boardStr != null) {
			lightsOutBoard = LightsOutBoard.deserialize(boardStr);
		}
		
		String rowsStr = preferences.getString("Lights_Rows", null);
		String columnsStr = preferences.getString("Lights_Columns", null);
		if (rowsStr != null && columnsStr != null) {
			try {
				int newBoardRowCount = Integer.parseInt(rowsStr);
				int newBoardColumnCount = Integer.parseInt(columnsStr);
				if (newBoardRowCount != boardRowCount ||
						newBoardColumnCount != boardColumnCount) {
					boardRowCount = newBoardRowCount;
					boardColumnCount = newBoardColumnCount;
					onSizeChanged(screenWidth, screenHeight);
					init();
					result = true;
				}
			}
			catch(NumberFormatException e) {
	
			}
		}
		
		if (lightsOutBoard == null || lightsOutBoard.isSolved() || result == true) {
    		LightsOutBoardFactory factory = new LightsOutBoardFactory();
    		int sizeX = boardColumnCount;
    		int sizeY = boardRowCount;
    		int[] board = factory.getBoard(sizeX, sizeY);
			lightsOutBoard = new LightsOutBoard(sizeX, sizeY, board);
		}
		
		initialBoard = new int[lightsOutBoard.getBoard().length];
		System.arraycopy(lightsOutBoard.getBoard(), 0, 
						 initialBoard, 0, initialBoard.length);
		
		return result;
	}
	
	@Override
	public void restart() {
		init();
		if (initialBoard != null) {
			int [] newBoard = new int [initialBoard.length];
			System.arraycopy(initialBoard, 0, 
					newBoard, 0, initialBoard.length);
			lightsOutBoard = new LightsOutBoard(boardColumnCount, 
										boardRowCount, newBoard);
		}
	}
	
	@Override
	public void storeState() {
		if (lightsOutBoard != null) {
			String str = lightsOutBoard.serialize();
			
	        SharedPreferences preferences =
	    		PreferenceManager.getDefaultSharedPreferences(context);        
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putString(boardKey, str);
	        editor.commit();  			
		}
	}

	public void onSizeChanged(int w, int h) {
		super.onSizeChanged(w, h);

		if (w == 0 || h == 0 || lightsOutBoard == null) {
			return;
		}

        int sizeX = boardColumnCount;
        int sizeY = boardRowCount;
        int squareWidthX = (w - 10) / sizeX;
        int squareWidthY = (h - 10) / sizeY;
        
        squareWidth = squareWidthX < squareWidthY ? squareWidthX : squareWidthY;

		offsetX = (screenWidth - squareWidth*sizeX)/2;
		offsetY = (screenHeight - squareWidth*sizeY)/2;

        initGraphicsObjects();
	}

    /**
     * Initialize puzzle's context - default size.
     */
    @Override
	public void init() {
        super.init();
        name = context.getResources().getString(R.string.lights);
        movesMade = 0;
        enableUndo = true;
        
        movesList = new ArrayList<Integer>();
    }

	/**
	 * Processes input from user).
	 *
	 * @param x Cursor's x position.
	 * @param y Cursor's y position.
	 * @return Return code for mouse click. One from RIDDLE_SOLVED,
	 *         MOVE_OUT_OF_BOUNDS, MOVE_NOT_ALLOWED, MOVE_SUCCESSFUL.
	 */
    public MoveResult onTouchEvent(float x, float y) {
        // Figure out the row/column
        int c = (int) (x - offsetX) / squareWidth;
        int r = (int) (y - offsetY) / squareWidth;
		//r = boardSize - 1 - r;

        MoveResult result = makeMove(r, c);
        if (result == MoveResult.MOVE_SUCCESSFUL) {
			if (!isStarted) {
				isStarted = true;
			}
        }

        return result;
    }

    /**
     * Makes the move at the selected square.
     */
    public MoveResult makeMove(int r, int c) {
        int sizeX = lightsOutBoard.getSizeX();
        int sizeY = lightsOutBoard.getSizeY();    
		if (c < sizeX && r < sizeY) {
            movesMade++;
            movesList.add(r*sizeX + c);
            lightsOutBoard.toggle(r, c);
            if (lightsOutBoard.isSolved()) {
                return MoveResult.RIDDLE_SOLVED;
            }
            return MoveResult.MOVE_SUCCESSFUL;
		}
		return MoveResult.MOVE_OUT_OF_BOUNDS;
    }

    /**
     * Undoes last move.
     */
    public void undoLastMove() {
        int size = movesList.size();
        if (size > 0) {
            int move = movesList.remove(size - 1);
            int sizeX= lightsOutBoard.getSizeX();
			int r = move / sizeX;
            int c = move % sizeX;
            lightsOutBoard.toggle(r, c);
            movesMade--;
        }
    }

    /** Gradient used to paint the background */
    private GradientDrawable backgroundGradient;    
    
    /** Gradient used to paint an On light */
    private GradientDrawable onGradient;
    
    /** Gradient used to paint an Off light */
    private GradientDrawable offGradient;

    /** Paint for drawing solution cell */
	private Paint solutionPaint;
    
    /**
    * Initializes graphics objects
    */
    private void initGraphicsObjects() {
        int [] colorsBackground = {0xFF006600, 0xFF008800};
		int[] colorsOn = {0xFFebf4d3, 0xFFd7e9a8, 0xFF9cc925, 0xFF7ba60d, 0xFF5d8005, 0xFF49811f};
        int[] colorsOff = {0xFFd2e7d2, 0xFFa4cfa4, 0xFF1c881c, 0xFF156615, 0xFF0e440e, 0xFFffffff};

		backgroundGradient = new GradientDrawable(Orientation.BOTTOM_TOP, colorsBackground);
    	backgroundGradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
    	backgroundGradient.setGradientRadius(180);
    	backgroundGradient.setDither(true);
    	
		onGradient = new GradientDrawable(Orientation.BOTTOM_TOP, colorsOn);
    	onGradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
    	onGradient.setGradientRadius(180);
    	onGradient.setDither(true);
        
		offGradient = new GradientDrawable(Orientation.BOTTOM_TOP, colorsOff);
    	offGradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
    	offGradient.setGradientRadius(270);
    	offGradient.setDither(true);
    	
    	solutionPaint = new Paint();
    	solutionPaint.setStyle(Style.FILL_AND_STROKE);
    	solutionPaint.setColor(0xFFFF0000);
    }
    
	/**
	 * Paints the puzzle's board.
	 *
	 * @param g Graphics object.
	 */
    public void draw(Canvas canvas) {
        int sizeX = lightsOutBoard.getSizeX();
        int sizeY = lightsOutBoard.getSizeY();        
    	float left = 0, top = 0;        

        Rect screenRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        backgroundGradient.setBounds(screenRect);
        backgroundGradient.draw(canvas);         
        
		for(int row=0; row<sizeY; row++) {
			for(int column=0;column<sizeX;column++) {
				left = offsetX + column*squareWidth;
				top = offsetY + row*squareWidth;
                Rect cb = new Rect((int) left, (int) top, 
                            (int) left + squareWidth, (int) top + squareWidth);
                
                int light = lightsOutBoard.getLightState(row, column);
                if (light == LightsOutBoard.LIGHT_ON) {
                    onGradient.setBounds(cb);
                    onGradient.draw(canvas);
                }
                else if (light == LightsOutBoard.LIGHT_OFF) {
                    offGradient.setBounds(cb);
                    offGradient.draw(canvas);                
                }
                
                int pos = row*sizeX+column;
				int solutionVal = lightsOutBoard.getSolution()[pos];
				if (solutionVal > 0) {
					canvas.drawCircle(left+squareWidth/2, top+squareWidth/2, 
							squareWidth/3, solutionPaint);
				}
				
			}
		}
    }

	/**
	 * Returns moves made so far.
	 *
	 * @return Returns moves made so far.
	 */
    protected int movesMade() {
    	return movesMade;
    }

    // Solving functionality

	/**
	 * Initialises solver.
	 */
	protected void initSolver() {

	}

	/**
	 * Returns puzzle's status.
	 *
	 * @return true if the puzzle is solved.
	 */
    public boolean isSolved() {
    	if (lightsOutBoard != null) {
    		return lightsOutBoard.isSolved();
    	}
        return false;
    }

	/**
	 * Finds the next possible move.
	 *
	 * @return true if a move has been found.
	 */
    protected boolean findNextMove() {
        return false;
    }

	/**
	 * Replays one move.
	 *
	 * @return false if there are no more moves to replay.
	 */
    protected boolean playNextMove()
    {
        return false;
    }

	/**
	 * Goes back (undo last move) when a deadlock is encountered.
	 *
	 * @return If it is impossible to go back false is returned. This
	 *         means that the puzzle cannot be solved.
	 */
    protected boolean goBack()
    {
        return false;
    }

	@Override
	public boolean areNoMoreMovesLeft() {
		return false;
	}
	
	@Override
	public MoveResult solve(boolean auto) {
		LightsOutSolver solver = new LightsOutSolver(lightsOutBoard, 2, 0);
		boolean result = solver.solve();
		if (result) {
			return MoveResult.RIDDLE_SOLVED;
		}
		return MoveResult.RIDDLE_UNSOLVABLE;
	}
}

