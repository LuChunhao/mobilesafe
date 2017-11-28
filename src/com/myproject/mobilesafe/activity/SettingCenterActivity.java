package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.myproject.mobilesafe.service.InterceptBlackListService;
import com.myproject.mobilesafe.service.PhoneAddressService;
import com.myproject.mobilesafe.service.WatchDogService;
import com.myproject.mobilesafe.view.SettingView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingCenterActivity extends Activity {

	private SettingView sv_auto_update;
	private SettingView sv_blackList;
	private SettingView sv_phonegsd;
	private SettingView sv_app_lock;
	private RelativeLayout rv_phone_address;
	private TextView tv_address_desc;

	private SharedPreferences sp;

	private Intent service;
	private Intent addressService;
	private Intent watchDogService;

	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_center);

		ctx = this;
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		/**
		 * 自动更新设置
		 */
		sv_auto_update = (SettingView) findViewById(R.id.sv_auto_update);
		boolean isAutoUpdate = sp.getBoolean(MyConstances.ISAUTOUPDATE, false);
		if (isAutoUpdate) {
			sv_auto_update.setChecked(true);
		} else {
			sv_auto_update.setChecked(false);
		}

		/**
		 * 黑名单设置
		 */
		sv_blackList = (SettingView) findViewById(R.id.sv_blackList);
		service = new Intent(this,InterceptBlackListService.class);
		boolean isBlackListEnable = sp.getBoolean(MyConstances.ISBLACKLISTENABLE, false);
		if (isBlackListEnable) {
			sv_blackList.setChecked(true);
			startService(service);
		} else {
			sv_blackList.setChecked(false);
			stopService(service);
		}

		/**
		 * 电话归属地查询
		 */
		addressService = new Intent(this,PhoneAddressService.class);
		sv_phonegsd = (SettingView) findViewById(R.id.sv_phonegsd);
		boolean isPhoneGSDEnable = sp.getBoolean(MyConstances.ISPHONEGSDENABLE, false);
		if (isPhoneGSDEnable) {
			sv_phonegsd.setChecked(true);
			startService(addressService);
		} else {
			sv_phonegsd.setChecked(false);
			stopService(addressService);
		}

		/**
		 * 程序锁
		 */
		watchDogService = new Intent(this,WatchDogService.class);
		sv_app_lock = (SettingView) findViewById(R.id.sv_app_lock);
		boolean isAPPLockEnable = sp.getBoolean(MyConstances.ISAPPLOCKENABLE, false);
		if (isAPPLockEnable) {
			sv_app_lock.setChecked(true);
			startService(watchDogService);
		}else{
			sv_app_lock.setChecked(false);
			stopService(watchDogService);
		}
		
		/**
		 * 归属地风格设置
		 */
		rv_phone_address = (RelativeLayout) findViewById(R.id.rv_phone_address);
		tv_address_desc = (TextView) findViewById(R.id.tv_address_desc);
		rv_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder adb = new Builder(ctx);
				adb.setTitle("归属地提示框风格");
				adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sp.edit().putInt(MyConstances.ADDRESSSTYLE, which).commit();
						dialog.dismiss();
						tv_address_desc.setText(items[which]);
					}
				});
				adb.show();
			}
		});
	}

	private String[] items = new String[] {"卫士蓝","金属灰","苹果绿","活力橙","半透明"};

	@Override
	protected void onStart() {
		super.onStart();
		boolean isServiceRunning = MyUtil.isServiceRunning(this, InterceptBlackListService.class);
		if (isServiceRunning) {
			sv_blackList.setChecked(true);
		} else {
			sv_blackList.setChecked(false);
		}
		isServiceRunning = MyUtil.isServiceRunning(this, PhoneAddressService.class);
		if (isServiceRunning) {
			sv_phonegsd.setChecked(true);
		} else {
			sv_phonegsd.setChecked(false);
		}
		// 程序锁
		isServiceRunning = MyUtil.isServiceRunning(this, WatchDogService.class);
		if (isServiceRunning) {
			sv_app_lock.setChecked(true);
		} else {
			sv_app_lock.setChecked(false);
		}
	}

	@Override
	protected void onPause() {
		boolean isAutoUpdate = sv_auto_update.isChecked();
		sp.edit().putBoolean(MyConstances.ISAUTOUPDATE, isAutoUpdate).commit();

		boolean isBlackListEnable = sv_blackList.isChecked();
		if (isBlackListEnable) {
			startService(service);
		} else {
			stopService(service);
		}
		sp.edit().putBoolean(MyConstances.ISBLACKLISTENABLE, isBlackListEnable).commit();

		boolean isPhoneGSDEnable = sv_phonegsd.isChecked();
		if(isPhoneGSDEnable){
			startService(addressService);
		}else{
			stopService(addressService);
		}
		sp.edit().putBoolean(MyConstances.ISPHONEGSDENABLE, isPhoneGSDEnable).commit();

		boolean isAPPLockEnable = sv_app_lock.isChecked();
		if(isPhoneGSDEnable){
			startService(watchDogService);
		}else{
			stopService(watchDogService);
		}
		sp.edit().putBoolean(MyConstances.ISAPPLOCKENABLE, isAPPLockEnable).commit();
		super.onPause();
	}

}
