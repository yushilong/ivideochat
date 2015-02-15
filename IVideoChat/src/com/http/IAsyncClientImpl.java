package com.http;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yushilong on 2015/1/22.
 */
public abstract class IAsyncClientImpl implements IAsyncClient {

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void progress(int bytesWritten, int totalSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void success(JSONObject successObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void success(JSONArray successArray) {
		// TODO Auto-generated method stub

	}

	@Override
	public void success(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failure(int statusCode, String failure) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void retry(int no) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
