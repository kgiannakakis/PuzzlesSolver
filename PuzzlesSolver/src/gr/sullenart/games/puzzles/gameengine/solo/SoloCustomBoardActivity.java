package gr.sullenart.games.puzzles.gameengine.solo;

import gr.sullenart.games.puzzles.CustomBoardEditListener;
import gr.sullenart.games.puzzles.PuzzleView;
import gr.sullenart.games.puzzles.R;
import gr.sullenart.games.puzzles.dialogs.InstructionsDialog;
import gr.sullenart.games.puzzles.dialogs.SaveBoardDialog;
import gr.sullenart.games.puzzles.gameengine.SoloPuzzle;
import gr.sullenart.games.puzzles.gameengine.solo.SoloGame.SoloGameType;
import gr.sullenart.time.TimeCounter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SoloCustomBoardActivity extends FragmentActivity 
                implements CustomBoardEditListener,
                           InstructionsDialog.InstructionsDialogListener,
                           SaveBoardDialog.SaveBoardDialogListener {	
	private SoloPuzzle puzzle;
	
	private PuzzleView puzzleView;
	
	private SoloPuzzleRepository soloPuzzleRepository;
	
	private String boardName;
	private int [] board;
	
	public static boolean showDirections = true;
	
	public static boolean boardsListUpdated = false;
	
	private SoloGameType boardType;
	private int targetPosition;
	
	private Button targetPositionButton;
	private Button saveButton;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
        board = null;
        targetPosition = -1;
        
        if (bundle != null) {
        	board = bundle.getIntArray("board");
        	boardName = bundle.getString("boardName");
        	targetPosition = bundle.getInt("targetPosition");
        	boardType = SoloGameType.getGameType(bundle.getInt("boardType"));
        }
		
		setContentView(R.layout.custom_board);	
		
		TextView editBoardMessage = (TextView) findViewById(R.id.custom_board_edit_message);
		if (boardName == null || board.length == 0) {
			editBoardMessage.setText(getResources().getString(R.string.edit_new_board));
		}
		else {
			editBoardMessage.setText(getResources().getString(R.string.edit_board) +
									 " " + boardName);
		}
		
        puzzle = new SoloPuzzle(this);        
        
		SoloGame soloGame = new SoloGame(board, targetPosition, boardType);
		puzzle.setGame(soloGame);
		puzzle.init();
		puzzle.setEditMode(true);
		puzzleView = (PuzzleView) findViewById(R.id.edit_puzzle_view);
		puzzleView.setPuzzle(puzzle);
		puzzleView.setTimeCounter(new TimeCounter());
		
		targetPositionButton = (Button) findViewById(R.id.custom_boards_button_target_position);
		saveButton = (Button) findViewById(R.id.custom_boards_button_save);
		
		targetPositionButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				doTargetPosition();
			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				doSave();
			}
		});

        updateButtons();
    }
    
    private void updateButtons() {
    	saveButton.setVisibility(isSavingEnabled() ? View.VISIBLE : View.GONE);
		targetPositionButton.setVisibility(isTargetPositionVisible() ? View.VISIBLE : View.GONE);
		
		if (puzzle.isInSetTargetMode()) {
			targetPositionButton.setText(R.string.edit_board);
		}
		else {
			targetPositionButton.setText(R.string.target_position);
		}        
    }
	
    @Override
    public void onStart() {
    	super.onStart();
    	SharedPreferences preferences =
    		PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        soloPuzzleRepository = new SoloPuzzleRepository(getApplicationContext());
        puzzle.setSoloPuzzleRepository(soloPuzzleRepository); 

		if (board == null && puzzle.configure(preferences)) {
    		puzzleView.invalidate();
    	}
    	
    	boolean showIntructionsFlag = 
    		preferences.getBoolean("Show_Custom_Board_Instructions", true);
    	
    	if (showIntructionsFlag && showDirections) {
    		showDirections = false;            
            showInstructionsDialog();
    	}
    	boardsListUpdated = false;
    }	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_board_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);

		MenuItem saveMenuItem = menu.findItem(R.id.save);
		boolean savingEnabled = isSavingEnabled();
		saveMenuItem.setEnabled(savingEnabled);
		
		MenuItem targetPositionMenuItem = menu.findItem(R.id.board_target_position);
		targetPositionMenuItem.setVisible(isTargetPositionVisible());
		
		if (puzzle.isInSetTargetMode()) {
			targetPositionMenuItem.setTitle(R.string.edit_board);
		}
		else {
			targetPositionMenuItem.setTitle(R.string.target_position);
		}
		
		MenuItem optionsMenuItem = menu.findItem(R.id.board_options);
		optionsMenuItem.setVisible(false);
		return true;
    }

	private boolean isSavingEnabled() {
		boolean savingEnabled = false;
		int size = Solo.GET_BOARD_SIZE(puzzle.getBoardTable());
		SoloBoard soloBoard = new SoloBoard(puzzle.getBoardTable(),size,size,
									boardType, targetPosition);
		savingEnabled = soloBoard.isBoardValid() && !soloBoard.isNotSolvable();
		return savingEnabled;
	}
    
    private boolean isTargetPositionVisible() {
        return targetPosition >= 0;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.save:
        	doSave();
        	break;
        case R.id.board_target_position:
        	doTargetPosition();
        	break;
        case R.id.board_options:       	       	
        	break;
        }
        return true;
    }

	private void doTargetPosition() {
		if (puzzle.isInSetTargetMode()) {
			puzzle.setInSetTargetMode(false);
		}
		else {
			puzzle.setInSetTargetMode(true);
		}
        updateButtons();
	}

	private void doSave() {
		if (boardName == null || boardName.length() == 0) {
            showSaveBoardDialog();
		}
		else {
		    targetPosition = puzzle.getTargetPosition();
			if (!soloPuzzleRepository.updateGame(boardName, 
					new SoloGame(puzzle.getBoardTable(), targetPosition, boardType))) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.an_error_occurred, Toast.LENGTH_LONG);
				toast.show();
			}
			else {
				saveNewBoardAsSelected();
				finish();
			}
		}
	}
    
    public void onBoardEdited() {
        updateButtons();
    }
    
    private void saveNewBoardAsSelected() {
        final SharedPreferences preferences =
    		PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("Solo_Puzzle_Type", boardName);
		editor.commit();
		boardsListUpdated = true;
    } 
    
    private void showInstructionsDialog() {
        DialogFragment instructionsDialog = InstructionsDialog.newInstance(
                                            getResources().getString(R.string.app_name),
                                            getResources().getString(R.string.solo_new_board_directions),
                                            R.drawable.icon);
        instructionsDialog.show(getSupportFragmentManager(), "instructions_dialog");  
    }
    
    public void onShowInstructionsChecked(boolean isChecked) {
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Show_Custom_Board_Instructions", !isChecked);
        editor.commit();    
    }
    
    private void showSaveBoardDialog() {
        DialogFragment saveBoardDialog = SaveBoardDialog.newInstance();
        saveBoardDialog.show(getSupportFragmentManager(), "save_board_dialog");     
    }

    public boolean onBoardSaving(String boardName) {
        if (soloPuzzleRepository.nameExists(boardName) ||
                !soloPuzzleRepository.addGame(boardName, 
        			new SoloGame(puzzle.getBoardTable(), targetPosition, boardType))) {
            return false;
        }
        saveNewBoardAsSelected();
        finish();
        return true;
    }
}
