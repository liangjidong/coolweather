package com.liangjidong.coolweather.util;

import java.util.ArrayList;
import java.util.List;

import com.liangjidong.coolweather.db.CoolWeatherOpenHelper;
import com.liangjidong.coolweather.model.City;
import com.liangjidong.coolweather.model.Country;
import com.liangjidong.coolweather.model.Province;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本号
	 */
	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 使用单例模式，将构造函数私有化
	 * 
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
	}

	/**
	 * 获取数据库操作类的实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			return new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 向数据库中存储一条省份信息
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			db.execSQL("insert into Province values(null,'"
					+ province.getProvinceName() + "','"
					+ province.getProvinceCode() + "')");
		}
	}

	/**
	 * 获取所有省份信息存储到list中
	 * 
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.rawQuery("select * from Province", null);
		while (cursor.moveToNext()) {
			Province province = new Province(cursor.getInt(cursor
					.getColumnIndex("id")), cursor.getString(cursor
					.getColumnIndex("province_name")), cursor.getString(cursor
					.getColumnIndex("province_code")));
			list.add(province);
		}
		return list;
	}

	/**
	 * 向数据库中存储一条city信息
	 * 
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			db.execSQL("insert into City values(null,'" + city.getCityName()
					+ "','" + city.getCityCode() + "'," + city.getProvinceId()
					+ ")");
		}
	}

	/**
	 * 根据省份id取出该省的所有市信息
	 * 
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.rawQuery("select * from City where province_id=?",
				new String[] { String.valueOf(provinceId) });
		while (cursor.moveToNext()) {
			City city = new City(cursor.getInt(cursor.getColumnIndex("id")),
					provinceId, cursor.getString(cursor
							.getColumnIndex("city_name")),
					cursor.getString(cursor.getColumnIndex("city_code")));
			list.add(city);
		}
		return list;
	}

	/**
	 * 存储一条县的信息
	 * 
	 * @param country
	 */
	public void saveCountry(Country country) {
		if (country != null) {
			db.execSQL("insert into Country values(null,'"
					+ country.getCountryName() + "','" + country.getCountryCode()
					+ "'," + country.getCityId() + ")");
		}
	}

	/**
	 * 根据市id获取到该市下辖的所有县
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Country> loadCountries(int cityId) {
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.rawQuery("select * from Country where city_id=?",
				new String[] { String.valueOf(cityId) });
		while (cursor.moveToNext()) {
			Country country = new Country(cursor.getInt(cursor
					.getColumnIndex("id")), cityId, cursor.getString(cursor
					.getColumnIndex("country_name")), cursor.getString(cursor
					.getColumnIndex("country_code")));
			list.add(country);
		}
		return list;
	}
}
