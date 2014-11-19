package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataSender;
import cz.cuni.mff.d3s.deeco.network.NetworkInterface;
import cz.cuni.mff.d3s.deeco.network.PacketReceiver;
import cz.cuni.mff.d3s.deeco.network.PacketSender;

/**
 * Created by jakubkinst on 12/11/14.
 */
public class UDPBroadcastHost extends AbstractHost implements NetworkInterface {


	private final PacketReceiver packetReceiver;
	private final PacketSender packetSender;

	public UDPBroadcastHost(String ipAddress) {
		super(ipAddress, new DefaultCurrentTimeProvider());
		this.packetReceiver = new PacketReceiver(id);
		this.packetSender = new PacketSender(this);
		this.packetReceiver.setCurrentTimeProvider(this);
	}

	public void setKnowledgeDataReceiver(KnowledgeDataReceiver knowledgeDataReceiver) {
		packetReceiver.setKnowledgeDataReceiver(knowledgeDataReceiver);
	}

	public KnowledgeDataSender getKnowledgeDataSender() {
		return packetSender;
	}

	// CALL THIS when received a packet through UDP
	@Override
	public void packetReceived(byte[] packet, double rssi) {
		packetReceiver.packetReceived(packet, rssi);
	}


	@Override
	public void sendPacket(byte[] packet, String recipient) {
		// SEND UDP packet via UDP interface
	}

	public void finalize() {
		packetReceiver.clearCachedMessages();
	}
}
