package com.myproject.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.myproject.mobilesafe.activity.HomeActivity;
import com.myproject.mobilesafe.myUtils.ActivityCollector;
import com.myproject.mobilesafe.myUtils.HttpCallbackListener;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

public class SplashActivity extends Activity {
	protected static final int ISUPDATE = 1;

	private Activity act;

	private PackageManager pm;
	private PackageInfo packageInfo;

	private TextView tv_versionName;
	
	private TextView tv_downLoading;

	private int currentVersionCode;
	private String update_url;
	private String describe;
	
	private SharedPreferences sp;

	private long startTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		act = this;
		ActivityCollector.addActivity(act);
		
		startTime = System.currentTimeMillis();
		
		pm = getPackageManager();
		
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);
		
		//初始化信息
		// 1、拷贝数据库至data目录
		String dbName = "address.db";
		copyDBToFile(dbName);
		
		// 2、创建桌面快捷方式
		if(!sp.getBoolean(MyConstances.ISCREATELAUNCHER, false)){
			createShortcutLauncher();
		}
		
		//获取自动更新设置是否开启
		boolean isAutoUpdate = sp.getBoolean(MyConstances.ISAUTOUPDATE, false);
		if(!isAutoUpdate){
			new Thread(){
				public void run() {
					try {
						Thread.sleep(1000);
						gotoHomePage();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
			return ;
		}

		tv_versionName = (TextView) findViewById(R.id.tv_versionName);
		tv_downLoading = (TextView) findViewById(R.id.tv_downLoading);
		try {
			packageInfo = pm.getPackageInfo(getPackageName(), 0);
			currentVersionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			tv_versionName.setText(versionName);
			// 访问网络获取当前应用程序最新版本
			LogUtil.d("httpurl", getResources().getString(R.string.checkupdate_url));
			MyUtil.getResponseFromHttpUrl(getResources().getString(R.string.checkupdate_url),
				new HttpCallbackListener() {
					@Override
					public void onFinish(String response) {
						try {
							JSONObject json = new JSONObject(response);
							int versionCode = json.getInt("versioncode");
							update_url = json.getString("update_url");
							describe = json.getString("describe");
							if (currentVersionCode == versionCode) {
								// 版本号一致进入主页面
								long now = System.currentTimeMillis();
								if(now - startTime < 2000){
									SystemClock.sleep(2000- now + startTime);
								}
								gotoHomePage();
							} else {
								// 版本号不一致，提示用户是否升级
								LogUtil.d("debug===", Thread.currentThread().getName());
								Message msg = Message.obtain();
								msg.what = ISUPDATE;
								handler.sendMessage(msg);
							}
						} catch (JSONException e) {
							MyUtil.makeToast(act, "应用程序错误，错误代码为101");
							LogUtil.e("error", "返回JSON数据格式不正确");
							gotoHomePage();
							e.printStackTrace();
						}
						LogUtil.d("response..", response);
					}

					@Override
					public void onError(Exception e) {
						MyUtil.makeToast(act, "访问网络出错");
						gotoHomePage();
					}
				});
		} catch (NameNotFoundException e) {
			MyUtil.makeToast(act, "应用程序错误，错误代码为100");
			LogUtil.e("error", "未找到对应包名");
			e.printStackTrace();
		}
	}

	/**
	 * 创建快捷方式
	 */
	private void createShortcutLauncher() {
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.trojan));
		
		Intent doWhat = new Intent();
		doWhat.setAction("com.myproject.homeactivity.launcher");
		doWhat.addCategory(Intent.CATEGORY_DEFAULT);
		
		intent.putExtra("duplicate", false);	// 设置不可重复创建快捷方式
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhat );
		sendBroadcast(intent);
		// sharedPerference 文件中记录已经创建过快捷方式
		sp.edit().putBoolean(MyConstances.ISCREATELAUNCHER, true).commit();
	}

	/**
	 * 拷贝数据库至file目录
	 * @param dbName
	 */
	private void copyDBToFile(String dbName) {
		try {
			File file = new File(getFilesDir(), dbName);
			if(!file.exists()){
				InputStream is = getAssets().open(dbName);
				FileOutputStream fop = openFileOutput(dbName, MODE_PRIVATE);
				byte[] buffer = new byte[512];
				int len = -1 ;
				while((len = is.read(buffer)) != -1){
					fop.write(buffer, 0, len);
				}
				is.close();
				fop.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出确认是否升级对话框
	 */
	protected void showIsUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle("请确认是否升级");
		builder.setMessage(describe);
		// 用户点击返回或者屏幕任意地方后返回主界面
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				gotoHomePage();
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				gotoHomePage();
			}
		});
		builder.setPositiveButton("现在升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyUtil.makeToast(act, "正在下载升级包");
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.download(update_url, Environment.getExternalStorageDirectory().getAbsoluteFile()+"/tmp.apk", new RequestCallBack<File>() {
					
					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						//MyUtil.makeToast(act, "下载成功");
						File file = responseInfo.result;
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						startActivityForResult(intent, 1);
					}
					
					@Override
					public void onFailure(HttpException error, String msg) {
						MyUtil.makeToast(act, "下载失败");
						error.printStackTrace();
						LogUtil.e("error", msg);
						gotoHomePage();
					}
					
					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						tv_downLoading.setText("当前进度："+current/total*100+"%");
						super.onLoading(total, current, isUploading);
					}
				});
			}
		});
		builder.show();
	}

	/**
	 * 进入主页面
	 */
	protected void gotoHomePage() {
		Intent intent = new Intent(act, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 销毁所有Activity
	 */
	@Override
	protected void onDestroy() {
		//ActivityCollector.removeAllActivity();
		super.onDestroy();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ISUPDATE:
				showIsUpdateDialog();
				break;
			default:
				break;
			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		gotoHomePage();		//安装界面点击返回按钮执行此处
		super.onActivityResult(requestCode, resultCode, data);
	};
	
}
