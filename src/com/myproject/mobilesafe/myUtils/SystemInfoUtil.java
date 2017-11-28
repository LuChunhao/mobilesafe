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
	 * ��ȡ�����������еĽ���
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
			String processName = info.processName; // ���ذ�����system
			progressInfo.setPackageName(processName); // ����

			// ����ռ���ڴ�
			android.os.Debug.MemoryInfo[] progressMemory = am.getProcessMemoryInfo(new int[] { info.pid });
//			progressInfo.setMemorySize(
//					Formatter.formatFileSize(context, (progressMemory[0].getTotalPrivateDirty() * 1024)));
			progressInfo.setMemorySize(progressMemory[0].getTotalPrivateDirty());
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(processName, 0); // ����application
				progressInfo.setIcon(appInfo.loadIcon(pm)); // ͼ��
				// APP����
				progressInfo.setProgressName(appInfo.loadLabel(pm).toString());
				// �Ƿ���ϵͳӦ��
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
	 * ��ȡ�����������еĽ��̸���
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
	 * ��ȡϵͳ��ǰ�����ڴ�
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
	 * ��ȡϵͳ���ڴ��С
	 * @param context
	 * @return
	 */
	public static long getSystemTotalMemory(Context context) {
		// ����1 API16�����
		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// MemoryInfo outInfo = new MemoryInfo();
		// am.getMemoryInfo(outInfo);
		// long totalMemory = outInfo.totalMem;
		long totalMemory = 0;
		// ����2 ��ȡϵͳ�ļ���ȡ
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
