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
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author mondeelr
 * 
 */
public class ContinuousRiskFactorMultiToOneUpdateRule extends
		ManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	 Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = { "age", "sex", "charID", "updateRuleParameters" };

	float meanDrift[][] = null;
	float stdDrift[][] = null;
	float offsetDrift[][] = null;
	float popMean [][]=null;
	float offset [][]=null;
	String type =null;
	
	int ageIndex = 1;
	
	int sexIndex = 2;

	int nCat = -1;

	int characteristicIndex = 3;

	String parameterFileName = null;

	

	public ContinuousRiskFactorMultiToOneUpdateRule(String configFileName) throws ConfigurationException {
		// constructor fills the parameters
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
		if (success)
			loadTransitionMatrix(parameterFileName);
		if (!success) throw new ConfigurationException("loading of configuration file failed for updateRule ContinuousRiskFactorMultiToOneUpdateRule");
		
	}


	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		try {
			Float newValue = null;
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			float oldValue = getFloat(currentValues, characteristicIndex);
			try {
				if (type == "Normal") 
					newValue=oldValue+meanDrift[ageIndex][sexIndex]+(oldValue-popMean[ageIndex][sexIndex])*
					stdDrift[ageIndex][sexIndex];
				 else if (type == "Lognormal") {double logOldValue= Math.log(oldValue-offset[ageIndex][sexIndex]);
					 newValue=(float)Math.exp(logOldValue+meanDrift[ageIndex][sexIndex]+(logOldValue-popMean[ageIndex][sexIndex])*
						stdDrift[ageIndex][sexIndex]+offset[ageIndex][sexIndex]+offsetDrift[ageIndex][sexIndex]);
				// TODO nog even goed checken;
				 } else
					throw new CDMUpdateRuleException(
							"ContinuousRiskFactorMultiToOneUpdateRule WARNING: no valid distributional form. Risk factor is not updated");
			} catch (Exception e) {
				log.fatal(e.getMessage());
				
				newValue=oldValue;
			}
			return newValue;
		} catch (CDMUpdateRuleException e) {log.fatal(e.getMessage());
		log.fatal("this message was issued by ContinuousRiskFactorMultiToOneUpdateRule"+
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
				.configurationsAt("param");
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
					switch (innerdex) {
					case 1:
						ageIndex = intValue;
						break;
					case 2:
						sexIndex = intValue;
						break;
					case 3:
						characteristicIndex = intValue;
						break;
					case 4:
						parameterFileName = value;
						break;
					}
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

	public double[][][][] loadTransitionMatrix(String inputFile) {

		if (inputFile != null) {
			File paramFile = new File(inputFile);double[][][][] transmat =null; return transmat;
		} else {
			nCat = 4;
			double[][][][] transmat = new double[96][2][nCat][nCat];
			for (int a = 0; a < 96; a++) {
				for (int g = 0; g < 2; g++)

				{
					transmat[a][g][0][0] = 0.9;
					transmat[a][g][1][0] = 0.1;
					transmat[a][g][2][0] = 0.0;
					transmat[a][g][3][0] = 0.0;
					transmat[a][g][0][0] = 0.1;
					transmat[a][g][1][0] = 0.8;
					transmat[a][g][2][0] = 0.1;
					transmat[a][g][3][0] = 0.0;
					transmat[a][g][0][0] = 0.0;
					transmat[a][g][1][0] = 0.1;
					transmat[a][g][2][0] = 0.8;
					transmat[a][g][3][0] = 0.1;
					transmat[a][g][0][0] = 0.0;
					transmat[a][g][1][0] = 0.1;
					transmat[a][g][2][0] = 0.1;
					transmat[a][g][3][0] = 0.8;

				}
				;
			}
			return transmat;
		}
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
