package com.myproject.mobilesafe.myUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	private static List<Activity> activityCollector = new ArrayList<Activity>();
	
	/**
	 * ���Activity���ռ���
	 * @param activity	�¿�����Activity
	 */
	public static void addActivity(Activity activity){
		activityCollector.add(activity);
	}
	
	/**
	 * �ر�ĳ��Activity
	 * @param activity	Ҫ�رյ�Activity
	 */
	public static void removeActivity(Activity activity){
		activityCollector.remove(activity);
	}
	
	/**
	 * �رյ�ǰ����Activity�Ƴ�����
	 */
	public static void removeAllActivity(){
		for(Activity activity : activityCollector){
			activity.finish();
		}
	}
}
