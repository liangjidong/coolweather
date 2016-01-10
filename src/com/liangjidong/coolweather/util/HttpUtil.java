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
		// 连接网络操作比较耗时，开一个子线程工作
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 创建url
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					// 获取到连接之后就可以获得输入流
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					// 新建一个stringbuilder用于接收字符串
					StringBuilder builder = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					// 到这里如果一切顺利，就可以获取到结果，通过回调函数将结果返回给需要的程序中去
					if (listener != null)
						listener.onFinish(builder.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// 出现异常调用onError方法告知相应程序出错
					if (listener != null)
						listener.onError(e);
					else {
						e.printStackTrace();
					}
				} finally {
					if (connection != null) {
						// 关闭连接
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
