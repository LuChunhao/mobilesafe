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
		
		//��ʼ����Ϣ
		// 1���������ݿ���dataĿ¼
		String dbName = "address.db";
		copyDBToFile(dbName);
		
		// 2�����������ݷ�ʽ
		if(!sp.getBoolean(MyConstances.ISCREATELAUNCHER, false)){
			createShortcutLauncher();
		}
		
		//��ȡ�Զ����������Ƿ���
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
			// ���������ȡ��ǰӦ�ó������°汾
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
								// �汾��һ�½�����ҳ��
								long now = System.currentTimeMillis();
								if(now - startTime < 2000){
									SystemClock.sleep(2000- now + startTime);
								}
								gotoHomePage();
							} else {
								// �汾�Ų�һ�£���ʾ�û��Ƿ�����
								LogUtil.d("debug===", Thread.currentThread().getName());
								Message msg = Message.obtain();
								msg.what = ISUPDATE;
								handler.sendMessage(msg);
							}
						} catch (JSONException e) {
							MyUtil.makeToast(act, "Ӧ�ó�����󣬴������Ϊ101");
							LogUtil.e("error", "����JSON���ݸ�ʽ����ȷ");
							gotoHomePage();
							e.printStackTrace();
						}
						LogUtil.d("response..", response);
					}

					@Override
					public void onError(Exception e) {
						MyUtil.makeToast(act, "�����������");
						gotoHomePage();
					}
				});
		} catch (NameNotFoundException e) {
			MyUtil.makeToast(act, "Ӧ�ó�����󣬴������Ϊ100");
			LogUtil.e("error", "δ�ҵ���Ӧ����");
			e.printStackTrace();
		}
	}

	/**
	 * ������ݷ�ʽ
	 */
	private void createShortcutLauncher() {
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "��ȫ��ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.trojan));
		
		Intent doWhat = new Intent();
		doWhat.setAction("com.myproject.homeactivity.launcher");
		doWhat.addCategory(Intent.CATEGORY_DEFAULT);
		
		intent.putExtra("duplicate", false);	// ���ò����ظ�������ݷ�ʽ
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhat );
		sendBroadcast(intent);
		// sharedPerference �ļ��м�¼�Ѿ���������ݷ�ʽ
		sp.edit().putBoolean(MyConstances.ISCREATELAUNCHER, true).commit();
	}

	/**
	 * �������ݿ���fileĿ¼
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
	 * ����ȷ���Ƿ������Ի���
	 */
	protected void showIsUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle("��ȷ���Ƿ�����");
		builder.setMessage(describe);
		// �û�������ػ�����Ļ����ط��󷵻�������
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				gotoHomePage();
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				gotoHomePage();
			}
		});
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyUtil.makeToast(act, "��������������");
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.download(update_url, Environment.getExternalStorageDirectory().getAbsoluteFile()+"/tmp.apk", new RequestCallBack<File>() {
					
					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						//MyUtil.makeToast(act, "���سɹ�");
						File file = responseInfo.result;
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						startActivityForResult(intent, 1);
					}
					
					@Override
					public void onFailure(HttpException error, String msg) {
						MyUtil.makeToast(act, "����ʧ��");
						error.printStackTrace();
						LogUtil.e("error", msg);
						gotoHomePage();
					}
					
					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						tv_downLoading.setText("��ǰ���ȣ�"+current/total*100+"%");
						super.onLoading(total, current, isUploading);
					}
				});
			}
		});
		builder.show();
	}

	/**
	 * ������ҳ��
	 */
	protected void gotoHomePage() {
		Intent intent = new Intent(act, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * ��������Activity
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
		gotoHomePage();		//��װ���������ذ�ťִ�д˴�
		super.onActivityResult(requestCode, resultCode, data);
	};
	
}
