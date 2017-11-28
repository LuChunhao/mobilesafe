package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Setup2Activity extends SetupBaseActivity {
	
	private Button bt_bdsim;
	private ImageView iv_locksim;
	
	private TelephonyManager tm; 
	
	private Activity act = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		bt_bdsim = (Button) findViewById(R.id.bt_bdsim);
		iv_locksim = (ImageView) findViewById(R.id.iv_locksim);
		
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		String isBdSIM = sp.getString(MyConstances.ISBDSIM, null);
		
		if(isBdSIM != null){
			iv_locksim.setBackgroundResource(R.drawable.lock);
		}else{
			iv_locksim.setBackgroundResource(R.drawable.unlock);
		}
		
		//��ť����¼�
		bt_bdsim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String isBdSIM = sp.getString(MyConstances.ISBDSIM, null);
				
				if(isBdSIM != null){
					//���
					sp.edit().putString(MyConstances.ISBDSIM, null).commit();
					iv_locksim.setBackgroundResource(R.drawable.unlock);
					MyUtil.makeToast(act, "�����SIM���ɹ�");
				}else{
					sp.edit().putString(MyConstances.ISBDSIM, tm.getSimSerialNumber()).commit();
					iv_locksim.setBackgroundResource(R.drawable.lock);
					MyUtil.makeToast(act, "��SIM���ɹ�");
				}
			}
		});
	}

	@Override
	public void toNext() {
		String isBdSIM = sp.getString(MyConstances.ISBDSIM, null);
		if(isBdSIM == null){
			MyUtil.makeToast(act, "���Ȱ�SIM��");
			return;
		}
		toNextSetup(Setup3Activity.class);
		
	}

	@Override
	public void toPre() {
		toPreSetup(Setup1Activity.class);
		
	}
}
