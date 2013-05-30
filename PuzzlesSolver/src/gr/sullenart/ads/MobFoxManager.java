package gr.sullenart.ads;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.adsdk.sdk.Ad;
import com.adsdk.sdk.AdListener;
import com.adsdk.sdk.AdManager;
import com.adsdk.sdk.banner.AdView;

public class MobFoxManager implements AdListener {

	private String publisherId = "2a9a84547bc55c6e0592f631f63416a3";
	private AdManager adManager;
	private Activity activity;
	
	public MobFoxManager(Activity activity) {
		this.activity = activity;
		adManager = new AdManager(activity, "http://my.mobfox.com/vrequest.php",
				publisherId, true);
		adManager.setListener(this);		
	}
	
	public void addAdsView(ViewGroup layout) {
		AdView mAdView = new AdView(activity, "http://my.mobfox.com/request.php",
				publisherId, true, true);
		mAdView.setAdListener(this);
		layout.addView(mAdView);		
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
	}

	@Override
	public void adShown(Ad arg0, boolean arg1) {
		Log.d("MobFoxManager", "Ad shown");
	}

	@Override
	public void noAdFound() {
		Log.d("MobFoxManager", "No Ad found");
		
	}

}
