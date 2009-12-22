/**
 * 
 */
package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.DynamoUpdateRuleConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.DynamoManyToManyUpdateRuleBase;

/**
 * @author Hendriek
 * This is the general version of the update rule for diseases and survival
 * for the DYNAMO-HIA model
 * It contains the methods and fields that are common to the more specific update rules for categorical, 
 * continuous and compound risk factors.
 * 
 */

/**
 * 
 * 
 * 
 * 
 * @author boshuizh configuration file should look like: <?xml version="1.0"
 *         encoding="UTF-8" standalone="no" ?> - <updateRuleConfiguration>
 *         <name>survival</name> <riskType>1</riskType> <nCat>2</nCat>
 *         <refValContinuousVariable>0.0</refValContinuousVariable> - <disease>
 *         <ClusterNumber>0</ClusterNumber>
 *         <diseaseNumberWithinCluster>0</diseaseNumberWithinCluster>
 *         <diseaseName>ziekte1</diseaseName>
 *         <baselineIncidenceFile>c:\hendriek\
 *         java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte1.xml</baselineIncidenceFile>
 *         <baselineFatalIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineFatalIncidence_4_ziekte1
 *         .xml</baselineFatalIncidenceFile>
 *         <attributableMortFile>c:\hendriek\java
 *         \dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte1.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte1.xml</relativeRiskFile> </disease>
 *         - <disease> <ClusterNumber>0</ClusterNumber>
 *         <diseaseNumberWithinCluster>1</diseaseNumberWithinCluster>
 *         <diseaseName>ziekte2</diseaseName>
 *         <baselineIncidenceFile>c:\hendriek\
 *         java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
 *         <baselineFatalIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineFatalIncidence_4_ziekte2
 *         .xml</baselineFatalIncidenceFile>
 *         <attributableMortFile>c:\hendriek\java
 *         \dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte2.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
 *         <charID>7</charID> <nclusters>1</nclusters> - <clusterInformation>
 *         <clusterNumber>0</clusterNumber>
 *         <startsAtDiseaseNumber>0</startsAtDiseaseNumber>
 *         <numberOfDiseasesInCluster>2</numberOfDiseasesInCluster>
 *         <diseaseOnDiseaseRelativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations
 *         \testsimulation\parameters\relativeRiskDiseaseOnDisease_cluster0
 *         .xml</diseaseOnDiseaseRelativeRiskFile> </clusterInformation>
 *         <baselineOtherMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineOtherMort.xml</baselineOtherMortFile>
 *         <relativeRiskOtherMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_OtherMort.xml</relativeRiskOtherMortFile>
 *         </updateRuleConfiguration>
 */

