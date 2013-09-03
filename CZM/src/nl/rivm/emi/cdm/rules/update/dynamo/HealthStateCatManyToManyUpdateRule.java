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
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * @author Hendriek This call implements a version of
 *         HealthStateManyToManyUpdateRule specifically for categorical Risk
 *         Factors. Such specialisation provided as faster update method as it
 *         does not need to check the type of risk factor Also in this case only
 *         a limited number of transition probability matrixes are needed, these
 *         are calculated once at class loading time and stored in stead of
 *         being recalculated at every update
 * 
 * 
 */
public class HealthStateCatManyToManyUpdateRule extends
		HealthStateManyToManyUpdateRule {

	/**
	 * @throws ConfigurationException
	 * @throws CDMUpdateRuleException
	 */

	float transMat[][][][][][];
	/*
	 * indexes are : age sex riskfactor diseaseCluster from to
	 */

	float incidenceInState[][][][][][];
	/*
	 * indexes are : age sex riskfactor diseaseCluster  TOdisease  diseaseStateWithinCluster
	 * 
	 * here TOdisease is the disease number within the cluster
	 */
	float fatalIncidenceInState[][][][][][];
	/*
	 * indexes are : age sex  riskfactor diseaseCluster TOdisease  diseaseStateWithinCluster
	 * 
	 * where TOdisease is the disease number within the cluster
	 */
	float OtherMortalitySurvival[][][];

	/*
	 * indexes are : age sex riskfactor status
	 */
	public HealthStateCatManyToManyUpdateRule() throws ConfigurationException,
			CDMUpdateRuleException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float[] newValue = null;

		try {
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			double[] currentHealthyState = new double[nCluster];
			float[] oldValue = getValues(currentValues,
					getCharacteristicIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {
				return updatedHealthStates(currentValues, ageValue,
						currentHealthyState, oldValue);
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

	/** calculates the updated health states
	 * @param currentValues
	 * @param ageValue
	 * @param currentHealthyState
	 * @param oldValue
	 * @return the new health states
	 * @throws CDMUpdateRuleException
	 */
	protected Object updatedHealthStates(Object[] currentValues, int ageValue,
			double[] currentHealthyState, float[] oldValue)
			throws CDMUpdateRuleException {
		float[] newValue;
		int sexValue = getInteger(currentValues, getSexIndex());
		if (ageValue > 95)
			ageValue = 95;
		int riskFactorValue = getInteger(currentValues,
				riskFactorIndex1);

		newValue = new float[oldValue.length];
		
		
		
		float[] currentDiseaseStateValues = new float[oldValue.length];
		
		
		/**************************************************************/
		
		/* CALCULATE THE NEW DISEASE PREVALENCES                     */
		
		/**************************************************************/
		
		/*
		 * float totInStates=0; for (int i=0;i<oldValue.length-1;i++)
		 * {currentDiseaseStateValues[i+1]=oldValue[i];
		 * totInStates+=oldValue[i];}
		 * currentDiseaseStateValues[0]=1-totInStates;
		 */

		// array currentDiseaseValue holds the current values of the
		// disease-characteristics
		// 
		// private int[] numberOfDiseasesInCluster == array over
		// clusters;
		// private int[] clusterStartsAtDiseaseNumber == array over
		// clusters;
		// private int totalNumberOfDiseases;
		// private int nCluster = -1;
		// private int[] DiseaseNumberWithinCluster;== array over
		// diseases
		int currentStateNo = 0;

		double survivalFraction = OtherMortalitySurvival[ageValue][sexValue][riskFactorValue];

		float[][] currentTransMat;
		for (int c = 0; c < nCluster; c++) {
			double survival = 0;
			currentTransMat = transMat[ageValue][sexValue][riskFactorValue][c];

			/* update single diseases */

			if (numberOfDiseasesInCluster[c] == 1) {

				int d = clusterStartsAtDiseaseNumber[c];
				currentHealthyState[c] = (1 - oldValue[currentStateNo]);
				survival = (1 - oldValue[currentStateNo])
						* (currentTransMat[0][0] + currentTransMat[1][0])
						+ oldValue[currentStateNo]
						* (currentTransMat[1][1]);

				newValue[currentStateNo] = (float) (((1 - oldValue[currentStateNo])
						* currentTransMat[1][0] + oldValue[currentStateNo]
						* currentTransMat[1][1]) / survival);

				survivalFraction *= survival;
				currentStateNo++;
				/* update diseases with cured fraction */
			} else if (withCuredFraction[c]) {
				currentHealthyState[c] = (1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1]);
				survival = currentHealthyState[c]
						* (currentTransMat[0][0]
								+ currentTransMat[1][0] + currentTransMat[2][0])
						+ oldValue[currentStateNo]
						* currentTransMat[1][1]
						+ oldValue[currentStateNo + 1]
						* currentTransMat[2][2];
				double unconditionalNewValues0 = currentTransMat[1][0]
						* currentHealthyState[c]
						+ oldValue[currentStateNo]
						* currentTransMat[1][1];
				double unconditionalNewValues1 = currentTransMat[2][0]
						* currentHealthyState[c]
						+ oldValue[currentStateNo + 1]
						* currentTransMat[2][2];
				newValue[currentStateNo] = (float) (unconditionalNewValues0 / survival);
				newValue[currentStateNo + 1] = (float) (unconditionalNewValues1 / survival);

				survivalFraction *= survival;
				currentStateNo++;
				currentStateNo++;

			} /* update cluster diseases */
			else {
// NB: currentHealthyState is also calculated within this this method!!
				double[] unconditionalNewValues = calculateUnconditionalNewValuesOfCluster(
						currentHealthyState, oldValue, currentStateNo,
						currentTransMat, c);

				/* calculate survival */
				survival = 0;
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

		
		// *********************************************************************************/
		
		                            /* calculate incidence */
		
		// *********************************************************************************/
		
		int currentStateNo2 = 0; /*
								 * this is the disease-state index,
								 * while currentStateNo is the state in
								 * the output-array (newvalues). i.o.w.
								 * CurrentStateNo is the index for the
								 * place where the incidence data are
								 * stored
								 */
		for (int c = 0; c < nCluster; c++) {

			/* update single diseases */
			float incidence = 0F;
			if (numberOfDiseasesInCluster[c] == 1) {
				currentStateNo++;
				int d = clusterStartsAtDiseaseNumber[c];
				incidence = incidenceInState[ageValue][sexValue][riskFactorValue][c][0][0];
				// incidence =
				// calculateIncidence(riskFactorValue,ageValue,
				// sexValue, d);
				/*
				 * incidence = incidence(in disease free) * fraction of
				 * personyears free of disease + fatal incidence;
				 */
				/*
				 * person years with disease= average of prevalence at
				 * beginning and end
				 */
				newValue[currentStateNo] = (float) ((0.5*survivalFraction*(1 - 
						 newValue[currentStateNo2]) + 0.5 *(1- oldValue[currentStateNo2]))
						* incidence + fatalIncidenceInState[ageValue][sexValue][riskFactorValue][c][0][0]*(0.5+0.5*survivalFraction));

				currentStateNo2++;

				/* update diseases with cured fraction */
			} else if (withCuredFraction[c]) {
				currentStateNo++;
				int d = clusterStartsAtDiseaseNumber[c];
				/*
				 * indexes are : age sex riskfactor diseaseCluster TOdiseaseWithinCluster
				 * diseaseStateWithinCluster
				 */
				/* incidence is only from the healthy states */
				double newHealthyState = 1-newValue[currentStateNo2]-newValue[currentStateNo2+1];
				double oldHealthyState = 1-oldValue[currentStateNo2]-oldValue[currentStateNo2+1];
				incidence = incidenceInState[ageValue][sexValue][riskFactorValue][c][0][0];
				// incidence =
				// calculateIncidence(riskFactorValue,ageValue,
				// sexValue, d);
				newValue[currentStateNo] = (float) ((0.5 * newHealthyState
						* survivalFraction + 0.5
						* oldHealthyState)
						* incidence + fatalIncidenceInState[ageValue][sexValue][riskFactorValue][c][0][0]*(0.5+0.5*survivalFraction));
				currentStateNo++;
				incidence = incidenceInState[ageValue][sexValue][riskFactorValue][c][1][0];
				newValue[currentStateNo] = (float) ((0.5 * newHealthyState
						* survivalFraction + 0.5
						* oldHealthyState)
						* incidence + fatalIncidenceInState[ageValue][sexValue][riskFactorValue][c][1][0]*(0.5+0.5*survivalFraction));
				currentStateNo2++;
				currentStateNo2++;
			}

			/* update cluster diseases */
			else {

				int dInCluster = 0;
				/*
				 * calculate the healthy state at the end of the time
				 * period
				 */
				double newHealthyState = 1;
				int startState = currentStateNo2 ;
				for (int state = startState; state < startState
						+ nCombinations[c] -1; state++)
					newHealthyState -= newValue[state];

				for (int d = clusterStartsAtDiseaseNumber[c]; d < clusterStartsAtDiseaseNumber[c]
						+ numberOfDiseasesInCluster[c]; d++) {
					currentStateNo++;
					int numberInCluster = DiseaseNumberWithinCluster[d];
					/*
					 * first add incidence from healthy state including
					 * fatal incidence
					 */
					double incidenceD = 0.5
							* (currentHealthyState[c] + newHealthyState
									* survivalFraction)
							* (incidenceInState[ageValue][sexValue][riskFactorValue][c][numberInCluster][0] 
							 + fatalIncidenceInState[ageValue][sexValue][riskFactorValue][c][numberInCluster][0]);
					/* add incidence from the non-healthy states */
					for (int state = 1; state < nCombinations[c]; state++) {
						/* incidence is only for disease free ) */
						if ((state & (1 << numberInCluster)) != (1 << numberInCluster))
							incidenceD += (0.5
									* newValue[startState + state-1]
									* survivalFraction + 0.5 * oldValue[startState
									+ state-1 ])
									* incidenceInState[ageValue][sexValue][riskFactorValue][c][numberInCluster][state];
						/* fatal incidence is for all */
						incidenceD += (0.5
								* newValue[startState + state-1 ]
								* survivalFraction + 0.5 * oldValue[startState
								+ state-1])
								* fatalIncidenceInState[ageValue][sexValue][riskFactorValue][c][numberInCluster][state];
					}

					newValue[currentStateNo] = (float) incidenceD;

				} /* end loop over diseases in cluster */
				currentStateNo2 += nCombinations[c] - 1;

			} // end if statement for cluster diseases

		} // end loop over clusters

		return newValue;
	}

	/** calculates the unconditional new prevalences in cluster c(that is, with a de nominator including
	 * those who die during the interval and ALSO calculated the value of currentHealthState
	 * for this cluster
	 * @param currentHealthyState: array with health states in each cluster (to be updated)
	 * @param oldValue: array with old values
	 * @param currentStateNo : state number of the first cluster disease
	 * @param currentTransMat: transitions matrix to be applied
	 * @param c: cluster number
	 * @return
	 */
	private double[] calculateUnconditionalNewValuesOfCluster(
			double[] currentHealthyState, float[] oldValue, int currentStateNo,
			float[][] currentTransMat, int c) {
		/*
		 * Multiply the matrix with the old values (column
		 * vector)
		 */
		double unconditionalNewValues[] = new double[nCombinations[c]];
		/* calculate the healthy state */
		currentHealthyState[c] = 1;
		for (int state = currentStateNo; state < currentStateNo
				+ nCombinations[c] - 1; state++)

			currentHealthyState[c] -= oldValue[state];

		/*
		 * NB the unconditional new state starts at 0 with the
		 * healthy state, so oldvalue[1] belongs with
		 * unconditionalnewstate[0]
		 */

		for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
		{ /* transitionProbabilities are [to][from] */
			unconditionalNewValues[state1] = currentTransMat[state1][0]
					* currentHealthyState[c];
			for (int state2 = 1; state2 < nCombinations[c]; state2++)
				// column=from

				unconditionalNewValues[state1] += currentTransMat[state1][state2]
						* oldValue[state2 - 1 + currentStateNo];
		}
		return unconditionalNewValues;
	}

	/**
	 * 
	 * 
	 * This function calculates the prevalence of disease d, given the states
	 * given in value
	 * 
	 * function is not used anymore
	 * 
	 * @param c
	 *            : cluster number
	 * @param d
	 *            : disease number (in total)
	 * @param firstStateIndex
	 *            : stateIndex of first state in the cluster in the array
	 *            "value"
	 * @param value
	 *            : array with state occupancy values
	 * @return prevalence of disease D (double)
	 */
	private double getPrevalenceOfD(int c, int diseaseNumber,
			int firstStateIndex, float[] value) {

		double prevalence = 0;
		int d = DiseaseNumberWithinCluster[diseaseNumber];
		for (int s = 0; s < numberOfDiseasesInCluster[c]; s++) {
			if ((s & (1 << d)) == (1 << d))
				prevalence += value[firstStateIndex + s];
		}
		return prevalence;
	}

	private double calculateIncidence(int riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
				* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];

		return incidence;
	}

	private double calculateFatalIncidence(int riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
				* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];

		return incidence;
	}

	private double calculateOtherCauseMortality(int riskFactorValue,
			int ageValue, int sexValue) {
		double otherCauseMortality = 0;

		otherCauseMortality = baselineOtherMort[ageValue][sexValue]
				* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];

		return otherCauseMortality;
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		try {

			/*
			 * XMLConfiguration configurationFileConfiguration = new
			 * XMLConfiguration( configurationFile); OUD vervangen door volgende
			 * regels
			 */

			XMLConfiguration configurationFileConfiguration = new XMLConfiguration();
			configurationFileConfiguration.setDelimiterParsingDisabled(true);
			configurationFileConfiguration.load(configurationFile);

			/**
			 * TODO: VALIDATION IS FOR FUTURE USE NICE TO HAVE FEATURE KEEP IT
			 * IN THE CODE The following schemas are not be validated:
			 * updateRuleConfiguration.xsd
			 */
			if (!"updateRuleConfiguration"
					.equals(configurationFileConfiguration.getRootElementName())) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls
				// load()).
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				configurationFileConfiguration.clear();

				// Validate the xml by xsd schema
				configurationFileConfiguration.setValidating(true);
				configurationFileConfiguration.load();
			}

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
			/* make matrixes with transition probabilities */
			MatrixExponential matExp = MatrixExponential.getInstance();
			OtherMortalitySurvival = new float[96][2][nCat];
			transMat = new float[96][2][nCat][nCluster][][];
			incidenceInState = new float[96][2][nCat][nCluster][][];
			fatalIncidenceInState = new float[96][2][nCat][nCluster][][];
			
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int r = 0; r < nCat; r++) {

						double otherMort = calculateOtherCauseMortality(r, a, g);
						double[] incidence = new double[getNDiseases()];
						double[] fatalIncidence = new double[getNDiseases()];
						double[] atMort = getAttributableMortality(a, g);
						for (int d = 0; d < getNDiseases(); d++) {
							incidence[d] = calculateIncidence(r, a, g, d);
							fatalIncidence[d] = calculateFatalIncidence(r, a,
									g, d);

						}
						// array currentDiseaseValue holds the current values of
						// the
						// disease-characteristics
						// 

						OtherMortalitySurvival[a][g][r] = (float) Math
								.exp(-otherMort * getTimeStep());

						/*
						 * int[] numberOfDiseasesInCluster == array over
						 * clusters; // int[] clusterStartsAtDiseaseNumber ==
						 * array over clusters; // int totalNumberOfDiseases; //
						 * int nCluster = -1; // int[]
						 * DiseaseNumberWithinCluster;== array over diseases
						 */

						int currentStateNo = 0;

						for (int c = 0; c < nCluster; c++) {
							if (numberOfDiseasesInCluster[c] == 1) {
								transMat[a][g][r][c] = new float[2][2];
								incidenceInState[a][g][r][c] = new float[1][1];
								fatalIncidenceInState[a][g][r][c] = new float[1][1];
								int d = clusterStartsAtDiseaseNumber[c];
								transMat[a][g][r][c][0][0] = (float) Math
										.exp(-incidence[d] - fatalIncidence[d]);
								transMat[a][g][r][c][0][1] = 0;
								transMat[a][g][r][c][1][1] = (float) Math
										.exp(-atMort[d] - fatalIncidence[d]);
								incidenceInState[a][g][r][c][0][0] = (float) incidence[d];
								fatalIncidenceInState[a][g][r][c][0][0] = (float) fatalIncidence[d];
								if (Math.abs(incidence[d] - atMort[d]) > 1E-15)
									transMat[a][g][r][c][1][0] = (float) (Math
											.exp(-fatalIncidence[d])
											* incidence[d]
											* (Math.exp(-atMort[d]) - Math
													.exp(-incidence[d])) / (incidence[d] - atMort[d]));
								else
									/*
									 * if incidence equal to attributable
									 * mortality, the denominator becomes zero
									 * and we need another formula
									 */
									transMat[a][g][r][c][1][0] = (float) (Math
											.exp(-incidence[d]
													- fatalIncidence[d]) * incidence[d]);

								currentStateNo++;
							} else if (withCuredFraction[c]) {

								/*
								 * zeroth disease state=healthy first disease
								 * state = cured disease (d) second disease
								 * state = not cured disease (d+1)
								 */

								/*
								 * Officially it is not possible to have both
								 * fatal incidences >0 and with cured fraction,
								 * However, fatal incidence is also included
								 * here in the formulae for possibly future use,
								 * under the assumption that the incidence of
								 * fatal disease is the same in healthy, with
								 * cured and with non-cured disease This implies
								 * that all transition rates are multiplied with
								 * a term: Math.exp( - fatalIncidence[d]) where
								 * we assume the fatalincidence to independent
								 * of having both the cured and uncured disease
								 * 
								 * 
								 * NB in other update rules this has not been implemented!
								 * 
								 * 
								 */
								transMat[a][g][r][c] = new float[3][3];
								
								incidenceInState[a][g][r][c] = new float[2][3];
								fatalIncidenceInState[a][g][r][c] = new float[2][3];

								int d = clusterStartsAtDiseaseNumber[c];
								transMat[a][g][r][c][0][0] = (float) Math
										.exp(-incidence[d] - incidence[d + 1]
												- fatalIncidence[d]);
								transMat[a][g][r][c][0][1] = 0;
								transMat[a][g][r][c][0][2] = 0;
								if ((incidence[d] + incidence[d + 1]) == 0)
									transMat[a][g][r][c][1][0] = 0;
								else
									transMat[a][g][r][c][1][0] = (float) (Math
											.exp(-fatalIncidence[d])
											* (1 - Math.exp(-incidence[d]
													- incidence[d + 1]))
											* incidence[d] / (incidence[d] + incidence[d + 1]));
								transMat[a][g][r][c][1][1] = (float) Math
										.exp(-fatalIncidence[d]);
								transMat[a][g][r][c][1][2] = 0;
								if (incidence[d] + incidence[d + 1] == atMort[d + 1])
									transMat[a][g][r][c][2][0] = (float) (Math
											.exp(-incidence[d]
													- incidence[d + 1]
													- fatalIncidence[d]) * incidence[d + 1]);
								else
									transMat[a][g][r][c][2][0] = (float) (((Math
											.exp(-atMort[d + 1]) - Math
											.exp(-incidence[d]
													- incidence[d + 1])) * incidence[d + 1])
											* Math.exp(-fatalIncidence[d]) / (incidence[d]
											+ incidence[d + 1] - atMort[d + 1]));
								transMat[a][g][r][c][2][1] = 0;
								transMat[a][g][r][c][2][2] = (float) Math
										.exp(-atMort[d + 1] - fatalIncidence[d]);
								incidenceInState[a][g][r][c][0][0] = (float) incidence[d];
								incidenceInState[a][g][r][c][0][1] = 0;
								incidenceInState[a][g][r][c][0][2] = 0;
								incidenceInState[a][g][r][c][1][0] = (float) incidence[d+1];
								incidenceInState[a][g][r][c][1][1] = 0;
								incidenceInState[a][g][r][c][1][2] = 0;
								/* only the [0][0] element is non-zero, so other elements are not initialized */
								fatalIncidenceInState[a][g][r][c][0][0] = (float) fatalIncidence[d];

								currentStateNo += 2;

							} else // cluster of dependent diseases
							{
								double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];
								incidenceInState[a][g][r][c] = new float[this.numberOfDiseasesInCluster[c]][nCombinations[c]];
								fatalIncidenceInState[a][g][r][c] = new float[this.numberOfDiseasesInCluster[c]][nCombinations[c]];

								/*
								 * first= changed state (row) second :sources of
								 * change(change=number in second state entry in
								 * matrix
								 * 
								 * thus [to][from]
								 */
								for (int row = 0; row < nCombinations[c]; row++)
									Arrays.fill(rateMatrix[row], 0);

								for (int row = 0; row < nCombinations[c]; row++)
									for (int column = 0; column < nCombinations[c]; column++)
										if (row == column)
										/*
										 * Matrix entry is formed as: / -
										 * attributable Mortality for each
										 * disease that is 1 in combi - sum
										 * incidence to all other disease that
										 * are 0 in combi (including RR's as
										 * above) - sum fatal incidences
										 */
										{

											for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {

												/*
												 * first add fatal incidence
												 * irrespective of value of d
												 */
												double RR = 1;
												for (int dCause = 0; dCause < getNDiseases(); dCause++) {
													/*
													 * if dCause==1 in
													 * row=column)
													 */
													if ((row & (1 << dCause)) == (1 << dCause))
														RR *= this.relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
												}

												rateMatrix[row][column] -= (RR * fatalIncidence[clusterStartsAtDiseaseNumber[c]
														+ d]);

												fatalIncidenceInState[a][g][r][c][d][row] = (float) (RR * fatalIncidence[clusterStartsAtDiseaseNumber[c]
														+ d]);

												if ((row & (1 << d)) != (1 << d))

												/*
												 * d is 0, thus incidence should
												 * be added
												 */
												{
													RR = 1;
													for (int dCause = 0; dCause < getNDiseases(); dCause++)
														if ((row & (1 << dCause)) == (1 << dCause))
															RR *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];

													rateMatrix[row][column] -= (RR * incidence[clusterStartsAtDiseaseNumber[c]
															+ d]);
													incidenceInState[a][g][r][c][d][row] = (float) (RR * incidence[clusterStartsAtDiseaseNumber[c]
															+ d]);
													// or d=1, then atmort
													// should be added
												} else
													rateMatrix[row][column] -= atMort[clusterStartsAtDiseaseNumber[c]
															+ d];
											}
										} else /* row not equal column */{
											// not a diagonal
											// find all patterns where row has
											// exactly one 1 more than the
											// column

											for (int bits = 0; bits < getNDiseases(); bits++) {
												if ((row ^ column) == (1 << bits))
													/*
													 * only 1 difference between
													 * row and column located at
													 * bits
													 */
													if ((row & (1 << bits)) == (1 << bits))
													/*
													 * row=1 at the location of
													 * the difference thus
													 * incidence should be added
													 */

													{
														double RR = 1;
														for (int dCause = 0; dCause < getNDiseases(); dCause++)
															if ((column & (1 << dCause)) == (1 << dCause))
																/*
																 * causal
																 * disease
																 * present
																 */
																RR *= relativeRiskDiseaseOnDisease[c][a][g][dCause][bits];

														rateMatrix[row][column] += (RR * incidence[clusterStartsAtDiseaseNumber[c]
																+ bits]);
													}
												;
											}
										}

								/* Exponentiale the matrix */

								transMat[a][g][r][c] = new float[nCombinations[c]][nCombinations[c]];

								try {
									transMat[a][g][r][c] = matExp
											.exponentiateFloatMatrix(rateMatrix);
								} catch (CDMUpdateRuleException e) {
									throw new CDMConfigurationException(e
											.getMessage()
											+ " for risk class "
											+ r
											+ " and disease cluster "
											+ c
											+ ", age: " + a + " sex: " + g);

								}

								currentStateNo += nCombinations[c] - 1;
							} // end if statement for cluster diseases

						} // end loop over clusters

					}

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

}
