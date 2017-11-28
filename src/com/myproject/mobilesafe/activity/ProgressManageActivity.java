package com.myproject.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.ProgressInfo;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyConstances;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.myproject.mobilesafe.myUtils.SystemInfoUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProgressManageActivity extends Activity implements OnItemClickListener, OnClickListener {

	private Activity act;
	
	private SharedPreferences sp;

	private TextView tv_running_progress;
	private TextView tv_free_progress_size;
	private ListView lv_progress_list;
	private Button bt_select_all;

	private List<ProgressInfo> allProgressInfoList;
	private List<ProgressInfo> userProgressInfoList;
	private List<ProgressInfo> sysProgressInfoList;

	private int runningPrecessCount; // ��ǰ���н��̸���
	private long totalSysMemory; // ϵͳ���ڴ�
	private long curFreeMemory; // ��ǰϵͳ�����ڴ�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_progress_manage);

		act = this;
		
		sp = getSharedPreferences(MyConstances.CONFIG, MODE_PRIVATE);

		tv_running_progress = (TextView) findViewById(R.id.tv_running_progress);
		tv_free_progress_size = (TextView) findViewById(R.id.tv_free_progress_size);
		lv_progress_list = (ListView) findViewById(R.id.lv_progress_list);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);

		bt_select_all.setOnClickListener(this);

		// ���õ�ǰϵͳ���н��̸�����ʾ
		runningPrecessCount = SystemInfoUtil.getAllRunningProcessCont(act);
		tv_running_progress.setText("�����н���" + runningPrecessCount + "��");

		// ��ȡϵͳ�����ڴ漰���ڴ�,����ʾ
		totalSysMemory = SystemInfoUtil.getSystemTotalMemory(act);
		curFreeMemory = SystemInfoUtil.getSystemFreeMemory(act);
		tv_free_progress_size.setText("����/���ڴ棺" + Formatter.formatFileSize(act, curFreeMemory) + "/"
				+ Formatter.formatFileSize(act, totalSysMemory));

		userProgressInfoList = new ArrayList<ProgressInfo>();
		sysProgressInfoList = new ArrayList<ProgressInfo>();

		myAdapter = new MyBaseAdapter();

		fillData();

		lv_progress_list.setAdapter(myAdapter);

		// ��listView��ӵ���¼�
		lv_progress_list.setOnItemClickListener(this);

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		myAdapter.notifyDataSetChanged();
	}

	/**
	 * ��ʼ������ListView����
	 */
	private void fillData() {

		// new Thread().start();

		allProgressInfoList = SystemInfoUtil.getAllRunningProgress(act);
		for (ProgressInfo info : allProgressInfoList) {
			if (info.isSysProgress()) {
				sysProgressInfoList.add(info);
			} else {
				userProgressInfoList.add(info);
			}
		}
	}

	private MyBaseAdapter myAdapter;

	private class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(sp.getBoolean(MyConstances.ISSHOWSYSTEMPROCESS, false)){
				return userProgressInfoList.size() + sysProgressInfoList.size();
			}else{
				return userProgressInfoList.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (position < userProgressInfoList.size()) {
				return userProgressInfoList.get(position);
			} else {
				return sysProgressInfoList.get(position - userProgressInfoList.size());
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProgressInfo progressInfo;
			View view;
			ViewHolder vh;
			if (convertView == null) {
				view = View.inflate(act, R.layout.item_progress_list, null);
				vh = new ViewHolder();
				vh.desc = (TextView) view.findViewById(R.id.tv_item_progress_desc);
				vh.icon = (ImageView) view.findViewById(R.id.iv_progress_icon);
				vh.name = (TextView) view.findViewById(R.id.tv_item_progress_name);
				vh.size = (TextView) view.findViewById(R.id.tv_item_process_size);
				vh.checkBox = (CheckBox) view.findViewById(R.id.cb_progress);
				view.setTag(vh);
			} else {
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}

			// �ȼ����û������ټ���ϵͳ����
			if (position < userProgressInfoList.size()) {
				progressInfo = userProgressInfoList.get(position);
			} else {
				progressInfo = sysProgressInfoList.get(position - userProgressInfoList.size());
			}

			if (position == 0 || position == userProgressInfoList.size()) {
				vh.desc.setVisibility(View.VISIBLE);
			} else {
				vh.desc.setVisibility(View.GONE);
			}

			// �жϵ�ǰ�������û����̻���ϵͳ����
			if (progressInfo.isSysProgress()) {
				vh.desc.setText("ϵͳ���̣�" + sysProgressInfoList.size());
			} else {
				vh.desc.setText("�û����̣�" + userProgressInfoList.size());
			}

			if (progressInfo.getPackageName().equals(act.getPackageName())) {
				// ��ǰ����Ϊ�Լ�
				vh.checkBox.setVisibility(View.GONE);
			} else {
				vh.checkBox.setVisibility(View.VISIBLE);
			}

			// ����ͼ��
			vh.icon.setBackgroundDrawable(progressInfo.getIcon());
			// �����ڴ��С
			vh.size.setText(Formatter.formatFileSize(act, (progressInfo.getMemorySize() * 1024)));
			// ���ý�������
			vh.name.setText(progressInfo.getProgressName());

			if (progressInfo.isChecked()) {
				vh.checkBox.setChecked(true);
			} else {
				vh.checkBox.setChecked(false);
			}

			return view;
		}

	}

	private class ViewHolder {
		private TextView desc;
		private ImageView icon;
		private TextView name;
		private TextView size;
		private CheckBox checkBox;
	}

	private ActivityManager am;

	/**
	 * �������
	 * 
	 * @param v
	 */
	public void cleanProgress(View v) {
		List<ProgressInfo> newUseProrList = new ArrayList<ProgressInfo>();
		List<ProgressInfo> newSysProList = new ArrayList<ProgressInfo>();
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int cont = 0;
		int size = 0;
//		for (ProgressInfo info : allProgressInfoList) {
//			if (info.isChecked()) {
//				if (info.isSysProgress()) {
//					sysProgressInfoList.remove(info);
//				} else {
//					userProgressInfoList.remove(info);
//				}
//				am.killBackgroundProcesses(info.getPackageName());
//				cont++;
//				size += info.getMemorySize();
//			}
//		}
		for (ProgressInfo info : userProgressInfoList) {
			if (info.isChecked()) {
				//userProgressInfoList.remove(info);
				am.killBackgroundProcesses(info.getPackageName());
				cont++;
				size += info.getMemorySize();
			}else{
				newUseProrList.add(info);
			}
		}
		for (ProgressInfo info : sysProgressInfoList) {
			if (info.isChecked()) {
				//sysProgressInfoList.remove(info);
				am.killBackgroundProcesses(info.getPackageName());
				cont++;
				size += info.getMemorySize();
			}else{
				newSysProList.add(info);
			}
		}
		
		userProgressInfoList = newUseProrList;
		sysProgressInfoList = newSysProList;
		
		myAdapter.notifyDataSetChanged();
		MyUtil.makeToast(act, "��������" + cont + "�����̣��ͷ���" + Formatter.formatFileSize(act, (size * 1024)) + "�ռ�");
		// �޸�������ʾ��Ϣ
		// ���õ�ǰϵͳ���н��̸�����ʾ
		tv_running_progress.setText("�����н���" + (runningPrecessCount-cont) + "��");
		// ������ɺ����¼��㵱ǰ�����н�����
		runningPrecessCount = runningPrecessCount-cont;
		// ��ȡϵͳ�����ڴ漰���ڴ�,����ʾ
		tv_free_progress_size.setText("����/���ڴ棺" + Formatter.formatFileSize(act, (curFreeMemory+size*1024)) + "/"
				+ Formatter.formatFileSize(act, totalSysMemory));
		// ������ɺ����¼��㵱ǰ�����ڴ�
		curFreeMemory = curFreeMemory+size*1024;
	}

	/**
	 * ����
	 */
	public void progressSetting(View v) {
		Intent intent = new Intent(this, ProgressSettingActivity.class);
		startActivity(intent);
	}

	private ProgressInfo selectedProgress;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		ViewHolder vh = (ViewHolder) view.getTag();

		selectedProgress = (ProgressInfo) parent.getItemAtPosition(position);
		if (selectedProgress.isChecked()) {
			selectedProgress.setChecked(false);
			vh.checkBox.setChecked(false);
		} else {
			selectedProgress.setChecked(true);
			vh.checkBox.setChecked(true);
		}

	}

	private boolean isSelectedAll = false;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_select_all) {
			// ����ȫѡ
			if (isSelectedAll) {
				for (ProgressInfo info : allProgressInfoList) {
					if (!info.getPackageName().equals(act.getPackageName())) {
						info.setChecked(false);
					}
				}
				isSelectedAll = false;
				bt_select_all.setText("ȫѡ");
				myAdapter.notifyDataSetChanged();
			} else {
				for (ProgressInfo info : allProgressInfoList) {
					if (!info.getPackageName().equals(act.getPackageName())) {
						info.setChecked(true);
					}
				}
				isSelectedAll = true;
				bt_select_all.setText("ȫ��ѡ");
				myAdapter.notifyDataSetChanged();
			}
		}

	}
}
