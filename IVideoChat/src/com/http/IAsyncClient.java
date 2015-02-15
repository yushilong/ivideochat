package com.http;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yushilong on 2015/1/22.
 */
public interface IAsyncClient {

    void start();

    void progress(int bytesWritten, int totalSize);

    void success(JSONObject successObject);

    void success(JSONArray successArray);

    void success(String string);

    void failure(int statusCode, String failure);

    void finish();

    void retry(int no);

    void cancel();
}
