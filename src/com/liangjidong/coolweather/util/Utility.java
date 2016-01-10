package com.liangjidong.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangjidong.coolweather.model.City;
import com.liangjidong.coolweather.model.Country;
import com.liangjidong.coolweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	/**
	 * 处理服务器返回的省级数据存入数据库
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province(0, array[1], array[0]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 将获取到的城市信息存入数据库
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City(0, provinceId, array[1], array[0]);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 将获取到的城市下面的县级信息存入数据库
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountries = response.split(",");
			if (allCountries != null && allCountries.length > 0) {
				for (String c : allCountries) {
					String[] array = c.split("\\|");
					Country country = new Country(0, cityId, array[1], array[0]);
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}

	public static void handleWeatherResponse(Context context, String response) {
		/*
		 * {"weatherinfo":{"city":"北京","cityid":"101010100",
		 * "temp1":"15℃","temp2":"5℃","weather":"多云",
		 * "img1":"d1.gif","img2":"n1.gif","ptime":"08:00"}}
		 */

		try {
			JSONObject object = new JSONObject(response);
			JSONObject weatherObject = object.getJSONObject("weatherinfo");
			String cityName = weatherObject.getString("city");
			String weatherCode = weatherObject.getString("cityid");
			String temp1 = weatherObject.getString("temp1");
			String temp2 = weatherObject.getString("temp2");
			String weatherDesp = weatherObject.getString("weather");
			String publicTime = weatherObject.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publicTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publicTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);//表示首次已经选择了城市，第二次默认直接进入显示天气的activity
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("public_time", publicTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();

	}
}
