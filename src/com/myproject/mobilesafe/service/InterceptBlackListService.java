package com.myproject.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.myproject.mobilesafe.myUtils.AddressDBUtils;
import com.myproject.mobilesafe.myUtils.DBUtils;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class InterceptBlackListService extends Service {

	private DBUtils dbu;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private InterceptSmsReceiver interceptSmsReceiver;
	
	private TelephonyManager tm;
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d("service", "服务开启");
		//拦截短信
		interceptSmsReceiver = new InterceptSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(interceptSmsReceiver, filter);
		
		dbu = DBUtils.getInstance(this);
		
		//挂断电话
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(interceptSmsReceiver);
		interceptSmsReceiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	
	private class InterceptSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for(Object object : objects){
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) object);
				String address = msg.getDisplayOriginatingAddress();
				String body = msg.getDisplayMessageBody();
				
				String modleStr = dbu.getModleByNumber(address);
				if("1".equals(modleStr) || "3".equals(modleStr)){
					//拦截短信
					LogUtil.i("InterceptSmsReceiver", address + ":" + body);
					abortBroadcast();
				}
			}
			
		}
		
	}
	private MyListener listener;
	//电话监听
	private class MyListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				//空闲
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//挂断
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//响铃
				String modleType = dbu.getModleByNumber(incomingNumber);
				if("2".equals(modleType) || "3".equals(modleType)){
					// 挂断电话前先添加监听删除通话记录
					Uri uri = Uri.parse("content://call_log/calls");
					observer = new MyLogObserver(new Handler(), incomingNumber);
					getContentResolver().registerContentObserver(uri, true, observer);
					// 挂断电话
					endCall();
				}else{
					// 显示来电归属地信息
//					String address = AddressDBUtils.getAddressByNumber(InterceptBlackListService.this, incomingNumber);
//					Toast.makeText(InterceptBlackListService.this, address,1).show();
				}
				
				break;
			default:
				break;
			}
		}
	}
	
	private MyLogObserver observer;
	/**
	 * 内容观察者监听通话记录变化
	 */
	private class MyLogObserver extends ContentObserver{
		private String incomingNumber;
		public MyLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			Uri uri = Uri.parse("content://call_log/calls");
			getContentResolver().delete(uri, "number = ?", new String[]{incomingNumber});
			
			getContentResolver().unregisterContentObserver(observer);
		}
	}
	
	/**
	 * 拦截电话
	 */
	public void endCall() {
		// TODO Auto-generated method stub
		try {
			Class clazz = getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
			itelephony.endCall();
			//开通呼叫转移 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
