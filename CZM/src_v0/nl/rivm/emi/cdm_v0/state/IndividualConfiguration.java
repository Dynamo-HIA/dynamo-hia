package nl.rivm.emi.cdm_v0.state;

import nl.rivm.emi.cdm_v0.characteristic.Characteristic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndividualConfiguration {

	static Log log = LogFactory.getLog(IndividualConfiguration.class.getName());
/**
 * The Characteristics that will be simulated for the Individual. 
 */
	Characteristic[] characteristics;
	
	}
