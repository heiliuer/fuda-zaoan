package com.heiliuer.fuda_zaoan.api.handler;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Administrator on 2016/12/18 0018.
 */
public class HandersBase {

    private static String TAG = HandersBase.class.getName();

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static WebResourceResponse getResponseFromResult(WebResourceRequest request, HandlerResult result) throws IOException {
        InputStream data = new ByteArrayInputStream(new Gson().toJson(result).getBytes());
//        InputStream data = IOUtils.toInputStream(new Gson().toJson(result), "UTF-8");
        HashMap<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Access-Control-Allow-Origin", "*");

        Log.i(TAG, String.format("getResponseFromResult:url：%s\n result:%s", request.getUrl().toString(),
                new GsonBuilder().setPrettyPrinting().create().toJson(result)));

        return
                new WebResourceResponse(
                        "text/json", "utf-8",
                        HttpsURLConnection.HTTP_OK, "OK",
                        responseHeaders,
                        data
                );
    }
}
