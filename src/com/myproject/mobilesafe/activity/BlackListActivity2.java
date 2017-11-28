package com.myproject.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.BlackListInfo;
import com.myproject.mobilesafe.myUtils.DBUtils;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BlackListActivity2 extends Activity implements OnClickListener {

	protected static final int COMPLETE = 1;

	protected static final int NOTHING = 0;

	private Activity ctx;

	private List<BlackListInfo> blackInfos;

	private ListView lv_balck_list;
	private ProgressBar pb_loading;
	private TextView tv_loading;
	private LinearLayout ll_page_control;
	private EditText et_dest_page;
	private TextView tv_show_page;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == COMPLETE) {
				lv_balck_list.setVisibility(View.VISIBLE);
				lv_balck_list.setAdapter(new MyAdapter(ctx, R.layout.item_balck_list, blackInfos));
				pb_loading.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
				ll_page_control.setVisibility(View.VISIBLE);
				//修改页数显示
				et_dest_page.setText(""+currentPage);
				tv_show_page.setText(currentPage + " / " + totalPage);
			} else if (msg.what == NOTHING) {
				pb_loading.setVisibility(View.GONE);
				tv_loading.setText("暂时没有黑名单数据请添加！");
			}
		};
	};
	
	/**
	 * 分页显示
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_balcklist2);
		ctx = this;

		lv_balck_list = (ListView) findViewById(R.id.lv_balck_list);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		ll_page_control = (LinearLayout) findViewById(R.id.ll_page_control);
		et_dest_page = (EditText) findViewById(R.id.et_dest_page);
		tv_show_page = (TextView) findViewById(R.id.tv_show_page);

		findViewById(R.id.bt_pre_page).setOnClickListener(this);
		findViewById(R.id.bt_next_page).setOnClickListener(this);
		findViewById(R.id.bt_goto_page).setOnClickListener(this);

		blackInfos = new ArrayList<BlackListInfo>();

		fillData();

	}

	private int currentPage = 1;
	private int limitCount = 20;
	private int totalCount;
	private int totalPage;

	private void fillData() {
		tv_loading.setVisibility(View.VISIBLE);
		pb_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
					DBUtils dbu = DBUtils.getInstance(ctx);
					// blackInfos = dbu.findAllBlackList();
					blackInfos = dbu.findBlackListByPage((currentPage - 1) * limitCount + "", limitCount + "");
					totalCount = dbu.getTotalCount();
					if(totalCount % limitCount == 0){
						totalPage = totalCount / limitCount;
					}else{
						totalPage = totalCount / limitCount + 1;
					}
					int what;
					if (totalCount > 0) {
						what = COMPLETE;
					} else {
						what = NOTHING;
					}
					handler.sendEmptyMessage(what);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	private class MyAdapter extends ArrayAdapter<BlackListInfo> {

		private int resourceId;

		public MyAdapter(Context context, int resource, List<BlackListInfo> objects) {
			super(context, resource, objects);
			resourceId = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(ctx, resourceId, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_black_modle = (TextView) view.findViewById(R.id.tv_black_modle);
				viewHolder.tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			
			BlackListInfo blackInfo = blackInfos.get(position);

			viewHolder.tv_black_number.setText(blackInfo.getNumber());
			String type = blackInfo.getModleType();
			switch (Integer.parseInt(type)) {
			case 1:
				viewHolder.tv_black_modle.setText("短信拦截");
				break;

			case 2:
				viewHolder.tv_black_modle.setText("电话拦截");
				break;

			case 3:
				viewHolder.tv_black_modle.setText("全部拦截");
				break;

			default:
				break;
			}

			return view;
		}

	}
	
	private class ViewHolder{
		private TextView tv_black_number;
		private TextView tv_black_modle;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_pre_page:
			if(currentPage > 1){
				currentPage --;
				fillData();
			}else{
				MyUtil.makeToast(ctx, "已经是第一页了");
			}
			break;

		case R.id.bt_next_page:
			if(currentPage < totalPage){
				currentPage ++;
				fillData();
			}else{
				MyUtil.makeToast(ctx, "已经是最后一页了");
			}
			break;

		case R.id.bt_goto_page:
			String destPageStr = et_dest_page.getText().toString().trim();
			int destPageIndex = Integer.parseInt(destPageStr);
			if(destPageIndex>=1 && destPageIndex <= totalPage){
				currentPage = destPageIndex;
				fillData();
			}else{
				MyUtil.makeToast(ctx, "输入有误");
			}
			
			break;

		default:
			break;
		}

	}
}
