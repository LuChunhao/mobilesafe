package com.myproject.mobilesafe.myUtils;

public interface HttpCallbackListener {
	public void onFinish(String response);
	
	public void onError(Exception e);
	
}
