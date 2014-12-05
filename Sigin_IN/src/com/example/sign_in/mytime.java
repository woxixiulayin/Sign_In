package com.example.sign_in;

import java.util.Calendar;

public class mytime {
	int hour;
	int min;
	
	public mytime() {
		hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		min = Calendar.getInstance().get(Calendar.MINUTE);		
	}
	
	public String getCurrTimeString(){
		return String.valueOf(hour) + ":" + String.valueOf(min);
	}
	
	public int getCurrTimeMins(){
		int currmins;
		currmins = hour * 60 + min;
		return currmins;
	}
	
}