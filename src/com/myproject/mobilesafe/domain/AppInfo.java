package com.myproject.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	/**
	 * 应用程序图标
	 */
	private Drawable icon;
	
	/**
	 * 应用程序名称
	 */
	private String appName;
	
	/**
	 * 应用程序大小
	 */
	private String appSize;
	
	/**
	 * 应用程序是否是系统应用
	 * true 是
	 * false 否
	 */
	private boolean isSysApp;
	
	/**
	 * 应用程序是否安装在SD卡
	 * true 是
	 * false 否
	 */
	private boolean isStupInSD;
	
	/**
	 * 应用程序包名
	 */
	
	private String packageName;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppSize() {
		return appSize;
	}

	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}

	public boolean isSysApp() {
		return isSysApp;
	}

	public void setSysApp(boolean isSysApp) {
		this.isSysApp = isSysApp;
	}

	public boolean isStupInSD() {
		return isStupInSD;
	}

	public void setStupInSD(boolean isStupInSD) {
		this.isStupInSD = isStupInSD;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", appName=" + appName + ", appSize=" + appSize + ", isSysApp=" + isSysApp
				+ ", isStupInSD=" + isStupInSD + ", packageName=" + packageName + "]";
	}
	
	
}
