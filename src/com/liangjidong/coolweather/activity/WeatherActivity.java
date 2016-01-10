package com.liangjidong.coolweather.activity;

import com.liangjidong.coolweather.R;
import com.liangjidong.coolweather.service.AutoUpdateService;
import com.liangjidong.coolweather.util.HttpCallbackListener;
import com.liangjidong.coolweather.util.HttpUtil;
import com.liangjidong.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements View.OnClickListener {
	private TextView titleArea;
	private TextView timeWhen;
	private TextView timeDay;
	private TextView weatherState;
	private TextView weatherLeft;
	private TextView weatherRight;
	private LinearLayout layoutShowInfo;

	private Button weatherRefresh;
	private Button chooseAnotherArea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化成员变量
		titleArea = (TextView) findViewById(R.id.title_area);
		timeWhen = (TextView) findViewById(R.id.time_when);
		timeDay = (TextView) findViewById(R.id.time_day);
		weatherState = (TextView) findViewById(R.id.weather_state);
		weatherLeft = (TextView) findViewById(R.id.weather_left);
		weatherRight = (TextView) findViewById(R.id.weather_right);
		layoutShowInfo = (LinearLayout) findViewById(R.id.layout_showInfo);
		weatherRefresh = (Button) findViewById(R.id.weather_refresh);
		weatherRefresh.setOnClickListener(this);
		chooseAnotherArea = (Button) findViewById(R.id.choose_another_area);
		chooseAnotherArea.setOnClickListener(this);
		// 获取到地区code，然后用code获得天气code，最后通过天气code获得天气情况（json格式）
		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)) {
			// 获取天气code
			timeWhen.setText("正在更新中.....");
			layoutShowInfo.setVisibility(View.INVISIBLE);
			titleArea.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			showWeather();
		}
	}

	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");// 根据countryCode查询到weatherCode
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/adat/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");// 根据countryCode查询到weatherCode
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countryCode".equals(type)) {
					// 解析返回的字符串获得weatherCode
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
					// 返回的是weatherCode
				} else if ("weatherCode".equals(type)) {
					// 返回的是天气字符串
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void showWeather() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(WeatherActivity.this);
		titleArea.setText(sp.getString("city_name", ""));
		timeWhen.setText(sp.getString("current_date", "")
				+ sp.getString("public_time", "") + "更新");
		timeDay.setText(sp.getString("current_date", ""));
		weatherState.setText(sp.getString("weather_desp", ""));
		weatherLeft.setText(sp.getString("temp2", ""));
		weatherRight.setText(sp.getString("temp1", ""));
		layoutShowInfo.setVisibility(View.VISIBLE);
		titleArea.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_another_area:
			// 选择城市
			Intent intent = new Intent(WeatherActivity.this,
					ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			this.finish();
			break;
		case R.id.weather_refresh:
			// 刷新天气
			timeWhen.setText("正在更新中.....");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = preferences.getString("weather_code", "");
			queryWeatherInfo(weatherCode);
			break;
		}

	}
}
