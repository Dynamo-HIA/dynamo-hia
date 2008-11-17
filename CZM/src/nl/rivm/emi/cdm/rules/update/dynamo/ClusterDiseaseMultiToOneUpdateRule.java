package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * @author Hendriek
 * 
 *        <p>
 *         this update rule updates a state from a multi-state disease table.
 *         </p>
 *        <p>
 *         the implemented configuration file looks like
 *         </p>
 * 
 *         <?xml version="1.0" encoding="UTF-8" standalone="no" ?> -
 *         <updateRuleConfiguration> <charID>6</charID> <name>cluster1_11</name>
 *         <riskType>1</riskType> <nCat>2</nCat>
 *         <refValContinuousVariable>0.0</refValContinuousVariable>
 *         <nInCluster>2</nInCluster>
 *         <CharIdFirstInCluster>4</CharIdFirstInCluster>
 *         <diseaseOnDiseaseRelativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations
 *         \testsimulation\parameters\relativeRiskDiseaseOnDisease_cluster0
 *         .xml</diseaseOnDiseaseRelativeRiskFile> <disease>
 *         <diseaseNumber>0</diseaseNumber> <diseaseName>ziekte1</diseaseName>
 *         <baselineIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte1.xml</baselineIncidenceFile>
 *         <attributableMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte1.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte1.xml</relativeRiskFile>
 * 
 *         <baselineFatalIncidenceFile>c:\hendriek\java\dynamohome\Simulations\
 *         testsimulation\parameters\baselineFatalIncidence_6_ziekte2.xml</
 *         baselineFatalIncidenceFile> </disease> <disease>
 *         <diseaseNumber>1</diseaseNumber> <diseaseName>ziekte2</diseaseName>
 *         <baselineIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
 *         <attributableMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte2.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte2.xml</relativeRiskFile>
 *         <baselineFatalIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineFatalIncidence_6_ziekte2
 *         .xml</baselineFatalIncidenceFile>
 * 
 *         </disease> </updateRuleConfiguration>
 */
