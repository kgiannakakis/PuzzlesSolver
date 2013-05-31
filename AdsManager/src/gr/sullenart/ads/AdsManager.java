package gr.sullenart.ads;

import gr.sullenart.adsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

public class AdsManager implements AdsNetworkListener {

	private List<AdsNetwork> networks;
	
	private Map<AdsNetworkType, Integer> networksMap;
	
	private ViewGroup adLayout;
	
	private Activity activity;
	
	private AdsNetworkType activeNetwork = AdsNetworkType.None;
	
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
		default:
			return;
		}
		networksMap.put(type, networks.size() - 1);
	}
	
	public void startShowingAds() {
		if (networks.size() > 0) {
			AdsNetwork adsNetwork = networks.get(0);
			adsNetwork.addAdsView(adLayout);
		}
	}
	
	public void stopShowingAds() {
		if (activeNetwork != AdsNetworkType.None) {
			int networkIndex = networksMap.get(activeNetwork);
			networks.get(networkIndex).removeAdsView(adLayout);
			activeNetwork = AdsNetworkType.None;
		}
	}
	
	@Override
	public void onAdReceived(AdsNetworkType adsNetworkType) {
		activeNetwork = adsNetworkType;
	}

	@Override
	public void onAdFailed(AdsNetworkType adsNetworkType) {
		int networkIndex = networksMap.get(adsNetworkType);
		int nextNetworkIndex = networkIndex + 1;
		if (nextNetworkIndex < networks.size()) {
			networks.get(nextNetworkIndex).addAdsView(adLayout);
		}
	}

}
