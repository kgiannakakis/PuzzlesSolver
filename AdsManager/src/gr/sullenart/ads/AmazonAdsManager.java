package gr.sullenart.ads;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;

public class AmazonAdsManager extends AdsNetwork implements AdListener {

	private static final String LOG_TAG = "AmazonAdsManager";
	
	private AdLayout adView;
	
	public AmazonAdsManager(Activity activity,
			AdsNetworkListener adsNetworklistener, String publisherId) {
		super(activity, adsNetworklistener, publisherId);

        // For debugging purposes enable logging, but disable for production builds
        AdRegistration.enableLogging(true);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds
        AdRegistration.enableTesting(true);
        
        //adView = (AdLayout)findViewById(R.id.ad_view);
        //adView.setListener(this);
        
        try {
            AdRegistration.setAppKey(publisherId);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception thrown: " + e.toString());
            return;
        }		
		
		type = AdsNetworkType.Amazon;
	}

    @Override
    public void onAdCollapsed(AdLayout view) {
        Log.d(LOG_TAG, "Ad collapsed.");
    }

    @Override
    public void onAdExpanded(AdLayout view) {
        Log.d(LOG_TAG, "Ad expanded.");
    }

    @Override
    public void onAdFailedToLoad(AdLayout view, AdError error) {
        Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
    }

    @Override
    public void onAdLoaded(AdLayout view, AdProperties adProperties) {
        Log.d(LOG_TAG, adProperties.getAdType().toString() + " Ad loaded successfully.");
        adsNetworkListener.onAdReceived(type);
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

}
