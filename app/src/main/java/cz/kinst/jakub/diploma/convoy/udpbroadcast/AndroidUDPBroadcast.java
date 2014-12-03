package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jakubkinst on 12/11/14.
 */
public abstract class AndroidUDPBroadcast extends UDPBroadcast {

    private final Context mContext;

    public AndroidUDPBroadcast(Context context) {
        mContext = context;
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public final InetAddress getMyIpAddress() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        byte[] bytes = BigInteger.valueOf(ip).toByteArray();
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    final InetAddress getBroadcastAddress() {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    final void logDebug(String message) {
        Log.d(UDPConfig.TAG, message);
    }

    @Override
    final void logError(String message) {
        Log.e(UDPConfig.TAG, message);
    }

    @Override
    final void logInfo(String message) {
        Log.i(UDPConfig.TAG, message);
    }
}
