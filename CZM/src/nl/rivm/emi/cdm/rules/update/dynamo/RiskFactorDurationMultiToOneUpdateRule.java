package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.List;
import java.util.Random;

import Jama.Matrix;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;



import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation for DYNAMO-HIA
 * 
 * @author boshuizh
 * 
 */
public class RiskFactorDurationMultiToOneUpdateRule extends
		ManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	 Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = {  "charID", "durationClass" };

	
	int durationClass =-1;
	int riskFactorIndex = 3;
	int characteristicIndex = 4;



	public RiskFactorDurationMultiToOneUpdateRule(String configFileName,int randomSeed) throws ConfigurationException {
		// constructor fills the parameters
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
		if (!success) throw new ConfigurationException("loading of configuration file failed for updateRule RiskFactorDurationMultiToOneUpdateRule");
	

	}


	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		try {
			float stepsize=1;
			float newValue = -1;
			int riskValue = getInteger(currentValues, riskFactorIndex);
			float oldValue = getFloat(currentValues, characteristicIndex);
			if (riskValue==durationClass) newValue=oldValue+stepsize;
			else newValue=0;
			
		
			return newValue;
		} catch (CDMUpdateRuleException e) {log.fatal(e.getMessage());
		log.fatal("this message was issued by RiskFactorDurationMultiToOneUpdateRule"+
		 " when updating characteristic number "+"characteristicIndex");
		e.printStackTrace();
		throw e;
		}
	}

	/** deze is niet goed
	 */
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
				configurationFile);
		
		List<SubnodeConfiguration> snConf = configurationFileConfiguration
				.configurationsAt("updateRuleParameters");
		if (!((snConf == null) || (snConf.isEmpty() || (snConf.size() > 1)))) {
			SubnodeConfiguration tagConf = snConf.get(0);
			ConfigurationNode confNode = tagConf.getRootNode();
			List children = confNode.getChildren();
			if (children.size() == 4) {
				for (int innerdex = 0; innerdex < children.size(); innerdex++) {
					ConfigurationNode childNode = (ConfigurationNode) children
							.get(innerdex);
					String value = (String) childNode.getValue();
					Integer intValue = Integer.parseInt(value);
					
				}
			}
		} else {
			throw new ConfigurationException(
					String
							.format(
									CDMConfigurationException.invalidUpdateRuleConfigurationFileFormatMessage,
									configurationFile.getName(), this
											.getClass().getSimpleName()));
		}
		return (false);
	}

	
	public boolean loadConfigurationFile(String configurationFileName){return true;}

	public  int draw(float p[], Random rand)  {
		// Generates a random draw from an array with percentages
		// To do: check if sum p=1 otherwise error
		float sumP=0;
		for(float item:p)sumP+=item;
		try {
		if (Math.abs(sumP-1)>1.0E-3)
			
				throw new CDMUpdateRuleException(" CategoricalRiskFactorMultiToOneUpdateRule WARNING: risk factor prevalence rates do not add up to 1; sum prevalence rates = " + sumP);
			} catch (CDMUpdateRuleException e) {
				log.fatal(e.getMessage());
				e.printStackTrace();
			}
		double cump = 0; // cump is cumulative p

		double d = rand.nextDouble(); // d is random value between 0 and 1
		int i;
		for (i = 0; i < p.length - 1; i++) {
			cump = +p[i];
			if (d < cump)
				break;
		}
		return i;
	}
}
