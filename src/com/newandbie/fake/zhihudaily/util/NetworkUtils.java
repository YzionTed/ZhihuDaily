package com.newandbie.fake.zhihudaily.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo ni = connMgr.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		}

		return ni.isConnected();
	}
}
