package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.myproject.mobilesafe.myUtils.SmsDBUtils;
import com.myproject.mobilesafe.myUtils.SmsDBUtils.SmsCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AdvancedToolsActivity extends Activity {

	private SharedPreferences sp;

	private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_advancedtools);

		act = this;

		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		pd = new ProgressDialog(act);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

	}

	/**
	 * 点击号码归属地查询事件
	 */
	public void phoneAddress(View v) {
		if (sp.getBoolean(MyConstances.ISPHONEGSDENABLE, false)) {
			Intent intent = new Intent(act, PhoneAddressActivity.class);
			startActivity(intent);
		} else {
			MyUtil.makeToast(act, "请先在设置中心开启号码归属地查询设置");
			return;
		}
	}

	private SmsCallBack callBack = new SmsCallBack() {

		@Override
		public void totalProgress(int totalSize) {
			pd.setMax(totalSize);
		}

		@Override
		public void currentProgress(int curProgress) {
			pd.setProgress(curProgress);
		}
	};

	private ProgressDialog pd;

	/**
	 * 短信备份
	 * 
	 * @param v
	 */
	public void bakupSmsInfo(View v) {

		pd.setTitle("备份短信");
		pd.setMessage("正在备份，请稍后...");
		pd.show();
		new Thread() {
			public void run() {
				try {
					if (SmsDBUtils.backupSms(act, callBack)) {
						MyUtil.makeToast(act, "短信备份成功");
					} else {
						MyUtil.makeToast(act, "短信备份失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pd.dismiss();
				}
			};
		}.start();
	}

	/**
	 * 短信还原
	 * 
	 * @param v
	 */
	public void restoreSmsInfo(View v) {
		pd.setTitle("还原短信");
		pd.setMessage("正在还原，请稍后...");
		pd.show();
		new Thread(){
			public void run() {
				try {
					if(SmsDBUtils.restoreSms(act, callBack)){
						MyUtil.makeToast(act, "短信还原成功");
					} else {
						MyUtil.makeToast(act, "短信还原失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					pd.dismiss();
				}
			};
		}.start();
	}

	/**
	 * 程序锁
	 * 
	 * @param v
	 */
	public void appLock(View v) {
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}
