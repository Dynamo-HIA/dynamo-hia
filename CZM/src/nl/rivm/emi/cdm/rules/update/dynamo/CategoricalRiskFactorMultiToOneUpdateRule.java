package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Random;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author Hendriek Boshuizen
 * /**
		 * implemented xml file for categorical risk factor:
		 * 
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <updateRuleConfiguration> <charID>3</charID>
		 * <nCat>4</nCat>
		 * <durationClass>2</durationClass>
		 * <nullTransition>1</nullTransition>
		 * <durationClass>2</durationClass>
		 * <transitionFile>c:/hendriek/java/workspace/dynamo/dynamodata/transdata.xml</transitionFile>
		 * <randomSeed>1234</randomSeed> </updateRuleConfiguration>
		 * 
		 */
 
public class CategoricalRiskFactorMultiToOneUpdateRule extends
		DynamoManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	

	Log log = LogFactory.getLog("nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule");

	// static String[] requiredTags = {"updateRuleConfiguration", "age", "sex", "charID" };

	private float transitionMatrix[][][][] = null;
	
	
	private int nCat = -1;

	private  String transitionMatrixFileName = null;
	private  String nCatLabel = "nCat";
	private  String randomSeedLabel = "randomSeed";
    private boolean isNullTransitions;
	private  String TransitionMatrixFileNameLabel="transitionFile";
	private  String isNullTransitionLabel = "nullTransition";
	private String configurationFileName;

	private Random randomGenerator = null;
	
    private  int randomSeed=0; 


	public CategoricalRiskFactorMultiToOneUpdateRule() throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		// temporary;
		super();
        int randomSeed=0;
		Random randomgenerator=new Random(randomSeed);
		
	}
	public CategoricalRiskFactorMultiToOneUpdateRule(String configFileName) throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		configurationFileName=configFileName;
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
        int randomSeed=0;
		Random randomgenerator=new Random(randomSeed);
		if (characteristicIndex!=3) throw new CDMUpdateRuleException("wrong character ID given for DYNAMO riskfactor update rule (should always be 3) ");
		
		if (!success) throw new ConfigurationException("loading of configuration file failed for updateRule CategoricalRiskFactorMultiToOneUpdateRule");
		
	}
	public  int getNCat() {
		return nCat;
	}


	public  void setNCat(int cat) {
		nCat = cat;
	}
	public float[][][][] getTransitionMatrix() {
		return transitionMatrix;
	}


	public void setTransitionMatrix(float[][][][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}
	

	public  String getTransitionMatrixFileName() {
		return transitionMatrixFileName;
	}


	public  void setTransitionMatrixFileName(String transitionMatrixFileName) {
		this.transitionMatrixFileName = transitionMatrixFileName;
	}


	public  int getRandomSeed() {
		return randomSeed;
	}
	public  void setRandomSeed(int randomSeed) {
		this.randomSeed= randomSeed;
	}
	public Random getRandomGenerator() {
		return randomGenerator;
	}
	public  void setRandomGenerator(Random randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		try {
			int oldValue = getInteger(currentValues, characteristicIndex);
			if (isNullTransitions()){Integer newValue=oldValue; 
			return newValue;}
			else{
			Integer newValue = null;
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			
			float[] p = new float[nCat];
			if (ageValue>95) ageValue=95;// ook in andere rules!
				
			
			p = transitionMatrix[ageValue][sexValue][oldValue];
			newValue = draw(p, randomGenerator);
			return newValue;}
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
		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
				configurationFile);
		//long seed = 21223445;
		//randomGenerator = new java.util.Random(seed);
		handleCharID(configurationFileConfiguration);
		handleNCat(configurationFileConfiguration);
		handleRandomSeed(configurationFileConfiguration);
		handleTransitionMatrixFileName(configurationFileConfiguration);
		handleIsNullTransition(configurationFileConfiguration);
		if (! isNullTransitions)
		setTransitionMatrix(loadTransitionMatrix(transitionMatrixFileName));
	//	setTransitionMatrix(loadTransitionMatrix());
		success=true;
		return success;
	}
	public float[][][][] loadTransitionMatrix(String inputFile) throws CDMConfigurationException {
		/* temporary blocked for testing
				if (inputFile != null) {
					File paramFile = new File(inputFile);double[][][][] transmat =null; return transmat;
				} else {*/
					float [][][][] transmat = new float[96][2][nCat][nCat];
					ArraysFromXMLFactory factory=new ArraysFromXMLFactory();
					try {
						transmat=factory.manufactureThreeDimArray(inputFile, "transitionMatrix", "transitionRates");
					} catch (ConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.fatal("error in transition matrix file ");
						throw new CDMConfigurationException(" error while reading transition matrix file " );
					}
					return transmat;
				}
	public float[][][][] loadTransitionMatrix() {
/* temporary blocked for testing
		if (inputFile != null) {
			File paramFile = new File(inputFile);double[][][][] transmat =null; return transmat;
		} else {*/
			float [][][][] transmat = new float[96][2][nCat][nCat];
			for (int a = 0; a < 96; a++) {
				for (int g = 0; g < 2; g++)

				{
					// with this matrix the final equilibrium is a prevalence of 2/7
					// in state 0-2 and of 1/7 in state 3
					transmat[a][g][0][0] = 0.9F;
					transmat[a][g][1][0] = 0.1F;
					transmat[a][g][2][0] = 0.0F;
					transmat[a][g][3][0] = 0.0F;
					transmat[a][g][0][1] = 0.1F;
					transmat[a][g][1][1] = 0.8F;
					transmat[a][g][2][1] = 0.1F;
					transmat[a][g][3][1] = 0.0F;
					transmat[a][g][0][2] = 0.0F;
					transmat[a][g][1][2] = 0.1F;
					transmat[a][g][2][2] = 0.8F;
					transmat[a][g][3][2] = 0.2F;
					transmat[a][g][0][3] = 0.0F;
					transmat[a][g][1][3] = 0.0F;
					transmat[a][g][2][3] = 0.1F;
					transmat[a][g][3][3] = 0.8F;

				}
				;
			}
			return transmat;
		}
//	}  temporary blocked
	public boolean loadConfigurationFile(String configurationFileName){return true;}

	static int draw(float[] p, Random rand) throws CDMUpdateRuleException {
		// Generates a random draws from an array with percentages
		// To do: check if sum p=1 otherwise error
		float sumP=0;
		for (float prev:p) sumP+=prev;
		if(Math.abs(sumP-1.0)>1E-4) throw new CDMUpdateRuleException("row of transition matrix for risk factor does not sum to 1");
		
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
	private  void handleNCat(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int nCat   = simulationConfiguration.getInt(nCatLabel);
			log.debug("Setting number of categories to " + nCat);
			setNCat(nCat);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(
					String
					.format(
							CDMConfigurationException.noConfigurationTagMessage,
							this.configurationFileName, this
									.getClass().getSimpleName(),nCatLabel));
		}
	}
	

	private  void handleIsNullTransition(
			HierarchicalConfiguration simulationConfiguration) throws CDMConfigurationException {
		try {
			int isNull   = simulationConfiguration.getInt(isNullTransitionLabel);
			log.debug("Setting isNullTransitions to " +isNull);
			setNullTransitions(isNull);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(
					String
					.format(
							CDMConfigurationException.noConfigurationTagMessage,
							this.configurationFileName, this
									.getClass().getSimpleName(),isNullTransitionLabel));
		}
	}
	private  void handleTransitionMatrixFileName(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			String fileName = simulationConfiguration.getString(TransitionMatrixFileNameLabel);
			log.debug("Setting TransitionMatrixFileName to " + fileName);
			setTransitionMatrixFileName(fileName);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(
					String
					.format(
							CDMConfigurationException.noConfigurationTagMessage,
							this.configurationFileName, this
									.getClass().getSimpleName(),TransitionMatrixFileNameLabel));
		}
	}
	private  void handleRandomSeed(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int randomSeed = simulationConfiguration.getInt(randomSeedLabel);
			log.debug("Setting randomSeed to " + randomSeed);
			setRandomSeed(randomSeed);
			Random randomgenerator=new Random(randomSeed);
			setRandomGenerator(randomgenerator);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(
					String
					.format(
							CDMConfigurationException.noConfigurationTagMessage,
							this.configurationFileName, this
									.getClass().getSimpleName(),randomSeedLabel));}
	}
	public boolean isNullTransitions() {
		return isNullTransitions;
	}
	public void setNullTransitions(boolean isNullTransitions) {
		this.isNullTransitions = isNullTransitions;
	}

	public void setNullTransitions(int isNullTransitions) throws CDMConfigurationException {
		if (isNullTransitions==0)
		this.isNullTransitions =false;
		else if (isNullTransitions==1)
			this.isNullTransitions =true;
		else throw new CDMConfigurationException("error in configuration File for updateRule "+ 
				this.getClass().getSimpleName()+" : isNullTransitions should be either 0 or 1 but is "
				+ isNullTransitions);
	}
	
	
	
}