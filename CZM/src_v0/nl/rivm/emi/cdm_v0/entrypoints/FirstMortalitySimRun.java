package nl.rivm.emi.cdm_v0.entrypoints;
/**
 * Refactored from TestMortalityGroup.
 */
import nl.rivm.emi.cdm_v0.cohort.MortalityGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FirstMortalitySimRun {
	Log logger = LogFactory.getLog(getClass().getName());

	static public void main(String argv[]) {
		MortalityGroup theGroup = new MortalityGroup();
		theGroup.runSimulation(100000);
	}
}
