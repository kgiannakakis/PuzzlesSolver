package gr.sullenart.ads;

import gr.sullenart.adsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AdsManager implements AdsNetworkListener {

	private List<AdsNetwork> networks;
	
	private Map<AdsNetworkType, Integer> networksMap;
	
	private ViewGroup adLayout;
	
	private Activity activity;
	
	private AdsNetworkType activeNetwork = AdsNetworkType.None;
	
	private AdsNetworkListener adsNetworkListener;
	
	private RelativeLayout.LayoutParams params = null;
	
	public void setAdsNetworkListener(AdsNetworkListener adsNetworkListener) {
		this.adsNetworkListener = adsNetworkListener;
	}

	public AdsManager(Activity activity, ViewGroup adLayout, RelativeLayout.LayoutParams params) {
		this.activity = activity;
		this.adLayout = adLayout;
		this.params = params;
		
		networks = new ArrayList<AdsNetwork>();
		networksMap = new HashMap<AdsNetworkType, Integer>();
	}
	
	public AdsManager(Activity activity, ViewGroup adLayout) {
		this.activity = activity;
		this.adLayout = adLayout;
		
		networks = new ArrayList<AdsNetwork>();
		networksMap = new HashMap<AdsNetworkType, Integer>();
	}	
	
	public void addNetwork(AdsNetworkType type) {
		switch(type) {
		case AdMob:
			String adMobPublisherId = activity.getString(R.string.admob_publisher_id);
			
			Log.d(AdsManager.class.getName(), "AdMob: " + adMobPublisherId);
			
			AdMobManager adMobManager = 
				new AdMobManager(activity, this, adMobPublisherId);
			networks.add(adMobManager);
			break;
		case MobFox:
			String mobFoxPublisherId = activity.getString(R.string.mobfox_publisher_id);
			
			Log.d(AdsManager.class.getName(), "MobFox: " + mobFoxPublisherId);
			
			MobFoxManager mobFoxManager = 
				new MobFoxManager(activity, this, mobFoxPublisherId);
			networks.add(mobFoxManager);			
			break;
		case Amazon:
			String amazonPublisherId = activity.getString(R.string.amazon_publisher_id);
			
			Log.d(AdsManager.class.getName(), "Amazon: " + amazonPublisherId);	
			
			
			AmazonAdsManager amazonAdsManager = 
				new AmazonAdsManager(activity, this, amazonPublisherId);
			networks.add(amazonAdsManager);			
			break;
		default:
			return;
		}
		networksMap.put(type, networks.size() - 1);
	}
	
	public void startShowingAds() {
		if (networks.size() > 0) {
			AdsNetwork adsNetwork = networks.get(0);
			if (params != null) {
				adsNetwork.addAdsView(adLayout, params);
			}
			else {
				adsNetwork.addAdsView(adLayout);
			}
		}
	}
	
	public void stopShowingAds() {
		if (activeNetwork != AdsNetworkType.None) {
			int networkIndex = networksMap.get(activeNetwork);
			networks.get(networkIndex).destroy();
			networks.get(networkIndex).removeAdsView(adLayout);
			activeNetwork = AdsNetworkType.None;
		}
	}
	
	public int getAdHeight() {
		if (activeNetwork != AdsNetworkType.None) {
			int networkIndex = networksMap.get(activeNetwork);
			return networks.get(networkIndex).getAdHeight();
		}
		else if (networks.size() > 0) {
			return networks.get(0).getAdHeight();
		}
		return 0;
	}
	
	public void loadInterstitialAd(String adUnitId) {
		if (networks.size() > 0) {
			AdsNetwork adsNetwork = networks.get(0);
			adsNetwork.loadInterstitial(adUnitId);
		}		
	}	
	
	public void showInterstitialAd(String adUnitId) {
		if (networks.size() > 0) {
			AdsNetwork adsNetwork = networks.get(0);
			adsNetwork.showInterstitial(adUnitId);
		}		
	}
	
	@Override
	public void onAdReceived(AdsNetworkType adsNetworkType) {
		activeNetwork = adsNetworkType;
		
		if (adsNetworkListener != null) {
			adsNetworkListener.onAdReceived(adsNetworkType);
		}
	}

	@Override
	public void onAdFailed(AdsNetworkType adsNetworkType) {
		int networkIndex = networksMap.get(adsNetworkType);
		int nextNetworkIndex = networkIndex + 1;
		if (nextNetworkIndex < networks.size()) {
			networks.get(nextNetworkIndex).addAdsView(adLayout);
		}
		
		if (adsNetworkListener != null) {
			adsNetworkListener.onAdFailed(adsNetworkType);
		}
	}

}
