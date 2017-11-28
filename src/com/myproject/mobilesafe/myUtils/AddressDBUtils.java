package com.myproject.mobilesafe.myUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDBUtils {

	private static SQLiteDatabase db;

	public static String getAddressByNumber(Context context, String number) {
		String address = "未知地区";
		Cursor cursor;
		boolean isMobleNumber = number.matches("^1[35789][0-9]{9}$");
		db = SQLiteDatabase.openDatabase(context.getFilesDir() + "/address.db", null, SQLiteDatabase.OPEN_READONLY);
		if (isMobleNumber) {
			cursor = db.rawQuery("select * from data2 where id = (select outkey from data1 where id = ?)",
					new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(cursor.getColumnIndex("location"));
			}
		} else {
			switch (number.length()) {
			case 3:
				if ("110".contentEquals(number)) {
					address = "报警电话";
					break;
				}
				if ("120".equals(number)) {
					address = "急救电话";
					break;
				}
				if ("119".equals(number)) {
					address = "火警电话";
				}
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			default:
				if (number.length() >= 10 && number.startsWith("0")) {
					String key1 = number.substring(1, 3);
					Cursor cursor1 = db.rawQuery("select * from data2 where area = ?", new String[] { key1 });
					if (cursor1.moveToNext()) {
						address = cursor1.getString(cursor1.getColumnIndex("location"));
					} else {
						String key2 = number.substring(1, 4);
						Cursor cursor2 = db.rawQuery("select * from data2 where area = ?", new String[] { key2 });
						if (cursor2.moveToNext()) {
							address = cursor2.getString(cursor2.getColumnIndex("location"));
						}
					}
					address = address.substring(0, address.length() - 2);
				}
				if (number.length() > 12) {
					address = "未知地区";
				}
				break;
			}
		}

		return address;
	}

}
