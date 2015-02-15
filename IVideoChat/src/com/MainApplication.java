package com;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class MainApplication extends Application {
	private static Application _instance;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_instance = this;
	}

	public static Application getInstance() {
		return _instance;
	}

	/**
	 * 判断当前设备是手机还是TV
	 * 
	 * @return
	 */
	public static boolean isPhone() {
		TelephonyManager telephony = (TelephonyManager) _instance
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			return false;
		}
		return true;
	}
}
