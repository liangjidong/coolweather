package com.liangjidong.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		// ������������ȽϺ�ʱ����һ�����̹߳���
		new Thread(new Runnable() {

			@Override
			public void run() {
				// ����url
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					// ��ȡ������֮��Ϳ��Ի��������
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					// �½�һ��stringbuilder���ڽ����ַ���
					StringBuilder builder = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					// ���������һ��˳�����Ϳ��Ի�ȡ�������ͨ���ص�������������ظ���Ҫ�ĳ�����ȥ
					if (listener != null)
						listener.onFinish(builder.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// �����쳣����onError������֪��Ӧ�������
					if (listener != null)
						listener.onError(e);
					else {
						e.printStackTrace();
					}
				} finally {
					if (connection != null) {
						// �ر�����
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
