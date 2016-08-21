package com.hyundai.hackathon.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Cho on 2016-08-21.
 */
public class NetworkModel {

    public AsyncHttpClient mHttpClient;
    private static NetworkModel instance;

    public NetworkModel() {

        mHttpClient = new AsyncHttpClient();
        mHttpClient.setTimeout(6000);
    }

    public static NetworkModel getInstance() {
        if (instance == null) instance = new NetworkModel();
        return instance;
    }


    public interface OnResultListener<T> {
        void onSuccess(T result);

        void onError(int code);
    }

    public void getTest(String url, double lng, double lnt, final OnResultListener<String> listener) {

        RequestParams params = new RequestParams();

        mHttpClient.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String temp = new String(responseBody);
                listener.onSuccess(temp);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.onError(statusCode);
            }

        });
    }
}
