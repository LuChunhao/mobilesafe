package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.AddressDBUtils;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneAddressActivity extends Activity {

	private EditText et_input_num;
	private TextView tv_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_phoneaddress);

		et_input_num = (EditText) findViewById(R.id.et_input_num);
		tv_address = (TextView) findViewById(R.id.tv_address);

		et_input_num.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String address = AddressDBUtils.getAddressByNumber(PhoneAddressActivity.this, s.toString());
				tv_address.setText("归属地：" + address);
			}
		});
	}

	/**
	 * 点击查询按钮
	 * 
	 * @param v
	 */
	public void search(View v) {
		String inputNumber = et_input_num.getText().toString().trim();
		if (TextUtils.isEmpty(inputNumber)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_input_num.startAnimation(shake);
			MyUtil.makeToast(this, "请输入电话号码");
			return;
		}
		String address = AddressDBUtils.getAddressByNumber(this, inputNumber);
		tv_address.setText("归属地：" + address);
	}
}
