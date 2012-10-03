package gr.sullenart.games.puzzles;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HighScoreDialog extends DialogFragment {
	
    public interface HighScoreDialogListener {
        void onFinishHighScoreDialog(String username);
    }
    
    private EditText playersNameEditText;
    
    public static DialogFragment newInstance(String playersName) {
        HighScoreDialog dialogFragment = new HighScoreDialog();
        Bundle args = new Bundle();
        args.putString("player", playersName);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.high_score_name_dialog);
        dialog.setTitle(R.string.app_name);

        Button okButton = (Button) dialog.findViewById(R.id.high_score_name_ok_button);

        playersNameEditText = (EditText) dialog.findViewById(R.id.players_name);
        playersNameEditText.setText(getArguments().getString("player"));

        okButton.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) {
        				String playerName = playersNameEditText.getText().toString();
                        HighScoreDialogListener listener = (HighScoreDialogListener) getActivity();
                        listener.onFinishHighScoreDialog(playerName);
                        dismiss();
        			}
        		});
        return dialog;	
    }

}
