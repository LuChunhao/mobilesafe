package com.myproject.mobilesafe.myUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

public class SmsDBUtils {

	public interface SmsCallBack {
		/**
		 * 总进度
		 * 
		 * @param totalSize
		 */
		void totalProgress(int totalSize);

		/**
		 * 当前进度
		 * 
		 * @param curProgress
		 */
		void currentProgress(int curProgress);
	}

	/**
	 * 备份短信到SD卡
	 * 
	 * @param act
	 * @param callBack
	 * @return
	 * @throws Exception
	 */
	public static boolean backupSms(Activity act, SmsCallBack callBack) throws Exception {
		// 1、检查SD卡是否存在
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// SD卡不存在
			MyUtil.makeToast(act, "未检测到SD卡，请检查");
			return false;
		}

		if (Environment.getExternalStorageDirectory().getFreeSpace() < 1024l * 1024l) {
			// SD空间不足
			MyUtil.makeToast(act, "SD卡空间不足");
			return false;
		}

		File file = new File(Environment.getExternalStorageDirectory(), "backup_sms.xml");
		FileOutputStream fos = new FileOutputStream(file);

		// 2、获取xml解析器
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		// 3、利用内容提供者访问短信数据库
		Uri uri = Uri.parse("content://sms/");
		String[] projection = new String[] { "address", "date", "type", "body" };
		Cursor cursor = act.getContentResolver().query(uri, projection, null, null, null);

		serializer.attribute(null, "totalsize", cursor.getCount() + "");
		callBack.totalProgress(cursor.getCount());
		int currentProgress = 0;

		// 4、拼凑xml内容
		while (cursor.moveToNext()) {
			serializer.startTag(null, "sms");

			serializer.startTag(null, "address");
			serializer.text(cursor.getString(0));
			serializer.endTag(null, "address");

			serializer.startTag(null, "date");
			serializer.text(cursor.getString(1));
			serializer.endTag(null, "date");

			serializer.startTag(null, "type");
			serializer.text(cursor.getString(2));
			serializer.endTag(null, "type");

			serializer.startTag(null, "body");
			String body = cursor.getString(3);
			body = Crypto.encrypt("123456", body);
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.endTag(null, "sms");

			currentProgress++;
			callBack.currentProgress(currentProgress);
			SystemClock.sleep(500);
		}

		serializer.endTag(null, "smss");
		serializer.endDocument();

		serializer.flush();

		fos.flush();
		fos.close();
		return true;
	}

	/**
	 * 从SD卡还原短信
	 * 
	 * @param act
	 * @param callBack
	 * @return
	 * @throws Exception
	 */
	public static boolean restoreSms(Activity act, SmsCallBack callBack) throws Exception {

		File file = new File(Environment.getExternalStorageDirectory(), "backup_sms.xml");
		if(!file.exists()){
			// 未找到备份文件，提示请先备份
			MyUtil.makeToast(act, "请先备份短信再还原");
			return false;
		}
		FileInputStream fis = new FileInputStream(file);

		// 利用内容提供者访问短信数据库
		Uri uri = Uri.parse("content://sms/");

		// 获取xml解析器
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");

		ContentValues values = null;
		
		int curProgress = 0;
		
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			
			switch (type) {
			case XmlPullParser.START_TAG:
				if("smss".equals(parser.getName())){
					String totalPregress = parser.getAttributeValue(0);
					callBack.totalProgress(Integer.parseInt(totalPregress));
					LogUtil.i("---total---", totalPregress);
				}
				if ("sms".equals(parser.getName())) {
					values = new ContentValues();
				}else if("address".equals(parser.getName())){
					values.put("address", parser.nextText());
				}else if("date".equals(parser.getName())){
					values.put("date", parser.nextText());
				}else if("type".equals(parser.getName())){
					values.put("type", parser.nextText());
				}else if("body".equals(parser.getName())){
					String body = parser.nextText();
					body = Crypto.decrypt("123456", body);
					values.put("body", body);
				}
				break;

			case XmlPullParser.END_TAG:
				if("sms".equals(parser.getName())){
					LogUtil.i("------------", (String) values.get("body"));
					act.getContentResolver().insert(uri, values);
					values = null;
					
					curProgress ++;
					callBack.currentProgress(curProgress);
					SystemClock.sleep(500);
				}
				break;
			default:
				break;
			}
			
			type = parser.next();
			
		}
		
		// 还原完毕后删除备份文件，避免重复操作
		file.delete();
		return true;
	}

}
