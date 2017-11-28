package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Setup3Activity extends SetupBaseActivity {
	
	private EditText et_safeNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		et_safeNum = (EditText) findViewById(R.id.et_safeNum);
		
		String safeNum = sp.getString(MyConstances.SAFENUMBER, null);
		et_safeNum.setText(safeNum);
	}
	
	/**
	 * 点击按钮获取系统联系人
	 * @param view
	 */
	public void getContactsList(View view){
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 11);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			String phone = data.getStringExtra("phone");
			et_safeNum.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void toNext() {
		
		String safeNum = et_safeNum.getText().toString().trim();
		if(safeNum == null){
			MyUtil.makeToast(this, "请先设置安全号码");
			return;
		}
		
		sp.edit().putString(MyConstances.SAFENUMBER, safeNum).commit();
		toNextSetup(Setup4Activity.class);
		
	}

	@Override
	public void toPre() {
		toPreSetup(Setup2Activity.class);
		
	}
}
