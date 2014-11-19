package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import android.os.AsyncTask;

/**
 * Created by jakubkinst on 12/11/14.
 */
public class PacketReceiverTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {
		AndroidUDPBroadcast.startReceive();
		return null;
	}
}
