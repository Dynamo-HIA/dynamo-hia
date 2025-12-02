/**
 * 
 */
package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.Arrays;
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
 *         does not need to check the type of risk factor.
 * 
 * 
 */

public class HealthStateContManyToManyUpdateRule

extends HealthStateManyToManyUpdateRule {

	/**
	 * @throws ConfigurationException
	 * @throws CDMUpdateRuleException
	 */
	int[][][] atIndex;
	/*
	 * indexes are : cluster number, disease number (within cluster) . For this
	 * combination there is an arrya that gives the locations within the
	 * transition rate matrix where attributable mortality for this disease
	 * should be added to the diagonal of the matrix
	 */

	int[][][] incIndex;
	/*
	 * indexes are : cluster number, disease number (within cluster) . For this
	 * combination there is an arrya that gives the locations within the
	 * transition rate matrix where incidence rate for this disease should be
	 * added to the diagonal of the matrix
	 */

	int[][][] incRowIndex;
	/*
	 * indexes are : cluster number, disease number (within cluster) . For this
	 * combination there is an arrya that gives the rows within the transition
	 * rate matrix where incidence rate for this disease should be added below
	 * the diagonal incidence entry indicated by incIndex
	 */

	float[][][][][] RRdis;
	/*
	 * indexes are :age, gender, cluster number, disease number (within cluster)
	 * . For this combination RR gives the value of RR due to other diseases
	 * with which the incidence should be multiplied at this place
	 */

	int[][][][] disFatalIndex;
	/*
	 * indexes are :age, gender, cluster number. disfatalIndex gives a list of
	 * numbers of disease within this cluster that have a fatal component
	 */

	float[][][][][] RRdisFatal;

	/*
	 * indexes are :age, gender, cluster number, diseasenumber of the fatal
	 * disease (number as given in disFatalIndex). RRdisFatal gives the disease
	 * related RR's for each location in the matrix for the fatal diseases
	 */

	float[][][] nonCuredRatio;
	/*
	 * indexes are :age, gender, cluster number gives the ratio not-cured/total
	 */
	MatrixExponential matExp = new MatrixExponential();

	public HealthStateContManyToManyUpdateRule() throws ConfigurationException,
			CDMUpdateRuleException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float[] newValue = null;

		try {
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			// TODO dit beter oplossen: op een of ander manier wordt deze steeds
			// -1;
			// wellicht doordat setter in andere classe dan deze??
			if (getCharacteristicIndex() > 0)
				setCharacteristicIndex(4);
			float[] oldValue = getValues(currentValues, 
					getCharacteristicIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {
				int sexValue = getInteger(currentValues, getSexIndex());
				if (ageValue > 95)
					ageValue = 95;
				float riskFactorValue = getFloat(currentValues,
						riskFactorIndex1);
				if (ageValue == 56 && sexValue == 1) {

					@SuppressWarnings("unused")
					int stop = 0;
					stop++;

				}

				newValue = new float[oldValue.length];
				@SuppressWarnings("unused")
				float[] currentDiseaseStateValues = new float[oldValue.length];
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

				double survivalFraction = calculateOtherCauseSurvival(
						riskFactorValue, ageValue, sexValue);
				@SuppressWarnings("unused")
				float[][] currentTransMat;
				double expAI;
				double expI;
				double expA;
				int d;
				double incidence;
				double incidence2;
				double atMort;
				double[] currentHealthyState = new double[nCluster];
				for (int c = 0; c < this.nCluster; c++) {
					double survival = 0;
					if (this.numberOfDiseasesInCluster[c] == 1) {

						d = this.clusterStartsAtDiseaseNumber[c];
						atMort = this.attributableMortality[d][ageValue][sexValue];
						incidence = calculateIncidence(riskFactorValue,
								ageValue, sexValue, d);
						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */
						expA = Math.exp(-atMort * timeStep);
						expI = Math.exp(-incidence * timeStep);
						if (Math.abs(incidence - atMort) > 1E-15)
							expAI = expA / expI;
						else
							expAI = 1;
						// finci = ((p0 * em - i) * exp((i - em) * time) + i *
						// (1 -
						// p0))
						// / ((p0 * em - i) * exp((i - em) * time) + em * (1 -
						// p0))
						if (expAI != 1)
							newValue[currentStateNo] = (float) (((oldValue[currentStateNo]
									* atMort - incidence)
									* expAI + incidence
									* (1 - (double) oldValue[currentStateNo])) / ((oldValue[currentStateNo]
									* atMort - incidence)
									* expAI + atMort
									* (1 - (double) oldValue[currentStateNo])));
						else
							newValue[currentStateNo] = (float) (1 - (1 - oldValue[currentStateNo])
									/ (1 + incidence
											* (1 - oldValue[currentStateNo])
											* timeStep));
						/*
						 * if incidence equal to attributable mortality, the
						 * denominator becomes zero and we need another formula
						 */
						// only calculate fatal part if there are fatal diseases
						if (disFatalIndex[ageValue][sexValue][c][0] == 0
								&& expAI != 1)
							survivalFraction *= Math.exp(-getTimeStep()
									* calculateFatalIncidence(riskFactorValue,
											ageValue, sexValue, d))
									* (atMort * (1 - oldValue[currentStateNo])
											* expI + (atMort
											* oldValue[currentStateNo] - incidence)
											* expA) / (atMort - incidence);
						else if ((expAI != 1))
							survivalFraction *= (atMort
									* (1 - oldValue[currentStateNo]) * expI + (atMort
									* oldValue[currentStateNo] - incidence)
									* expA)
									/ (atMort - incidence);
						else if (disFatalIndex[ageValue][sexValue][c][0] != 0)
							survivalFraction *= expA
									* (incidence
											* (1 - oldValue[currentStateNo])
											* timeStep + 1);
						else
							survivalFraction *= Math.exp(-getTimeStep()
									* calculateFatalIncidence(riskFactorValue,
											ageValue, sexValue, d))
									* expA
									* (incidence
											* (1 - oldValue[currentStateNo])
											* timeStep + 1);
						currentStateNo++;
						/* update diseases with cured fraction */
					} else if (withCuredFraction[c]) {
						d = clusterStartsAtDiseaseNumber[c];
						// TODO: change input with cured fraction in stead of
						// two diseases
						/*
						 * incidence 1: cured incidence2: non-cured
						 */
						atMort = attributableMortality[d + 1][ageValue][sexValue];
						incidence2 = calculateIncidence(riskFactorValue,
								ageValue, sexValue, d + 1);
						/*
						 * non cured ratio can be only zero when either
						 * incidence d+1= zero, or both are zero, so the last
						 * option can not occur
						 */
						if (nonCuredRatio[ageValue][sexValue][c] != 0)
							incidence = incidence2
									/ nonCuredRatio[ageValue][sexValue][c];
						else if (incidence2 == 0)
							incidence = 0;
						else
							incidence = 0;

						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */

						expI = Math.exp(-incidence * timeStep);
						expA = Math.exp(-atMort * timeStep);
						double transMat10;
						double transMat20;
						if (incidence != 0)
							transMat10 = (1 - expI) * (incidence - incidence2)
									/ incidence;
						else
							transMat10 = 0;

						if (incidence == atMort)
							transMat20 = expI * incidence2;
						else
							transMat20 = (expA - expI) * incidence2
									/ (incidence - atMort);

						survival = (1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* expI
								+ oldValue[currentStateNo]
								+ oldValue[currentStateNo + 1]
								* expA
								+ (1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* (transMat10 + transMat20);
						newValue[currentStateNo] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* transMat10 + oldValue[currentStateNo]) / survival);
						newValue[currentStateNo + 1] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* transMat20 + oldValue[currentStateNo + 1]
								* expA) / survival);
						/*
						 * NB disease with cured fraction can not be fatal at
						 * the same time
						 */
						survivalFraction *= survival;
						currentStateNo += 2;
					}

					else /* now cluster diseases */
					{

						/*
						 * Multiply the matrix with the old values (column
						 * vector)
						 */
						double unconditionalNewValues[] = new double[this.nCombinations[c]];

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

						/* make transition rate matrix */
						double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];
						for (int dc = 0; dc < numberOfDiseasesInCluster[c]; dc++) {
							d = clusterStartsAtDiseaseNumber[c] + dc;
							incidence = calculateIncidence(riskFactorValue,
									ageValue, sexValue, d);
							atMort = attributableMortality[d][ageValue][sexValue];
							/* >>1 is fast for /2 */
							for (int loc = 0; loc < nCombinations[c] >> 1; loc++) {
								rateMatrix[incIndex[c][dc][loc]][incIndex[c][dc][loc]] += -incidence
										* RRdis[ageValue][sexValue][c][dc][loc];
								rateMatrix[incRowIndex[c][dc][loc]][incIndex[c][dc][loc]] += incidence
										* RRdis[ageValue][sexValue][c][dc][loc];
								rateMatrix[atIndex[c][dc][loc]][atIndex[c][dc][loc]] += -atMort;

							}// end loop locations of disease
						}// end loop over diseases in cluster

						/*
						 * now loop over the fatal diseases (separate, as this
						 * is only done if the disease is really fatal to save
						 * running time
						 */
						if (disFatalIndex[ageValue][sexValue][c][0] >= 0)
							for (int fataldisnr = 0; fataldisnr < disFatalIndex[ageValue][sexValue][c].length; fataldisnr++) {

								incidence = calculateFatalIncidence(
										riskFactorValue,
										ageValue,
										sexValue,
										disFatalIndex[ageValue][sexValue][c][fataldisnr]
												+ clusterStartsAtDiseaseNumber[c]);
								for (int loc = 0; loc < nCombinations[c]; loc++) {

									rateMatrix[loc][loc] += -incidence
											* RRdisFatal[ageValue][sexValue][c][fataldisnr][loc];

								} // end loop over locations
							} // end loop over fatal diseases
						float[][] transMat = null;
						try {
							transMat = matExp
									.exponentiateFloatMatrix(rateMatrix);
						} catch (CDMUpdateRuleException e) {
							String message = e.getMessage();
							throw new CDMUpdateRuleException(
									message
											+ " for cluster "
											+ c
											+ " containing disease "
											+ this.getDiseaseNames()[clusterStartsAtDiseaseNumber[c]]
											+ ". \nThis occurs for risk factor value "
											+ riskFactorValue + ", age "
											+ ageValue + " and gender "
											+ sexValue);
						}

						for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
						{ /* transitionProbabilities are [to][from] */
							unconditionalNewValues[state1] = transMat[state1][0]
									* currentHealthyState[c];
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += transMat[state1][state2]
										* oldValue[state2 - 1 + currentStateNo];
						}
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
					float incidenceC = 0F;
					float fatalIncidenceC = 0F;
					if (numberOfDiseasesInCluster[c] == 1) {
						currentStateNo++;
						d = clusterStartsAtDiseaseNumber[c];

						// private double calculateIncidence(float
						// riskFactorValue, int ageValue, int sexValue, int
						// diseaseNumber)
						incidenceC = (float) calculateIncidence(
								riskFactorValue, ageValue, sexValue, d);
						fatalIncidenceC = (float) calculateFatalIncidence(
								riskFactorValue, ageValue, sexValue, d);

						/*
						 * incidence = incidence(in disease free) * fraction of
						 * personyears free of disease + fatal incidence;
						 */
						/*
						 * person years with disease= average of prevalence at
						 * beginning and end
						 */
						newValue[currentStateNo] = (float) ((0.5
								* survivalFraction
								* (1 - newValue[currentStateNo2]) + 0.5 * (1 - oldValue[currentStateNo2]))
								* incidenceC + fatalIncidenceC
								* (0.5 + 0.5 * survivalFraction));

						currentStateNo2++;

						/* update diseases with cured fraction */
					} else if (withCuredFraction[c]) {
						currentStateNo++;
						d = clusterStartsAtDiseaseNumber[c];
						/*
						 * indexes are : age sex riskfactor diseaseCluster
						 * TOdiseaseWithinCluster diseaseStateWithinCluster
						 */
						/* incidence is only from the healthy states */
						double newHealthyState = 1 - newValue[currentStateNo2]
								- newValue[currentStateNo2 + 1];
						double oldHealthyState = 1 - oldValue[currentStateNo2]
								- oldValue[currentStateNo2 + 1];
						incidenceC = (float) calculateIncidence(
								riskFactorValue, ageValue, sexValue, d);
						fatalIncidenceC = (float) calculateFatalIncidence(
								riskFactorValue, ageValue, sexValue, d);
						// incidence =
						// calculateIncidence(riskFactorValue,ageValue,
						// sexValue, d);
						newValue[currentStateNo] = (float) ((0.5
								* newHealthyState * survivalFraction + 0.5 * oldHealthyState)
								* incidenceC + fatalIncidenceC
								* (0.5 + 0.5 * survivalFraction));
						currentStateNo++;
						incidenceC = (float) calculateIncidence(
								riskFactorValue, ageValue, sexValue, d + 1);
						fatalIncidenceC = (float) calculateFatalIncidence(
								riskFactorValue, ageValue, sexValue, d + 1);

						newValue[currentStateNo] = (float) ((0.5
								* newHealthyState * survivalFraction + 0.5 * oldHealthyState)
								* incidenceC + fatalIncidenceC
								* (0.5 + 0.5 * survivalFraction));
						currentStateNo2++;
						currentStateNo2++;
					}

					/* update cluster diseases */
					else {

						@SuppressWarnings("unused")
						int dInCluster = 0;
						/*
						 * calculate the healthy state at the end of the time
						 * period
						 */
						double newHealthyState = 1;
						int startState = currentStateNo2;
						for (int state = startState; state < startState
								+ nCombinations[c] - 1; state++)
							newHealthyState -= newValue[state];

						for (int dd = clusterStartsAtDiseaseNumber[c]; dd < clusterStartsAtDiseaseNumber[c]
								+ numberOfDiseasesInCluster[c]; dd++) {
							currentStateNo++;
							int numberInCluster = DiseaseNumberWithinCluster[dd];
							/*
							 * first add incidence from healthy state including
							 * fatal incidence
							 */

							/*
							 * NB: first index flags whether this is incidence
							 * (0) or fatal incidence (1)
							 * second index: number of disease in cluster
							 * third index=health state
							 */
							double[][][] incidenceInState = calculateIncidenceInState(
									riskFactorValue, ageValue, sexValue, c);
// incidenceD is niet fataal (eerste term) + fataal (tweede term)
							double incidenceD = 0.5
									* (currentHealthyState[c] + newHealthyState
											* survivalFraction)
									* (incidenceInState[0][numberInCluster][0] + incidenceInState[1][numberInCluster][0]);
							/* add incidence from the non-healthy states */
							for (int state = 1; state < nCombinations[c]; state++) {
   // als ziekte in in state dan ook gewone incidentie toevoegen
								if ((state & (1 << numberInCluster)) != (1 << numberInCluster))
									incidenceD += (0.5
											* newValue[startState + state - 1]
											* survivalFraction + 0.5 * oldValue[startState
											+ state - 1])
											* incidenceInState[0][numberInCluster][state];
								/* fatal incidence is for all */
								incidenceD += (0.5
										* newValue[startState + state - 1]
										* survivalFraction + 0.5 * oldValue[startState
										+ state - 1])
										* incidenceInState[1][numberInCluster][state];
							}

							newValue[currentStateNo] = (float) incidenceD;

						} /* end loop over diseases in cluster */
						currentStateNo2 += nCombinations[c] - 1;

					} // end if statement for cluster diseases

				} // end loop over clusters

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

	private double calculateIncidence(float riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
				* Math.pow(

				relRiskContinous[diseaseNumber][ageValue][sexValue],
						(riskFactorValue - referenceValueContinous));
		return incidence;
	}

	private double calculateFatalIncidence(float riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;
		incidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
				* Math.pow(relRiskContinous[diseaseNumber][ageValue][sexValue],
						(riskFactorValue - referenceValueContinous));

		return incidence;
	}

	private double calculateOtherCauseSurvival(float riskFactorValue,
			int ageValue, int sexValue) {
		double otherCauseSurvival = 0;

		otherCauseSurvival = Math.exp(-baselineOtherMort[ageValue][sexValue]
				* Math.pow(

				relRiskOtherMortContinous[ageValue][sexValue],
						(riskFactorValue - referenceValueContinous)));

		return otherCauseSurvival;
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

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();

			// Validate the xml by xsd schema
			// TODO weeraanzetten
			// configurationFileConfiguration.setValidating(true);
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
			/*
			 * NB: riskType should be set before handling the disease
			 * information
			 */
			handleRefValueContinuous(configurationFileConfiguration);
			handleNClusters(configurationFileConfiguration);
			handleOtherMort(configurationFileConfiguration);
			handleDiseaseData(rootNode);
			/* make matrixes with transition probabilities */
			@SuppressWarnings("unused")
			MatrixExponential matExp = MatrixExponential.getInstance();

			atIndex = new int[nCluster][][];
			incIndex = new int[nCluster][][];
			incRowIndex = new int[nCluster][][];
			RRdis = new float[96][2][nCluster][][];
			RRdisFatal = new float[96][2][nCluster][][];
			disFatalIndex = new int[96][2][nCluster][];
			nonCuredRatio = new float[96][2][nCluster];
			for (int c = 0; c < nCluster; c++) {
				/*
				 * as the indexes give the locations, and both incidence and
				 * attributable mortality are needed only for half the states,
				 * we can save memory space by using only nCombination/n
				 */
				atIndex[c] = new int[numberOfDiseasesInCluster[c]][nCombinations[c] / 2];
				incIndex[c] = new int[numberOfDiseasesInCluster[c]][nCombinations[c] / 2];
				incRowIndex[c] = new int[numberOfDiseasesInCluster[c]][nCombinations[c] / 2];
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						RRdis[a][g][c] = new float[nCombinations[c]][nCombinations[c] / 2];
						int nFatal = 0;
						for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {
							int dd = clusterStartsAtDiseaseNumber[c] + d;
							if (baselineFatalIncidence[dd][a][g] > 0)
								nFatal++;
							if (withCuredFraction[c] && d == 0)
								if (baselineIncidence[dd][a][g]
										+ baselineIncidence[dd + 1][a][g] != 0)
									nonCuredRatio[a][g][c] = baselineIncidence[dd + 1][a][g]
											/ (baselineIncidence[dd][a][g] + baselineIncidence[dd + 1][a][g]);
								else
									nonCuredRatio[a][g][c] = 0;

						}
						if (nFatal > 0) {
							disFatalIndex[a][g][c] = new int[nFatal];
							RRdisFatal[a][g][c] = new float[nFatal][nCombinations[c]];
						} else {
							disFatalIndex[a][g][c] = new int[1];
							disFatalIndex[a][g][c][0] = -1;
						}
						/*
						 * Extract the number of the diseases with fatal
						 * incidence, and place them in the array disFatalIndex
						 */
						int indexFatal = 0;
						for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {
							int dd = clusterStartsAtDiseaseNumber[c] + d;
							if (baselineFatalIncidence[dd][a][g] > 0) {
								disFatalIndex[a][g][c][indexFatal] = d;
								indexFatal++;
							}
						}

					}
			}

			/*
			 * int[] numberOfDiseasesInCluster == array over clusters; // int[]
			 * clusterStartsAtDiseaseNumber == array over clusters; // int
			 * totalNumberOfDiseases; // int nCluster = -1; // int[]
			 * DiseaseNumberWithinCluster;== array over diseases
			 */

			@SuppressWarnings("unused")
			int currentStateNo = 0;

			for (int c = 0; c < nCluster; c++) {
				if (numberOfDiseasesInCluster[c] == 1) {

					currentStateNo++;
				} else if (withCuredFraction[c]) {
					currentStateNo += 2;

				} else // cluster of dependent diseases
				{

					/*
					 * Matrix entry is formed as: / - attributable Mortality for
					 * each disease that is 1 in combi - sum incidence to all
					 * other disease that are 0 in combi (including RR's as
					 * above) - sum fatal incidences
					 */

					for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {
						int nInc = 0;
						int nAt = 0;
						for (int column = 0; column < nCombinations[c]; column++) {

							/*
							 * add the relative risks for disease on disease for
							 * each fatal disease
							 */

							if ((column & (1 << d)) != (1 << d)) {
								/*
								 * d is 0, thus incidence should be added
								 */

								incIndex[c][d][nInc] = column;
								/*
								 * calculate the row where incidence should also
								 * be added
								 */
								incRowIndex[c][d][nInc] = column + (1 << d);
								/* calculate the RR from diseases */
								for (int a = 0; a < 96; a++)
									for (int g = 0; g < 2; g++) {
										RRdis[a][g][c][d][nInc] = 1;

										for (int dCause = 0; dCause < numberOfDiseasesInCluster[c]; dCause++)
											if ((column & (1 << dCause)) == (1 << dCause))
												RRdis[a][g][c][d][nInc] *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];

									}
								nInc++;

								/* else d=1, then atmort should be added */
							} else {

								atIndex[c][d][nAt] = column; // add at
								nAt++;
							}
						}
						// end loop over columns

						/* now part for fatal diseases */
						for (int a = 0; a < 96; a++)
							for (int g = 0; g < 2; g++) {
								/* only for fatal diseases */
								if (disFatalIndex[a][g][c][0] > -1) {
									/* look if disease d is a fatal disease */
									int diseaseNumberOfFatalDisease = 0;
									for (int fatalDiseaseNumber = 0; fatalDiseaseNumber < disFatalIndex[a][g][c].length; fatalDiseaseNumber++) {
										diseaseNumberOfFatalDisease = disFatalIndex[a][g][c][fatalDiseaseNumber];
										if (diseaseNumberOfFatalDisease == d)
										/*
										 * if yes, add RR terms to the
										 * RRdisFatal entry for this disease
										 */

										{
											/*
											 * these terms are 1 is it is an
											 * independent disease
											 */
											Arrays
													.fill(
															RRdisFatal[a][g][c][fatalDiseaseNumber],
															1);
											/*
											 * if the column contains a
											 * potential causal disease then add
											 * the RR from this disease (=1 if
											 * not causal)
											 */
											for (int dCause = 0; dCause < numberOfDiseasesInCluster[c]; dCause++)
												for (int column = 0; column < nCombinations[c]; column++)
													if ((column & (1 << dCause)) == (1 << dCause))
														RRdisFatal[a][g][c][fatalDiseaseNumber][column] *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
										}
									}
								}
							}

					}// end loop over diseases within cluster

					currentStateNo += nCombinations[c] - 1;

				} // end if statement for cluster diseases

			} // end loop over clusters

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
	 * This function returns a 3-dimension array with the fatal incidences and
	 * incidences per state in cluster c
	 * 
	 * @param a
	 *            : age
	 * @param g
	 *            : gender
	 * @param r
	 *            : risk factor value (float)
	 * @param c
	 *            : cluster number
	 * @return 3-dimensional array with indexes: indicator (0=incidence, 1=fatal
	 *         incidence) ; disease (number within cluster) ; state
	 */
	private double[][][] calculateIncidenceInState(float r, int a, int g, int c) {

		double[][][] incidenceInState = new double[2][this.numberOfDiseasesInCluster[c]][nCombinations[c]];
		double[] incidence = new double[this.numberOfDiseasesInCluster[c]];
		double[] fatalIncidence = new double[this.numberOfDiseasesInCluster[c]];
		/*
		 * first calculate the incidences without taking other diseases into
		 * account
		 */
		for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {

			fatalIncidence[d] = calculateFatalIncidence(r, a, g, d);
			incidence[d] = calculateIncidence(r, a, g, d);
		}
		/*
		 * first= changed state (row) second :sources of change(change=number in
		 * second state entry in matrix
		 * 
		 * thus [to][from]
		 */

		for (int state = 0; state < nCombinations[c]; state++)

		/*
		 * Matrix entry is formed as: / - attributable Mortality for each
		 * disease that is 1 in combi - sum incidence to all other disease that
		 * are 0 in combi (including RR's as above) - sum fatal incidences
		 */
		{

			for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {

				/*
				 * first add fatal incidence irrespective of value of d
				 */
				double RR = 1;
				for (int dCause = 0; dCause < getNDiseases(); dCause++) {
					/*
					 * if dCause==1 in row=column)
					 */
					if ((state & (1 << dCause)) == (1 << dCause))
						RR *= this.relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
				}

				incidenceInState[1][d][state] = (float) (RR * fatalIncidence[clusterStartsAtDiseaseNumber[c]
						+ d]);

				if ((state & (1 << d)) != (1 << d))

				/*
				 * d is 0, thus incidence should be added
				 */
				{
					RR = 1;
					for (int dCause = 0; dCause < getNDiseases(); dCause++)
						if ((state & (1 << dCause)) == (1 << dCause))
							RR *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];

					incidenceInState[0][d][state] = (float) (RR * incidence[clusterStartsAtDiseaseNumber[c]
							+ d]);
					// or d=1, then atmort
					// should be added
				}
			}
		}
		return incidenceInState;
	}
}
