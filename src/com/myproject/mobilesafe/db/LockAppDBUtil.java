package com.myproject.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LockAppDBUtil {

	public static String TABLENAME = "lock_app";

	private static LockAppDBUtil instance;

	private LockAppDBOpenHelper helper;
	
	private Context ctx;

	private LockAppDBUtil(Context context) {
		helper = new LockAppDBOpenHelper(context, "lock_app.db", 1);
		ctx = context;
	}

	public static synchronized LockAppDBUtil getInstance(Context context) {
		if (instance == null) {
			instance = new LockAppDBUtil(context);
		}
		return instance;
	}

	// 增加
	public long addLockApp(String packageName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("package_name", packageName);
		long insert = db.insert(TABLENAME, "_id", values);
		ctx.getContentResolver().notifyChange(Uri.parse("content://com.mypooject.lockapp.changed"), null);
		return insert;
	}

	// 删除
	public int removeLockApp(String packageName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		int delete = db.delete(TABLENAME, "package_name = ?", new String[]{packageName});
		ctx.getContentResolver().notifyChange(Uri.parse("content://com.mypooject.lockapp.changed"), null);
		return delete;
	}

	/**
	 * 根据包名判断该app是否加锁
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean isAPPLocked(String packageName) {

		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor cursor = db.query(TABLENAME, null, "package_name = ?", new String[]{packageName}, null, null, null);
		
		if(cursor.moveToNext()){
			return true;
		}
		
		return false;
	}

	// 查询所有
	public List<String> getAllLockedAppInfo() {
		List<String> infoList = new ArrayList<String>();

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db.query(TABLENAME, null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			String packageName = cursor.getString(cursor.getColumnIndex("package_name"));
			infoList.add(packageName);
		}
		cursor.close();
		db.close();
		return infoList;
	}
}
