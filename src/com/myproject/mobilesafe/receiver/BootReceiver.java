package com.myproject.mobilesafe.receiver;

import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";

	private TelephonyManager tm;

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.i(TAG, "�ֻ�������");
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		sp = context.getSharedPreferences(MyConstances.CONFIG, Context.MODE_PRIVATE);
		boolean isSafeEnable = sp.getBoolean(MyConstances.ISSAFEENABLE, false);
		String hisSerialNumber = sp.getString(MyConstances.ISBDSIM, null);
		String newSerialNumber = tm.getSimSerialNumber();
		if(isSafeEnable){	//�Ѿ�������������
			if (!hisSerialNumber.equals(newSerialNumber + "abc")) {
				LogUtil.i(TAG, "sim�������仯��");
				String safeNum = sp.getString(MyConstances.SAFENUMBER, null);
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(safeNum, null, "sim card is changed!", null, null);
			}
		}
		
	}

}
