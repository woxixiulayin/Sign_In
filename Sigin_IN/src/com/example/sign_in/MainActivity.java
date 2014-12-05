package com.example.sign_in;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
	private String serverIP;
	private int port;
	
	private String simID;
	private DataBase mydatabase;
	
	private AlarmManager alarmManage;
	
	AlertDialog.Builder builder;  
   AlertDialog dialog; 
   
   boolean nosimPermission = true;
   
	Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
         InitData();
         InitView();
    }
    
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    
    public void ActionSign(View v) {
    	if(simID == null || simID.compareTo("")==0){
			showMessage("请插入SIM卡");
			if(!nosimPermission) return;
		}
    	String receiveString = "";
    	Client client = new Client(MainActivity.this, serverIP, port, 
    			Action.sign, simID, new Handler(){
    		@Override
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			Bundle data = msg.getData();
    			String receive = data.getString("receive");
    			//showMessage(receive);		
    			dealReceivedData(receive);
    			
    			
    		}
    	});
    	
    /*	try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
    //	try {
    		/*client.SendAction(Action.sign, simID, new Handler(){
        		@Override
        		public void handleMessage(Message msg){
        			super.handleMessage(msg);
        			Bundle data = msg.getData();
        			String receive = data.getString("receive");
        			//showMessage(receive);		
        			dealReceivedData(receive);
        			
        			
        		}
        	});*/
			
		/*} catch (Exception e) {
			
			showMessage(e.toString());
		}*/
    //	client.CLose();   	 	
	}
    
    public void ActionRegister(View v) {
    	if(simID == null || simID.compareTo("")==0){
			showMessage("请插入SIM卡");
			if(!nosimPermission) return;
		}
        try {
        	registerAlert();
		} catch (Exception e) {
			showMessage(e.toString());
		}
    
	}
    
    private void InitData(){	
    	try {
    		
    		builder = new AlertDialog.Builder(this);
    		dialog = builder.create();
    		
    		serverIP = "192.168.39.127";
        	port = 8888;
        	
        	mydatabase = new DataBase(this);
        	
    		simID = GetsimID();
    		if(simID == null || simID.compareTo("")==0){
    			showMessage("请插入SIM卡");
    		}
    		else{
    			mydatabase.storedata("simID", simID);
    		}
    		
    		//全局定时器，每30S执行一次
    		alarmManage = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    		Intent intent = new Intent(this, Alarm.class);
    		PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, intent, 0);
    		alarmManage.setRepeating(AlarmManager.RTC, 0, 30000, pendingIntent);
    		
		} catch (Exception e) {
			showMessage(e.toString());
		}	
    }
    
    public void showMessage(String s){
       dialog.dismiss(); 
       if(s.compareTo("dismiss") == 0) return;
       dialog.setMessage(s);
       dialog.show();
    }
    
    
    //获得本机simID
    private String GetsimID(){
    	TelephonyManager mTelephonyMgr = (TelephonyManager) this  
                .getSystemService(Context.TELEPHONY_SERVICE);  
        Log.d("getImsi", "get mTelephonyMgr " + mTelephonyMgr.toString());  
        String imsi = mTelephonyMgr.getSubscriberId();
    //  String imei = mTelephonyMgr.getDeviceId();  
    	return imsi; 
    }
    
    private void dealReceivedData(String s){
    	
    	String ssString = s;
    	String[] receive = ssString.split(",");
    	if(receive.length < 2) return;
    	if("sign".compareTo(receive[0]) == 0){
    		pro_re_sign(receive);
    	}
    	
    	else if("register".compareTo(receive[0]) == 0){
    		pro_re_register(receive);
    	}
    	
    	else if("alert".compareTo(receive[0]) == 0){
    		pro_alert(receive);
    	}
    	
    	else {
    		showMessage("receive data no match");
    	}
    }
    
    private void pro_re_sign(String[] receive){
    	if("space".compareTo(receive[1]) == 0){
			tostmessage("已打卡成功，下次打卡请再等待" + 
		(60-Integer.parseInt(receive[2])) +"秒" );
		}
		
		else if("many".compareTo(receive[1]) == 0)
		{
			mydatabase.storedata("firstsign", receive[2].split("\\s+")[1]);
			mydatabase.storedata("lastsign",  receive[3].split("\\s+")[1]);
    		
    		showMessage("成功打卡\n" + mydatabase.getdata("lastsign", "无"));
    		
    		setsigntime();
		}
		else if ("first".compareTo(receive[1]) == 0) {
			mydatabase.storedata("firstsign", receive[2].split("\\s+")[1]);
			mydatabase.storedata("lastsign",  "无");
    		
    		showMessage("今日首次打卡成功\n");
    		
    		setsigntime();	
		}
		else if ("false".compareTo(receive[1]) == 0) {
			showMessage(this.getString(R.string.notregister));
		}
    }
    
    private void pro_re_register(String[] receive){
    	if("ok".compareTo(receive[1]) == 0){
    		tostmessage(this.getString(R.string.registerok));
    		
    	}
    	else if("false".compareTo(receive[1]) == 0){
    		tostmessage(this.getString(R.string.alreadyregister));
    	}
    }
    
    private void pro_alert(String[] receive){
    	showMessage(receive[1]);
    }
    
    private void InitView(){
    	
    	setsigntime();
    	
    }
    private void setsigntime(){
    	try {
			TextView tx = (TextView)findViewById(R.id.first_sign);
    		tx.setText("首次打卡\n" + mydatabase.getdata("firstsign", "无"));
    		tx = (TextView)findViewById(R.id.last_sign);
    		tx.setText("上次打卡\n" + mydatabase.getdata("lastsign", "无"));
			} catch (Exception e) {
				showMessage(e.toString());
			}
    }
    
    private void tostmessage(String s){
    	if(toast != null) toast.cancel();
    	toast = Toast.makeText(this, s, Toast.LENGTH_LONG); 
		toast.show(); 
    }
    
    private void registerAlert(){
    	if(simID == null || simID.compareTo("")==0){
			showMessage("请插入SIM卡");
			if(!nosimPermission) return;
		}
    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	View viewregister = layoutInflater.inflate(R.layout.register, null);
    	final EditText  NameVIew = (EditText)viewregister.findViewById(R.id.editName);
    	final EditText  IDVIew = (EditText)viewregister.findViewById(R.id.editID);
    	final EditText  simIDVIew = (EditText)viewregister.findViewById(R.id.editsimID);
    	NameVIew.setText(mydatabase.getdata("name", ""));
    	IDVIew.setText(mydatabase.getdata("id", ""));
    	simIDVIew.setText(simID);
    	
    	simIDVIew.setEnabled(false);
		//tx.setText("首次打卡\n" + mydatabase.getdata("firstsign", "无")); 
    	
    	new AlertDialog.Builder(this).setTitle(MainActivity.this
    			.getString(R.string.giveyourname)).setView(
    			viewregister).setPositiveButton("确认",
    	       new DialogInterface.OnClickListener() {
    	           @Override
    	           public void onClick(DialogInterface dialog, int which) {
    	        	   String name = NameVIew.getText().toString();
    	        	   String id = IDVIew.getText().toString();
    	        	   if(name.compareTo("") != 0 && id.compareTo("") != 0){
    	        		   mydatabase.storedata("name", name);
    	        		   mydatabase.storedata("id", id);
    	        		   registerfun();
    	        	   }
    	        	   else {
    	        		   showMessage(MainActivity.this.getString(R.string.nameempty));
    	        	   }
    	              // insertEmployee();
    	           }
    	       }).setNegativeButton("取消",null).show();
    }
    
    private void registerfun(){
    	String receiveString = "";
    	Client client = new Client(MainActivity.this, serverIP, port,
    			Action.register, simID, new Handler(){
    		@Override
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			Bundle data = msg.getData();
    			String receive = data.getString("receive");
    			//showMessage(receive);		
    			dealReceivedData(receive);
    		}
    	});
    	
    	/*try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
    		client.SendAction(Action.register, simID, new Handler(){
        		@Override
        		public void handleMessage(Message msg){
        			super.handleMessage(msg);
        			Bundle data = msg.getData();
        			String receive = data.getString("receive");
        			//showMessage(receive);		
        			dealReceivedData(receive);
        		}
        	});
			
		} catch (Exception e) {
			
			showMessage(e.toString());
		}*/
    }
    
}
