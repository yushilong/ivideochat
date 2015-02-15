package net.john.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectUtil {
	
	private ConnectivityManager cm;
	
	public NetworkConnectUtil(Context context) {
		cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
	}
	
	public boolean checkGprsState() {
		
		boolean network_state = false;
		network_state = cm.getNetworkInfo(cm.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ? true:false;
		return network_state;
	}
	
	public boolean checkWifiState() {
		
		boolean network_state = false;
		network_state = cm.getNetworkInfo(cm.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED? true:false;
		return network_state;
	}
}