public class HealthStateManyToManyUpdateRule extends
		DynamoManyToManyUpdateRuleBase implements ConfigurationEntryPoint {
	Log log = LogFactory.getLog(this.getClass().getName());

	/* XML labels */
	protected String nDiseasesLabel = "Survival rule";
	protected String clusterInformationLabel = "clusterInformation";
	private String clusterNumberLabel = "clusterNumber";
	private String diseaseNumberWithinClusterLabel = "diseaseNumberWithinCluster";
	private String startsAtDiseaseNumberLabel = "startsAtDiseaseNumber";
	private static String numberOfDiseasesInClusterLabel = "numberOfDiseasesInCluster";

	/* update rule data on the structure of the disease data */
	protected String nClusterLabel = "nclusters";
	protected int[] nCombinations;
	protected int[] numberOfDiseasesInCluster;
	protected int[] clusterStartsAtDiseaseNumber;
	protected int totalNumberOfDiseases;
	protected boolean[] withCuredFraction;
	protected int nCluster = -1;
	protected int[] DiseaseNumberWithinCluster;
	protected int nDiseases;

	/* update rule cluster specific data */
	/* indexes are: 1:cluster number, 2: age 3: sex 4: from 5: to */
	protected float[][][][][] relativeRiskDiseaseOnDisease;
	private String[] diseaseOnDiseaseRelativeRiskFileName;

	/* update rule disease specific data */
	/* indexes are: 1:disease number, 2: age 3: sex 4: category */
	protected float baselineIncidence[][][] = null;
	protected float baselineFatalIncidence[][][] = null;
	protected float relRiskContinous[][][] = null;

	protected float relRiskCategorical[][][][] = null;
	protected float relRiskEnd[][][] = null;
	protected float relRiskBegin[][][] = null;
	protected float alfaDuur[][][] = null;
	protected float attributableMortality[][][] = null;

	/* other mortality data */
	protected float relRiskOtherMortEnd[][] = new float[96][2];
	protected float relRiskOtherMortBegin[][] = new float[96][2];
	protected float alfaDuurOtherMort[][] = new float[96][2];
	protected float relRiskOtherMortContinous[][] = new float[96][2];
	protected float[][][] relRiskOtherMortCategorical;
	protected float[][] baselineOtherMort;

	protected static String diseaseLabel = "disease";
	protected static String numberLabel = "diseaseNumber";
	protected static String diseaseNameLabel = "diseaseName";
	protected String nameLabel = "name";
	protected static String diseaseOnDiseaseRelativeRiskFileNameLabel = "diseaseOnDiseaseRelativeRiskFile";

	String relRiskDiseaseOnDiseaseFileName = null;
	String[] relRiskCatFileName = null;
	String[] relRiskContFileName = null;
	String[] relRiskEndFileName = null;
	String[] relRiskBeginFileName = null;
	String[] alfaDuurFileName = null;
	String[] attributableMortalityFileName = null;
	String[] baselineIncidenceFileName = null;
	String[] baselineFatalIncidenceFileName = null;

	private String baselineOtherMortFileName;
	private String relRiskOtherMortFileName;
	private String relRiskBeginOtherMortFileName;
	private String relRiskEndOtherMortFileName;
	private String alfaOtherMortFileName;

	private int[] diseaseNumber;
	private String[] diseaseNames;

	static protected String withCuredFractionLabel = "withCuredFraction";
	static protected String relRiskEndFileNameLabel = "endRelativeRiskFile";
	static protected String relRiskBeginFileNameLabel = "beginRelativeRiskFile";
	static protected String alfaDuurFileNameLabel = "alfaFile";
	static protected String durationClassLabel = "durationClass";
	static protected String referenceValueContinousLabel = "refValContinuousVariable";
	static protected String relRiskFileNameLabel = "relativeRiskFile";
	static protected String relRiskDiseaseOnDiseaseFileNameLabel = "diseaseOnDiseaseRelativeRiskFile";
	static protected String baselineIncidenceFileNameLabel = "baselineIncidenceFile";
	static protected String baselineFatalIncidenceFileNameLabel = "baselineFatalIncidenceFile";
	static protected String attributableMortalityFileNameLabel = "attributableMortFile";
	static protected String baselineOtherMortFileLabel = "baselineOtherMortFile";
	static protected String relativeRiskOtherMortFileLabel = "relativeRiskOtherMortFile";
	static protected String alfaDuurOtherMortFileNameLabel = "alfaRelRiskOtherMortFile";
	static protected String relativeRiskBeginOtherMortFileNameLabel = "beginRelativeRiskOtherMortFile";
	static protected String relativeRiskEndOtherMortFileNameLabel = "endRelativeRiskOtherMortFile";

	public HealthStateManyToManyUpdateRule() throws ConfigurationException,
			CDMUpdateRuleException {
		super();
	};

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float[] newValue = null;

		try {
			float[] oldValue = getValues(currentValues,
					getCharacteristicIndex());
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {
				int sexValue = getInteger(currentValues, getSexIndex());
				if (ageValue > 95)
					ageValue = 95;

				newValue = new float[oldValue.length];
				float[] currentDiseaseStateValues = new float[oldValue.length];
				/*
				 * float totInStates=0; for (int i=0;i<oldValue.length-1;i++)
				 * {currentDiseaseStateValues[i+1]=oldValue[i];
				 * totInStates+=oldValue[i];}
				 * currentDiseaseStateValues[0]=1-totInStates;
				 */

				double otherMort = calculateOtherCauseMortality(currentValues,
						ageValue, sexValue);
				double[] incidence = new double[getNDiseases()];
				double[] fatalIncidence = new double[getNDiseases()];
				double[] atMort = getAttributableMortality(ageValue, sexValue);
				for (int d = 0; d < getNDiseases(); d++) {
					incidence[d] = calculateIncidence(currentValues, ageValue,
							sexValue, d);
					fatalIncidence[d] = calculateFatalIncidence(currentValues,
							ageValue, sexValue, d);

				}
				// array currentDiseaseValue holds the current values of the
				// disease-characteristics
				// 

				double survivalFraction = Math.exp(-otherMort * getTimeStep());

				// private int[] numberOfDiseasesInCluster == array over
				// clusters;
				// private int[] clusterStartsAtDiseaseNumber == array over
				// clusters;
				// private int totalNumberOfDiseases;
				// private int nCluster = -1;
				// private int[] DiseaseNumberWithinCluster;== array over
				// diseases

				int currentStateNo = 0;

				for (int c = 0; c < nCluster; c++) {
					if (numberOfDiseasesInCluster[c] == 1) {

						int d = clusterStartsAtDiseaseNumber[c];

						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */
						double expAI = Math.exp((incidence[d] - atMort[d])
								* timeStep);
						// finci = ((p0 * em - i) * exp((i - em) * time) + i *
						// (1 -
						// p0))
						// / ((p0 * em - i) * exp((i - em) * time) + em * (1 -
						// p0))
						if (Math.abs(incidence[d] - atMort[d]) > 1E-15)
							newValue[currentStateNo] = (float) (((oldValue[currentStateNo]
									* atMort[d] - incidence[d])
									* expAI + incidence[d]
									* (1 - (double) oldValue[currentStateNo])) / ((oldValue[currentStateNo]
									* atMort[d] - incidence[d])
									* expAI + atMort[d]
									* (1 - (double) oldValue[currentStateNo])));
						else
							newValue[currentStateNo] = (float) (1 - (1 - oldValue[currentStateNo])
									/ (1 + incidence[d]
											* (1 - oldValue[currentStateNo])
											* timeStep));
						/*
						 * if incidence equal to attributable mortality, the
						 * denominator becomes zero and we need another formula
						 */

						survivalFraction *= Math.exp(-getTimeStep()
								* fatalIncidence[d])
								* (atMort[d]
										* (1 - oldValue[d])
										* Math.exp(-getTimeStep()
												* incidence[d]) + (atMort[d]
										* oldValue[d] - incidence[d])
										* Math.exp(-getTimeStep() * atMort[d]))
								/ (atMort[d] - incidence[d]);
						currentStateNo++;
					} else if (withCuredFraction[c]) {
						int d = clusterStartsAtDiseaseNumber[c];
						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */
						double transMat10;
						double transMat20;
						double expInc = Math
								.exp((-incidence[d] - incidence[d + 1])
										* -getTimeStep());
						double expAT = Math.exp((-atMort[d + 1])
								* -getTimeStep());
						double transMat00 = expInc;

						if ((incidence[d] + incidence[d + 1]) != 0)
							transMat10 = (1 - expInc) * incidence[d]
									/ (incidence[d] + incidence[d + 1]);
						else
							transMat10 = 0;

						if (incidence[d] + incidence[d + 1] == atMort[d + 1])
							transMat20 = expInc * incidence[d + 1];
						else
							transMat20 = (expAT - expInc)
									* incidence[d + 1]
									/ (incidence[d] + incidence[d + 1] - atMort[d + 1]);

						double transMat22 = (float) expAT;

						double survival = (1 - oldValue[d] - oldValue[d + 1])
								* transMat00 + oldValue[d] + oldValue[d + 1]
								* transMat22;
						survival += (1 - oldValue[d] - oldValue[d + 1])
								* (transMat10 + transMat20);
						newValue[currentStateNo] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat10 + oldValue[d]) / survival);
						newValue[currentStateNo + 1] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat20 + oldValue[d + 1] * transMat22) / survival);

						survivalFraction *= survival;

					}

					else {
						double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];

						/*
						 * first= changed state (row) second :sources of
						 * change(change=number in second state entry in matrix
						 * 
						 * thus [to][from]
						 */
						for (int row = 0; row < nCombinations[c]; row++)
							Arrays.fill(rateMatrix[row], 0);

						for (int row = 0; row < nCombinations[c]; row++)
							for (int column = 0; column < nCombinations[c]; column++)
								if (row == column)
								/*
								 * Matrix entry is formed as: / - attributable
								 * Mortality for each disease that is 1 in combi
								 * - sum incidence to all other disease that are
								 * 0 in combi (including RR's as above)-
								 * fatalIncidence (in all cases)
								 */
								{

									for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {

										/*
										 * first add fatal incidence
										 * irrespective of value of d
										 */
										double RR = 1;
										for (int dCause = 0; dCause < getNDiseases(); dCause++)
											if ((row & (1 << dCause)) == (1 << dCause))
												RR *= relativeRiskDiseaseOnDisease[c][ageValue][sexValue][dCause][d];

										rateMatrix[row][column] -= (RR * fatalIncidence[clusterStartsAtDiseaseNumber[c]
												+ d]);

										if ((row & (1 << d)) != (1 << d))

										/*
										 * d is 0, thus incidence should be
										 * added
										 */
										{
											RR = 1;
											for (int dCause = 0; dCause < getNDiseases(); dCause++)
												if ((row & (1 << dCause)) == (1 << dCause))
													RR *= relativeRiskDiseaseOnDisease[c][ageValue][sexValue][dCause][d];

											rateMatrix[row][column] -= (RR * incidence[clusterStartsAtDiseaseNumber[c]
													+ d]);
											// or d=1, then atmort should be
											// added
										} else
											rateMatrix[row][column] -= atMort[clusterStartsAtDiseaseNumber[c]
													+ d];
									}
								} else {
									// not a diagonal
									// find all patterns where row has exactly
									// one 1
									// more than the column

									for (int bits = 0; bits < getNDiseases(); bits++) {
										if ((row ^ column) == (1 << bits)) // only
																			// 1
											// difference
											// between
											// row
											// and
											// column
											if ((row & (1 << bits)) == (1 << bits)) // row=1
											// at
											// that
											// place
											// and
											// not
											// column

											{
												double RR = 1;
												for (int dCause = 0; dCause < getNDiseases(); dCause++)
													if ((column & (1 << dCause)) == (1 << dCause))
														RR *= relativeRiskDiseaseOnDisease[c][ageValue][sexValue][dCause][bits];

												rateMatrix[row][column] += (RR * incidence[clusterStartsAtDiseaseNumber[c]
														+ bits]);
											}
										;
									}
								}

						/* Exponentiale the matrix */

						MatrixExponential matExp = MatrixExponential
								.getInstance();
						float[][] TransitionProbabilities = new float[nCombinations[c]][nCombinations[c]];

						TransitionProbabilities = matExp
								.exponentiateFloatMatrix(rateMatrix);

						/*
						 * Multiply the matrix with the old values (column
						 * vector)
						 */
						double unconditionalNewValues[] = new double[nCombinations[c]];
						/* calculate the healthy state */
						double currentHealthyState = 1;
						for (int state = currentStateNo; state < currentStateNo
								+ nCombinations[c] - 1; state++)

							currentHealthyState -= oldValue[state];

						/*
						 * NB the unconditional new state starts at 0 with the
						 * healthy state, so oldvalue[1] belongs with
						 * unconditionalnewstate[0]
						 */

						for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
						{ /* transitionProbabilities are [to][from] */
							unconditionalNewValues[state1] = TransitionProbabilities[state1][0]
									* currentHealthyState;
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += TransitionProbabilities[state1][state2]
										* oldValue[state2 - 1 + currentStateNo];
						}
						/* calculate survival */
						double survival = 0;
						for (int state = 0; state < nCombinations[c]; state++) {
							survival += unconditionalNewValues[state];
						}
						survivalFraction *= survival;
						for (int state = currentStateNo; state < currentStateNo
								+ nCombinations[c] - 1; state++) {
							newValue[state] = (float) (unconditionalNewValues[state
									- currentStateNo + 1] / survival);
						}
						;
						currentStateNo += nCombinations[c] - 1;
					} // end if statement for cluster diseases

				} // end loop over clusters
				newValue[currentStateNo] = (float) survivalFraction
						* oldValue[currentStateNo];

				return newValue;
			}
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by HealthStateMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ getCharacteristicIndex());
			e.printStackTrace();
			throw e;

		}

	}

	/**
	 * @param ageValue
	 * @param sexValue
	 * @return
	 */

	private double calculateIncidence(Object[] currentValues, int ageValue,
			int sexValue, int diseaseNumber) throws CDMUpdateRuleException {
		double incidence = 0;
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
					* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
					* Math
							.pow(
									(riskFactorValue - referenceValueContinous),
									relRiskContinous[diseaseNumber][ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);

				incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
						* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
								* Math
										.exp(-riskDurationValue
												* alfaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);
			} else
				incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
						* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}
		return incidence;
	}

	private double calculateFatalIncidence(Object[] currentValues,
			int ageValue, int sexValue, int diseaseNumber)
			throws CDMUpdateRuleException {
		double fatalIncidence = 0;
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			fatalIncidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
					* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			fatalIncidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
					* Math
							.pow(
									(riskFactorValue - referenceValueContinous),
									relRiskContinous[diseaseNumber][ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);

				fatalIncidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
						* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
								* Math
										.exp(-riskDurationValue
												* alfaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);
			} else
				fatalIncidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
						* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}
		return fatalIncidence;
	}

	private double calculateOtherCauseMortality(Object[] currentValues,
			int ageValue, int sexValue) throws CDMUpdateRuleException {
		double otherCauseMortality = 0;
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			otherCauseMortality = baselineOtherMort[ageValue][sexValue]
					* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			otherCauseMortality = baselineOtherMort[ageValue][sexValue]
					* Math.pow(
							(riskFactorValue - getReferenceValueContinous()),
							relRiskOtherMortContinous[ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);

				otherCauseMortality = baselineOtherMort[ageValue][sexValue]
						* ((relRiskOtherMortBegin[ageValue][sexValue] - relRiskOtherMortEnd[ageValue][sexValue])
								* Math
										.exp(-riskDurationValue
												* alfaDuurOtherMort[ageValue][sexValue]) + relRiskOtherMortEnd[ageValue][sexValue]);
			} else
				otherCauseMortality = baselineOtherMort[ageValue][sexValue]
						* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
		}
		return otherCauseMortality;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint#
	 * loadConfigurationFile(java.io.File)
	 */
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		try {
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();

			// Validate the xml by xsd schema
			configurationFileConfiguration.setValidating(true);
			configurationFileConfiguration.load();

			ConfigurationNode rootNode = configurationFileConfiguration
					.getRootNode();
			if (configurationFileConfiguration.getRootElementName() != globalTagName)

				throw new DynamoUpdateRuleConfigurationException(" Tagname "
						+ globalTagName
						+ " expected in file for updaterule ClusterDisease"
						+ " but found tag " + rootNode.getName());

			/* first handle the general information (not disease dependent) */
			handleCharID(configurationFileConfiguration);
			handleRiskType(configurationFileConfiguration);
			handleNCat(configurationFileConfiguration);
			handleNClusters(configurationFileConfiguration);
			handleOtherMort(configurationFileConfiguration);

			handleDiseaseData(rootNode);

			success = true;
			return success;
		} catch (NoSuchElementException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(),
					new ConfigurationException(
							CDMConfigurationException.noConfigurationTagMessage
									+ this.nDiseasesLabel), configurationFile
							.getAbsolutePath());
		} catch (DynamoUpdateRuleConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
		}
		return success;
	}

	/**
	 * this method reads in the data of a single disease from an XML file with
	 * the following format:
	 * 
	 * <clusterInformation> <clusterNumber>0</clusterNumber>
	 * <startsAtDiseaseNumber>0</startsAtDiseaseNumber>
	 * <numberOfDiseasesInCluster>2</numberOfDiseasesInCluster>
	 * <diseaseOnDiseaseRelativeRiskFile>c:\hendriek\
	 * java\dynamohome\Simulations\testsimulation\parameters
	 * \relativeRiskDiseaseOnDisease_cluster0.xml
	 * </diseaseOnDiseaseRelativeRiskFile> </clusterInformation>
	 * 
	 * @param rootNode
	 *            : Node containing the disease data
	 * @throws ConfigurationException
	 */
	protected void handleDiseaseData(ConfigurationNode rootNode)
			throws ConfigurationException {
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
				.getChildren();

		/*
		 * handle cluster info, as this is needed to initialize the arrays that
		 * are to be filled for the individual diseases
		 */

		/*
		 * INFO to handle: - <clusterInformation>
		 * <clusterNumber>0</clusterNumber>
		 * <startsAtDiseaseNumber>0</startsAtDiseaseNumber>
		 * <numberOfDiseasesInCluster>2</numberOfDiseasesInCluster>
		 * <diseaseOnDiseaseRelativeRiskFile
		 * >c:\hendriek\java\dynamohome\Simulations \testsimulation\parameters
		 * \relativeRiskDiseaseOnDisease_cluster0.xml
		 * </diseaseOnDiseaseRelativeRiskFile> </clusterInformation>
		 */

		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() == clusterInformationLabel) {
				handleClusterInformation(rootChild);

			}

			/* also read the name of the info file (not important) */
			else if (rootChild.getName() == nameLabel) {
				String value = (String) rootChild.getValue();
				log.debug("Setting overall name to: " + value);
				setNameLabel(value);
			}
		}
		/* count the total number of diseases and set it */
		int nTotDiseases = 0;
		nCombinations = new int[getNCluster()];
		for (int c = 0; c < getNCluster(); c++) {
			nTotDiseases += getNdiseasesInCluster(c);
			nCombinations[c] = (int) Math.pow(2, getNdiseasesInCluster(c));
		}
		/* handle the disease dependent information */
		setNDiseases(nTotDiseases);
		DiseaseNumberWithinCluster = new int[nTotDiseases];

		int diseaseRead = 0;
		for (ConfigurationNode rootChild : rootChildren) {

			if (rootChild.getName() == diseaseLabel) {
				diseaseRead = handleDiseaseInfo(rootChild, diseaseRead);

				/*
				 * reads per disease: <disease> <ClusterNumber>0</ClusterNumber>
				 * <diseaseNumberWithinCluster >1</diseaseNumberWithinCluster>
				 * <diseaseName>ziekte2</diseaseName> <baselineIncidenceFile>
				 * c:\hendriek\java\dynamohome\Simulations
				 * \testsimulation\parameters
				 * \baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
				 * <baselineFatalIncidenceFile>c:\hendriek\java\dynamohome\
				 * Simulations
				 * \testsimulation\parameters\baselineFatalIncidence_4_ziekte2
				 * .xml</baselineFatalIncidenceFile> <attributableMortFile>c:
				 * \hendriek\java\dynamohome\Simulations
				 * \testsimulation\parameters
				 * \attributableMort_4_ziekte2.xml</attributableMortFile>
				 * <relativeRiskFile >c:\hendriek\java\dynamohome\Simulations\
				 * testsimulation\parameters
				 * \relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
				 */
			}

		}// end loop over diseases

		if (diseaseRead != getNDiseases())
			log
					.fatal("Number of disease read ("
							+ diseaseRead
							+ "does not agree with number of diseases as given in XML file"
							+ getNDiseases());
		// TODO gooi exception
	}

	/**
	 * this method reads in the number of categories of a categorical covariate
	 * from the configurationfile
	 * 
	 * @param simulationConfiguration
	 * @throws ConfigurationException
	 */
	protected void handleNCat(HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			int nCat = simulationConfiguration.getInt(nCatLabel);
			log.debug("Setting number of categories to " + nCat);
			setNCat(nCat);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading number of categories");
		}
	}

	/**
	 * this method reads in the number of categories of a categorical covariate
	 * from the configurationfile
	 * 
	 * @param simulationConfiguration
	 * @throws ConfigurationException
	 */
	protected void handleRefValueContinuous(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			float refVal = simulationConfiguration.getFloat(refValLabel);
			log.debug("Setting reference value to " + refVal);
			setReferenceValueContinous(refVal);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading reference value continuous variable");
		}
	}

	/**
	 * this method reads in the number of categories of a categorical covariate
	 * from the configurationfile
	 * 
	 * @param simulationConfiguration
	 * @throws ConfigurationException
	 */
	protected void handleDurationClass(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			float refVal = simulationConfiguration.getFloat(durationClassLabel);
			log.debug("Setting reference value to " + refVal);
			setDurationClass((int) refVal);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading reference value continuous variable");
		}
	}

	/**
	 * this method reads in the riskTypenumber (categorical, continuous,
	 * compound) from the configurationfile
	 * 
	 * @param simulationConfiguration
	 * @throws ConfigurationException
	 */
	protected void handleRiskType(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			int riskType = simulationConfiguration.getInt(riskTypeLabel);
			log.debug("Setting riskType to " + riskType);
			setRiskType(riskType);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage);
		}
	}

	protected int handleDiseaseInfo(ConfigurationNode node, int diseaseNo)

	throws ConfigurationException {

		/*
		 * to read per disease <disease> <ClusterNumber>0</ClusterNumber>
		 * <diseaseNumberWithinCluster>1</diseaseNumberWithinCluster>
		 * <diseaseName>ziekte2</diseaseName>
		 * <baselineIncidenceFile>c:\hendriek\
		 * java\dynamohome\Simulations\testsimulation
		 * \parameters\baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
		 * <baselineFatalIncidenceFile
		 * >c:\hendriek\java\dynamohome\Simulations\testsimulation
		 * \parameters\baselineFatalIncidence_4_ziekte2
		 * .xml</baselineFatalIncidenceFile>
		 * <attributableMortFile>c:\hendriek\java
		 * \dynamohome\Simulations\testsimulation
		 * \parameters\attributableMort_4_ziekte2.xml</attributableMortFile>
		 * <relativeRiskFile
		 * >c:\hendriek\java\dynamohome\Simulations\testsimulation
		 * \parameters\relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
		 */

		try {

			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

			List<ConfigurationNode> diseaseChildren = (List<ConfigurationNode>) node
					.getChildren();
			for (ConfigurationNode diseaseElement : diseaseChildren) {
				if (diseaseElement.getName() == diseaseNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseasename of disease" + diseaseNo
							+ " to: " + value);
					setDiseaseName(value, diseaseNo);
				}

				else if (diseaseElement.getName() == clusterNumberLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting clusterNumber of disease " + diseaseNo
							+ " to: " + value);
					// TODO check if OK with other info
				}

				else if (diseaseElement.getName() == diseaseNumberWithinClusterLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseaseNumber within Cluster of disease"
							+ diseaseNo + " to: " + value);
					setDiseaseNumberWithinCluster(value, diseaseNo);
				}

				else if (diseaseElement.getName() == attributableMortalityFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting AttributableMortalityFilename to: "
							+ value);
					setAttributableMortalityFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							attributableMortalityFileName[diseaseNo],
							"attributableMortalities", "attributableMortality",
							false);
					setAttributableMortality(inputData, diseaseNo);
					log.debug("reading AttributableMortality data for disease "
							+ diseaseNo);

				}

				else if (diseaseElement.getName() == baselineIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineIncidenceFilename to: " + value);
					setBaselineIncidenceFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							baselineIncidenceFileName[diseaseNo],
							"baselineIncidences", "baselineIncidence", false);
					setBaselineIncidence(inputData, diseaseNo);
					log.debug("reading BaselineIncidence data for disease "
							+ diseaseNo);

				}

				else if (diseaseElement.getName() == baselineFatalIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineFatalIncidenceFilename to: "
							+ value);
					setBaselineFatalIncidenceFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							baselineFatalIncidenceFileName[diseaseNo],
							"baselineFatalIncidences",
							"baselineFatalIncidence", false);
					setBaselineFatalIncidence(inputData, diseaseNo);
					log
							.debug("reading BaselineFatalIncidence data for disease "
									+ diseaseNo);

				}

				else if (diseaseElement.getName() == relRiskFileNameLabel) {
					String value = (String) diseaseElement.getValue();

					log.debug("Setting baselineRelativeRiskFilename to: "
							+ value);

					setRelRiskFileName(value, diseaseNo);
					if (riskType != 2) {
						if (riskType == 1) {
							float[][][] inputData;
							inputData = factory.manufactureTwoDimArray(
									relRiskCatFileName[diseaseNo],
									/* "relativeRisks" */ "relrisksfromriskfactor_categorical4p",
									/* "relativeRisk" */"relativerisk", false);
							setRelRiskCat(inputData, diseaseNo);
						} else {
							float[][][] inputData;
							inputData = factory.manufactureTwoDimArray(
									relRiskCatFileName[diseaseNo],
									"relativeRisks",
									/* "relativeRisk" */"relativerisk", false);
							setRelRiskCat(inputData, diseaseNo);
						}
						log.debug("reading relative risks for disease "
								+ diseaseNo);
					} else {

						float[][] inputData;
						inputData = factory.manufactureOneDimArray(
								relRiskCatFileName[diseaseNo], /*"relativeRisks"*/ "relrisksfromriskfactor_continuous4p",
								/* "relativeRisk" */"relativerisk", false);
						setRelRiskCont(inputData, diseaseNo);

						log.debug("reading relative risks for disease "
								+ diseaseNo);
					}

				}

				else if (diseaseElement.getName() == relRiskEndFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting relRiskEndFileName to: " + value);
					setRelRiskEndFileName(value, diseaseNo);
					float[][] inputData2 = new float[96][2];
					inputData2 = factory.manufactureOneDimArray(
							getRelRiskEndFileName()[diseaseNo],
							"relativeRisks", /* "relativeRisk" */"relativerisk",
							false);
					setRelRiskEnd(inputData2, diseaseNo);
					log.debug("reading relative risks end for disease "
							+ diseaseNo);

				} else if (diseaseElement.getName() == relRiskBeginFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting relRiskBeginFileName to: " + value);
					setRelRiskBeginFileName(value, diseaseNo);

					float[][] inputData2 = new float[96][2];
					inputData2 = factory.manufactureOneDimArray(
							getRelRiskBeginFileName()[diseaseNo],
							"relativeRisks", /* "relativeRisk" */"relativerisk",
							false);
					setRelRiskBegin(inputData2, diseaseNo);
					log.debug("reading relative risks begin for disease "
							+ diseaseNo);

				} else if (diseaseElement.getName() == alfaDuurFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting alfaDuurFileName for disease "
							+ diseaseNo + " to: " + value);
					setAlfaDuurFileName(value, diseaseNo);
					float[][] inputData2 = new float[96][2];
					inputData2 = factory.manufactureOneDimArray(
							getAlfaDuurFileName()[diseaseNo], "alfa", "alfa",
							false);
					setAlfaDuur(inputData2, diseaseNo);
					log.debug("reading alfaduur for disease " + diseaseNo);

				}

			}

			// TODO checken of de nummering consistent is in de invoer file
			diseaseNo++;

			return diseaseNo;

		} catch (Exception e) {// TODO}

		}
		return diseaseNo;
	}

	protected void handleNClusters(
			XMLConfiguration configurationFileConfiguration)
	/* <nclusters>1</nclusters> */
	throws ConfigurationException {
		try {
			int nClusters = configurationFileConfiguration
					.getInt(nClusterLabel);
			log.debug("Setting number of clusters to " + nClusters);
			setNCluster(nClusters);
			diseaseOnDiseaseRelativeRiskFileName = new String[nClusters];
			relativeRiskDiseaseOnDisease = new float[nClusters][96][2][][];
			numberOfDiseasesInCluster = new int[nClusters];
			clusterStartsAtDiseaseNumber = new int[nClusters];
			withCuredFraction = new boolean[nClusters];

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading number of categories");
		}
	}

	private void setDiseaseNumberWithinCluster(String value, int i) {

		DiseaseNumberWithinCluster[i] = Integer.parseInt(value);

	}

	protected void handleClusterInformation(ConfigurationNode rootChild) {

		try {
			List<ConfigurationNode> clusterChildren = (List<ConfigurationNode>) rootChild
					.getChildren();

			int clusterNumber = 0;
			int nDiseasesInCluster = 0;
			for (ConfigurationNode clusterElement : clusterChildren) {

				if (clusterElement.getName() == clusterNumberLabel) {
					String value = (String) clusterElement.getValue();
					log.debug("Setting clusterNumber to: " + value);
					clusterNumber = Integer.parseInt(value);
				}

				if (clusterElement.getName() == startsAtDiseaseNumberLabel) {
					String value = (String) clusterElement.getValue();
					log.debug("Setting number of first disease in cluster to: "
							+ value);
					setStartsAtDiseaseNumber(value, clusterNumber);
				}
				if (clusterElement.getName() == withCuredFractionLabel) {
					String value = (String) clusterElement.getValue();
					log.debug("Setting with cured fraction to: " + value);
					setWithCuredFraction(value, clusterNumber);
				}

				if (clusterElement.getName() == numberOfDiseasesInClusterLabel) {
					String value = (String) clusterElement.getValue();
					nDiseasesInCluster = Integer.parseInt(value);
					log.debug("Setting number of diseases in cluster to: "
							+ value);
					setNumberOfDiseasesInCluster(value, clusterNumber);
				}

				if (clusterElement.getName() == diseaseOnDiseaseRelativeRiskFileNameLabel) {
					String value = (String) clusterElement.getValue();
					log
							.debug("Setting name of diseaseOnDiseaseRelativeRiskFile to: "
									+ value);
					setDiseaseOnDiseaseRelativeRiskFile(value, clusterNumber);
				}
			}

			if (nDiseasesInCluster == 0)
				throw new CDMUpdateRuleException(
						"configuration file for survival update rule has no number of disease in cluster "
								+ clusterNumber);
			else if (nDiseasesInCluster == 1) {
				setRelativeRisksDiseaseOnDisease(null, clusterNumber);
				// TODO if disease with cured fraction
			} else {
				float[][][][] inputData = new float[96][2][nDiseasesInCluster][nDiseasesInCluster];
				ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
				inputData = factory.manufactureThreeDimArray(
						getDiseaseOnDiseaseRelativeRiskFileName(clusterNumber),
						/* "relativeRisks" */"relativerisks_diseaseondisease", /* "relativeRisk" */
						"relativerisk");
				setRelativeRisksDiseaseOnDisease(inputData, clusterNumber);
				log
						.debug("reading DiseaseOnDiseaseRelativeRiskFile for cluster "
								+ clusterNumber);
			}
			totalNumberOfDiseases += nDiseasesInCluster;
		}

		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	private void setRelativeRisksDiseaseOnDisease(float[][][][] inputData,
			int clusterNumber) {
		relativeRiskDiseaseOnDisease[clusterNumber] = inputData;

	}

	private String getDiseaseOnDiseaseRelativeRiskFileName(int i) {

		return diseaseOnDiseaseRelativeRiskFileName[i];
	}

	protected int getNCluster() {
		return nCluster;

	}

	private void setNumberOfDiseasesInCluster(String value, int i) {

		numberOfDiseasesInCluster[i] = Integer.parseInt(value);

	}

	public int getNumberOfDiseasesInCluster(int i) {

		return numberOfDiseasesInCluster[i];

	}

	private void setDiseaseOnDiseaseRelativeRiskFile(String value, int i) {
		diseaseOnDiseaseRelativeRiskFileName[i] = value;

	}

	// TODO Auto-generated method stub

	private void setStartsAtDiseaseNumber(String value, int i) {
		clusterStartsAtDiseaseNumber[i] = Integer.parseInt(value);

	}

	private void setStartsAtDiseaseNumber(int value, int i) {
		clusterStartsAtDiseaseNumber[i] = value;

	}

	protected int getNdiseasesInCluster(int i) {

		return numberOfDiseasesInCluster[i];
	}

	protected void handleOtherMort(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(baselineOtherMortFileLabel);
			log.debug("Setting BaselineOtherMortalityFilename to: " + FileName);
			setBaselineOtherMortFileName(FileName);
			float[][] inputData = new float[96][2];
			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
			inputData = factory.manufactureOneDimArray(
					getBaselineOtherMortFileName(), "baselineOtherMortalities",
					"baselineOtherMortality", false);
			setBaselineOtherMort(inputData);
			log.debug("reading BaselineOtherMortality for disease "
					+ diseaseNumber);
			FileName = simulationConfiguration
					.getString(relativeRiskOtherMortFileLabel);
			log.debug("Setting RelRiskOtherMortalityFilename to: " + FileName);
			setRelRiskOtherMortFileName(FileName);
			if (riskType == 1) {
				float[][][] inputData2 = new float[96][2][];
				inputData2 = factory.manufactureTwoDimArray(
						getRelRiskOtherMortFileName(), /* "relativeRisks" */ "relativerisks_othermort_categorical",
						/* "relativeRisk" */"relativerisk" /*
															 * Maybe later to:
															 * XMLTagEntityEnum
															 * .RELATIVERISK...
															 */, false);
				setRelRiskOtherMortCategorical(inputData2);
			} else if (riskType == 3) {
				float[][][] inputData2 = new float[96][2][];
				inputData2 = factory.manufactureTwoDimArray(
						getRelRiskOtherMortFileName(), /* "relativeRisks" */ "relativerisks_othermort_categorical",
						/* "relativeRisk" */"relativerisk", true);
				setRelRiskOtherMortCategorical(inputData2);
			} else {
				float[][] inputData2 = new float[96][2];
				inputData2 = factory.manufactureOneDimArray(
						getRelRiskOtherMortFileName(), /* "relativeRisks" */ "relativerisks_othermort_continuous",
						/* "relativeRisk" */"relativerisk", false);
				setRelRiskOtherMortContinous(inputData2);
			}
			if (riskType == 3) {
				/* rr's for end */
				FileName = simulationConfiguration

				.getString(relativeRiskEndOtherMortFileNameLabel);
				log.debug("Setting RelRiskEndOtherMortalityFilename to: "
						+ FileName);
				setRelRiskEndOtherMortFileName(FileName);
				float[][] inputData2 = new float[96][2];
				inputData2 = factory.manufactureOneDimArray(
						getRelRiskOtherMortEndFileName(), "relativeRisks",
						/* "relativeRisk" */"relativerisk", false);
				setRelRiskOtherMortEnd(inputData2);
				/* rr's for begin */

				FileName = simulationConfiguration
						.getString(relativeRiskBeginOtherMortFileNameLabel);
				log.debug("Setting RelRiskBeginOtherMortalityFilename to: "
						+ FileName);
				setRelRiskBeginOtherMortFileName(FileName);

				inputData2 = factory.manufactureOneDimArray(
						getRelRiskBeginOtherMortFileName(), "relativeRisks",
						/* "relativeRisk" */"relativerisk", false);
				setRelRiskOtherMortBegin(inputData2);
				/* alfa */

				FileName = simulationConfiguration
						.getString(alfaDuurOtherMortFileNameLabel);
				log.debug("Setting alfaOtherMortalityFilename to: " + FileName);
				setAlfaOtherMortFileName(FileName);
				inputData2 = factory.manufactureOneDimArray(
						getAlfaDuurOtherMortFileName(), "alfa", "alfa", false);
				setAlfaDuurOtherMort(inputData2);

			}
			// TODO checken if other risk types completely done

			//
			// for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			//	 

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	/**
	 * @return
	 */
	private String getAlfaDuurOtherMortFileName() {

		return alfaOtherMortFileName;
	}

	/**
	 * @return
	 */
	private String getRelRiskOtherMortEndFileName() {

		return relRiskEndOtherMortFileName;
	}

	private void setRelRiskOtherMortCategorical(float[][][] input) {

		relRiskOtherMortCategorical = input;
	}

	public float[][] getBaselineOtherMort() {
		return baselineOtherMort;
	}

	// obsolete: only used for testing
	protected void handleRelativeRisks(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			// String FileName =
			// simulationConfiguration.getString(attributableMortalityFileNameLabel);
			// log.debug("Setting BaselineIncidenceFilename to: " + FileName );
			// setattributableMortalityFileName(FileName);

			relRiskCategorical = new float[96][2][nDiseases][nCat];
			float[] fill = { 1, 1.1F, 1.2F, 1.5F };
			for (int d = 0; d < 6; d++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						relRiskCategorical[a][g][d] = fill;

					}
			;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	/* real version that reads from XML */

	/**
	 * 
	 * this method reads the xml files for the relative risks and other
	 * parameter data stored in three dimensional arrays
	 * 
	 * @param XMLFileName
	 *            : name of the file to read from (entire path)
	 * @param dataName
	 *            : name of the type of input (currently implemented are:
	 *            "baselineOtherMortality", "attributableMortality" and
	 *            "baselineIncidence")
	 * 
	 * 
	 * @throws ConfigurationException
	 */
	protected void loadOneDimData(String XMLFileName, String dataName,
			int dNumber) throws ConfigurationException {
		try {

			log.debug("Setting " + dataName);
			float[][] inputData = new float[96][2];

			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

			if (dataName == " baselineOtherMortality") {

				inputData = factory.manufactureOneDimArray(
						baselineOtherMortFileName, "baselineOtherMortalities",
						"baselineOtherMortality", false);
				setBaselineOtherMort(inputData);
			}

			if (dataName == "baselineIncidence") {

				inputData = factory.manufactureOneDimArray(
						baselineIncidenceFileName[dNumber],
						"baselineIncidences", "baselineIncidence", false);
				setBaselineIncidence(inputData, dNumber);
			}

			if (dataName == "baselineFatalIncidence") {

				inputData = factory.manufactureOneDimArray(
						baselineFatalIncidenceFileName[dNumber],
						"baselineFatalIncidences", "baselineFatalIncidence",
						false);
				setBaselineIncidence(inputData, dNumber);
			}

			if (dataName == "attributableMortality") {
				inputData = factory.manufactureOneDimArray(
						attributableMortalityFileName[dNumber],
						"attributableMortalities", "attributableMortality",
						false);

				setAttributableMortality(inputData, dNumber);
			}

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	/**
	 * this method reads the xml files for the relative risks and other
	 * parameter data stored in three dimensional arrays
	 * 
	 * @param XMLFileName
	 *            : name of the file to read from (entire path)
	 * @param dataName
	 *            : name of the type of input (currently implemented are:
	 *            "relRiskCat", "relRiskCatOtherMort")
	 * 
	 * @throws ConfigurationException
	 */
	protected void loadTwoDimData(String XMLFileName, String dataName,
			int dNumber) throws ConfigurationException {
		try {

			/*
			 * XMLConfiguration configurationFileConfiguration = new
			 * XMLConfiguration( XMLFileName);
			 */log.debug("Setting " + dataName);
			float[][][] inputData;
			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

			if (dataName == "relRiskCat") {
				inputData = factory.manufactureTwoDimArray(
						relRiskCatFileName[dNumber], "relativeRisks",
						"relativeRisk", false);

				setRelRiskCategorical(inputData, dNumber);
			}
			if (dataName == "relRiskCatOtherMort") {

				inputData = factory.manufactureTwoDimArray(
						relRiskOtherMortFileName, "relativeRisks",
						"relativeRisk", false);

				setRelRiskOtherMortCategorical(inputData);
			}

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	/* temporary for testing */
	/* should be reading from XML */
	/* in that case argument should be string (filename) */

	protected float[][] getData(float fill) throws ConfigurationException {
		try {
			float[][] data = new float[96][2];
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {
					data[a][g] = fill;
				}
			;
			return data;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	/**
	 * the method was used during testing phase (filled arrays with data before
	 * they were read from XML) now obsolete
	 * 
	 * @param fill
	 * @return
	 * @throws ConfigurationException
	 */
	protected float[][][] getDataTwoDim(float fill[])
			throws ConfigurationException {
		try {
			int nCat = fill.length;
			float[][][] data = new float[96][2][nCat];
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {
					data[a][g] = fill;
				}
			;
			return data;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleAttributableMortality(
			HierarchicalConfiguration simulationConfiguration, int dNumber)
			throws ConfigurationException {
		try {
			String attributableMortalityFileName = simulationConfiguration
					.getString(attributableMortalityFileNameLabel);
			log.debug("Setting AttributableMortalityFilename to: "
					+ attributableMortalityFileName);
			setAttributableMortalityFileName(attributableMortalityFileName,
					dNumber);
			// loadOneDimData(
			// attributableMortalityFileName,"attributableMortality",0.01F);
			loadOneDimData(attributableMortalityFileName,
					"attributableMortality", dNumber);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleBaselineIncidence(
			HierarchicalConfiguration simulationConfiguration, int dNumber)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(baselineIncidenceFileNameLabel);
			log.debug("Setting BaselineIncidenceFilename to: " + FileName);
			setBaselineIncidenceFileName(FileName, dNumber);
			loadOneDimData(baselineIncidenceFileName[dNumber],
					"baselineIncidence", dNumber);
			// loadOneDimData(
			// baselineIncidenceFileName,"baselineIncidence",0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleBaselineFatalIncidence(
			HierarchicalConfiguration simulationConfiguration, int dNumber)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(baselineFatalIncidenceFileNameLabel);
			log.debug("Setting BaselineFatalIncidenceFilename to: " + FileName);
			setBaselineIncidenceFileName(FileName, dNumber);
			loadOneDimData(baselineFatalIncidenceFileName[dNumber],
					"baselineFatalIncidence", dNumber);
			// loadOneDimData(
			// baselineIncidenceFileName,"baselineIncidence",0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleBaselineOtherMort(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(baselineOtherMortFileLabel);
			log.debug("Setting BaselineOtherMortalityFilename to: " + FileName);
			setBaselineOtherMortFileName(FileName);
			loadOneDimData(baselineOtherMortFileName, "baselineOtherMort", 0);
			// loadOneDimData(
			// baselineIncidenceFileName,"baselineIncidence",0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleRelRiskCat(
			HierarchicalConfiguration simulationConfiguration, int dNumber)
			throws ConfigurationException {
		try {

			// relRiskCatFileName
			String FileName = simulationConfiguration
					.getString(relRiskFileNameLabel);
			log.debug("Setting RelativeRiskFilename to: " + FileName);
			setRelRiskFileName(FileName, dNumber);
			// float[] fill = { 1.0F, 1.2F, 1.5F, 2F };
			// loadTwoDimData( relRiskCatFileName,"relRiskCat",fill);
			loadTwoDimData(relRiskCatFileName[dNumber], "relRiskCat", dNumber);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleRelRiskOtherMortCat(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {

			// relRiskCatFileName
			String FileName = simulationConfiguration
					.getString(relativeRiskOtherMortFileLabel);
			log.debug("Setting relativeRiskOtherMortFile to: " + FileName);
			setRelativeRiskOtherMortCatFileName(FileName);
			// temporary filling with data for testing
			// float[] fill={1.0F,1.2F,1.5F,2F};
			// loadTwoDimData( relRiskCatFileName,"relRiskCatOtherMort",fill);
			if (riskType == 1 || riskType == 3)
				loadTwoDimData(relRiskOtherMortFileName, "relRiskCatOtherMort",
						0);
			else
				loadOneDimData(relRiskOtherMortFileName,
						"relRiskContOtherMort", 0);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	public void setNCluster(int cluster) {
		nCluster = cluster;
	}

	public String getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(String nameLabel) {
		this.nameLabel = nameLabel;
	}

	protected void setRelRiskFileName(String value, int i) {
		relRiskCatFileName[i] = value;

	}

	protected void setRelRiskCat(float value[][][], int i) {
		relRiskCategorical[i] = value;

	}

	protected void setRelRiskCont(float value[][], int i) {
		relRiskContinous[i] = value;

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

	protected double[] getAttributableMortality(int age, int sex) {
		int dim = this.attributableMortality.length;
		double[] returnarray = new double[dim];
		for (int d = 0; d < dim; d++)
			returnarray[d] = this.attributableMortality[d][age][sex];
		return returnarray;
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
		setNDiseases(Integer.parseInt(diseases));

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

	protected void setAttributableMortality(float[][] attributableMortality,
			int i) {
		this.attributableMortality[i] = attributableMortality;
	}

	public String getRelRiskDiseaseOnDiseaseFileName() {
		return relRiskDiseaseOnDiseaseFileName;
	}

	public void setRelRiskDiseaseOnDiseaseFileName(
			String relRiskDiseaseOnDiseaseFileName) {
		this.relRiskDiseaseOnDiseaseFileName = relRiskDiseaseOnDiseaseFileName;
	}

	private void setRelativeRiskOtherMortCatFileName(String fileName) {

		relRiskOtherMortFileName = fileName;
	}

	public String getBaselineIncidenceFileName(int dNumber) {
		return baselineIncidenceFileName[dNumber];
	}

	public float[][] getRelRiskContinous(int dNumber) {
		return relRiskContinous[dNumber];
	}

	public float[][][] getRelRiskCategorical(int dNumber) {
		return relRiskCategorical[dNumber];
	}

	public void setRelRiskCategorical(float[][][] input, int dNumber) {
		this.relRiskCategorical[dNumber] = new float[96][2][input[0][0].length];
		this.relRiskCategorical[dNumber] = input;
	}

	public float[][][] getRelRiskEnd() {
		return relRiskEnd;
	}

	public void setRelRiskEnd(float[][][] relRiskEnd) {
		this.relRiskEnd = relRiskEnd;
	}

	public void setRelRiskEnd(float[][] relRiskEnd, int d) {
		this.relRiskEnd[d] = relRiskEnd;
	}

	public float[][][] getRelRiskBegin() {
		return relRiskBegin;
	}

	public void setRelRiskBegin(float[][][] relRiskBegin) {
		this.relRiskBegin = relRiskBegin;
	}

	public void setRelRiskBegin(float[][] relRiskIn, int d) {
		this.relRiskBegin[d] = relRiskIn;
	}

	public float[][][] getAlfaDuur() {
		return alfaDuur;
	}

	public void setAlfaDuur(float[][][] alfaDuur) {
		this.alfaDuur = alfaDuur;
	}

	public void setAlfaDuur(float[][] alfaDuur, int d) {
		this.alfaDuur[d] = alfaDuur;
	}

	public float[][][] getAttributableMortality() {
		return attributableMortality;
	}

	public void setAttributableMortality(float[][][] attributableMortality) {
		this.attributableMortality = attributableMortality;
	}

	public float[][][] getBaselineIncidence() {
		return baselineIncidence;
	}

	public void setBaselineIncidence(float[][][] baselineIncidence) {
		this.baselineIncidence = baselineIncidence;
	}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getDurationClass() {
		return durationClass;
	}

	public void setDurationClass(int durationClass) {
		this.durationClass = durationClass;
	}

	public int getAgeIndex() {
		return ageIndex;
	}

	public void setAgeIndex(int ageIndex) {
		this.ageIndex = ageIndex;
	}

	public int getSexIndex() {
		return sexIndex;
	}

	public int getCharacteristicIndex() {
		return this.characteristicIndexOfThisRule;
	}

	public void setSexIndex(int sexIndex) {
		this.sexIndex = sexIndex;
	}

	public int getRiskFactorIndex1() {
		return riskFactorIndex1;
	}

	public void setRiskFactorIndex1(int riskFactorIndex1) {
		this.riskFactorIndex1 = riskFactorIndex1;
	}

	public int getRiskFactorIndex2() {
		return riskFactorIndex2;
	}

	public void setRiskFactorIndex2(int riskFactorIndex2) {
		this.riskFactorIndex2 = riskFactorIndex2;
	}

	public String[] getRelRiskCatFileName() {
		return relRiskCatFileName;
	}

	public void setRelRiskCatFileName(String[] relRiskCatFileName) {
		this.relRiskCatFileName = relRiskCatFileName;
	}

	public String[] getRelRiskContFileName() {
		return relRiskContFileName;
	}

	public void setRelRiskContFileName(String[] relRiskContFileName) {
		this.relRiskContFileName = relRiskContFileName;
	}

	public String[] getRelRiskEndFileName() {
		return relRiskEndFileName;
	}

	public void setRelRiskEndFileName(String[] relRiskEndFileName) {
		this.relRiskEndFileName = relRiskEndFileName;
	}

	public void setRelRiskEndFileName(String relRiskEndFileName, int d) {
		this.relRiskEndFileName[d] = relRiskEndFileName;
	}

	public void setRelRiskBeginFileName(String input, int d) {
		this.relRiskBeginFileName[d] = input;
	}

	public void setAlfaDuurFileName(String input, int d) {
		this.alfaDuurFileName[d] = input;
	}

	public String[] getRelRiskBeginFileName() {
		return relRiskBeginFileName;
	}

	public void setRelRiskBeginFileName(String[] relRiskBeginFileName) {
		this.relRiskBeginFileName = relRiskBeginFileName;
	}

	public String[] getAlfaDuurFileName() {
		return alfaDuurFileName;
	}

	public void setAlfaDuurFileName(String[] alfaDuurFileName) {
		this.alfaDuurFileName = alfaDuurFileName;
	}

	public String[] getAttributableMortalityFileName() {
		return attributableMortalityFileName;
	}

	public void setAttributableMortalityFileName(
			String[] attributableMortalityFileName) {
		this.attributableMortalityFileName = attributableMortalityFileName;
	}

	public int getNCat() {
		return nCat;
	}

	public static String getRelativeRiskOtherMortFileLabel() {
		return relativeRiskOtherMortFileLabel;
	}

	public void setNCat(int cat) {
		nCat = cat;
	}

	public static String getRelRiskCatFileNameLabel() {
		return relRiskFileNameLabel;
	}

	public float[][] getRelRiskOtherMortBegin() {
		return relRiskOtherMortBegin;
	}

	public void setRelRiskOtherMortBegin(float[][] relRiskOtherMortBegin) {
		this.relRiskOtherMortBegin = relRiskOtherMortBegin;
	}

	public float[][] getRelRiskOtherMortContinous() {
		return relRiskOtherMortContinous;
	}

	public void setRelRiskOtherMortContinous(float[][] relRiskOtherMortContinous) {
		this.relRiskOtherMortContinous = relRiskOtherMortContinous;
	}

	public void setRelRiskOtherMortFileName(String relRiskOtherMortCatFileName) {
		this.relRiskOtherMortFileName = relRiskOtherMortCatFileName;
	}

	public String getRelRiskOtherMortFileName() {
		return relRiskOtherMortFileName;
	}

	public float[][] getRelRiskOtherMortEnd() {
		return relRiskOtherMortEnd;
	}

	public void setRelRiskOtherMortEnd(float[][] relRiskOtherMortEnd) {
		this.relRiskOtherMortEnd = relRiskOtherMortEnd;
	}

	public float[][] getAlfaDuurOtherMort() {
		return alfaDuurOtherMort;
	}

	public void setAlfaDuurOtherMort(float[][] alfaDuurOtherMort) {
		this.alfaDuurOtherMort = alfaDuurOtherMort;
	}

	public String[] getBaselineFatalIncidenceFileName() {
		return baselineFatalIncidenceFileName;
	}

	public void setBaselineFatalIncidenceFileName(
			String[] baselineFatalIncidenceFileName) {
		this.baselineFatalIncidenceFileName = baselineFatalIncidenceFileName;
	}

	public String getBaselineOtherMortFileName() {
		return baselineOtherMortFileName;
	}

	public void setBaselineOtherMortFileName(String baselineOtherMortFileName) {
		this.baselineOtherMortFileName = baselineOtherMortFileName;
	}

	public void setBaselineOtherMort(float[][] baselineOtherMort) {
		this.baselineOtherMort = baselineOtherMort;
	}

	public boolean getWithCuredFraction(int d) {
		return withCuredFraction[d];
	}

	public void setWithCuredFraction(String value, int d) {
		if (Integer.parseInt(value) == 1)
			this.withCuredFraction[d] = true;
		else
			this.withCuredFraction[d] = false;
	}

	public String getRelRiskEndOtherMortFileName() {
		return relRiskEndOtherMortFileName;
	}

	public void setRelRiskEndOtherMortFileName(
			String relRiskEndOtherMortFileName) {
		this.relRiskEndOtherMortFileName = relRiskEndOtherMortFileName;
	}

	public String getAlfaOtherMortFileName() {
		return alfaOtherMortFileName;
	}

	public void setAlfaOtherMortFileName(String alfaOtherMortFileName) {
		this.alfaOtherMortFileName = alfaOtherMortFileName;
	}

	public String getRelRiskBeginOtherMortFileName() {
		return relRiskBeginOtherMortFileName;
	}

	public void setRelRiskBeginOtherMortFileName(
			String relRiskBeginOtherMortFileName) {
		this.relRiskBeginOtherMortFileName = relRiskBeginOtherMortFileName;
	}

}
