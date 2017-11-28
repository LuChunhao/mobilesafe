package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.myproject.mobilesafe.myUtils.SmsDBUtils;
import com.myproject.mobilesafe.myUtils.SmsDBUtils.SmsCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AdvancedToolsActivity extends Activity {

	private SharedPreferences sp;

	private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_advancedtools);

		act = this;

		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		pd = new ProgressDialog(act);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

	}

	/**
	 * �����������ز�ѯ�¼�
	 */
	public void phoneAddress(View v) {
		if (sp.getBoolean(MyConstances.ISPHONEGSDENABLE, false)) {
			Intent intent = new Intent(act, PhoneAddressActivity.class);
			startActivity(intent);
		} else {
			MyUtil.makeToast(act, "�������������Ŀ�����������ز�ѯ����");
			return;
		}
	}

	private SmsCallBack callBack = new SmsCallBack() {

		@Override
		public void totalProgress(int totalSize) {
			pd.setMax(totalSize);
		}

		@Override
		public void currentProgress(int curProgress) {
			pd.setProgress(curProgress);
		}
	};

	private ProgressDialog pd;

	/**
	 * ���ű���
	 * 
	 * @param v
	 */
	public void bakupSmsInfo(View v) {

		pd.setTitle("���ݶ���");
		pd.setMessage("���ڱ��ݣ����Ժ�...");
		pd.show();
		new Thread() {
			public void run() {
				try {
					if (SmsDBUtils.backupSms(act, callBack)) {
						MyUtil.makeToast(act, "���ű��ݳɹ�");
					} else {
						MyUtil.makeToast(act, "���ű���ʧ��");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pd.dismiss();
				}
			};
		}.start();
	}

	/**
	 * ���Ż�ԭ
	 * 
	 * @param v
	 */
	public void restoreSmsInfo(View v) {
		pd.setTitle("��ԭ����");
		pd.setMessage("���ڻ�ԭ�����Ժ�...");
		pd.show();
		new Thread(){
			public void run() {
				try {
					if(SmsDBUtils.restoreSms(act, callBack)){
						MyUtil.makeToast(act, "���Ż�ԭ�ɹ�");
					} else {
						MyUtil.makeToast(act, "���Ż�ԭʧ��");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					pd.dismiss();
				}
			};
		}.start();
	}

	/**
	 * ������
	 * 
	 * @param v
	 */
	public void appLock(View v) {
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}
