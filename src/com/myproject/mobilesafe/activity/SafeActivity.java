package com.myproject.mobilesafe.activity;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SafeActivity extends Activity {
	
	private static final String TAG = "SafeActivity";
	private TextView tv_reSetup;
	private ImageView iv_isLocked;
	private TextView tv_safeNum;
	private TextView tv_phoneLostFindName;
	
	private SharedPreferences sp;
	
	private Activity act = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_safe);
		tv_reSetup = (TextView) findViewById(R.id.tv_reSetup);
		iv_isLocked = (ImageView) findViewById(R.id.iv_isLocked);
		tv_safeNum = (TextView) findViewById(R.id.tv_safeNum);
		tv_phoneLostFindName = (TextView) findViewById(R.id.tv_phoneLostFindName);
		
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);
		
		//��ʼ���ֻ�����ģ������
		String modleOneName = sp.getString(MyConstances.PHONELOSTFINDNAME, null);
		if(modleOneName != null){
			tv_phoneLostFindName.setText(modleOneName);
		}
		
		//��ʼ����ȫ���뼰�Ƿ���������
		String safeNum = sp.getString(MyConstances.SAFENUMBER, null);
		
		if(safeNum == null){
			tv_safeNum.setText("δ���ð�ȫ��������������");
		}else{
			tv_safeNum.setText(safeNum);
		}
		
		if(sp.getBoolean(MyConstances.ISSAFEENABLE, false)){
			iv_isLocked.setBackgroundResource(R.drawable.lock);
		}else{
			iv_isLocked.setBackgroundResource(R.drawable.unlock);
		}
		
		//�����������
		tv_reSetup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SafeActivity.this,Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_safe, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() == R.id.item_changeName){
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle("������������");
			final EditText view = new EditText(this);
			adb.setView(view);
			adb.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newName = view.getText().toString().trim();
					if(TextUtils.isEmpty(newName)){
						MyUtil.makeToast(act, "������������");
						return ;
					}
					sp.edit().putString(MyConstances.PHONELOSTFINDNAME, newName).commit();
					tv_phoneLostFindName.setText(newName);
				}
			});
			
			adb.setNegativeButton("ȡ��", null);
			adb.show();
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
