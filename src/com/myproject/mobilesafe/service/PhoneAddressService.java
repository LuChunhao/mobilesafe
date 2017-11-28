package com.myproject.mobilesafe.service;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.AddressDBUtils;
import com.myproject.mobilesafe.myUtils.DBUtils;
import com.myproject.mobilesafe.myUtils.MyConstances;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneAddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private WindowManager mWM;

	private DBUtils dbu;

	private Context ctx;

	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		dbu = DBUtils.getInstance(this);

		ctx = this;

		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 注册打出去电话的监听
		outReceiver = new OutCallReceiver();
		registerReceiver(outReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(outReceiver);
		outReceiver = null;

		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}

	private OutCallReceiver outReceiver;

	/**
	 * 打出电话监听
	 */
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			showAddress(number);
		}

	}

	private final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

	private class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					mWM.removeView(view);
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				String modleType = dbu.getModleByNumber(incomingNumber);
				if (!"2".equals(modleType) || !"3".equals(modleType)) {
					// 显示来电归属地信息
					showAddress(incomingNumber);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			}
		}

	}

	private int[] itemValues = new int[] { R.drawable.call_locate_blue, R.drawable.call_locate_gray,
			R.drawable.call_locate_green, R.drawable.call_locate_orange, R.drawable.call_locate_white };

	private View view;

	/**
	 * 展示自定义Toast弹出框
	 * 
	 * @param incomingNumber
	 */
	private void showAddress(String incomingNumber) {
		String address = AddressDBUtils.getAddressByNumber(ctx, incomingNumber);
//		 Toast.makeText(ctx, address, 1).show();

		view = View.inflate(ctx, R.layout.toast_address, null);
		TextView tv_toast_address = (TextView) view.findViewById(R.id.tv_toast_address);
		tv_toast_address.setText(address);
		tv_toast_address.setTextColor(Color.RED);
		tv_toast_address.setTextSize(30);

		// 从sharedPreference获取样式
		int style = sp.getInt(MyConstances.ADDRESSSTYLE, 0);
		view.setBackgroundResource(itemValues[style]);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

		params.x = sp.getInt(MyConstances.ADDRESSX, 0);
		params.y = sp.getInt(MyConstances.ADDRESSY, 0);
		
		mWM.addView(view, params);

		view.setOnTouchListener(new OnTouchListener() {
			int startX ;
			int startY ;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int distanceX = (int) (event.getRawX() - startX);
					int distanceY = (int) (event.getRawY() - startY);
					
					params.x += distanceX;
					params.y += distanceY;
					
					mWM.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					sp.edit().putInt(MyConstances.ADDRESSX, params.x).commit();
					sp.edit().putInt(MyConstances.ADDRESSY, params.y).commit();
					break;
				}
				return true;
			}
		});
	}

}
