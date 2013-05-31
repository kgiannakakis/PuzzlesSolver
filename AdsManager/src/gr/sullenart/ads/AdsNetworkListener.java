package gr.sullenart.ads;

public interface AdsNetworkListener {

	void onAdReceived(AdsNetworkType adsNetworkType);
	
	void onAdFailed(AdsNetworkType adsNetworkType);
}
