package cz.kinst.jakub.diploma.jdeecoudptest;

import java.util.Set;

/**
 * Created by jakubkinst on 19/11/14.
 */
public class DeviceUpdateEvent {
	private final String name;
	private final Set<String> otherDevices;

	public DeviceUpdateEvent(String name, Set<String> otherDevices) {
		this.name = name;
		this.otherDevices = otherDevices;
	}

	public String getName() {
		return name;
	}

	public Set<String> getOtherDevices() {
		return otherDevices;
	}
}
