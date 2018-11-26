package com.example.android.indiatry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setusename(String usename) {

        prefs.edit().putString("usename", usename).commit();

    }

    public void setusertoken(String userToken) {

        prefs.edit().putString("userToken", userToken).commit();
    }

    public String getusename() {

        String usename = prefs.getString("usename", "");
        return usename;
    }

    public String getusertoken() {

        String userToken = prefs.getString("userToken", "");
        return userToken;

    }
}

