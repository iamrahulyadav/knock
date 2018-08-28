package com.proclivis.smartknock.Retrf;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proclivis.smartknock.Common.Constant;


import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


/**
 * Created by dgohil on 6/17/15.
 */
@SuppressLint("Registered")
public class RestClient extends Activity
{
    private static CommonService REST_CLIENT_MUTUAL_TRANSFER;

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    private static void setupRestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        REST_CLIENT_MUTUAL_TRANSFER = buildAdapter(Constant.URL, gson).create(CommonService.class);
    }

    private static RestAdapter buildAdapter(String endPoint, Gson gson) {

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(endPoint)
                .setConverter(new GsonConverter(gson))
                .build();
    }


    public static CommonService getApi() {
        return REST_CLIENT_MUTUAL_TRANSFER;
    }


}
