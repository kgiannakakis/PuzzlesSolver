package gr.sullenart.games.puzzles.dialogs;

import gr.sullenart.games.puzzles.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaveBoardDialog extends DialogFragment {
	
    public interface SaveBoardDialogListener {
        boolean onBoardSaving(String name);
    }
    
    private Dialog saveDialog;
    
    public static DialogFragment newInstance() {
        SaveBoardDialog dialogFragment = new SaveBoardDialog();
        return dialogFragment;
    }    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        saveDialog = new Dialog(getActivity());
    	saveDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        saveDialog.setContentView(R.layout.new_board_dialog);
        saveDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
        saveDialog.setTitle(R.string.app_name);

        Button okButton = (Button) saveDialog.findViewById(R.id.new_board_ok_button);

        okButton.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) {
        				String boardName = ((EditText) saveDialog.findViewById(R.id.board_name_prompt))
        										.getText().toString();
        				if (boardName == null || boardName.length() == 0) {
        					TextView boardErrorText = (TextView) saveDialog.findViewById(R.id.add_board_error);
        					boardErrorText.setVisibility(View.VISIBLE);
        					boardErrorText.setText(R.string.name_cant_be_empty);
        				}
                        else {
                            boolean result = ((SaveBoardDialogListener) getActivity()).onBoardSaving(boardName);
                            if (result) {
                                dismiss();
                            }
                            else {
                                TextView boardErrorText = (TextView) saveDialog.findViewById(R.id.add_board_error);
                                boardErrorText.setVisibility(View.VISIBLE);
                                boardErrorText.setText(R.string.name_already_in_use);
                            }
                        }
        			}
        		});         
        return saveDialog; 
    }

}
