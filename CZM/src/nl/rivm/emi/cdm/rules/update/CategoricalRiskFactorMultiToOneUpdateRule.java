package nl.rivm.emi.cdm.rules.update;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Random;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PAS OP FOUTE VERSIE!!!!
 * 
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author mondeelr
 * 
 */
public class CategoricalRiskFactorMultiToOneUpdateRule extends
		ManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	

	static Log log = LogFactory.getLog("nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule");

	// static String[] requiredTags = {"updateRuleConfiguration", "age", "sex", "charID" };

	double transitionMatrix[][][][] = null;
	
	int ageIndex = 1;
	
	int sexIndex = 2;

	static int nCat = -1;

	private static String transitionMatrixFileName = null;
	private static  String nCatLabel = "nCat";

	private static String TransitionMatrixFileNameLabel;
	

	private Random randomGenerator = null;

	private int characteristicIndex;

	public static int getNCat() {
		return nCat;
	}


	public static void setNCat(int cat) {
		nCat = cat;
	}
	public double[][][][] getTransitionMatrix() {
		return transitionMatrix;
	}


	public void setTransitionMatrix(double[][][][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}
	

	public static String getTransitionMatrixFileName() {
		return transitionMatrixFileName;
	}


	public static void setTransitionMatrixFileName(String transitionMatrixFileName) {
		CategoricalRiskFactorMultiToOneUpdateRule.transitionMatrixFileName = transitionMatrixFileName;
	}


	public CategoricalRiskFactorMultiToOneUpdateRule(String configFileName,int randomSeed) throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);

		Random randomgenerator=new Random(randomSeed);
		int characteristicIndex = 0;
		if (characteristicIndex!=3) throw new CDMUpdateRuleException("wrong character ID given for DYNAMO riskfactor update rule (should always be 3) ");
		
		if (!success) throw new ConfigurationException("loading of configuration file failed for updateRule CategoricalRiskFactorMultiToOneUpdateRule");
		
	}


	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		try {
			Integer newValue = null;
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			int oldValue = getInteger(currentValues, characteristicIndex);
			double[] p = new double[nCat];
			p = transitionMatrix[ageValue][sexValue][oldValue];
			newValue = draw(p, randomGenerator);
			return newValue;
		} catch (CDMUpdateRuleException e) {log.fatal(e.getMessage());
		log.fatal("this message was issued by CategoricalRiskFactorMultiToOneUpdateRule"+
		 " when updating characteristic number "+"characteristicIndex");
		e.printStackTrace();
		throw e;
		}
	}

	/**
	 * @throws CDMUpdateRuleException 
	 */
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException{
		boolean success = false;
		try {			
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);
			
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();
			
			// Validate the xml by xsd schema
			configurationFileConfiguration.setValidating(true);			
			configurationFileConfiguration.load();
			
			long seed = 21223445;
			randomGenerator = new java.util.Random(seed);
			handleNCat(configurationFileConfiguration);
			handleNCat(configurationFileConfiguration);
			handleTransitionMatrixFileName(configurationFileConfiguration);
			loadTransitionMatrix(transitionMatrixFileName);
			success=true;
			return success;			
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), 
					e, configurationFile.getAbsolutePath());
			return success;
		}
	}

		
	public double[][][][] loadTransitionMatrix(String inputFile) {

		if (inputFile != null) {
			File paramFile = new File(inputFile);double[][][][] transmat =null; return transmat;
		} else {
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

	static int draw(double p[], Random rand) {
		// Generates a random draws from an array with percentages
		// To do: check if sum p=1 otherwise error
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
	private static void handleNCat(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int nCat   = simulationConfiguration.getInt(nCatLabel);
			log.debug("Setting number of categories to " + nCat);
			setNCat(nCat);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationTimestepMessage);
		}
	}
	private static void handleTransitionMatrixFileName(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			String fileName = simulationConfiguration.getString(TransitionMatrixFileNameLabel);
			log.debug("Setting TransitionMatrixFileName to " + fileName);
			setTransitionMatrixFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException. noUpdateTransitionMatrixFileNameMessage);
		}
	}


	@Override
	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException, CDMUpdateRuleException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
