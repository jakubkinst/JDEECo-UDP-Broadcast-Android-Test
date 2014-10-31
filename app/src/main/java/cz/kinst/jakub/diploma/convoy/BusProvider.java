package cz.kinst.jakub.diploma.convoy;

import de.greenrobot.event.EventBus;

/**
 * Created by jakubkinst on 31/10/14.
 */
public class BusProvider {
	private static EventBus sBus = new EventBus();

	public static EventBus get() {
		return sBus;
	}
}
