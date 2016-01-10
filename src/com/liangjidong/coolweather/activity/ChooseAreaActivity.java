package com.liangjidong.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liangjidong.coolweather.R;
import com.liangjidong.coolweather.model.City;
import com.liangjidong.coolweather.model.Country;
import com.liangjidong.coolweather.model.Province;
import com.liangjidong.coolweather.util.CoolWeatherDB;
import com.liangjidong.coolweather.util.HttpCallbackListener;
import com.liangjidong.coolweather.util.HttpUtil;
import com.liangjidong.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity {
	// ���������������������ֵ�ǰlistview����ʾ�����ĸ�����������б�
	public static final int LEVEL_PROVINCE = 0;// listview����ʾ����ʡ����Ϣ
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinces;
	private List<City> cities;
	private List<Country> countries;
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;

	private int currentLevel;

	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weather_activity", false);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("city_selected", false)
				&& !isFromWeatherActivity) {
			// ��⵽�ϴ��Ѿ�ѡ�������򣬱���ֱ�ӽ��뵽��ʾ����
			Log.d("ChooseAreaActivity", "�Ѿ�ѡ���˳���");
			Intent intent = new Intent(ChooseAreaActivity.this,
					WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		titleText = (TextView) this.findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.listview);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinces.get(position);
					queryCits();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cities.get(position);
					queryCountries();
				} else if (currentLevel == LEVEL_COUNTRY) {
					selectedCountry = countries.get(position);
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("country_code",
							selectedCountry.getCountryCode());
					startActivity(intent);
					finish();
				}

			}
		});
		queryProvinces();
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��в�ѯ��û�����ݣ���������л�ȡ
	 */
	private void queryProvinces() {
		provinces = coolWeatherDB.loadProvinces();// ��ȡ���ݿ��е���Ϣ
		if (provinces.size() > 0) {
			// ���ݿ���������
			dataList.clear();
			for (Province province : provinces) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			// �������л�ȡȫ������ʡ�����µ����ݿ���
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯĳ��ʡ�����������
	 */
	private void queryCits() {
		cities = coolWeatherDB.loadCities(selectedProvince.getId());// ��ȡ���ݿ��е���Ϣ
		if (cities.size() > 0) {
			// ���ݿ���������
			dataList.clear();
			for (City city : cities) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			// �������л�ȡ�����µ����ݿ���
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCountries() {
		countries = coolWeatherDB.loadCountries(selectedCity.getId());// ��ȡ���ݿ��е���Ϣ
		if (countries.size() > 0) {
			// ���ݿ���������
			dataList.clear();
			for (Country country : countries) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			// �������л�ȡ�����µ����ݿ���
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	/**
	 * �ӷ������˻�ȡʡ���أ������б�
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			// ��ȡ��/��
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			// ��ȡʡ
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// �������ַ������ݺ����Ƚ����ݴ洢�����ݿ���
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleCountriesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// �����Ѿ��洢�����ݿ�����
					// ʹ��runOnUiThread�����л��̵߳�ui�̣߳�����listview��
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCits();
							} else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();

						Toast.makeText(ChooseAreaActivity.this, "��������ʧ��", 1000)
								.show();
					}
				});
			}

		});

	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCits();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				// ��weatherActivity��ת������
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
