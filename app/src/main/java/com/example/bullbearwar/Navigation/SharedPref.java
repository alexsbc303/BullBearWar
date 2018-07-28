package com.example.bullbearwar.Navigation;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences SharedPref;
    public SharedPref(Context context) {
        SharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setNightModeState (Boolean state) {
        SharedPreferences.Editor editor = SharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightModeState () {
        Boolean state = SharedPref.getBoolean("NightMode", false);
        return state;
    }
}
