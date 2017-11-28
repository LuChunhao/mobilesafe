package com.myproject.mobilesafe.view;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SettingView extends FrameLayout {

	public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.myproject.mobilesafe", "title");
		String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.myproject.mobilesafe", "desc");
		descs = desc.split("#");
		init();
	}

	public SettingView(Context context) {
		super(context);
		init();
	}

	private TextView tv_setting_center_title;
	private TextView tv_setting_center_desc;
	private CheckBox cb_isSettingChecked;
	
	public void init() {
		View view = View.inflate(getContext(), R.layout.item_setting_center, null);
		tv_setting_center_title = (TextView) view.findViewById(R.id.tv_setting_center_title);
		tv_setting_center_desc = (TextView) view.findViewById(R.id.tv_setting_center_desc);
		cb_isSettingChecked = (CheckBox) view.findViewById(R.id.cb_isSettingChecked);
		
		tv_setting_center_title.setText(title);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cb_isSettingChecked.isChecked()) {
//					cb_isSettingChecked.setChecked(false);
//					tv_setting_center_desc.setText(descs[0]);
//					tv_setting_center_desc.setTextColor(Color.RED);
					setChecked(false);
				} else {
//					cb_isSettingChecked.setChecked(true);
//					tv_setting_center_desc.setText(descs[1]);
//					tv_setting_center_desc.setTextColor(Color.GREEN);
					setChecked(true);
				}

			}
		});

		addView(view);
	}

	private String[] descs;
	
	private String title;

	/**
	 * 设置自定义控件标题
	 * 
	 * @param title
	 */
	public void setDescs(String[] descs) {
		this.descs = descs;
		tv_setting_center_title.setText(descs[0]);
	}

	/**
	 * 设置该设置条目是否选中
	 * @param b
	 */
	public void setChecked(boolean b) {
		if(b){
			cb_isSettingChecked.setChecked(true);
			tv_setting_center_desc.setText(descs[1]);
			tv_setting_center_desc.setTextColor(Color.GREEN);
		}else{
			cb_isSettingChecked.setChecked(false);
			tv_setting_center_desc.setText(descs[0]);
			tv_setting_center_desc.setTextColor(Color.RED);
		}
	}

	public boolean isChecked(){
		
		return cb_isSettingChecked.isChecked();
	}

}
