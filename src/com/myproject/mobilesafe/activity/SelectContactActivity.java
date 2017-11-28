package com.myproject.mobilesafe.activity;

import java.util.List;

import com.myproject.mobilesafe.R;
import com.myproject.mobilesafe.domain.ContactInfo;
import com.myproject.mobilesafe.myUtils.MyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {
	
	private ListView lv_contacts;
	
	private List<ContactInfo> contactInfos;
	
	private Activity act = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_contacts);
		
		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		
		contactInfos = MyUtil.getSysContacts(this);
		
		lv_contacts.setAdapter(new MyAdapter());
		
		//ListViewµã»÷ÊÂ¼þ
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent data = new Intent(act,Setup3Activity.class);
				String phone = contactInfos.get(position).getPhone();
				data.putExtra("phone", phone);
				setResult(11, data);
				finish();
			}
		});
		
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return contactInfos.size();
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
			TextView tv_contact = new TextView(act);
			ContactInfo contactInfo = contactInfos.get(position);
			String text = contactInfo.getName() + ":\t" + contactInfo.getPhone();
			tv_contact.setText(text);
			tv_contact.setLines(1);
			tv_contact.setTextSize(20);
			return tv_contact;
		}
		
	}
}
