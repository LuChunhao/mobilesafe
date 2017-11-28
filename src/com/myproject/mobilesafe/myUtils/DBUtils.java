package com.myproject.mobilesafe.myUtils;

import java.util.ArrayList;
import java.util.List;

import com.myproject.mobilesafe.db.MyDataBaseOpenHelper;
import com.myproject.mobilesafe.domain.BlackListInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
	
	private List<BlackListInfo> blackListInfos;
	
	private static DBUtils instance;
	
	private MyDataBaseOpenHelper helper;
	private DBUtils(Context context) {
		helper = new MyDataBaseOpenHelper(context, "blackList.db", 1);
	}

	public static synchronized DBUtils getInstance(Context context){
		if(instance == null){
			instance = new DBUtils(context);
		}
		return instance;
	}

	public long addBlackList(String number,String modleType){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("modletype", modleType);
		return db.insert(MyDataBaseOpenHelper.DBNAME, null, values );
	}
	
	public int removeBlackList(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		return db.delete(MyDataBaseOpenHelper.DBNAME, "number = " + number, null);
	}
	
	public int updateBlackList(String number,String modleType){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("modletype", modleType);
		return db.update(MyDataBaseOpenHelper.DBNAME, values, "number = " + number, null);
	}
	
	/**
	 * 查询所有数据
	 * @return
	 */
	public List<BlackListInfo> findAllBlackList(){
		blackListInfos = new ArrayList<BlackListInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(MyDataBaseOpenHelper.DBNAME, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String modleType = cursor.getString(cursor.getColumnIndex("modletype"));
			BlackListInfo balckInfo = new BlackListInfo(number, modleType);
			blackListInfos.add(balckInfo);
		}
		cursor.close();
		return blackListInfos;
	}
	
	/**
	 * 查询数据库总共多少记录
	 * @return
	 */
	public int getTotalCount(){
		int returnValue = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		//Cursor cursor = db.query(MyDataBaseOpenHelper.DBNAME, null, null, null, null, null, null);
		Cursor cursor = db.rawQuery("select count(*) from black_list", null);
		if(cursor.moveToNext()){
			returnValue = cursor.getInt(0);
		}
		cursor.close();
		return returnValue;
	}
	
	/**
	 * 分页显示数据
	 * @return
	 */
	public List<BlackListInfo> findBlackListByPage(String offset,String limitCont){
		blackListInfos = new ArrayList<BlackListInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		//Cursor cursor = db.query(MyDataBaseOpenHelper.DBNAME, null, null, null, null, null, null);
		Cursor cursor = db.rawQuery("select * from black_list order by _id desc limit ? offset ?", new String[]{limitCont,offset});
		while(cursor.moveToNext()){
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String modleType = cursor.getString(cursor.getColumnIndex("modletype"));
			BlackListInfo balckInfo = new BlackListInfo(number, modleType);
			blackListInfos.add(balckInfo);
		}
		cursor.close();
		return blackListInfos;
	}
	
	
	/**
	 * 根据黑名单号码获取拦截类型
	 * @param number
	 * @return modle
	 */
	public String getModleByNumber(String number){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(MyDataBaseOpenHelper.DBNAME, null, "number = " + number, null, null, null, null);
		if(cursor.moveToNext()){
			return cursor.getString(cursor.getColumnIndex("modletype"));
		}
		return "0";
	}
}
