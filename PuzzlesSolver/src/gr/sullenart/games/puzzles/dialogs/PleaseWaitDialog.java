package gr.sullenart.games.puzzles.dialogs;

import gr.sullenart.games.puzzles.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PleaseWaitDialog extends DialogFragment {
	
    public interface PleaseWaitDialogListener {
        void onCancelButtonClicked();
    }
    
    public static DialogFragment newInstance(String message) {
        PleaseWaitDialog dialogFragment = new PleaseWaitDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog pleaseWaitDialog = new Dialog(getActivity());
        pleaseWaitDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        pleaseWaitDialog.setContentView(R.layout.please_wait_dialog);
        pleaseWaitDialog.setTitle(R.string.app_name);
        pleaseWaitDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
        setCancelable(false);

        Button button = (Button) pleaseWaitDialog.findViewById(R.id.cancel_button);
        TextView messageText = (TextView) pleaseWaitDialog.findViewById(R.id.messageTextView);
        messageText.setText(getArguments().getString("message"));
        
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PleaseWaitDialogListener listener = (PleaseWaitDialogListener) getActivity();
                listener.onCancelButtonClicked();
                dismiss();
            }
        });

        return pleaseWaitDialog; 	
    }

}
