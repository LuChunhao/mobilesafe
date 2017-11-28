package com.myproject.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.BlackListInfo;
import com.myproject.mobilesafe.myUtils.DBUtils;
import com.myproject.mobilesafe.myUtils.LogUtil;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BlackListActivity extends Activity {

	protected static final int COMPLETE = 1;

	protected static final int NOTHING = 0;

	private Activity ctx;

	private List<BlackListInfo> blackInfos;

	private ListView lv_balck_list;
	private ProgressBar pb_loading;
	private TextView tv_loading;
	
	private DBUtils dbu;

	private MyAdapter myAdapter;
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == COMPLETE) {
				lv_balck_list.setVisibility(View.VISIBLE);
				if(myAdapter == null){
					myAdapter = new MyAdapter(ctx, R.layout.item_balck_list, blackInfos);
					lv_balck_list.setAdapter(myAdapter);
				}else{
					myAdapter.notifyDataSetChanged();
				}
				
				pb_loading.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
			} else if (msg.what == NOTHING) {
				pb_loading.setVisibility(View.GONE);
				tv_loading.setText("��ʱû�к�������������ӣ�");
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_balcklist);
		ctx = this;

		lv_balck_list = (ListView) findViewById(R.id.lv_balck_list);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		tv_loading = (TextView) findViewById(R.id.tv_loading);

		blackInfos = new ArrayList<BlackListInfo>();
		dbu = DBUtils.getInstance(ctx);
		fillData();
		/**
		 * ListView�����¼�
		 */
		lv_balck_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE){
					int lastPosition = lv_balck_list.getLastVisiblePosition();
					//LogUtil.d("lastPosition==", lastPosition+"");
					//LogUtil.d("myAdapter==", myAdapter.getCount()+"");
					if(lastPosition == myAdapter.getCount()-1){
						if(currentPage < totalPage){
							currentPage ++;
							fillData();
						}else{
							MyUtil.makeToast(ctx, "�Ѿ�û�и���������");
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/**
		 * ListView����¼�
		 */
		lv_balck_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BlackListInfo blackInfo = blackInfos.get(position);
				String number = blackInfo.getNumber();
				final String hisModle = blackInfo.getModleType();
				AlertDialog.Builder builder = new Builder(ctx);
				dialog = builder.create();
				View dview = getLayoutInflater().inflate(R.layout.dialog_add_black_num, null);
				dialog.setView(dview, 0, 0, 0, 0);
				dialog.show();
				
				final int newPosition = position;
				final EditText et_black_num = (EditText) dview.findViewById(R.id.et_black_num);
				final CheckBox cb_intercept_phone = (CheckBox) dview.findViewById(R.id.cb_intercept_phone);
				final CheckBox cb_intercept_sms = (CheckBox) dview.findViewById(R.id.cb_intercept_sms);
				
				//չʾԭ����Ϣ
				et_black_num.setText(number);
				et_black_num.setEnabled(false);
				if("1".equals(hisModle)){
					cb_intercept_sms.setChecked(true);
					cb_intercept_phone.setChecked(false);
				}else if("2".equals(hisModle)){
					cb_intercept_sms.setChecked(false);
					cb_intercept_phone.setChecked(true);
				}else if("3".equals(hisModle)){
					cb_intercept_sms.setChecked(true);
					cb_intercept_phone.setChecked(true);
				}
				
				Button bt_ok = (Button) dview.findViewById(R.id.bt_ok);
				Button bt_cancel = (Button) dview.findViewById(R.id.bt_cancel);
				bt_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String number = et_black_num.getText().toString().trim();
						String modleType = "0";
						if(TextUtils.isEmpty(number)){
							MyUtil.makeToast(ctx, "���������������");
							return ;
						}
						if(!cb_intercept_phone.isChecked() && !cb_intercept_sms.isChecked()){
							MyUtil.makeToast(ctx, "��ѡ����������");
							return ;
						}else if(cb_intercept_phone.isChecked() && !cb_intercept_sms.isChecked()){
							modleType = "2";
						}else if(!cb_intercept_phone.isChecked() && cb_intercept_sms.isChecked()){
							modleType = "1";
						}else if(cb_intercept_phone.isChecked() && cb_intercept_sms.isChecked()){
							modleType = "3";
						}
						//�������ݿ�����
						dbu.updateBlackList(number, modleType);
						//����blackInfos
						blackInfos.get(newPosition).setModleType(modleType);
						myAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
				
				bt_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						
					}
				});
			}
		});
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
					
					// blackInfos = dbu.findAllBlackList();
					if(blackInfos == null){
						blackInfos = dbu.findBlackListByPage((currentPage - 1) * limitCount + "", limitCount + "");
					}else{
						//blackInfos.addAll(dbu.findBlackListByPage((currentPage - 1) * limitCount + "", limitCount + ""));
						blackInfos.addAll(dbu.findBlackListByPage(blackInfos.size() + "", limitCount + ""));
					}
					
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
				viewHolder.iv_remove_item = (ImageView) view.findViewById(R.id.iv_remove_item);
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
				viewHolder.tv_black_modle.setText("��������");
				break;

			case 2:
				viewHolder.tv_black_modle.setText("�绰����");
				break;

			case 3:
				viewHolder.tv_black_modle.setText("ȫ������");
				break;

			default:
				break;
			}

			//���ɾ���¼�
			final int newPosition = position;
			viewHolder.iv_remove_item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BlackListInfo blackInfo = blackInfos.get(newPosition);
					dbu.removeBlackList(blackInfo.getNumber());
					blackInfos.remove(blackInfo);
					myAdapter.notifyDataSetChanged();
