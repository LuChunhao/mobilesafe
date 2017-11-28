package com.myproject.mobilesafe.test;

import com.myproject.mobilesafe.myUtils.DBUtils;

import android.test.AndroidTestCase;

public class TestDBUtils extends AndroidTestCase {
	
	public void testAdd(){
		DBUtils dbu = DBUtils.getInstance(getContext());
		for (int i = 0; i < 100; i++) {
			dbu.addBlackList("1380000000"+i, (int)(Math.random() * 3 + 1)+"");
		}
		
	}
	
	public void updateTest(){
		DBUtils dbu = DBUtils.getInstance(getContext());
		dbu.updateBlackList("1380000000", "2");
	}
	
	public void deleteTest(){
		DBUtils dbu = DBUtils.getInstance(getContext());
		dbu.removeBlackList("1380000000");
	}
}
