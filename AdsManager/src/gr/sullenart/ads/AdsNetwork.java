package gr.sullenart.ads;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public abstract class AdsNetwork {

	protected String publisherId;
	
	protected Activity activity;
	
	protected AdsNetworkListener adsNetworkListener;
	
	protected AdsNetworkType type;
	
	public AdsNetworkType getType() {
		return type;
	}

	public AdsNetwork(Activity activity, AdsNetworkListener adsNetworklistener,
					  String publisherId) {
		this.activity = activity;
		this.adsNetworkListener = adsNetworklistener;
		this.publisherId = publisherId;
	}
	
	abstract void addAdsView(ViewGroup layout, RelativeLayout.LayoutParams params);
	
	abstract void addAdsView(ViewGroup layout);
	
	abstract void removeAdsView(ViewGroup layout);
	
	abstract int getAdHeight();
	
	public void destroy() {
		
	}
	
	public void loadInterstitial(String adUnitId) {
		
	}	
	
	public void showInterstitial(String adUnitId) {
		
	}
}
