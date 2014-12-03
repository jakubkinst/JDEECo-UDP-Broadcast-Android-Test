package cz.kinst.jakub.diploma.convoy.udpbroadcast;

import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataSender;
import cz.cuni.mff.d3s.deeco.network.NetworkInterface;
import cz.cuni.mff.d3s.deeco.network.PacketReceiver;
import cz.cuni.mff.d3s.deeco.network.PacketSender;
import cz.kinst.jakub.diploma.convoy.BusProvider;
import cz.kinst.jakub.diploma.convoy.ConvoyApplication;

/**
 * Created by jakubkinst on 12/11/14.
 */
public class UDPBroadcastHost extends AbstractHost implements NetworkInterface {


	private final PacketReceiver packetReceiver;
	private final PacketSender packetSender;
    private final UDPBroadcast udpBroadcast;

    public UDPBroadcastHost(String ipAddress, UDPBroadcast udpBroadcast) {
		super(ipAddress, new DefaultCurrentTimeProvider());
        this.udpBroadcast = udpBroadcast;
		this.packetReceiver = new PacketReceiver(id, UDPConfig.PACKET_SIZE);
		this.packetSender = new PacketSender(this, UDPConfig.PACKET_SIZE, false, false);
		this.packetReceiver.setCurrentTimeProvider(this);
		BusProvider.get().register(this);
	}

	public void setKnowledgeDataReceiver(KnowledgeDataReceiver knowledgeDataReceiver) {
		packetReceiver.setKnowledgeDataReceiver(knowledgeDataReceiver);
	}

	public KnowledgeDataSender getKnowledgeDataSender() {
		return packetSender;
	}

	public void onEvent(PacketReceivedEvent event) {
		// check if not receiving my own packet
		if (!event.getSender().equals(id))
			packetReceived(event.getPacket(), 1);
	}

	// CALL THIS when received a packet through UDP
	@Override
	public void packetReceived(byte[] packet, double rssi) {
		packetReceiver.packetReceived(packet, rssi);
	}


	@Override
	public void sendPacket(byte[] packet, String recipient) {
		// SEND UDP packet via UDP interface
		udpBroadcast.sendPacket(packet);
	}

	public void finalize() {
		BusProvider.get().unregister(this);
		packetReceiver.clearCachedMessages();
	}
}
