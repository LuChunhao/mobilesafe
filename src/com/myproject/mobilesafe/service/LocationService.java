package com.myproject.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.ModifyOffset;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.myproject.mobilesafe.myUtils.PointDouble;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	private LocationManager lm;

	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精度
		criteria.setCostAllowed(true); // 产生费用
		String provider = lm.getBestProvider(criteria, true);
		
//		String provider = null;
//		List<String> providers = lm.getAllProviders();
//		if(providers.contains(lm.GPS_PROVIDER)){
//			provider = lm.GPS_PROVIDER;
//		}else if(providers.contains(lm.NETWORK_PROVIDER)){
//			provider = lm.NETWORK_PROVIDER;
//		}
		
		//LogUtil.d("loaction----------", provider);
		lm.requestLocationUpdates(provider, 0, 0, new MyLocationListener());

	}

	public class MyLocationListener implements LocationListener {

		private static final String TAG = "MyLocationListener";

		@Override
		public void onLocationChanged(Location location) {
			StringBuilder sb = new StringBuilder();
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			float accuracy = location.getAccuracy();
			
			//火星坐标转换
			try {
				InputStream is = getAssets().open("axisoffset.dat");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				
				PointDouble spt = new PointDouble(longitude, latitude);
				PointDouble cpt = offset.s2c(spt);
				longitude = cpt.x;
				latitude = cpt.y;
			} catch (Exception e) {
				e.printStackTrace();
			}
			sb.append("longitude:" + longitude);
			sb.append("\n latitude" + latitude);
			sb.append("\n accuracy" + accuracy);
			LogUtil.i(TAG, sb.toString());
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(sp.getString(MyConstances.SAFENUMBER, null), null, sb.toString(), null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}

}
