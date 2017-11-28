package com.myproject.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CleanProcessByScreenOffService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		receiver = new ScreenOffReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addCategory(Intent.ACTION_DEFAULT);
		registerReceiver(receiver, filter );
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	private ScreenOffReceiver receiver;
	
	private ActivityManager am;
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// 开始清理后台进程
			am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : runningAppProcessInfoList) {
				String processName = info.processName; // 返回包名及system
				am.killBackgroundProcesses(processName);
			}
		}
		
	}

}
