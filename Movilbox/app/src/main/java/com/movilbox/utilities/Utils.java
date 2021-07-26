package com.movilbox.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    private String URLApi = "https://jsonplaceholder.typicode.com/";
    private Context context;

    public void init(Context context) {

        this.context = context;
    }

    public boolean haveInternet() {

        boolean haveInternet = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {

            haveInternet = false;
        }

        return haveInternet;
    }

    public String getURLApi() {
        return this.URLApi;
    }
}
