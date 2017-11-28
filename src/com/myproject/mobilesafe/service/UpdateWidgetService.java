package com.myproject.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.SystemInfoUtil;
import com.myproject.mobilesafe.provider.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	
	private Context ctx;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private Timer timer;
	private TimerTask timeTask;
	protected AppWidgetManager awm;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ctx = getApplicationContext();
		awm = AppWidgetManager.getInstance(this);
		// ��ʱˢ��widget����
		timer = new Timer();
		timeTask = new TimerTask() {
			
			@Override
			public void run() {
				ComponentName provider = new ComponentName(ctx, MyAppWidgetProvider.class);

				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				
				views.setTextViewText(R.id.process_count, "�����н��̣�" + SystemInfoUtil.getAllRunningProcessCont(ctx)+"��");
				views.setTextViewText(R.id.process_memory, "�����ڴ棺" + Formatter.formatFileSize(ctx, SystemInfoUtil.getSystemFreeMemory(ctx)));
				
				// ���͹㲥����ť����¼�
				Intent intent = new Intent();
				intent.setAction("com.myproject.mobilesafe.cleanprocess");
				
				PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 26, intent, PendingIntent.FLAG_ONE_SHOT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent );
				
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(timeTask, 0, 10000);
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
