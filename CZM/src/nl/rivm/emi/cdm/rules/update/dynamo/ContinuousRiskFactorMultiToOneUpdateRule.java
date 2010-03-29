package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import Jama.Matrix;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.DynamoManyToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author Hendriek Boshuizen
 * 
 *         implemented configuration file <?xml version="1.0" encoding="UTF-8"
 *         standalone="no" ?> - <updateRuleConfiguration> <charID>3</charID>
 *         <refValContinuousVariable>23.0</refValContinuousVariable>
 *         <meanValueFileName
 *         >c:\hendriek\java\dynamohome\Simulations\simulation2
 *         \parameters\meanValueRiskFactor.xml</meanValueFileName>
 *         <offsetFileName
 *         >c:\hendriek\java\dynamohome\Simulations\simulation2\parameters
 *         \stdValueRiskFactor.xml</offsetFileName>
 *         <meanDriftFileName>c:\hendriek
 *         \java\dynamohome\Simulations\simulation2
 *         \parameters\meanDriftRiskFactor.xml</meanDriftFileName>
 *         <stdDriftFileName
 *         >c:\hendriek\java\dynamohome\Simulations\simulation2\
 *         parameters\stdDriftRiskFactor.xml</stdDriftFileName>
 *         <offsetDriftFileName
 *         >c:\hendriek\java\dynamohome\Simulations\simulation2
 *         \parameters\offsetDriftRiskFactor.xml</offsetDriftFileName>
 *         <durationClass>0</durationClass> <nullTransition>1</nullTransition>
 *         </updateRuleConfiguration>
 */

