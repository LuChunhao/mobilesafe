package com.myproject.mobilesafe.myUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.ProgressInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.Formatter;

public class SystemInfoUtil {

	/**
	 * 获取所有正在运行的进程
	 * 
	 * @param context
	 * @return
	 */
	public static List<ProgressInfo> getAllRunningProgress(Context context) {
		List<ProgressInfo> progressInfoList = new ArrayList<ProgressInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();

		for (RunningAppProcessInfo info : runningAppProcessInfoList) {
			ProgressInfo progressInfo = new ProgressInfo();
			String processName = info.processName; // 返回包名及system
			progressInfo.setPackageName(processName); // 包名

			// 进程占用内存
			android.os.Debug.MemoryInfo[] progressMemory = am.getProcessMemoryInfo(new int[] { info.pid });
//			progressInfo.setMemorySize(
//					Formatter.formatFileSize(context, (progressMemory[0].getTotalPrivateDirty() * 1024)));
			progressInfo.setMemorySize(progressMemory[0].getTotalPrivateDirty());
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(processName, 0); // 返回application
				progressInfo.setIcon(appInfo.loadIcon(pm)); // 图标
				// APP名称
				progressInfo.setProgressName(appInfo.loadLabel(pm).toString());
				// 是否是系统应用
				if ((ApplicationInfo.FLAG_SYSTEM & appInfo.flags) != 0) {
					progressInfo.setSysProgress(true);
				} else {
					progressInfo.setSysProgress(false);
				}

			} catch (NameNotFoundException e) {
				e.printStackTrace();
				progressInfo.setProgressName(processName);
				progressInfo.setSysProgress(true);
				progressInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
			}

			progressInfoList.add(progressInfo);
		}

		return progressInfoList;
	}

	/**
	 * 获取所有正在运行的进程个数
	 * 
	 * @param context
	 * @return
	 */
	public static int getAllRunningProcessCont(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();

		return runningAppProcessInfoList.size();
	}

	/**
	 * 获取系统当前可用内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getSystemFreeMemory(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);

		return outInfo.availMem;
	}

	/**
	 * 获取系统总内存大小
	 * @param context
	 * @return
	 */
	public static long getSystemTotalMemory(Context context) {
		// 方法1 API16后可用
		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// MemoryInfo outInfo = new MemoryInfo();
		// am.getMemoryInfo(outInfo);
		// long totalMemory = outInfo.totalMem;
		long totalMemory = 0;
		// 方法2 读取系统文件获取
		FileInputStream fis;
		StringBuilder sb = new StringBuilder();
		try {
			fis = new FileInputStream("/proc/meminfo");

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if(c >= '0' && c<='9'){
					sb.append(c);
				}
			}
			
			return Integer.parseInt(sb.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
