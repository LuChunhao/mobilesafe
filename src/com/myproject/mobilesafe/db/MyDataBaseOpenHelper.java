package com.myproject.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseOpenHelper extends SQLiteOpenHelper {
	
	public static String DBNAME = "black_list";

	public MyDataBaseOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table black_list(_id integer primary key autoincrement ,number varchar(20),modletype var(1));");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
