package com.myproject.mobilesafe.myUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.domain.AppInfo;
import com.myproject.mobilesafe.domain.ContactInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Formatter;
import android.widget.Toast;

public class MyUtil {
	/**
	 * 在前台弹出Toast
	 * 
	 * @param act
	 *            当前Activity名称
	 * @param msg
	 *            需要弹出提示的String
	 */
	public static void makeToast(final Activity act, final String msg) {
		if ("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
		} else {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * 访问网络服务器获取返回String数据
	 * 
	 * @param internetUrl
	 *            访问网络地址
	 * @param listener
	 *            回调函数
	 */
	public static void getResponseFromHttpUrl(final String internetUrl, final HttpCallbackListener listener) {
		new Thread() {
			public void run() {
				try {
					URL url = new URL(internetUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(2000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						StringBuffer sb = new StringBuffer();
						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						LogUtil.i("info", sb.toString());
						listener.onFinish(sb.toString());
					} else {
						LogUtil.e("error", "访问网络异常，返回码不正确");
						listener.onError(new Exception());
					}
				} catch (MalformedURLException e) {
					LogUtil.e("error", "访问网络异常，未连接到服务器");
					listener.onError(e);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					listener.onError(e);
				}

			};
		}.start();
	}

	/**
	 * 将一个输入流转换为字符串
	 * 
	 * @param is
	 *            传入的输入流
	 * @return 返回的字符串
	 */
	public static String getStringFromInputStream(InputStream is) {
		String response = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			is.close();
			response = new String(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * MD5加密
	 * 
	 * @param password
	 *            传入的明文密码
	 * @return 返回的加密密码
	 */
	public static String md5Encryption(String password) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] bts = md.digest(password.getBytes());
			for (byte b : bts) {
				int num = b & 0XFF;
				String encyStr = Integer.toHexString(num);
				if (encyStr.length() == 1) {
					sb.append("0" + encyStr);
				} else {
					sb.append(encyStr);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e("MD5", "MD5加密出错");
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取系统联系人姓名及电话
	 * 
	 * @param context
	 * @return List<ContactInfo> ContactInfo.name 联系人姓名 ContactInfo.phone 联系人电话
	 */
	public static List<ContactInfo> getSysContacts(Context context) {
		List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String contact_id = cursor.getString(cursor.getColumnIndex("contact_id"));
			if (contact_id != null) {
				ContactInfo contactInfo = new ContactInfo();
				Cursor dataCursor = resolver.query(dataUri, null, "raw_contact_id=?", new String[] { contact_id },
						null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(dataCursor.getColumnIndex("data1"));
					String mimeType = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
					if (mimeType.equals("vnd.android.cursor.item/phone_v2")) {
						contactInfo.setPhone(data1);
					} else if (mimeType.equals("vnd.android.cursor.item/name")) {
						contactInfo.setName(data1);
					}
				}
				dataCursor.close();
				contactInfos.add(contactInfo);
			}
		}
		cursor.close();
		return contactInfos;
	}

	/**
	 * 判断当前系统是否有盖服务在运行
	 * @param context
	 * @param class1
	 * @return
	 */
	public static boolean isServiceRunning(Context context, Class class1) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager.getRunningServices(20);
		for(RunningServiceInfo serviceInfo : runningServices){
			String runningClassName = serviceInfo.service.getClassName();
			if(runningClassName.equals(class1.getName())){
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取手机所有应用APP
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAllAppInfo(Context context){
		
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		for(PackageInfo packageInfo : packageInfos){
			AppInfo appInfo = new AppInfo();
			
			appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
			appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
			appInfo.setPackageName(packageInfo.applicationInfo.packageName);
			
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			File file = new File(sourceDir);
			appInfo.setAppSize(Formatter.formatFileSize(context, file.length()));
			
			int flags = packageInfo.applicationInfo.flags;
			if((ApplicationInfo.FLAG_SYSTEM & flags) == ApplicationInfo.FLAG_SYSTEM){
				// 是系统应用
				appInfo.setSysApp(true);
			}else{
				appInfo.setSysApp(false);
			}
			
			if((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0){
				// 安装在SD卡
				appInfo.setStupInSD(true);
			}else{
				appInfo.setStupInSD(false);
			}
			
			appInfos.add(appInfo);
		}
		
		return appInfos;
	}

}
