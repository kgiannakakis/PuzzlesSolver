package gr.sullenart.ads;

import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdProperties.AdType;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.amazon.device.ads.InterstitialAd;

public class AmazonAdsManager extends AdsNetwork implements AdListener {

	private static final String LOG_TAG = "AmazonAdsManager";
	
	private AdLayout adView;
	
	private InterstitialAd interstitialAd;
	
	private String appKey;
	
	final private String interstitialAdTimeKey = "InterstitialAdTime";
	
	public AmazonAdsManager(Activity activity,
			AdsNetworkListener adsNetworklistener, String publisherId) {
		super(activity, adsNetworklistener, publisherId);

		appKey = publisherId;
		
        // For debugging purposes enable logging, but disable for production builds
        AdRegistration.enableLogging(false);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds
        AdRegistration.enableTesting(false);
        
        //adView = (AdLayout)findViewById(R.id.ad_view);
        //adView.setListener(this);
        
		interstitialAd = new InterstitialAd(activity);
        interstitialAd.setListener(this);        
        
        try {
            AdRegistration.setAppKey(publisherId);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception thrown: " + e.toString());
            return;
        }		
		
		type = AdsNetworkType.Amazon;
	}

    @Override
    public void onAdCollapsed(Ad ad) {
        Log.d(LOG_TAG, "Ad collapsed.");
    }

	@Override
	public void onAdDismissed(Ad arg0) {
		Log.d(LOG_TAG, "Ad dismissed.");
	}
    
    
    @Override
    public void onAdExpanded(Ad arg0) {
        Log.d(LOG_TAG, "Ad expanded.");
    }

    @Override
    public void onAdFailedToLoad(Ad arg0, AdError error) {
        Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
    }

    @Override
    public void onAdLoaded(Ad arg0, AdProperties adProperties) {
        Log.d(LOG_TAG, adProperties.getAdType().toString() + " Ad loaded successfully.");
        adsNetworkListener.onAdReceived(type);
        if (adProperties.getAdType() == AdType.INTERSTITIAL) {
        	SharedPreferences preferences = PreferenceManager
	        		.getDefaultSharedPreferences(activity.getBaseContext());        	
			Editor edit = preferences.edit();
			edit.putLong(interstitialAdTimeKey, (new Date()).getTime());
			edit.commit();        	
			showInterstitial(publisherId);
        }
    }

	@Override
	void addAdsView(ViewGroup layout) {
        AdTargetingOptions adOptions = new AdTargetingOptions();
        AdLayout adView = new AdLayout(activity);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
											   LayoutParams.WRAP_CONTENT);	
		layout.addView(adView, params);
        adView.setListener(this);
		adView.loadAd(adOptions);			
	}
	
	@Override
	void addAdsView(ViewGroup layout, RelativeLayout.LayoutParams params) {
        AdTargetingOptions adOptions = new AdTargetingOptions();
        AdLayout adView = new AdLayout(activity);	
		layout.addView(adView, params);
        adView.setListener(this);
		adView.loadAd(adOptions);			
	}	

	@Override
	void removeAdsView(ViewGroup layout) {
        if (adView != null) {
            layout.removeView(adView);
            adView = null;
        }
	}

	@Override
	int getAdHeight() {
		return 0;
	}

	@Override
	public void destroy() {
		if (adView != null) {
			adView.destroy();
		}
	}
	
	@Override
	public void loadInterstitial(String adUnitId) {	
        final SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(activity.getBaseContext());
        long adTime = preferences.getLong(interstitialAdTimeKey, 0);        
        long now = (new Date()).getTime();
        long dayMs = 24*3600*1000;
        if (now - adTime < dayMs) {
        	return;
        }		
		
        interstitialAd.loadAd();
	}	

	@Override
	public void showInterstitial(String adUnitId) {
		
	}	
	
}
