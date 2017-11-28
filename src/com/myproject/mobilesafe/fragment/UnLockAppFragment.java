package com.myproject.mobilesafe.fragment;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.db.LockAppDBUtil;
import com.myproject.mobilesafe.domain.AppInfo;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UnLockAppFragment extends Fragment {

	private List<AppInfo> allAppInfo;
	private List<AppInfo> showAppInfo;
	
	private ListView listView;
	private TextView tv_list_desc;
	
	private LockAppDBUtil ldbu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_unlock_app, null);
		
		ldbu = LockAppDBUtil.getInstance(getContext());
		
		listView = (ListView) view.findViewById(R.id.lv_unlock_app_list);
		tv_list_desc = (TextView) view.findViewById(R.id.tv_list_desc);

		allAppInfo = MyUtil.getAllAppInfo(getContext());

		showAppInfo = new ArrayList<AppInfo>();
		
		fillData();

		return view;
	}

	/**
	 * 加载ListView数据
	 */
	public void fillData() {
		
		for(AppInfo info : allAppInfo){
			if(ldbu.isAPPLocked(info.getPackageName())){
				LogUtil.i("已存在", info.getPackageName());
			}else{
				showAppInfo.add(info);
			}
		}
		
		tv_list_desc.setText("未加锁应用：" + showAppInfo.size() + "个");
		
		myAdapter = new MyAdapter(getContext(), R.layout.item_fragment_lockapp, showAppInfo);
		listView.setAdapter(myAdapter);
		
	}

	private MyAdapter myAdapter;

	private class MyAdapter extends ArrayAdapter<AppInfo> {

		public MyAdapter(Context context, int resource, List<AppInfo> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view;
			ViewHolder vh;
			if (convertView == null) {
				view = View.inflate(getContext(), R.layout.item_fragment_lockapp, null);
				vh = new ViewHolder();
				vh.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				vh.appName = (TextView) view.findViewById(R.id.tv_app_name);
				vh.isLocked = (ImageView) view.findViewById(R.id.iv_islocked);
				view.setTag(vh);
			} else {
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}
			
			final AppInfo appInfo = getItem(position);
			
			vh.icon.setBackgroundDrawable(appInfo.getIcon());
			vh.appName.setText(appInfo.getAppName());
			
			vh.isLocked.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 点击加锁图片后删除此Item并添加至数据库
					ldbu.addLockApp(appInfo.getPackageName());
					// 添加动画
					TranslateAnimation ta = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, 
							Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
					ta.setDuration(500);
					
					ta.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							showAppInfo.remove(appInfo);
							notifyDataSetChanged();
							tv_list_desc.setText("未加锁应用：" + showAppInfo.size() + "个");
							
						}
					});
					view.startAnimation(ta);
				}
			});
			return view;
		}
	}

	private class ViewHolder {
		private ImageView icon;
		private TextView appName;
		private ImageView isLocked;
	}

}
