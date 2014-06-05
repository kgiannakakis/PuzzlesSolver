package gr.sullenart.games.puzzles;

import gr.sullenart.ads.AdsManager;
import gr.sullenart.ads.AdsNetworkType;
import gr.sullenart.games.puzzles.dialogs.AboutDialog;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PuzzlesCollection extends FragmentActivity {

	private final int START_GAME_ACTION = 0;
	private final int SHOW_SCORES_ACTION = 1;
	private int action;

	private PopupWindow gameSelectPopupWindow = null;
	
	private AdsManager adsManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button newGameButton = (Button) findViewById(R.id.newGameButton);
        final Button scoresButton = (Button) findViewById(R.id.scoresButton);
        Button helpButton = (Button) findViewById(R.id.helpButton);
        Button aboutButton = (Button) findViewById(R.id.aboutButton);

        newGameButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				action = START_GAME_ACTION;
				showGameSelectPopup((View) newGameButton);
			}
		});

        helpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent  = new Intent("gr.sullenart.games.puzzles.WEB_VIEW");
				startActivity(intent);
			}
		});

        
        aboutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	            FragmentManager fm = getSupportFragmentManager();
	            AboutDialog aboutDialog = new AboutDialog();            
	            aboutDialog.show(fm, "about_dialog");
			}
		});        
        
        scoresButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				action = SHOW_SCORES_ACTION;
				showGameSelectPopup((View) scoresButton);
			}
		});

        LinearLayout layout = (LinearLayout)findViewById(R.id.banner_layout);
        
        adsManager = new AdsManager(this, layout);
        adsManager.addNetwork(AdsNetworkType.AdMob);
        adsManager.addNetwork(AdsNetworkType.MobFox);
        adsManager.startShowingAds();
        adsManager.showInterstitialAd(getString(R.string.admob_intestitial_ad_unit_id));
    }

    @Override
    public void onStop() {
    	if (gameSelectPopupWindow != null) {
    		gameSelectPopupWindow.dismiss();
    	}
    	super.onStop();
    }
    
    private void showGameSelectPopup(View parentView) {
		LayoutInflater inflater = (LayoutInflater)
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.puzzle_select, null, false);
		
		gameSelectPopupWindow = new PopupWindow(this);
		gameSelectPopupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					gameSelectPopupWindow.dismiss();
					gameSelectPopupWindow = null;
					return true;
				}
				return false;
			}
		});
		gameSelectPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		gameSelectPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		gameSelectPopupWindow.setTouchable(true);
		gameSelectPopupWindow.setFocusable(true);
		gameSelectPopupWindow.setOutsideTouchable(true);
		gameSelectPopupWindow.setContentView(layout);
		
		int i = 0;
		final Map<View, Integer> gameButtonsMap = new HashMap<View, Integer>();
		gameButtonsMap.put(layout.findViewById(R.id.queens_select), i++);
		gameButtonsMap.put(layout.findViewById(R.id.knights_tour_select), i++);
		gameButtonsMap.put(layout.findViewById(R.id.solo_select), i++);
		gameButtonsMap.put(layout.findViewById(R.id.lights_select), i++);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int gameType = gameButtonsMap.get(v);
				gameSelectPopupWindow.dismiss();
				if (action == START_GAME_ACTION) {
					Intent intent  = new Intent("gr.sullenart.games.puzzles.PUZZLES");
					intent.putExtra("GameType", gameType);
					startActivity(intent);
				}
				else if (action == SHOW_SCORES_ACTION) {
					Intent intent  = new Intent("gr.sullenart.games.puzzles.SCORES");
					intent.putExtra("GameType", gameType);
					startActivity(intent);
				}
			}
		};
		for(View view: gameButtonsMap.keySet()) {
			view.setOnClickListener(clickListener);
		}
		
		int [] location = new int [] {0, 0};
		parentView.getLocationInWindow(location);
		int w = parentView.getWidth();
		// Pop-up width is 1.5x the size of the button
		int x = location[0] - w/4;
		int y = location[1] + parentView.getHeight();
		
		gameSelectPopupWindow.setAnimationStyle(R.style.AnimationPopup);
		gameSelectPopupWindow.showAtLocation(layout, Gravity.NO_GRAVITY, x, y);     	
    }   

    /*private Dialog createAboutDialog() {
        aboutDialog = new Dialog(this);
        aboutDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        aboutDialog.setContentView(R.layout.about_dialog);
        aboutDialog.setTitle(R.string.app_name);

        Button button = (Button) aboutDialog.findViewById(R.id.help_ok_button);

        button.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) {
        				aboutDialog.dismiss();
        			}
        		});

        return aboutDialog;
    }*/

}