public class ClusterDiseaseMultiToOneUpdateRule extends
		SingleDiseaseMultiToOneUpdateRule implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = { "updateRuleConfiguration", "age", "sex", "charID" }; // not
																					// implemented

	int nDiseases;
	int charIDFirstDiseaseInCluster;
	String[] diseaseNames;
	int[] diseaseNumber;
	String Clustername;
	protected static String diseaseLabel = "disease";
	protected static String numberLabel = "diseaseNumber";
	protected static String diseaseNameLabel = "diseaseName";
	protected static String nameLabel = "name";
	protected static String diseaseOnDiseaseRelativeRiskFileNameLabel = "diseaseOnDiseaseRelativeRiskFile";

	protected float relRiskContinous[][][] = null;
	protected float referenceValueContinous;
	protected float relRiskCategorical[][][][] = null;
	protected float relRiskEnd[][][] = null;
	protected float relRiskBegin[][][] = null;
	protected float alfaDuur[][][] = null;
	protected float attributableMortality[][][] = null;

	protected float baselineIncidence[][][] = null;
	protected float baselineFatalIncidence[][][] = null;
	protected float relRiskDiseaseOnDisease[][][][] = null;

	int startIndex = -1;

	String relRiskDiseaseOnDiseaseFileName = null;
	String[] relRiskCatFileName = null;
	String[] relRiskContFileName = null;
	String[] relRiskEndFileName = null;
	String[] relRiskBeginFileName = null;
	String[] alfaDuurFileName = null;
	String[] attributableMortalityFileName = null;
	String[] baselineIncidenceFileName = null;
	String[] baselineFatalIncidenceFileName = null;

	private Random randomGenerator = null;

	public ClusterDiseaseMultiToOneUpdateRule(String configFile)
			throws ConfigurationException, CDMUpdateRuleException {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClusterDiseaseMultiToOneUpdateRule() throws ConfigurationException,
			CDMUpdateRuleException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		double timestep = 1;

		try {
			int ageValue = (int) getFloat(currentValues, ageIndex);
			if (ageValue > 95)
				ageValue = 95;
			int sexValue = getInteger(currentValues, sexIndex);
			int nCombinations = (int) Math.pow(2, nDiseases);
			float[] oldValue = new float[nCombinations];
			float sumOldValues = 0; /*
									 * add state 0 to the combinations of
									 * diseases;
									 */
			for (int state = 1; state < nCombinations; state++) {

				oldValue[state] = getFloat(currentValues, startIndex + state
						- 1);
				sumOldValues += oldValue[state];
			}
			oldValue[0] = 1 - sumOldValues;
			/* state 0 should be added to oldValues */

			

			double[] incidence = calculateIncidence(currentValues, ageValue,
					sexValue);
			// make transitionrate matrix

			// plus 1 for total matrix = including state of dead
			double[][] rateMatrix = new double[nCombinations][nCombinations];
			/*
			 * first= changed state (row) second :sources of
			 * change(change=number in second state entry in matrix
			 */
			for (int row = 0; row < nCombinations; row++)
				Arrays.fill(rateMatrix[row], 0);
			for (int row = 0; row < nCombinations; row++)
				for (int column = 0; column < nCombinations; column++)
					/*
					 * Matrix entry from each disease is formed as:
					 * 
					 * if value first=0 and value second=1: incidence second
					 * product of RRdiseaseOnDisease over other(=all) diseases
					 * that are 1 on second disease if value first=0 and
					 * first=second: - sum incidence to all other diseases
					 * (including RR's as above plus mortality due to cluster
					 * diseases: - attributable Mortality for each disease that
					 * is 1 in
					 */

					if (row == column)
					/*
					 * Matrix entry is formed as: / - all mortality: - otherMort
					 * - attributable Mortality for each disease that is 1 in
					 * combi - sum incidence to all other disease that are 0 in
					 * combi(including RR's as above
					 */
					{

						for (int d = 0; d < nDiseases; d++)
							if ((row & (1 << d)) != (1 << d)) {
								double RR = 1;
								for (int dCause = 0; dCause < nDiseases; dCause++)
									if ((row & (1 << dCause)) == (1 << dCause))
										RR *= this.relRiskDiseaseOnDisease[ageValue][sexValue][dCause][d];

								rateMatrix[row][column] -=  (RR * incidence[d] + attributableMortality[d][ageValue][sexValue]);
							}
					} else
						for (int d1 = 0; d1 < nDiseases; d1++)
							for (int d2 = 0; d2 < nDiseases; d2++)
								if (((row & (1 << d1)) == (1 << d1))
										&& ((column & (1 << d2)) != (1 << d2))) {
									double RR = 1;
									for (int dCause = 0; dCause < nDiseases; dCause++)
										if ((column & (1 << dCause)) == (1 << dCause))
											RR *= relRiskDiseaseOnDisease[ageValue][sexValue][dCause][d1];

									rateMatrix[row][column] +=  (RR * incidence[d1]);
								}
			;

			/* Exponentiale the matrix */

			MatrixExponential matExp = MatrixExponential.getInstance();
			double[][] TransitionProbabilities = new double[nCombinations][nCombinations];
			TransitionProbabilities = matExp.exponentiateMatrix(rateMatrix);

			/* Multiply the matrix with the old values (column vector) */
			double unconditionalNewValues[] = new double[nCombinations];

			for (int state1 = 0; state1 < nCombinations; state1++) // row
			{
				unconditionalNewValues[state1] = 0;
				for (int state2 = 0; state2 < nCombinations; state2++)

					unconditionalNewValues[state1] += TransitionProbabilities[state1][state2]
							* oldValue[state2];
				/*
				 * lastly, make into conditional probabilities (conditional on
				 * being alive; leave out state 0 to make numbering equal to
				 * what needs to be updated
				 */}
			/* calculate survival */
			double survival = 0;
			for (int state = 0; state < nCombinations; state++) {
				survival += unconditionalNewValues[state];
			}
			float newValue[] = new float[nCombinations - 1];
			for (int state = 1; state < nCombinations; state++)
				newValue[state - 1] = (float) (unconditionalNewValues[state] / survival);

			return newValue[characteristicIndex - startIndex];
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by ClusterDiseaseMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ "characteristicIndex");
			e.printStackTrace();
			throw e;
		}
	}

	protected double[] calculateIncidence(Object[] currentValues, int ageValue,
			int sexValue) throws CDMUpdateRuleException {
		double[] incidence = new double[getNDiseases()];
		Arrays.fill(incidence, -1);
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			for (int d = 0; d < nDiseases; d++)
				incidence[d] = baselineIncidence[d][ageValue][sexValue]
						* relRiskCategorical[d][ageValue][sexValue][riskFactorValue];

		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			for (int d = 0; d < nDiseases; d++)
				incidence[d] = baselineIncidence[d][ageValue][sexValue]
						* Math.pow((riskFactorValue - referenceValueContinous),
								relRiskContinous[d][ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);
				for (int d = 0; d < nDiseases; d++)
					incidence[d] = baselineIncidence[d][ageValue][sexValue]
							* ((relRiskBegin[d][ageValue][sexValue] - relRiskEnd[d][ageValue][sexValue])
									* Math.exp(-riskDurationValue
											* alfaDuur[d][ageValue][sexValue]) + relRiskEnd[d][ageValue][sexValue]);

			} else
				for (int d = 0; d < nDiseases; d++)
					incidence[d] = baselineIncidence[d][ageValue][sexValue]
							* relRiskCategorical[d][ageValue][sexValue][riskFactorValue];
		}
		for (int d = 0; d < nDiseases; d++)
			if (incidence[d] == -1)
				throw new CDMUpdateRuleException(
						"No incidence could be calculated for disease " + d
								+ ". Most likely cause: riskType out of range");

		return incidence;
	}

	protected double[] getCurrentDiseaseValues(Object[] currentValues,
			int ageValue, int sexValue, int Nstart, int Ntot)
			throws CDMUpdateRuleException {
		double diseaseValues[] = new double[Ntot];
		Arrays.fill(diseaseValues, -1);
		for (int d = 0; d < Ntot; d++)
			diseaseValues[d] = (double) getFloat(currentValues, Nstart + d);
		return diseaseValues;
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		try {

			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);

			ConfigurationNode rootNode = configurationFileConfiguration
					.getRootNode();
			if (configurationFileConfiguration.getRootElementName() != globalTagName)

				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName
						+ " expected in file for updaterule ClusterDisease"
						+ " but found tag " + rootNode.getName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			handleNInCluster(configurationFileConfiguration);
			handleNCat(configurationFileConfiguration);
			handleRiskType(configurationFileConfiguration);
			handleCharID(configurationFileConfiguration);
			int diseaseRead = 0;
			for (ConfigurationNode rootChild : rootChildren) {
				if (rootChild.getName() == firstCharLabel) {
					String value = (String) rootChild.getValue();
					log
							.debug("Setting ID of first characteristic in cluster to: "
									+ value);
					setStartIndex(value);
				}

				else if (rootChild.getName() == nameLabel) {
					String value = (String) rootChild.getValue();
					log.debug("Setting cluster name to: " + value);
					setClusterName(value);
				}

				else if (rootChild.getName() == diseaseLabel) {
					handleDiseaseData(rootChild, diseaseRead);
					diseaseRead++;
				}

				else if (rootChild.getName() == relRiskDiseaseOnDiseaseFileNameLabel) {
					String value = (String) rootChild.getValue();
					log
							.debug("Setting RelativeRisfFilename  (Disease on Disease) to: "
									+ value);
					relRiskDiseaseOnDiseaseFileName = value;

					ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
					float[][][][] inputData = factory.manufactureThreeDimArray(
							relRiskDiseaseOnDiseaseFileName, "relativeRisks",
							"relativeRisk");
					setRelRiskDiseaseOnDisease(inputData);
					log.debug("reading relative risks disease on disease");

					if (diseaseRead != getNDiseases())
						log
								.fatal("Number of disease read ("
										+ diseaseRead
										+ "does not agree with number of diseases as given in XML file"
										+ getNDiseases());
					// TODO gooi exception
				}

			}
		} catch (DynamoConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setRelRiskDiseaseOnDisease(float[][][][] inputData) {
		relRiskDiseaseOnDisease = inputData;

	}

	protected void handleNInCluster(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {

			int nDiseases = simulationConfiguration.getInt(nInClusterLabel);
			log.debug("Setting number of diseases in the cluster to: "
					+ nDiseases);
			setNDiseases(nDiseases);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading number of categories");
		}
	}

	protected void handleDiseaseData(ConfigurationNode rootChild,
			int diseaseNumber) throws ConfigurationException {
		try {
			List<ConfigurationNode> diseaseChildren = (List<ConfigurationNode>) rootChild
					.getChildren();

			float[][] inputData = new float[96][2];
			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
			for (ConfigurationNode diseaseElement : diseaseChildren) {
				if (diseaseElement.getName() == attributableMortalityFileNameLabel) {

					String value = (String) diseaseElement.getValue();
					log.debug("Setting AttributableMortalityFilename to: "
							+ value);
					setAttributableMortalityFileName(value, diseaseNumber);
					inputData = factory.manufactureOneDimArray(
							attributableMortalityFileName[diseaseNumber],
							"attributableMortalities", "attributableMortality");
					setAttributableMortality(inputData, diseaseNumber);
					log.debug("reading AttributableMortality data for disease "
							+ diseaseNumber);
				}

				else if (diseaseElement.getName() == baselineIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineIncidenceFilename to: " + value);
					setBaselineIncidenceFileName(value, diseaseNumber);
					inputData = factory.manufactureOneDimArray(
							baselineIncidenceFileName[diseaseNumber],
							"baselineIncidences", "baselineIncidence");
					setBaselineIncidence(inputData, diseaseNumber);
					log.debug("reading BaselineIncidence data for disease "
							+ diseaseNumber);

				}

				else if (diseaseElement.getName() == baselineFatalIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineFatalIncidenceFilename to: "
							+ value);
					setBaselineFatalIncidenceFileName(value, diseaseNumber);
					inputData = factory
							.manufactureOneDimArray(
									baselineFatalIncidenceFileName[diseaseNumber],
									"baselineFatalIncidences",
									"baselineFatalIncidence");
					setBaselineFatalIncidence(inputData, diseaseNumber);
					log
							.debug("reading BaselineFatalIncidence data for disease "
									+ diseaseNumber);

				}

				else if (diseaseElement.getName() == diseaseNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseaseName to: " + value);
					setDiseaseName(value, diseaseNumber);
				}

				else if (diseaseElement.getName() == numberLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseaseNumber to: " + value);
					setDiseaseNumber(value, diseaseNumber);
					if (Integer.parseInt(value) != diseaseNumber)
						throw new CDMUpdateRuleException(
								"numbering of diseases"
										+ "out of synch in update rule configuration for cluster disease ");
				}

				else if (diseaseElement.getName() == relRiskCatFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log
							.debug("Setting baselineRelativeRiskFilename  (categorical) to: "
									+ value);
					setRelRiskCatFileName(value, diseaseNumber);
					float[][][] inputData2;
					inputData2 = factory.manufactureTwoDimArray(
							relRiskCatFileName[diseaseNumber], "relativeRisks",
							"relativeRisk");
					setRelRiskCat(inputData2, diseaseNumber);
					log.debug("reading relative risks for disease "
							+ diseaseNumber);

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setRelRiskCatFileName(String value, int i) {
		relRiskCatFileName[i] = value;

	}

	protected void setRelRiskCat(float value[][][], int i) {
		relRiskCategorical[i] = value;

	}

	protected void setBaselineFatalIncidenceFileName(String value, int i) {
		baselineFatalIncidenceFileName[i] = value;

	}

	protected void setBaselineIncidenceFileName(String value, int i) {

		baselineIncidenceFileName[i] = value;
	}

	protected float[][][] getClusterAttributableMortality() {
		return attributableMortality;
	}

	protected void setAttributableMortality(float[][] attributableMortality,
			int i) {
		this.attributableMortality[i] = attributableMortality;
	}

	protected void setAttributableMortalityFileName(String value, int i) {
		attributableMortalityFileName[i] = value;

	}

	protected float[][][] getClusterBaselineIncidence() {
		return baselineIncidence;
	}

	protected void setBaselineIncidence(float[][] baselineIncidence, int i) {
		this.baselineIncidence[i] = baselineIncidence;
	}

	protected void setBaselineFatalIncidence(float[][] baselineFatalIncidence,
			int i) {
		this.baselineFatalIncidence[i] = baselineFatalIncidence;
	}

	protected int getCharIDFirstDiseaseInCluster() {
		return charIDFirstDiseaseInCluster;
	}

	protected void setCharIDFirstDiseaseInCluster(
			int charIDFirstDiseaseInCluster) {
		this.charIDFirstDiseaseInCluster = charIDFirstDiseaseInCluster;
	}

	protected int getNDiseases() {
		return nDiseases;
	}

	protected void setNDiseases(int nDis) {
		nDiseases = nDis;
		diseaseNames = new String[nDiseases];
		diseaseNumber = new int[nDiseases];
		relRiskCatFileName = new String[nDiseases];
		relRiskContFileName = new String[nDiseases];
		relRiskEndFileName = new String[nDiseases];
		relRiskBeginFileName = new String[nDiseases];
		alfaDuurFileName = new String[nDiseases];
		attributableMortalityFileName = new String[nDiseases];
		baselineIncidenceFileName = new String[nDiseases];
		baselineFatalIncidenceFileName = new String[nDiseases];

		relRiskCategorical = new float[nDiseases][96][2][];
		relRiskContinous = new float[nDiseases][96][2];
		relRiskEnd = new float[nDiseases][96][2];
		relRiskBegin = new float[nDiseases][96][2];
		alfaDuur = new float[nDiseases][96][2];
		attributableMortality = new float[nDiseases][96][2];
		baselineIncidence = new float[nDiseases][96][2];
		baselineFatalIncidence = new float[nDiseases][96][2];

	}

	protected void setNDiseases(String diseases) {
		nDiseases = Integer.parseInt(diseases);
		setNDiseases(nDiseases);

	}

	public String[] getDiseaseNames() {
		return diseaseNames;
	}

	public String getDiseaseName(int i) {
		return diseaseNames[i];
	}

	public void setDiseaseName(String diseaseNames, int i) {
		this.diseaseNames[i] = diseaseNames;
	}

	public void setDiseaseNames(String[] diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	public int getDiseaseNumber(int i) {
		return diseaseNumber[i];
	}

	public void setDiseaseNumber(int diseaseNumber, int i) {
		this.diseaseNumber[i] = diseaseNumber;
	}

	public void setDiseaseNumber(String diseaseNumber, int i) {
		this.diseaseNumber[i] = Integer.parseInt(diseaseNumber);
	}

	public String getClustername() {
		return Clustername;
	}

	public void setClusterName(String clustername) {
		Clustername = clustername;
	}

	public String getRelRiskDiseaseOnDiseaseFileName() {
		return relRiskDiseaseOnDiseaseFileName;
	}

	public void setRelRiskDiseaseOnDiseaseFileName(
			String relRiskDiseaseOnDiseaseFileName) {
		this.relRiskDiseaseOnDiseaseFileName = relRiskDiseaseOnDiseaseFileName;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int value) {
		this.startIndex = value;
	}

	public void setStartIndex(String value) {
		this.startIndex = Integer.parseInt(value);
	}

}