public class ContinuousRiskFactorMultiToOneUpdateRule extends
		DynamoManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule");

	// static String[] requiredTags = {"updateRuleConfiguration", "age", "sex",
	// "charID" };

	private float transitionMatrix[][][][] = null;

	private String meanDriftFileName = null;
	private String stdDriftFileName = null;
	private String offsetDriftFileName = null;
	private String offsetFileName = null;
	// private String meanValueFileName = null;

	// private String meanValueFileNameLabel = "meanValueFileName";
	private String offsetFileNameLabel = "offsetFileName";
	private String meanDriftFileNameLabel = "meanDriftFileName";
	private String stdDriftFileNameLabel = "stdDriftFileName";
	private String offsetDriftFileNameLabel = "offsetDriftFileName";
	private String DistributionTypeLabel = "DistributionType";

	private float[][] meanDrift;
	private float[][] stdDrift;
	private float[][] offsetDrift;
	// private float[][] meanValue;
	private float[][] offset;
	// private float[][][] meanByStepByAge; /* indexes: step age sex */
	// private float[][][] offsetByStepByAge; /* indexes: step age sex */

	private boolean isNullTransitions;
	private boolean isNormal;
	private String isNullTransitionLabel = "nullTransition";
	private String configurationFileName;

	private Random randomGenerator = null;

	private int randomSeed = 0;
	// constants needed in normInverse
	double[] a = { -3.969683028665376e+01, 2.209460984245205e+02,
			-2.759285104469687e+02, 1.383577518672690e+02,
			-3.066479806614716e+01, 2.506628277459239e+00 };

	double[] b = { -5.447609879822406e+01, 1.615858368580409e+02,
			-1.556989798598866e+02, 6.680131188771972e+01,
			-1.328068155288572e+01 };

	double[] c = { -7.784894002430293e-03, -3.223964580411365e-01,
			-2.400758277161838e+00, -2.549732539343734e+00,
			4.374664141464968e+00, 2.938163982698783e+00 };

	double[] d = { 7.784695709041462e-03, 3.224671290700398e-01,
			2.445134137142996e+00, 3.754408661907416e+00 };
	//  break-points for norm Inverse.
	double plow = 0.02425;
	double phigh = 1 - plow;
	
	
	
	
	
	
	public ContinuousRiskFactorMultiToOneUpdateRule()
			throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		// temporary;
		super();
		int randomSeed = 0;
		Random randomgenerator = new Random(randomSeed);

	}

	public ContinuousRiskFactorMultiToOneUpdateRule(String configFileName)
			throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		configurationFileName = configFileName;
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
		int randomSeed = 0;
		Random randomgenerator = new Random(randomSeed);
		if (characteristicIndex != 3)
			throw new CDMUpdateRuleException(
					"wrong character ID given for DYNAMO riskfactor update rule (should always be 3) ");

		if (!success)
			throw new ConfigurationException(
					"loading of configuration file failed for updateRule CategoricalRiskFactorMultiToOneUpdateRule");

	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public Random getRandomGenerator() {
		return randomGenerator;
	}

	public void setRandomGenerator(Random randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException {

		try {
			/* only the highest 32 bits are to be used */
		
			double pRandom = (((int) (seed >>> 16)) + 2147483648.0) / 4294967295.0;
			
			Float newValue = null;
			float oldValue = getFloat(currentValues, characteristicIndex);
			if (isNullTransitions()) {
				newValue = oldValue;
				return newValue;
			} else {

				int ageValue = (int) getFloat(currentValues, ageIndex);
				if (ageValue < 0) {
					newValue = oldValue;
					return newValue;
				} else {
					int sexValue = getInteger(currentValues, sexIndex);
					if (ageValue > 95)
						ageValue = 95;
                    if (isNormal)   {
					newValue = (float) (oldValue + meanDrift[ageValue][sexValue]+stdDrift[ageValue][sexValue]*normInverse(pRandom));
                    } else{
                    	 double newOnLogScale = 0;
                    	if (oldValue>offset[ageValue][sexValue])
                    newOnLogScale= Math.log(oldValue-offset[ageValue][sexValue])+meanDrift[ageValue][sexValue]+stdDrift[ageValue][sexValue]*normInverse(pRandom);
                    newValue=(float) (Math.exp(newOnLogScale)+offset[ageValue][sexValue]+offsetDrift[ageValue][sexValue]);
                   
                    	
                    }
					return newValue;
					
				}
			}
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by ContinuousRiskFactorMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ "characteristicIndex");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @throws CDMUpdateRuleException
	 */
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
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
			// TODO weer aanzetten
		//	configurationFileConfiguration.setValidating(true);			
			configurationFileConfiguration.load();
			
			// long seed = 21223445;
			// randomGenerator = new java.util.Random(seed);
	
			handleCharID(configurationFileConfiguration);
			handleIsNullTransition(configurationFileConfiguration);
			if (!isNullTransitions) {
				handleIsNormal(configurationFileConfiguration);
				handleMeanDriftFileName(configurationFileConfiguration);
				setMeanDrift(loadData(meanDriftFileName, "meandrift", "meandrift"));
				handleStdDriftFileName(configurationFileConfiguration);
				setStdDrift(loadData(stdDriftFileName, "stddrift", "stddrift"));
				if (!isNormal) {
	
					handleOffsetDriftFileName(configurationFileConfiguration);
					setOffsetDrift(loadData(offsetDriftFileName, "offsetdrift",
							"offsetdrift"));
					handleOffsetFileName(configurationFileConfiguration);
					setOffset(loadData(offsetFileName, "offset", "offset"));
	
				}
				/*
				 * left out are reading of files needed for more complex update
				 * rules that can not be realised in the current situation
				 */
				/*
				 * 
				 * handleMeanValueFileName(configurationFileConfiguration);
				 * 
				 * setMeanDrift(loadData(stdDriftFileName, "stddrift", "stddrift"));
				 * if (!isNormal) {
				 * 
				 * 
				 * } setAimValues();
				 */
	
			}
			success = true;
			return success;			
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), 
					e, configurationFile.getAbsolutePath());
			return success;	
		}							
	}
	
	
	/*
	 * private void setAimValues() { meanByStepByAge = new float[200][96][2]; if
	 * (!isNormal) offsetByStepByAge = new float[200][96][2]; for (int a = 0; a
	 * < 96; a++) for (int g = 0; g < 2; g++) for (int step = 0; step < 96;
	 * step++) { if (step == 0 || a == 0) meanByStepByAge[step][a][g] =
	 * meanValue[a][g]; else if (a > 0) meanByStepByAge[step][a][g] =
	 * meanByStepByAge[step - 1][a - 1][g] + meanDrift[a - 1][g]; if (!isNormal)
	 * { if (step == 0 || a == 0) offsetByStepByAge[step][a][g] =
	 * meanValue[a][g]; else if (a > 0) offsetByStepByAge[step][a][g] =
	 * offsetByStepByAge[step - 1][a - 1][g] + offsetDrift[a - 1][g]; }
	 * 
	 * } };
	 */
	/**
	 * @param configurationFileConfiguration
	 */
	private void handleIsNormal(XMLConfiguration simulationConfiguration)
			throws CDMConfigurationException {
		try {
			String type = simulationConfiguration
					.getString(DistributionTypeLabel);
			log.debug("Setting DistributionType to " + type);
			if (type.compareToIgnoreCase("normal") == 0)
				setNormal(true);
			else
				setNormal(false);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), DistributionTypeLabel));
		}
	}

	public float[][] loadData(String inputFile, String tag1, String tag2)
			throws CDMConfigurationException {
		/*
		 * temporary blocked for testing if (inputFile != null) { File paramFile
		 * = new File(inputFile);double[][][][] transmat =null; return transmat;
		 * } else {
		 */
		float[][] data = new float[96][2];
		ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
		try {
			data = factory.manufactureOneDimArray(inputFile, tag1, tag2, false);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.fatal("error in transition matrix file ");
			throw new CDMConfigurationException(" error while reading "
					+ inputFile);
		}
		return data;
	}

	public boolean loadConfigurationFile(String configurationFileName) {
		return true;
	}

	private void handleIsNullTransition(
			HierarchicalConfiguration simulationConfiguration)
			throws CDMConfigurationException {
		try {
			int isNull = simulationConfiguration.getInt(isNullTransitionLabel);
			log.debug("Setting isNullTransitions to " + isNull);
			setNullTransitions(isNull);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), isNullTransitionLabel));
		}
	}

	private void handleMeanDriftFileName(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String fileName = simulationConfiguration
					.getString(meanDriftFileNameLabel);
			log.debug("Setting meanDriftFileName to " + fileName);
			setMeanDriftFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), meanDriftFileNameLabel));
		}
	}

	private void handleOffsetDriftFileName(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String fileName = simulationConfiguration
					.getString(offsetDriftFileNameLabel);
			log.debug("Setting offsetDriftFileName to " + fileName);
			setOffsetDriftFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), offsetDriftFileNameLabel));
		}
	}

	private void handleStdDriftFileName(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String fileName = simulationConfiguration
					.getString(stdDriftFileNameLabel);
			log.debug("Setting meanDriftFileName to " + fileName);
			setStdDriftFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), stdDriftFileNameLabel));
		}
	}

	private void handleOffsetFileName(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String fileName = simulationConfiguration
					.getString(offsetFileNameLabel);
			log.debug("Setting meanDriftFileName to " + fileName);
			setOffsetFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.configurationFileName,
					this.getClass().getSimpleName(), offsetFileNameLabel));
		}
	}

	/*
	 * private void handleMeanValueFileName( HierarchicalConfiguration
	 * simulationConfiguration) throws ConfigurationException { try { String
	 * fileName = simulationConfiguration .getString(meanValueFileNameLabel);
	 * log.debug("Setting meanDriftFileName to " + fileName);
	 * setMeanValueFileName(fileName); } catch (NoSuchElementException e) {
	 * throw new CDMConfigurationException(String.format(
	 * CDMConfigurationException.noConfigurationTagMessage,
	 * this.configurationFileName, this.getClass().getSimpleName(),
	 * meanValueFileNameLabel)); } }
	 */
	public boolean isNullTransitions() {
		return isNullTransitions;
	}

	public void setNullTransitions(boolean isNullTransitions) {
		this.isNullTransitions = isNullTransitions;
	}

	public void setNullTransitions(int isNullTransitions)
			throws CDMConfigurationException {
		if (isNullTransitions == 0)
			this.isNullTransitions = false;
		else if (isNullTransitions == 1)
			this.isNullTransitions = true;
		else
			throw new CDMConfigurationException(
					"error in configuration File for updateRule "
							+ this.getClass().getSimpleName()
							+ " : isNullTransitions should be either 0 or 1 but is "
							+ isNullTransitions);
	}

	
	public double normInverse(double p){
		
	// Rational approximation for lower region:
	if (p < plow) {
		double q = Math.sqrt(-2 * Math.log(p));
		return (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4])
				* q + c[5])
				/ ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
	}

	// Rational approximation for upper region:
	if (phigh < p) {
		double q = Math.sqrt(-2 * Math.log(1 - p));
		return -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4])
				* q + c[5])
				/ ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
	}

	// Rational approximation for central region:
	double q = p - 0.5;
	double r = q * q;
	return (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5])
			* q
			/ (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1);
}

	
	
	
	public String getOffsetDriftFileNameLabel() {
		return offsetDriftFileNameLabel;
	}

	public void setOffsetDriftFileNameLabel(String offsetDriftFileNameLabel) {
		this.offsetDriftFileNameLabel = offsetDriftFileNameLabel;
	}

	public void setDriftFileName(String driftFileName) {
		this.meanDriftFileName = driftFileName;
	}

	public String getDriftFileName() {
		return meanDriftFileName;
	}

	public String getMeanDriftFileName() {
		return meanDriftFileName;
	}

	public void setMeanDriftFileName(String meanDriftFileName) {
		this.meanDriftFileName = meanDriftFileName;
	}

	public String getStdDriftFileName() {
		return stdDriftFileName;
	}

	public void setStdDriftFileName(String stdDriftFileName) {
		this.stdDriftFileName = stdDriftFileName;
	}

	public String getOffsetDriftFileName() {
		return offsetDriftFileName;
	}

	public void setOffsetDriftFileName(String offsetDriftFileName) {
		this.offsetDriftFileName = offsetDriftFileName;
	}

	public String getOffsetFileName() {
		return offsetFileName;
	}

	public void setOffsetFileName(String offsetFileName) {
		this.offsetFileName = offsetFileName;
	}

	/*
	 * public String getMeanValueFileName() { return meanValueFileName; }
	 * 
	 * public void setMeanValueFileName(String meanValueFileName) {
	 * this.meanValueFileName = meanValueFileName; }
	 */

	public float[][] getMeanDrift() {
		return meanDrift;
	}

	public void setMeanDrift(float[][] meanDrift) {
		this.meanDrift = meanDrift;
	}

	public float[][] getStdDrift() {
		return stdDrift;
	}

	public void setStdDrift(float[][] stdRatio) {
		this.stdDrift = stdRatio;
	}

	public float[][] getOffsetDrift() {
		return offsetDrift;
	}

	public void setOffsetDrift(float[][] offsetDrift) {
		this.offsetDrift = offsetDrift;
	}

	/*
	 * public float[][] getMeanValue() { return meanValue; }
	 * 
	 * public void setMeanValue(float[][] meanValue) { this.meanValue =
	 * meanValue; }
	 */

	public float[][] getOffset() {
		return offset;
	}

	public void setOffset(float[][] offset) {
		this.offset = offset;
	}

	public boolean isNormal() {
		return isNormal;
	}

	public void setNormal(boolean isNormal) {
		this.isNormal = isNormal;
	}

}
