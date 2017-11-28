package com.myproject.mobilesafe.receiver;

import java.util.List;

import com.myproject.mobilesafe.myUtils.LogUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DealWidgetButtonReceiver extends BroadcastReceiver {
	
	private ActivityManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.i("DealWidgetButtonReceiver", "�����ť��");
		
		// �������
		am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

		for(RunningAppProcessInfo info : runningAppProcessInfo){
			am.killBackgroundProcesses(info.processName);
		}
	}

}
