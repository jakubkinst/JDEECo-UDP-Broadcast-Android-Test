package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by jakubkinst on 03/12/14.
 */
public abstract class UDPBroadcast {

    public final void sendPacket(byte[] packet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, getBroadcastAddress(), UDPConfig.PORT);
            socket.send(sendPacket);
            logDebug("Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            logError("IOException: " + e.getMessage());
        }
    }

    public final void startReceiving() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            DatagramSocket socket = new DatagramSocket(UDPConfig.PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                logDebug("Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[UDPConfig.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                // get actual length of data and trim the byte array accordingly
                int length = packet.getLength();
                byte[] data = Arrays.copyOfRange(packet.getData(), 0, length);
                packet.setData(data);

                //Packet received
                logDebug("Packet received from: " + packet.getAddress().getHostAddress());
                logDebug("Size: " + data.length);
                String content = new String(data).trim();
                logDebug("Content: " + content);
                onPacketReceived(packet);
            }
        } catch (IOException ex) {
            logError("Oops" + ex.getMessage());
        }
    }

    abstract InetAddress getBroadcastAddress();

    public final String getMyIpAddressString(){
        return getMyIpAddress().getHostAddress();
    }

    abstract InetAddress getMyIpAddress();

    protected abstract void onPacketReceived(DatagramPacket packet);

    abstract void logDebug(String message);
    
    abstract void logError(String message);

    abstract void logInfo(String message);



}
