package nl.rivm.emi.cdm.rules.update.dynamo;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 */
 
public class CategoricalRiskFactorIdentityUpdateRule extends
		OneToOneUpdateRuleBase{

	Log log = LogFactory.getLog(this.getClass().getSimpleName());

	public CategoricalRiskFactorIdentityUpdateRule() throws ConfigurationException, CDMUpdateRuleException {
		super();
	}

	@Override
	public Object update(Object currentValue) throws CDMUpdateRuleException {
		return currentValue;
	}
	
	
	
	
}
