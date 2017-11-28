package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.service.CleanProcessByScreenOffService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class ProgressSettingActivity extends Activity {

	private LinearLayout ll_isshowsystemprocess;
	private LinearLayout ll_iscleanprocess_screenoff;

	private CheckBox cb_isshowsystemprocess;
	private CheckBox cb_iscleanprocess_screenoff;

	private SharedPreferences sp;
	
	private Intent service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_progress_setting);

		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		ll_isshowsystemprocess = (LinearLayout) findViewById(R.id.ll_isshowsystemprocess);
		ll_iscleanprocess_screenoff = (LinearLayout) findViewById(R.id.ll_iscleanprocess_screenoff);

		cb_isshowsystemprocess = (CheckBox) findViewById(R.id.cb_isshowsystemprocess);
		cb_iscleanprocess_screenoff = (CheckBox) findViewById(R.id.cb_iscleanprocess_screenoff);

		cb_isshowsystemprocess.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sp.edit().putBoolean(MyConstances.ISSHOWSYSTEMPROCESS, isChecked).commit();
			}
		});

		service = new Intent(this,CleanProcessByScreenOffService.class);
		
		cb_iscleanprocess_screenoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startService(service);
				}else{
					stopService(service);
				}

			}
		});

		ll_isshowsystemprocess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_isshowsystemprocess.isChecked()) {
					cb_isshowsystemprocess.setChecked(false);
					sp.edit().putBoolean(MyConstances.ISSHOWSYSTEMPROCESS, false).commit();
				} else {
					cb_isshowsystemprocess.setChecked(true);
					sp.edit().putBoolean(MyConstances.ISSHOWSYSTEMPROCESS, true).commit();
				}

			}
		});

		ll_iscleanprocess_screenoff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_iscleanprocess_screenoff.isChecked()) {
					cb_iscleanprocess_screenoff.setChecked(false);
					stopService(service);
				} else {
					cb_iscleanprocess_screenoff.setChecked(true);
					startService(service);
				}

			}
		});

		// ≥ı ºªØ
		init();
	}

	public void init() {
		boolean isShowSysPro = sp.getBoolean(MyConstances.ISSHOWSYSTEMPROCESS, false);
		if (isShowSysPro) {
			cb_isshowsystemprocess.setChecked(true);
		} else {
			cb_isshowsystemprocess.setChecked(false);
		}
	}
}
