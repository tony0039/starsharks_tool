package com.g.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * HTTP請求工具類
 * 
 * 
 */
public class HttpUtil {
	/**
	 * get請求
	 * 
	 * @return
	 */
	public static String doGet(String url) {
		try {
			// 建立CloseableHttpClient
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
			// 執行
			HttpUriRequest httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
			httpGet.setHeader("content-type","application/json; charset=UTF-8");
			httpGet.setHeader("cf-ray","6eab66102da80cd7-LAX");
			httpGet.setHeader("report-to","{'endpoints':[{'url':'https://a.nel.cloudflare.com/report/v3?s=i3aifx54lHYvUw05M4hvEmM7JFRkzlVt2dQHYORtr1nC7fAZh9xMt802H47foFaBvoCcE7uIzhzC8QhVdlkdT9kU0h2LOagBPLk6h2ZeR%2BciywkC9MUmRFhmTkoTPwq0'}],'group':'cf-nel','max_age':604800}");
			CloseableHttpResponse response = client.execute(httpGet);
			System.out.println(response);
			// 請求傳送成功，並得到響應
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 讀取伺服器返回過來的json字串資料
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String entityStr = EntityUtils.toString(entity, "UTF-8");
					System.out.println(entityStr);
					return entityStr;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post請求(用於key-value格式的引數)
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPost(String url, Map params) {
		BufferedReader in = null;
		try {
			// 定義HttpClient
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
			// 例項化HTTP方法
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity("beppe", "UTF-8"));
			// 設定引數
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String value = String.valueOf(params.get(name));
				nvps.add(new BasicNameValuePair(name, value));
				System.out.println(name + "-" + value);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			CloseableHttpResponse response = client.execute(httpPost);
			int state = response.getStatusLine().getStatusCode();
			// 請求成功
			if (state == HttpStatus.SC_OK) {
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent(), "UTF-8"));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				// 第二種方法
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String entityStr = EntityUtils.toString(entity, "utf-8");
					System.out.println(entityStr);
				}
				return sb.toString();
			} else {
				System.out.println("POST請求狀態碼：" + state);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * post請求（用於請求json格式的引數）
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPost(String url, String params) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 建立httpPost
		setHeaders(httpPost);
		String charSet = "UTF-8";
		StringEntity entity = new StringEntity(params, charSet);
		httpPost.setEntity(entity);
		System.out.println(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			int state = status.getStatusCode();
			if (state == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity,
						charSet);
				return jsonString;
			} else {
				System.out.println("請求返回:" + state + "(" + url + ")");
			}
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private static void setHeaders(HttpPost httpPost) { 
		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;"); 
		httpPost.setHeader("Accept-Language", "zh-cn"); 
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"); 
		httpPost.setHeader("Accept-Charset", "utf-8"); 
		httpPost.setHeader("Keep-Alive", "300"); 
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Connection", "Keep-Alive"); 
		httpPost.setHeader("Cache-Control", "no-cache"); 
	} 
}