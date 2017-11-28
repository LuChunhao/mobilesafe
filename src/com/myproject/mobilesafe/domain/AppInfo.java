package com.myproject.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	/**
	 * Ӧ�ó���ͼ��
	 */
	private Drawable icon;
	
	/**
	 * Ӧ�ó�������
	 */
	private String appName;
	
	/**
	 * Ӧ�ó����С
	 */
	private String appSize;
	
	/**
	 * Ӧ�ó����Ƿ���ϵͳӦ��
	 * true ��
	 * false ��
	 */
	private boolean isSysApp;
	
	/**
	 * Ӧ�ó����Ƿ�װ��SD��
	 * true ��
	 * false ��
	 */
	private boolean isStupInSD;
	
	/**
	 * Ӧ�ó������
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
