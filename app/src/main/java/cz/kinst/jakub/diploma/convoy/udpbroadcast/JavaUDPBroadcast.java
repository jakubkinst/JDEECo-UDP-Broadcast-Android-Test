package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public abstract class JavaUDPBroadcast extends UDPBroadcast {

    @Override
    final InetAddress getBroadcastAddress() {
        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Don't want to broadcast to the loopback interface
                for (InterfaceAddress interfaceAddress :
                        networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    return broadcast;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public final InetAddress getMyIpAddress() {
        try {
            return Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    final void logDebug(String message) {
        System.out.println("DEBUG: " + UDPConfig.TAG + ": " + message);
    }

    @Override
    final void logError(String message) {
        System.err.println("ERROR: " + UDPConfig.TAG + ": " + message);
    }

    @Override
    final void logInfo(String message) {
        System.out.println("INFO: " + UDPConfig.TAG + ": " + message);
    }

//	public static String getMyIpAddress(Context context) {
//		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//		int ip = wifiInfo.getIpAddress();
//		return Formatter.formatIpAddress(ip);
//	}
}
