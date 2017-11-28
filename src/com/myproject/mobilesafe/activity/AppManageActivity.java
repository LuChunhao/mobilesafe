package com.myproject.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.AppInfo;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyUtil;
import com.stericson.RootTools.RootTools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManageActivity extends Activity implements OnClickListener {

	private TextView tv_sys_freespace;
	private TextView tv_sd_freespace;
	private TextView tv_appmanage_desc;
	private ListView lv_appmanage_list;

	private List<AppInfo> allAppInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> sysAppInfos;

	private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appmanage);

		act = this;

		tv_sys_freespace = (TextView) findViewById(R.id.tv_sys_freespace);
		tv_sd_freespace = (TextView) findViewById(R.id.tv_sd_freespace);
		tv_appmanage_desc = (TextView) findViewById(R.id.tv_appmanage_desc);
		lv_appmanage_list = (ListView) findViewById(R.id.lv_appmanage_list);

		// �����ֻ���SD��ʣ��ռ�
		tv_sys_freespace
				.setText("ʣ���ֻ��ռ䣺" + Formatter.formatFileSize(this, Environment.getDataDirectory().getFreeSpace()));
		tv_sd_freespace.setText(
				"ʣ��SD���ռ䣺" + Formatter.formatFileSize(this, Environment.getExternalStorageDirectory().getFreeSpace()));

		fillData();

		tv_appmanage_desc.setText("�û�����" + userAppInfos.size() + "��");

		// ��listView��ӹ��������¼�
		lv_appmanage_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
				if (firstVisibleItem >= userAppInfos.size()) {
					tv_appmanage_desc.setText("ϵͳӦ�ã�" + sysAppInfos.size() + "��");
				} else {
					tv_appmanage_desc.setText("�û�����" + userAppInfos.size() + "��");
				}
			}
		});

		// ��listView��ӵ���¼�
		lv_appmanage_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 1����ȡ�����Ŀ����
				clickedAppInfo = (AppInfo) parent.getItemAtPosition(position);
				// 2�� ����popupwindow����
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
				popupWindow = new PopupWindow(act);
				View contentView = View.inflate(act, R.layout.popup_app_item, null);

				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				ll_setting = (LinearLayout) contentView.findViewById(R.id.ll_setting);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);

				ll_uninstall.setOnClickListener(AppManageActivity.this);
				ll_setting.setOnClickListener(AppManageActivity.this);
				ll_share.setOnClickListener(AppManageActivity.this);
				ll_start.setOnClickListener(AppManageActivity.this);

				popupWindow.setContentView(contentView);
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				popupWindow.setWidth(-2); // -2 ��ʾ�������� -1��ʾ��丸����
				popupWindow.setHeight(-2);

				int[] location = new int[2];
				view.getLocationInWindow(location);

				popupWindow.showAtLocation(view, Gravity.LEFT + Gravity.TOP, 100, location[1]);

			}
		});
		
		receiver = new UninstallReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
	}

	private PopupWindow popupWindow;

	// �����ListView item����
	private AppInfo clickedAppInfo;

	private void fillData() {
		userAppInfos = new ArrayList<AppInfo>();
		sysAppInfos = new ArrayList<AppInfo>();

		// ��ȡ����Ӧ������
		allAppInfos = MyUtil.getAllAppInfo(this);
		for (AppInfo appInfo : allAppInfos) {
			if (appInfo.isSysApp()) {
				sysAppInfos.add(appInfo);
			} else {
				userAppInfos.add(appInfo);
			}
		}

		myBaseAdapter = new MyBaseAdapter();
		lv_appmanage_list.setAdapter(myBaseAdapter);
	}

	class ViewHolder {
		TextView tv_item_appmanage_desc;
		ImageView iv_appmanage_icon;
		TextView tv_item_appname;
		TextView tv_item_issysapp;
		TextView tv_item_appsize;
	}

	private MyBaseAdapter myBaseAdapter;

	private class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return allAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < userAppInfos.size()) {
				return userAppInfos.get(position);
			} else {
				return sysAppInfos.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(AppManageActivity.this, R.layout.item_appmanage_list, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_appmanage_icon = (ImageView) view.findViewById(R.id.iv_appmanage_icon);
				viewHolder.tv_item_appmanage_desc = (TextView) view.findViewById(R.id.tv_item_appmanage_desc);
				viewHolder.tv_item_appname = (TextView) view.findViewById(R.id.tv_item_appname);
				viewHolder.tv_item_appsize = (TextView) view.findViewById(R.id.tv_item_appsize);
				viewHolder.tv_item_issysapp = (TextView) view.findViewById(R.id.tv_item_issysapp);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			// AppInfo appInfo = allAppInfos.get(position);
			AppInfo appInfo;

			if (position < userAppInfos.size()) {
				appInfo = userAppInfos.get(position);
				viewHolder.tv_item_appmanage_desc.setText("�û�Ӧ�ã�" + userAppInfos.size() + "��");
			} else {
				// ���򣬾���ʾϵͳӦ���б���Ϣ
				appInfo = sysAppInfos.get(position - userAppInfos.size());
				viewHolder.tv_item_appmanage_desc.setText("ϵͳӦ�ã�" + sysAppInfos.size() + "��");
			}

			if (position == 0 || position == userAppInfos.size()) {
				viewHolder.tv_item_appmanage_desc.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tv_item_appmanage_desc.setVisibility(View.GONE);
			}

			if (appInfo.isStupInSD()) {
				viewHolder.tv_item_issysapp.setText("SD��");
			} else {
				viewHolder.tv_item_issysapp.setText("�ֻ��ڴ�");
			}
			viewHolder.iv_appmanage_icon.setBackgroundDrawable(appInfo.getIcon());
			// viewHolder.iv_appmanage_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_item_appname.setText(appInfo.getAppName());
			viewHolder.tv_item_appsize.setText(appInfo.getAppSize());
			LogUtil.i("----position------", position + "");

			return view;
		}

	}

	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private LinearLayout ll_setting;

	/**
	 * ������ж�ء��������ð�ť����¼�
	 */
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.ll_start:
			intent = getPackageManager().getLaunchIntentForPackage(clickedAppInfo.getPackageName());
			if (intent != null) {
				startActivity(intent);
			}
			break;

		case R.id.ll_uninstall:
			if (!clickedAppInfo.isSysApp()) {
				// <action android:name="android.intent.action.DELETE" />
				// <action
				// android:name="android.intent.action.UNINSTALL_PACKAGE" />
				// <category android:name="android.intent.category.DEFAULT" />
				// <data android:scheme="package" />
				intent = new Intent();
				intent.setAction("android.intent.action.UNINSTALL_PACKAGE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + clickedAppInfo.getPackageName()));
				startActivity(intent);
				
			} else {
				try {
					if (!RootTools.isRootAvailable()) {
						MyUtil.makeToast(act, "���ֻ�û��ROOT������ɾ��ϵͳӦ��");
						return;

					} else {
						if (!RootTools.isAccessGiven()) {
							MyUtil.makeToast(act, "����Ȩ������ʿrootȨ��");
							return;
						}

						RootTools.sendShell("mount -o remount ,rw /system", 3000);
						RootTools.sendShell("rm -r " + clickedAppInfo.getPackageName(), 30000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case R.id.ll_share:
//			 <action android:name="android.intent.action.SEND" />
//          <category android:name="android.intent.category.DEFAULT" />
//          <data android:mimeType="text/plain" />
			intent = new Intent();
			intent.setAction("android.intent.action.SEND");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "������㣺"+clickedAppInfo.getAppName());
			startActivity(intent);
			break;

		case R.id.ll_setting:
			intent = new Intent();
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:"+clickedAppInfo.getPackageName()));
			startActivity(intent);
			break;
		}

	}
	
	private UninstallReceiver receiver;
	
	private class UninstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String uninstallPackageName = intent.getData().toString();
			LogUtil.i("unInstallPackageName", uninstallPackageName);
			fillData();
		}
		
	}

}
