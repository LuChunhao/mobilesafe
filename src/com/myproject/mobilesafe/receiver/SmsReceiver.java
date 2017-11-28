package com.myproject.mobilesafe.receiver;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.service.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony.Sms;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";
	
	private SharedPreferences sp;
	
	private DevicePolicyManager dpm;
	private ComponentName mDeviceAdminSample;

	@Override
	public void onReceive(Context context, Intent intent) {
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		sp = context.getSharedPreferences(MyConstances.CONFIG, Context.MODE_PRIVATE);
		String safeNum = sp.getString(MyConstances.SAFENUMBER, null);
		
		LogUtil.i(TAG, "收到短信了");
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
			String address = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			
			if ("#*location*#".equals(body)) {
				// 给安全号码发送位置信息
				Intent service = new Intent(context,LocationService.class);
				context.startService(service);
				abortBroadcast();
				myAbordBroadcast(context);
			} else if ("#*alarm*#".equals(body)) {
				// 播放音乐
				MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
				mp.setVolume(1f, 1f);
				mp.setLooping(false);
				mp.start();
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				// 清除数据
				dpm.wipeData(0);
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {
				// 远程锁屏
				//mDeviceAdminSample = new ComponentName(context, cls);
				
				dpm.resetPassword("123", 0);
				dpm.lockNow();
				abortBroadcast();
			}
		}

	}
	
	private boolean isActiveAdmin() {
	    return dpm.isAdminActive(mDeviceAdminSample);
	}
	
	public void myAbordBroadcast(final Context context){
		ContentObserver mObserver = new ContentObserver(new Handler()) {

			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				ContentResolver resolver = context.getContentResolver();
				Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"), new String[] { "_id", "address", "body" }, null, null, "_id desc");
				long id = -1;

				if (cursor.getCount() > 0 && cursor.moveToFirst()) {
					id = cursor.getLong(0);
					String address = cursor.getString(1);
					String body = cursor.getString(2);
					LogUtil.d("test=======", String.format("address: %s\n body: %s", address, body));
				}
				cursor.close();

				if (id != -1) {
					int count = resolver.delete(Sms.CONTENT_URI, "_id=" + id, null);
					LogUtil.d("_id=====", id+"");
					//int count = resolver.delete(Sms.CONTENT_URI, "_id=?" , new String[]{id+""});
					LogUtil.d("test=======", count == 1 ? "删除成功" : "删除失败");
				}
			}

			};

			context.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mObserver);
	}

}
