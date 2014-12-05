package com.example.sign_in;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.view.animation.ScaleAnimation;

public class WifiAdmin {
    // ����WifiManager����
    public WifiManager mWifiManager;
    // ����WifiInfo����
    public WifiInfo mWifiInfo;
    // ɨ��������������б�
    private List<ScanResult> mWifiList;
    // ���������б�
    private List<WifiConfiguration> mWifiConfiguration;
    // ����һ��WifiLock
    WifiLock mWifiLock;

    // ������
    public WifiAdmin(Context context) {
        // ȡ��WifiManager����
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // ȡ��WifiInfo����
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // ��WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // �ر�WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // ��鵱ǰWIFI״̬
    public int checkState() {
        return mWifiManager.getWifiState();
    }
    
    public void setWifiEnable() {
        mWifiManager.setWifiEnabled(true);
    }
    // ��WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // ����WifiLock
    public void releaseWifiLock() {
        // �ж�ʱ����
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // ����һ��WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // �õ����úõ�����
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // ָ�����úõ������������
    public void connectConfiguration(int index) {
        // ����������úõ����������
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // �������úõ�ָ��ID������
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // �õ�ɨ����
        mWifiList = mWifiManager.getScanResults();
        // �õ����úõ���������
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // �õ������б�
    public List<ScanResult> getWifiList() {
    	 startScan();
        return mWifiList;
    }

    // �鿴ɨ����
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // ��ScanResult��Ϣת����һ���ַ��
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // �õ�MAC��ַ
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // �õ�������BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    public String getSSID() {
    	if(mWifiInfo == null) return "NULL";
    	String string = mWifiInfo.getSSID();
    	if(string.contains("\"")){
    		string = string.split("\"")[1];
    	}
       return  string;
    }
    

    // �õ�IP��ַ
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // �õ����ӵ�ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // �õ�WifiInfo��������Ϣ��
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // ���һ�����粢����
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        mWifiManager.enableNetwork(wcgID, true);
    }

    // �Ͽ�ָ��ID������
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    
    public WifiConfiguration isExsits(String SSID) {
    	List<WifiConfiguration> existingConfigs = getConfiguration();
    	for (WifiConfiguration existingConfig : existingConfigs) {
    	if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
    	return existingConfig;
    	}
    	}
    	return null;
    	}
    
    public boolean APisExsits(String SSID) {
    	List<ScanResult> list = getWifiList();
    	for (ScanResult ap : list) {
    	if (ap.SSID.equals(SSID)) {
    	return true;
    	}
    	}
    	return false;
    	}
}
