package com.newandbie.fake.zhihudaily;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;

/**
 * 用来监听网络状态
 * 
 * @author ray
 * 
 */
public class ZhihuNetworkStateBroadcastReceiver extends BroadcastReceiver {
	public static final IntentFilter NET_STATE_FILTER = new IntentFilter(
			ConnectivityManager.CONNECTIVITY_ACTION);

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo ni = ConnectivityManagerCompat
				.getNetworkInfoFromBroadcast((ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE), intent);
		Intent notifyIntent = new Intent();
		if (ni.isConnected()) {
			notifyIntent.setAction(ZhihuService.ACTION_BROADCAST_CONNECTED);
		} else {
			notifyIntent.setAction(ZhihuService.ACTION_BROADCAST_DISCONNECTED);
		}
		context.startService(notifyIntent);
	}

}
