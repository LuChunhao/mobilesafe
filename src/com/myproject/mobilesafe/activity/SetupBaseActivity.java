package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public abstract class SetupBaseActivity extends Activity {
	
	private GestureDetector gestureDetector;
	
	public SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);
		
		//�໬�¼�
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			@Override
			/**
			 * ����ָ����Ļ���ٻ���ʱ���ص��˷��� 
			 * e1 down�¼�ʱ��event
			 * e2 move�¼�ʱ��event
			 * velocityX x������ٶ�
			 * velocityY y������ٶ�
			 */
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				LogUtil.d("cehua", String.valueOf(velocityX));
				if(Math.abs(velocityX) > 200){
					if(velocityX > 0){	//����
						toPre();
					}else{
						toNext();
					}
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	public void preSetup(View view) {
		toPre();
	}

	public void nextSetup(View view) {
		toNext();
	}
	
	public abstract void toNext();
	
	public abstract void toPre();
	
	/**
	 * �л�����һ������
	 * @param cls
	 */
	public void toNextSetup(Class cls){
		Intent intent = new Intent(this,cls);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}
	
	/**
	 * �л�����һ������
	 * @param cls
	 */
	public void toPreSetup(Class cls){
		Intent intent = new Intent(this,cls);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}
}
