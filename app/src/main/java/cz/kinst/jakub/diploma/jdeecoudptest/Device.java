package cz.kinst.jakub.diploma.jdeecoudptest;

import android.util.Log;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.kinst.jakub.diploma.jdeecoudptest.BusProvider;
import cz.kinst.jakub.diploma.jdeecoudptest.DeviceUpdateEvent;

@Component
public class Device implements Serializable {

	public String name;
	public Set<String> otherDevices = new HashSet<>();

	public Device(String name) {
		this.name = name;
	}

	@Process
	@PeriodicScheduling(period = 1000)
	public static void update(@In("name") String name, @In("otherDevices") Set<String> otherDevices) {
		Log.e("Device", "updating device");
		BusProvider.get().post(new DeviceUpdateEvent(name, otherDevices));
	}
}
