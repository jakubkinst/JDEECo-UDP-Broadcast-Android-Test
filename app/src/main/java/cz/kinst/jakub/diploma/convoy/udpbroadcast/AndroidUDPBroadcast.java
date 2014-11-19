package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import cz.kinst.jakub.diploma.convoy.BusProvider;

/**
 * Created by jakubkinst on 12/11/14.
 */
public class AndroidUDPBroadcast {
	public static void sendPacket(Context context, byte[] packet) {
		// Hack Prevent crash (sending should be done using an async task)
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			//Open a random port to send the package
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, getBroadcastAddress(context), UDPConfig.PORT);
			socket.send(sendPacket);
			Log.d(UDPConfig.TAG, "Broadcast packet sent to: " + getBroadcastAddress(context).getHostAddress());
		} catch (IOException e) {
			Log.e(UDPConfig.TAG, "IOException: " + e.getMessage());
		}
	}

	static InetAddress getBroadcastAddress(Context context) throws IOException {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	/*
	This has to be run in a background thread
	 */
	public static void startReceive() {
		try {
			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			DatagramSocket socket = new DatagramSocket(UDPConfig.PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {
				Log.d(UDPConfig.TAG, "Ready to receive broadcast packets!");

				//Receive a packet
				byte[] recvBuf = new byte[UDPConfig.PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);
				// get actual length of data and trim the byte array accordingly
				int length = packet.getLength();
				byte[] data = Arrays.copyOfRange(packet.getData(), 0, length);

				//Packet received
				Log.d(UDPConfig.TAG, "Packet received from: " + packet.getAddress().getHostAddress());
				Log.d(UDPConfig.TAG, "Size: " + data.length);
				BusProvider.get().post(new PacketReceivedEvent(data, packet.getAddress().getHostAddress()));
			}
		} catch (IOException ex) {
			Log.e(UDPConfig.TAG, "Oops" + ex.getMessage());
		}
	}

	public static String getMyIpAddress(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		return Formatter.formatIpAddress(ip);
	}
}
