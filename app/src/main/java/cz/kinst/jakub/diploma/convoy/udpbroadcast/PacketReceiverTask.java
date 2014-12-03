package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import android.os.AsyncTask;

/**
 * Created by jakubkinst on 12/11/14.
 */
public class PacketReceiverTask extends AsyncTask<Void, Void, Void> {

    private final UDPBroadcast mUdpBroadcast;

    public PacketReceiverTask(UDPBroadcast udpBroadcast) {
        mUdpBroadcast = udpBroadcast;
    }

    @Override
	protected Void doInBackground(Void... params) {
		mUdpBroadcast.startReceiving();
		return null;
	}
}
