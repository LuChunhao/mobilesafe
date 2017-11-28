package com.myproject.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyLoopTextView extends TextView {


	public MyLoopTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyLoopTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLoopTextView(Context context) {
		super(context);
	}
	
	@Override
	public boolean isFocused() {
		return true;
	}
}
