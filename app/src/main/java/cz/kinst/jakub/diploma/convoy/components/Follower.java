package cz.kinst.jakub.diploma.convoy.components;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.convoy.BusProvider;
import cz.kinst.jakub.diploma.convoy.model.LogEvent;
import cz.kinst.jakub.diploma.convoy.model.Waypoint;

@Component
public class Follower {

	public String name = "F";
	public Waypoint position = new Waypoint(1, 4);
	public Waypoint destination = new Waypoint(7, 2);
	public Waypoint leaderPosition;

	@Process
	@PeriodicScheduling(1000)
	public static void followProcess(
			@InOut("position") ParamHolder<Waypoint> me,
			@In("destination") Waypoint destination,
			@In("name") String name,
			@In("leaderPosition") Waypoint leader
	) {

		if (!destination.equals(me.value) && leader != null) {
			me.value.x += Integer.signum(leader.x - me.value.x);
			me.value.y += Integer.signum(leader.y - me.value.y);
		}

		BusProvider.get().post(new LogEvent("Follower " + name + ": me = " + me.value + " leader = " + leader));
	}
}
