package com.example.sign_in;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


class Client {
	
	Socket s;
	InputStream inputStream;
	DataInputStream input;
	OutputStream os;
	OutputStreamWriter osw;
	BufferedWriter bw;
	InputStreamReader isr;
	BufferedReader br;
	Context con;
	boolean connectflag = false;
	DataBase mydaDataBase;
	
	boolean wifiEnableFlag = false;
	Toast toast;
	
	
	
	//建立客户端，建立与服务器的连接
	public Client(Context context, final String serveIP, final int port,
			final Action action, final String simID, final Handler handler){
		
		final Context mc = context;
		con=context;
		mydaDataBase = new DataBase(mc);
		
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {	
					connectflag = false;
					int wifi_stat = 0;//0 nowifi ;1 noAP  ;2  notconnectAP ;3 connectOK
					String DutyAP = mydaDataBase.getdata("DutyAP", "HQJQ");
					String APpass = mydaDataBase.getdata("APpass", "88888888");
					WifiAdmin myWifiAdmin = new WifiAdmin(con);
	
					//showMessage("ssss");
					//判断wifi链接情况
					if(myWifiAdmin.checkState() != WifiManager.WIFI_STATE_ENABLED){
					//	showMessage("正在连接服务器");
						wifi_stat = 0;
						sendMsgToMain(handler, "alert,正在搜索WIFI");
						wifiEnableFlag = false;
						myWifiAdmin.setWifiEnable();
						wifi_stat = 1;
					}
					else {
						wifiEnableFlag = true;
						wifi_stat = 1;
						if(new WifiAdmin(con).getSSID().compareTo(DutyAP) == 0){
							wifi_stat = 3;
						}
						else {
						myWifiAdmin.disconnectWifi(myWifiAdmin.getNetworkId());
						}
					}
					
					if(wifi_stat < 3){
						//判断AP是否在附近
						// sendMsgToMain(handler, "alert," +"111");
						myWifiAdmin =  new WifiAdmin(con);
						List<ScanResult> wifiList = myWifiAdmin.getWifiList();
						while (wifiList == null || wifiList.size() <= 0){
							myWifiAdmin =  new WifiAdmin(con);
							wifiList =  myWifiAdmin.getWifiList();
						}
					//	sendMsgToMain(handler, "alert," +"222");
						for(int i=0; i<wifiList.size(); i++){
							ScanResult wifi = wifiList.get(i);
							if(wifi.SSID.compareTo(DutyAP) == 0){
								sendMsgToMain(handler, "alert,找到指定AP\n开始连接");
								
								WifiConfiguration apConfiguration= CreateWifiInfo(DutyAP, APpass, 3);
		
								WifiConfiguration tempConfig = myWifiAdmin.isExsits(DutyAP);
								if (tempConfig != null) {
									myWifiAdmin.mWifiManager.removeNetwork(tempConfig.networkId);
									Log.d("tempConfig", tempConfig.SSID);
									}
								int netID = myWifiAdmin.mWifiManager.addNetwork(apConfiguration);
								 
								myWifiAdmin.mWifiManager.enableNetwork(netID,true);
								while(new WifiAdmin(con).getIPAddress() == 0);
								Thread.sleep(500);
								wifi_stat = 3;
								break;
							}
							if(i == wifiList.size()-1){
								sendMsgToMain(handler, "alert, 没有搜到指定AP\n请检查设置或在范围内打卡");
							wifi_stat = 1;	
							}
						} 
					}
					if(wifi_stat == 3){     
						 		s = new Socket();
						 		SocketAddress socAddress = new InetSocketAddress(serveIP, port);
						 		s.connect(socAddress, 8000);
				            sendMsgToMain(handler, "alert,dismiss");
				            s.setTcpNoDelay(true);
				            inputStream = s.getInputStream();
				            input = new DataInputStream(inputStream);
				            os = s.getOutputStream();
				            osw = new OutputStreamWriter(os);
				            bw = new BufferedWriter(osw);
				            isr = new InputStreamReader(inputStream);
				            br = new BufferedReader(isr);
				            connectflag = true;
				            SendAction(action, simID, handler);
					}
				}
		        catch(Exception ex)
		        {
		          ex.printStackTrace();
		          Log.d("tcp error", ex.toString());
		          sendMsgToMain(handler, "alert," + ex.toString());
		          connectflag = false;
		        }    
			}
		}).start();
		
	}
	
	
	//发送请求，表明开始发送数据，等待服务器返回正确数据，超时或错误则退出
	/*public boolean Request(){
		SendData("request");
		if(ReceiveData() == "conform"){
			return true;
		}
		
		return false;
	}*/
	
	
	
	
	private void sendMsgToMain(Handler handler, String s){
		Message msgMessage = new Message();
		Bundle dataBundle = new Bundle();
		dataBundle.putString("receive", s);
		msgMessage.setData(dataBundle);
		handler.sendMessage(msgMessage);
	}
	
	//发送本次动作信息,并处理返回的结果
	public void SendAction(Action action, String simID, Handler handler){
	//	String datareceive = null;
	//	String simID ;
		if(!connectflag) 
		{
		showMessage( "服务器出错");
		return;
		}
		switch (action) {
		case sign:
					SendData("sign," + simID);
			
			break;
		case getinfo:
					SendData("getinfo," + simID);
			break;
		case request:
					SendData("request," + simID);
			break;
		case register:
					SendData("register,"
							+ mydaDataBase.getdata("name", "无名氏") + ","
							+ mydaDataBase.getdata("id", "noid") + ","
							+ simID);
		default:
			break;
		}
		
		
		//用handler处理返回的结果
		ReceiveData(handler);
		
	//	return datareceive;
	}
	
	
	//发送数据
	public void SendData(String data){
		
		final String fdata = data;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					bw.write("begin," + fdata + ",stop");
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("tcp send error", e.toString());
				}
				
			}
		}).start();
		
		
	}
	
	
	//将收到的数据交给handler处理
	public void ReceiveData(final Handler handler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			//	List<String> datalist = new ArrayList<String>();
				
				try {
					String data = "";
					data = br.readLine();
					sendMsgToMain(handler, data);
				/*	Message msgMessage = new Message();
					Bundle dataBundle = new Bundle();
					dataBundle.putString("receive", data);
					msgMessage.setData(dataBundle);
					handler.sendMessage(msgMessage);*/
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("tcp receive error", e.toString());
				}
				
			}
		}).start();
		
	}
	
	//关闭连接
	public void CLose(){
		try {
			s.close();
			connectflag = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void tostmessage(String s){
		//if(toast != null) toast.cancel();
    	toast= Toast.makeText(con, s, Toast.LENGTH_LONG); 
		toast.show(); 
    }
	
    public void showMessage(String s){
    	try {
    		new AlertDialog.Builder(con)
        	.setMessage(s)
        	.create()
        	.show();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("AlertDialog error", e.toString());
		}
    }
    public String intToIp(int i) {return ""+( i & 0xFF)+
            "."+((i >> 8 ) & 0xFF)+ "." +  ((i >> 16 ) & 0xFF)+"." + ((i >> 24 ) & 0xFF )  ;  } 
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)     
    {     
          WifiConfiguration config = new WifiConfiguration();       
           config.allowedAuthAlgorithms.clear();     
           config.allowedGroupCiphers.clear();     
           config.allowedKeyManagement.clear();     
           config.allowedPairwiseCiphers.clear();     
           config.allowedProtocols.clear();     
          config.SSID = "\"" + SSID + "\"";       
              
          if(Type == 1) //WIFICIPHER_NOPASS    
          {     
               config.wepKeys[0] = "";     
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);     
               config.wepTxKeyIndex = 0;     
          }     
          if(Type == 2) //WIFICIPHER_WEP    
          {     
              config.hiddenSSID = true;    
              config.wepKeys[0]= "\""+Password+"\"";     
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);     
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);     
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);     
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);     
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);     
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);     
              config.wepTxKeyIndex = 0;     
          }     
          if(Type == 3) //WIFICIPHER_WPA    
          {     
          config.preSharedKey = "\""+Password+"\"";     
          config.hiddenSSID = true;       
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);       
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                             
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                             
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                        
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);      
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);    
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);    
          config.status = WifiConfiguration.Status.ENABLED;       
          }    
           return config;     
    }    
	
}

enum Action{
	sign, register, getinfo, request
}

