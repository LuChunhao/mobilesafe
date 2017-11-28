package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.fragment.LockedAppFragment;
import com.myproject.mobilesafe.fragment.UnLockAppFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class AppLockActivity extends FragmentActivity implements OnClickListener {

	private FragmentManager fm;

	private UnLockAppFragment unlockFragment;
	private LockedAppFragment lockedFragment;

	private TextView tv_unlock_app;
	private TextView tv_locked_app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_app_lock);

		tv_unlock_app = (TextView) findViewById(R.id.tv_unlock_app);
		tv_locked_app = (TextView) findViewById(R.id.tv_locked_app);

		tv_unlock_app.setOnClickListener(this);
		tv_locked_app.setOnClickListener(this);

		fm = getSupportFragmentManager();

		unlockFragment = new UnLockAppFragment();
		lockedFragment = new LockedAppFragment();

		// 默认展示未加锁界面
		fm.beginTransaction().replace(R.id.ll_app_lock, unlockFragment).commit();
	}


	@Override
	public void onClick(View v) {
		FragmentTransaction beginTransaction = fm.beginTransaction();
		switch (v.getId()) {
		case R.id.tv_unlock_app:
			// 点击未加锁按钮
			tv_unlock_app.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_locked_app.setBackgroundResource(R.drawable.tab_right_default);
			beginTransaction.replace(R.id.ll_app_lock, unlockFragment);
			break;

		case R.id.tv_locked_app:
			// 点击已加锁按钮
			tv_unlock_app.setBackgroundResource(R.drawable.tab_left_default);
			tv_locked_app.setBackgroundResource(R.drawable.tab_right_pressed);
			beginTransaction.replace(R.id.ll_app_lock, lockedFragment);
			break;
		}
		beginTransaction.commit();
	}

}
