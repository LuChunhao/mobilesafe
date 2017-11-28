package com.myproject.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.activity.InputPasswordActivity;
import com.myproject.mobilesafe.db.LockAppDBUtil;
import com.myproject.mobilesafe.myUtils.LogUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class WatchDogService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private ActivityManager am;

	private boolean isRunning = true;
	
	private List<String> allLockedApp;
	
	/**
	 * �Ѿ�������ȷ�����app
	 */
	private List<String> noLockedList;
	
	private LockAppDBUtil ldbu;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i("doggggggggg", "������");
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		// ע�����ݹ۲���
		Uri uri = Uri.parse("content://com.mypooject.lockapp.changed");
		getContentResolver().registerContentObserver(uri , true, observer);
		
		// ע��㲥
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.myproject.applock.locked");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addCategory(Intent.ACTION_DEFAULT);
		registerReceiver(receiver, filter );
		
		noLockedList = new ArrayList<String>();
		
		ldbu = LockAppDBUtil.getInstance(this);
		allLockedApp = ldbu.getAllLockedAppInfo();
		
		startDog();

	}

	private void startDog() {
		new Thread() {
			public void run() {
				
				while (isRunning) {
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					ComponentName topActivity = runningTaskInfo.topActivity;
					String packageName = topActivity.getPackageName();
					//LogUtil.i("name", packageName);
					try {
						if(allLockedApp.contains(packageName)){
							//LogUtil.i("----", packageName);
							// ������д�������,������ڽ����б��������´������������
							if(noLockedList.contains(packageName)){
								LogUtil.i("----", "�Ѿ�������");
							}else{
								Intent intent = new Intent(WatchDogService.this,InputPasswordActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("packageName", packageName);
								startActivity(intent);
							}
						}
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	
	private WatchDogReceiver receiver;
	
	private class WatchDogReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.myproject.applock.locked")){
				String packageName = intent.getStringExtra("packageName");
				LogUtil.i("111111", packageName);
				noLockedList.add(packageName);
			}else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
				noLockedList.clear();
				isRunning = false;
			}else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				isRunning = true;
				startDog();
			}
			
		}
		
	}
	
	/**
	 * ���ݹ۲��ߣ�������ݿ�仯
	 */
	private ContentObserver observer = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			allLockedApp = ldbu.getAllLockedAppInfo();
		};
	};

	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(observer);
		unregisterReceiver(receiver);
		receiver = null;
		observer = null;
		super.onDestroy();
	}

}
