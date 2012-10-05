package gr.sullenart.games.puzzles.dialogs;

import gr.sullenart.games.puzzles.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class InstructionsDialog extends DialogFragment {
	
    public interface InstructionsDialogListener {
        void onShowInstructionsChecked(boolean isChecked);
    }
    
    public static DialogFragment newInstance(String title, String instructions, int icon) {
        InstructionsDialog dialogFragment = new InstructionsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("instructions", instructions);
        args.putInt("icon", icon);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Dialog instructionsDialog = new Dialog(getActivity());
    	instructionsDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
    	instructionsDialog.setContentView(R.layout.instructions_dialog);
    	instructionsDialog.setTitle(getArguments().getString("title"));
        instructionsDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
                                               getArguments().getInt("icon"));
    	
        Button okButton = (Button) instructionsDialog.findViewById(R.id.instructions_ok_button);

        CheckBox instructionsCheckbox = (CheckBox)
        	instructionsDialog.findViewById(R.id.show_instructions_again);
        
        ((TextView) instructionsDialog.findViewById(R.id.instructions_text)).setText(
                                getArguments().getString("instructions"));
        
        instructionsCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        ((InstructionsDialogListener) getActivity()).onShowInstructionsChecked(isChecked);
			}
		});
        
        okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
        });
 	
    	return instructionsDialog;
    }

}
