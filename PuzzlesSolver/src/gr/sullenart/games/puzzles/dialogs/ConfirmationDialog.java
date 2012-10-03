package gr.sullenart.games.puzzles.dialogs;

import gr.sullenart.games.puzzles.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmationDialog extends DialogFragment {
	
    public interface ConfirmationDialogListener {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
    }
    
    public static DialogFragment newInstance(String questionMessage) {
        ConfirmationDialog dialogFragment = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString("question", questionMessage);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setMessage(getArguments().getString("question"))
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
                        ConfirmationDialogListener listener = (ConfirmationDialogListener) getActivity();
                        listener.onPositiveButtonClicked();
    	           }
    	       })
    	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
                        ConfirmationDialogListener listener = (ConfirmationDialogListener) getActivity();
                        listener.onNegativeButtonClicked();                   
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	return alert;  	
    }

}
