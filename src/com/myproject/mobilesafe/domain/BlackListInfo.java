package com.myproject.mobilesafe.domain;

public class BlackListInfo {

	private String number;
	private String modleType;

	public BlackListInfo(String number, String modleType) {
		super();
		this.number = number;
		this.modleType = modleType;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getModleType() {
		return modleType;
	}

	public void setModleType(String modleType) {
		this.modleType = modleType;
	}

}
