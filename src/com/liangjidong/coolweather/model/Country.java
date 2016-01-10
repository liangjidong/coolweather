package com.liangjidong.coolweather.model;

public class Country {
	private int id;
	private int cityId;
	private String countryName;
	private String countryCode;

	public Country(int id, int cityId, String countryName, String countryCode) {
		super();
		this.id = id;
		this.cityId = cityId;
		this.countryName = countryName;
		this.countryCode = countryCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

}
