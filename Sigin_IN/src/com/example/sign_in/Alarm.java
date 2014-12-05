package com.example.sign_in;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;


public class Alarm extends BroadcastReceiver
{

	static int i = 0;
	DataBase  myDataBase;
	Context mContext;
	int currmins;
	
	String firstsign = "firstsign";
	String lastsign = "lastsign";
	WifiAdmin mywWifiAdmin;
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		mContext = context;
		myDataBase = new DataBase(context);
		currmins = new mytime().getCurrTimeMins();
		
		if(currmins >360 && currmins<362){
			myDataBase.storedata(firstsign, "无");
			myDataBase.storedata(lastsign, "无");
			return;
		}
		
		else if(myDataBase.getdata(firstsign, "无").compareTo("无")==0
				&&(currmins > 480 && currmins < 570)){
			//检测AP
		//	mywWifiAdmin = new WifiAdmin(context);
		//	Intent it = new Intent(context, MainActivity.class);   
		//	it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);   
			
			
		}
		else if(myDataBase.getdata(firstsign, "无").compareTo("无")!=0
				&&(currmins > 1050 || currmins < 120)){
			//if()
			//检测AP
		//	mywWifiAdmin = new WifiAdmin(context);
			
		}
		/*
		if(i == 0)
		{
		new AlertDialog.Builder(Main.mainactivity).setTitle("ÏÂ°àÊ±Œä")
		.setPositiveButton("È·¶š", null)
		.setNegativeButton("È¡Ïû", null)
		.show();
		}
		*/
		/*if (alarm != null)
		{
			if(alarm == "on_begin")
			{
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ring);
				mediaPlayer.start();
			}
			if(alarm == "on_end")
			{
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ring);
				mediaPlayer.start();
			}
			if(alarm == "off_begin")
			{
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ring);
				mediaPlayer.start();
			}
			if(alarm == "off_end")
			{
				new AlertDialog.Builder(Main.mainactivity).setTitle("ÏÂ°àÊ±Œä")
				.setPositiveButton("È·¶š", null)
				.setNegativeButton("È¡Ïû", null)
				.show();
				Log.d("realtime","before_ring");
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ring);
				mediaPlayer.start();
			}
		}*/

	}
	
	private void tostmessage(String s){
    	Toast toast = Toast.makeText(mContext, s, Toast.LENGTH_LONG); 
		toast.show(); 
    }

}