//					blackInfos.removeAll(blackInfos);
//					fillData();
				}
			});
			return view;
		}

	}
	
	private class ViewHolder{
		private TextView tv_black_number;
		private TextView tv_black_modle;
		private ImageView iv_remove_item;
	}
	
	private AlertDialog dialog;
	/**
	 * ��Ӻ�������ť����¼�
	 */
	public void addBlackNum(View v){
		AlertDialog.Builder builder = new Builder(ctx);
		dialog = builder.create();
		View view = getLayoutInflater().inflate(R.layout.dialog_add_black_num, null);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		
		final EditText et_black_num = (EditText) view.findViewById(R.id.et_black_num);
		final CheckBox cb_intercept_phone = (CheckBox) view.findViewById(R.id.cb_intercept_phone);
		final CheckBox cb_intercept_sms = (CheckBox) view.findViewById(R.id.cb_intercept_sms);
		Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		//���ȷ����ť
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String number = et_black_num.getText().toString().trim();
				String modleType = "0";
				if(TextUtils.isEmpty(number)){
					MyUtil.makeToast(ctx, "���������������");
					return ;
				}
				if(!cb_intercept_phone.isChecked() && !cb_intercept_sms.isChecked()){
					MyUtil.makeToast(ctx, "��ѡ����������");
					return ;
				}else if(cb_intercept_phone.isChecked() && !cb_intercept_sms.isChecked()){
					modleType = "2";
				}else if(!cb_intercept_phone.isChecked() && cb_intercept_sms.isChecked()){
					modleType = "1";
				}else if(cb_intercept_phone.isChecked() && cb_intercept_sms.isChecked()){
					modleType = "3";
				}
				if(!dbu.getModleByNumber(number).equals("0")){
					MyUtil.makeToast(ctx, "�ú����������Ѵ���");
					return ;
				}
				//��������������ݿ�
				dbu.addBlackList(number, modleType);
				// �����������BlackList
//				BlackListInfo balckInfo = new BlackListInfo(number, modleType);
//				blackInfos.add(0,balckInfo);
//				myAdapter.notifyDataSetChanged();
				blackInfos.removeAll(blackInfos);
				fillData();
				dialog.dismiss();
			}
		});
		//���ȡ����ť
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	
	
}
