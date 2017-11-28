package com.myproject.mobilesafe.provider;

import com.myproject.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyAppWidgetProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
	}
	
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
	}
}
