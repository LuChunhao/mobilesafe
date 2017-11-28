package com.myproject.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class ProgressInfo {
	
	private Drawable icon;
	
	private String progressName;
	
	private String packageName;
	
	private boolean isSysProgress;
	
	/**
	 * ´óÐ¡ÎªKB
	 */
	private int memorySize;
	
	private boolean isChecked = false;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getProgressName() {
		return progressName;
	}

	public void setProgressName(String progressName) {
		this.progressName = progressName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSysProgress() {
		return isSysProgress;
	}

	public void setSysProgress(boolean isSysProgress) {
		this.isSysProgress = isSysProgress;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	

}
