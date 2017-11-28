package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class InputPasswordActivity extends Activity {
	
	private EditText et_input_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_input_password);
		
		et_input_password = (EditText) findViewById(R.id.et_input_password);
		
		packageName = getIntent().getStringExtra("packageName");
	}
	
	private String packageName;
	
	public void sendPassword(View v){
		String password = et_input_password.getText().toString().trim();
		if(TextUtils.isEmpty(password)){
			Toast.makeText(this, "密码不能为空", 0).show();
			return ;
		}
		if(password.equals("123")){
			// 密码正确
			
			Intent intent = new Intent();
			intent.setAction("com.myproject.applock.locked");
			intent.putExtra("packageName", packageName);
			LogUtil.i("0000", packageName);
			sendBroadcast(intent);
			finish();
		}
		
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
}
