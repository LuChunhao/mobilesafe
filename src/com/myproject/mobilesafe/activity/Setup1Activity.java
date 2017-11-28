package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;

import android.os.Bundle;

public class Setup1Activity extends SetupBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	

	@Override
	public void toNext() {
		toNextSetup(Setup2Activity.class);
	}

	@Override
	public void toPre() {
		//没有上一个页面啥也不做
	}
}
