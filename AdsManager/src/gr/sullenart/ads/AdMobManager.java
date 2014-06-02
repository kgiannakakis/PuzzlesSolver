package gr.sullenart.ads;

import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobManager extends AdsNetwork {
	
	private int adHeight = 0;
	
	private AdView adView = null;	
	
	private InterstitialAd interstitial;
	
	public AdMobManager(Activity activity,
			AdsNetworkListener adsNetworklistener, String publisherId) {
		super(activity, adsNetworklistener, publisherId);
		
		type = AdsNetworkType.AdMob;
	}
	
	public int getAdHeight() {
		return adHeight;
	}

	@Override
	public void addAdsView(ViewGroup layout) {
		addAdsView(layout, null);
	}
	
	@Override
	public void addAdsView(ViewGroup layout, RelativeLayout.LayoutParams params) {
        int screenLayout = activity.getResources().getConfiguration().screenLayout;
        if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= 4) {
        	adView = new AdView(activity);
			adView.setAdSize(AdSize.LEADERBOARD);
        	adView.setAdUnitId(publisherId);
            adHeight = 90; // 728x90 size for xlarge screens (>= 960dp x 720dp)
            Log.d("AdMobManager", "Adding LEADERBOARD " + 
            		((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)));
        }
        else if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 3) {
        	adView = new AdView(activity);
			adView.setAdSize(AdSize.BANNER);
        	adView.setAdUnitId(publisherId);        	
            adHeight = 75; // 468x60 size for large screens (>= 640dp x 480dp)
            Log.d("AdMobManager", "Adding BANNER " + 
            		((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)));            
        }        
        else {
        	adView = new AdView(activity);
			adView.setAdSize(AdSize.BANNER);
        	adView.setAdUnitId(publisherId); 
            adHeight = 75; // 320x50 size for normal (>= 470dp x 320dp) and small screens
            Log.d("AdMobManager", "Adding BANNER " + 
            		((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)));            
        }        
        if (params != null) {
        	layout.addView(adView, params);
        }
        else {
        	layout.addView(adView);
        }
        
        adView.setAdListener(new AdListener() {
        	  @Override
        	  public void onAdFailedToLoad(int errorCode) {
			      Log.e(AdMobManager.class.getName(), "Failed to receive ad (" + errorCode + ")");
			      adsNetworkListener.onAdFailed(type);
        	  }
        	  
        	  @Override
        	  public void onAdLoaded() {
        		  adsNetworkListener.onAdReceived(type);
        	  }
        	});        
        
        AdRequest request = new AdRequest.Builder()
        		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        		.addTestDevice("5EA5B374B0B6F1201A685AACAC300DDD")
        		.build();
        adView.loadAd(request);

        //Log.i(AdsManager.class.getName(), "Adding ads view");
	}
	
	@Override
	void removeAdsView(ViewGroup layout) {
        if (adView != null) {
            layout.removeView(adView);
            adHeight = 0;
            adView = null;
        }
	}	
	
	@Override
	public void showInterstitial(String adUnitId) {
		final String interstitialAdTimeKey = "InterstitialAdTime";
        final SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(activity.getBaseContext());
        long adTime = preferences.getLong(interstitialAdTimeKey, 0);        
        long now = (new Date()).getTime();
        long dayMs = 24*3600*1000;
        if (now - adTime < dayMs) {
        	return;
        }
		
	    interstitial = new InterstitialAd(activity);
	    interstitial.setAdUnitId(adUnitId);

	    // Create ad request.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
		
		interstitial.setAdListener(new AdListener() {
      	  @Override
      	  public void onAdLoaded() {
      		  try {
	      		  if (interstitial.isLoaded()) {
	      			  Editor edit = preferences.edit();
	      			  edit.putLong(interstitialAdTimeKey, (new Date()).getTime());
	      			  edit.commit();
	      			  interstitial.show();
	      		  }    
      		  }
      		  catch(Exception ex) {
      			  
      		  }
      	  }			
		});
	}	

}