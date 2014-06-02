package gr.sullenart.ads;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.adsdk.sdk.Ad;
import com.adsdk.sdk.AdListener;
import com.adsdk.sdk.banner.AdView;

public class MobFoxManager extends AdsNetwork implements AdListener {

	//private AdManager adManager;
	
	public MobFoxManager(Activity activity,
			AdsNetworkListener adsNetworklistener, String publisherId) {
		super(activity, adsNetworklistener, publisherId);
		
		//adManager = new AdManager(activity, "http://my.mobfox.com/vrequest.php",
		//		publisherId, true);
		//adManager.setListener(this);
		
		type = AdsNetworkType.MobFox;
	}
	
	@Override
	public void addAdsView(ViewGroup layout, RelativeLayout.LayoutParams params) {
		AdView mAdView = new AdView(activity, "http://my.mobfox.com/request.php",
				publisherId, true, true);
		mAdView.setAdListener(this);
		layout.addView(mAdView, params);		
	}

	@Override
	public void addAdsView(ViewGroup layout) {
		AdView mAdView = new AdView(activity, "http://my.mobfox.com/request.php",
				publisherId, true, true);
		mAdView.setAdListener(this);
		layout.addView(mAdView);		
	}
	
	@Override
	void removeAdsView(ViewGroup layout) {
		// TODO Auto-generated method stub
		
	}	
	
	@Override
	public void adClicked() {
		
	}

	@Override
	public void adClosed(Ad arg0, boolean arg1) {
		
	}

	@Override
	public void adLoadSucceeded(Ad arg0) {
		Log.d("MobFoxManager", "Ad loaded");
		adsNetworkListener.onAdReceived(type);
	}

	@Override
	public void adShown(Ad arg0, boolean arg1) {
		Log.d("MobFoxManager", "Ad shown");
	}

	@Override
	public void noAdFound() {
		Log.d("MobFoxManager", "No Ad found");
		adsNetworkListener.onAdFailed(type);
	}

	public int getAdHeight() {
		return 0;
	}
}
