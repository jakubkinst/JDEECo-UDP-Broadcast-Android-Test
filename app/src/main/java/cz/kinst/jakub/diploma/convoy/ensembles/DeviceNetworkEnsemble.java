package cz.kinst.jakub.diploma.convoy.ensembles;

import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;


@Ensemble
@PeriodicScheduling(period = 200)
public class DeviceNetworkEnsemble {

	@Membership
	public static boolean membership(@In("member.name") String memberName, @In("coord.name") String coordName) {
		return true;
	}

	@KnowledgeExchange
	public static void map(@InOut("member.otherDevices") ParamHolder<Set<String>> memberOtherDevices, @In("coord.name") String coordName) {
		memberOtherDevices.value.add(coordName);
	}

}
