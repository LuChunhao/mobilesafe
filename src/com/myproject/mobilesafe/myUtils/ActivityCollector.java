package com.myproject.mobilesafe.myUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	private static List<Activity> activityCollector = new ArrayList<Activity>();
	
	/**
	 * 添加Activity到收集器
	 * @param activity	新开启的Activity
	 */
	public static void addActivity(Activity activity){
		activityCollector.add(activity);
	}
	
	/**
	 * 关闭某个Activity
	 * @param activity	要关闭的Activity
	 */
	public static void removeActivity(Activity activity){
		activityCollector.remove(activity);
	}
	
	/**
	 * 关闭当前所有Activity推出程序
	 */
	public static void removeAllActivity(){
		for(Activity activity : activityCollector){
			activity.finish();
		}
	}
}
