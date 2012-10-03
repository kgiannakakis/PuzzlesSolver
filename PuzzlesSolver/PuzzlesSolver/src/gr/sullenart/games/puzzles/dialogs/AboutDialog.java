package gr.sullenart.games.puzzles.dialogs;

import gr.sullenart.games.puzzles.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AboutDialog extends DialogFragment {
	
    public AboutDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog aboutDialog = new Dialog(getActivity());
        aboutDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        aboutDialog.setContentView(R.layout.about_dialog);
        aboutDialog.setTitle(R.string.app_name);
        aboutDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

        Button button = (Button) aboutDialog.findViewById(R.id.help_ok_button);

        button.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) {
        				dismiss();
        			}
        		});

        return aboutDialog;    	
    }

}
