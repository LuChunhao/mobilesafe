package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class Setup4Activity extends SetupBaseActivity {
	
	private CheckBox cb_isSafeEnable;
	private TextView tv_setup4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		tv_setup4 = (TextView) findViewById(R.id.tv_setup4);
		cb_isSafeEnable = (CheckBox) findViewById(R.id.cb_isSafeEnable);
		cb_isSafeEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tv_setup4.setText("防盗保护已经开启");
					sp.edit().putBoolean(MyConstances.ISSAFEENABLE, isChecked).commit();
				}else{
					tv_setup4.setText("防盗保护没有开启");
					sp.edit().putBoolean(MyConstances.ISSAFEENABLE, isChecked).commit();
				}
				
			}
		});
		
		if(sp.getBoolean(MyConstances.ISSAFEENABLE, false)){
			tv_setup4.setText("防盗保护已经开启");
			cb_isSafeEnable.setChecked(true);
		}else{
			tv_setup4.setText("防盗保护没有开启");
			cb_isSafeEnable.setChecked(false);
		}
	}
	
	@Override
	public void toNext() {
		Intent intent = new Intent(this,SafeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void toPre() {
		toPreSetup(Setup3Activity.class);
		
	}
}
