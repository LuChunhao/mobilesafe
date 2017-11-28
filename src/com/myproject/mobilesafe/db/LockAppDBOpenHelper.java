package com.myproject.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LockAppDBOpenHelper extends SQLiteOpenHelper{
	
	public static String DBNAME = "lock_app";

	public LockAppDBOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table lock_app(_id integer primary key autoincrement ,"
				+ "package_name varchar(30));");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
