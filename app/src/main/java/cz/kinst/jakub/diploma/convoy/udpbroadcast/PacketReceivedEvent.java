package cz.kinst.jakub.diploma.convoy.udpbroadcast;

/**
 * Created by jakubkinst on 19/11/14.
 */
public class PacketReceivedEvent {
	private final byte[] packet;
	private final String sender;

	public PacketReceivedEvent(byte[] packet, String sender) {
		this.packet = packet;
		this.sender = sender;
	}

	public byte[] getPacket() {
		return packet;
	}

	public String getSender() {
		return sender;
	}
}
