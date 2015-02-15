package net.john.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpConnectUtil {
	
	//private static final String root_url = "http://10.3.12.118:8080";
	private static final String root_url = "http://192.168.97.1:8080";
	
	public static JSONObject getResponseByPost(String url, String phone, String password) {
		String request_url = root_url + url + "?phone=" + phone + "&password=" + password;
		JSONObject jsonres = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		HttpPost httpPost = new HttpPost(request_url);
		try {
			jsonres.put("code", 2);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
					stringBuilder.append(s);
				}
				jsonres = new JSONObject(stringBuilder.toString());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonres;
	}
	
	public static JSONObject updateRealnameByPost(String url, String phone, String realname) {
		String request_url = root_url + url + "?phone=" + phone + "&realName=" + realname;
		JSONObject jsonres = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(request_url);
		try {
			jsonres.put("code", 2);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
					stringBuilder.append(s);
				}
				jsonres = new JSONObject(stringBuilder.toString());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonres;
	}
	
	public static JSONObject uploadFileByPost(String url, File file) {
		String request_url = root_url + url;
		JSONObject jsonres = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(request_url);
		FileEntity fileEntity = new FileEntity(file, "binary/octet-stream");
		httpPost.setEntity(fileEntity);
		try {
			jsonres.put("code", 0);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
					stringBuilder.append(s);
				}
				Log.d("DEBUG", stringBuilder.toString());

				jsonres = new JSONObject(stringBuilder.toString());
			} 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonres;
	}
	
	public static int getToUserIdByPost(String url, String phone) {
		int intres = -1;
		String request_url = root_url + url + "?userphone=" + phone;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(request_url);
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
					stringBuilder.append(s);
				}
				Log.d("DEBUG", stringBuilder.toString());
				if (stringBuilder.toString().equals("user id is error")) {
					intres = -1;
				} else {
					intres = Integer.parseInt(stringBuilder.toString());
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intres;
	}
}
