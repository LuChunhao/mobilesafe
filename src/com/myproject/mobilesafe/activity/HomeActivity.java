package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.ActivityCollector;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemClickListener {

	private Activity act = this;

	private GridView gv_item_name;

	private SharedPreferences sp;

	private final String[] item_name = new String[] { "�ֻ�����", "ͨѶ��ʿ", "����ܼ�", "���̹���", "����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����",
			"��������" };
	private final int[] image_id = new int[] { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.item_launcher,
			R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		ActivityCollector.addActivity(act);

		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		gv_item_name = (GridView) findViewById(R.id.gv_item_name);
		gv_item_name.setAdapter(new MyAdapter());

		gv_item_name.setOnItemClickListener(this);
	}
	
	@Override
	protected void onStart() {
		gv_item_name.setAdapter(new MyAdapter());
		gv_item_name.setOnItemClickListener(this);
		super.onStart();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 9;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView != null) {
				view = convertView;
			} else {
				view = View.inflate(act, R.layout.item_grid_home, null);
			}
			ImageView iv_home_launcher = (ImageView) view.findViewById(R.id.iv_home_launcher);
			TextView tv = (TextView) view.findViewById(R.id.tv_item_name);
			iv_home_launcher.setImageResource(image_id[position]);
			String newName = sp.getString(MyConstances.PHONELOSTFINDNAME, null);
			if(newName != null && position == 0){
				tv.setText(newName);
			}else{
				tv.setText(item_name[position]);
			}
			return view;
		}

	}

	@Override
	protected void onDestroy() {
		// ActivityCollector.removeAllActivity();
		super.onDestroy();
	}
	
	long lastTime = 0;
	long curTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		curTime = System.currentTimeMillis();
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if((curTime - lastTime) > 2000){
				Toast.makeText(this, "�ٴε�������˳�", 0).show();;
				lastTime = System.currentTimeMillis();
				return false;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0: // �ֻ�����
			String password = sp.getString(MyConstances.ISLOSTPWD, null);

			if (password == null) { // ��һ�ν��룬����������
				setLostPwdDialog();
			} else {
				inputLostPwdDialog();
			}
			break;

		case 1:		// ͨѶ��ʿ
			startActivity(new Intent(act,BlackListActivity.class));
			break;
			
		case 2:		// ����ܼ�
			startActivity(new Intent(act,AppManageActivity.class));
			break;
			
		case 3:		// ���̹���
			startActivity(new Intent(act,ProgressManageActivity.class));
			break;
			
		case 7:
			startActivity(new Intent(act,AdvancedToolsActivity.class));
			break;
		case 8:
			Intent intent = new Intent(act,SettingCenterActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	/**
	 * �����������
	 */
	private void inputLostPwdDialog() {
		AlertDialog.Builder adb = new Builder(act);
		View view = getLayoutInflater().inflate(R.layout.dialog_inputpwd, null);
		final EditText et_setpwd = (EditText) view.findViewById(R.id.et_setpwd);
		Button bt_setok = (Button) view.findViewById(R.id.bt_setok);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// ���ȷ����ť
		bt_setok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = et_setpwd.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					MyUtil.makeToast(act, "����������");
					return;
				}
				String pwd = sp.getString(MyConstances.ISLOSTPWD, null);
				if (pwd.equals(MyUtil.md5Encryption(password))) {
					MyUtil.makeToast(act, "������ȷ");
					// ������ȷ����������ҳ��
					Intent intent;
					if(sp.getBoolean(MyConstances.ISSAFEENABLE, false)){
						intent = new Intent(act,SafeActivity.class);
					}else{
						intent = new Intent(act,Setup1Activity.class);
					}
					startActivity(intent);
					dialog.dismiss();
				}else{
					MyUtil.makeToast(act, "�������");
					return;
				}

			}
		});

		// �û����ȡ��
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = adb.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	private AlertDialog dialog;

	/**
	 * �����ֻ�����ǰ��������
	 */
	private void setLostPwdDialog() {
		AlertDialog.Builder adb = new Builder(act);
		View view = View.inflate(act, R.layout.dialog_setpwd, null);
		final EditText et_setpwd = (EditText) view.findViewById(R.id.et_setpwd);
		final EditText et_setpwd2 = (EditText) view.findViewById(R.id.et_setpwd2);
		Button bt_setok = (Button) view.findViewById(R.id.bt_setok);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// ���ȷ����ť
		bt_setok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = et_setpwd.getText().toString().trim();
				String password2 = et_setpwd2.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					MyUtil.makeToast(act, "����������");
					return;
				}
				if (!password.equals(password2)) {
					MyUtil.makeToast(act, "�����������벻һ�£�����������");
					return;
				}
				// ��������һ�£�����������
				sp.edit().putString(MyConstances.ISLOSTPWD, MyUtil.md5Encryption(password)).commit();
				dialog.dismiss();
			}
		});

		// �û����ȡ��
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog = adb.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}
