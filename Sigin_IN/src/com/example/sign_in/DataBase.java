package com.example.sign_in;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;



public class DataBase{
	SharedPreferences myshare;


	public DataBase(Context context){
		myshare = context.getSharedPreferences("sign_DB", Activity.MODE_PRIVATE);
	}

	public void storedata(String key, String value) {
		SharedPreferences.Editor editor=myshare.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void storedata(String key, boolean flag){
		SharedPreferences.Editor editor=myshare.edit();
		editor.putBoolean(key, flag);
		editor.commit();
	}

	public String getdata(String key, String value){
		return myshare.getString(key, value);
	}

	public boolean getdata(String key, boolean flag){
		return myshare.getBoolean(key, flag);
	}
}
