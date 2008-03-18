package nl.rivm.emi.cdm_v0.cohort.test;

	import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm_v0.cohort.MortalityGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

	public class TestMortalityGroup {
		Log logger = LogFactory.getLog(getClass().getName());

		@Test
		public void testOneHundredThousand() {
			MortalityGroup theGroup = new MortalityGroup();
			theGroup.runSimulation(100000);
			}

		public static junit.framework.Test suite() {
			return new JUnit4TestAdapter(
					nl.rivm.emi.cdm_v0.inputdata.cbs.test.TestMortalityCSVImporter.class);
		}

	
	
	
	
}
