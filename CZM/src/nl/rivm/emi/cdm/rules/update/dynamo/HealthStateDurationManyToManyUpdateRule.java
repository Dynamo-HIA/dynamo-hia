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
 *         does not need to check the type of risk factor.
 * 
 * 
 */

public class HealthStateDurationManyToManyUpdateRule

extends HealthStateManyToManyUpdateRule {

	
	
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
	 * indexes are :age, gender, cluster number, number of fatal disease (number
	 * as given in disFatalIndex). RRdisFatal gives the disease related RR's for
	 * each location in the matrix for the fatal diseases
	 */

	float[][][] nonCuredRatio;
	/*
	 * indexes are :age, gender, cluster number gives the ratio not-cured/total
	 */

	float transMat[][][][][][];
	/*
	 * indexes are : age sex riskfactor diseaseCluster from to
	 */

	float OtherMortalitySurvival[][][];

	/*
	 * indexes are : age sex riskfactor status
	 */

	MatrixExponential matExp = new MatrixExponential();

	public HealthStateDurationManyToManyUpdateRule()
			throws ConfigurationException, CDMUpdateRuleException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float[] newValue = null;

		try {
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			float[] oldValue = getValues(currentValues,
					getCharacteristicIndex());
			if (ageValue < 0) {
				newValue = oldValue;  return newValue;} else {
			int sexValue = getInteger(currentValues, getSexIndex());
			if (ageValue > 95)
				ageValue = 95;
			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			float riskDurationValue = getFloat(currentValues, riskFactorIndex2);
			// TODO dit beter oplossen: op een of ander manier wordt deze steeds
			// -1;
			// wellicht doordat setter in andere classe dan deze??
			if (getCharacteristicIndex() > 0)
				setCharacteristicIndex(5);
			
			newValue = new float[oldValue.length];
			float[] currentDiseaseStateValues = new float[oldValue.length];
			
			
			
			/*
			 *   UPDATE OF DURATION CLASS
			 */
			
			//NB All transitions are [to][from]
			
			
			
			if (riskFactorValue == durationClass) {
				int currentStateNo = 0;
				double survival = 0;
				double survivalFraction = calculateOtherCauseSurvival(
						riskDurationValue, ageValue, sexValue);

				double expAI;
				double expI;
				double expA;
				int d;
				double incidence;
				double incidence2;
				double atMort;
				for (int c = 0; c < nCluster; c++) {
                  /* update single diseases */
					if (numberOfDiseasesInCluster[c] == 1) {

						d = clusterStartsAtDiseaseNumber[c];
						atMort = attributableMortality[ageValue][sexValue][d];
						incidence = calculateIncidence(riskDurationValue,
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
						if (disFatalIndex[ageValue][sexValue][c][0] == 0)
							survivalFraction *= Math.exp(-getTimeStep()
									* calculateFatalIncidence(riskDurationValue,
											ageValue, sexValue, d))
									* (atMort * (1 - oldValue[d]) * expI + (atMort
											* oldValue[d] - incidence)
											* expA) / (atMort - incidence);
						else
							survivalFraction *= (atMort * (1 - oldValue[d])
									* expI + (atMort * oldValue[d] - incidence)
									* expA)
									/ (atMort - incidence);
						currentStateNo++;
						/* update diseases with cured fraction */
					} else if (withCuredFraction[c]) {
						d = clusterStartsAtDiseaseNumber[c];

						atMort = attributableMortality[ageValue][sexValue][d + 1];
						incidence2 = calculateIncidence(riskDurationValue,
								ageValue, sexValue, d + 1);
						incidence = incidence2
								/ nonCuredRatio[ageValue][sexValue][c];

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

						survival = (1 - oldValue[d] - oldValue[d + 1]) * expI
								+ oldValue[d] + oldValue[d + 1] * expA
								+ (1 - oldValue[d] - oldValue[d + 1])
								* (transMat10 + transMat20);
						newValue[currentStateNo] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat10 + oldValue[d]) / survival);
						newValue[currentStateNo + 1] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat20 + oldValue[d + 1] * expA) / survival);
						/*
						 * NB disease with cured fraction can not be fatal at
						 * the same time
						 */
						survivalFraction *= survival;

					}

					else /* now cluster diseases */
					{

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

						double[][] rateMatrix = fillRateMatrixForCluster(
								ageValue, sexValue, riskDurationValue, c);
						float[][] transMat = matExp
								.exponentiateFloatMatrix(rateMatrix);

						for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
						{ /* transitionProbabilities are [to][from] */
							unconditionalNewValues[state1] = transMat[state1][0]
									* currentHealthyState;
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += transMat[state1][state2]
										* oldValue[state2 - 1 + currentStateNo];
						}
						/* calculate survival */
                        survival=0; 
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

			}
			

			
			/*
			 *   UPDATE OF CATEGORIES WITHOUT DURATION
			 */
			
			
			/* for categories without duration */
			else {
				int currentStateNo = 0;
				double survival = 0;
				double survivalFraction = OtherMortalitySurvival[ageValue][sexValue][riskFactorValue];

				float[][] currentTransMat;
				for (int c = 0; c < nCluster; c++) {
					currentTransMat = transMat[ageValue][sexValue][riskFactorValue][c];
					if (numberOfDiseasesInCluster[c] == 1) {

						/* for a single disease we do not need the do loop (although it would work) */

						survival = (1 - oldValue[currentStateNo])
								* (currentTransMat[1][0] +currentTransMat[0][0])
								+ oldValue[currentStateNo]
								* (currentTransMat[1][1]);

						newValue[currentStateNo] = (float) ((1 - oldValue[currentStateNo])
								* (currentTransMat[1][0] + oldValue[currentStateNo]
										* currentTransMat[1][1]) / survival);

						survivalFraction *= survival;
						currentStateNo++;
					} else if (withCuredFraction[c]) {
						
						
						/* as some elements are zero here, this is faster than
						 * using the more general implementation for clusterdiseases
						 */

						survival = (1 - oldValue[currentStateNo]-oldValue[currentStateNo + 1]) *(currentTransMat[2][0]+currentTransMat[1][0]+ currentTransMat[0][0])
						
						 + oldValue[currentStateNo]* (currentTransMat[1][1])
						
						+ oldValue[currentStateNo + 1] *( currentTransMat[2][2]);
								
						newValue[currentStateNo] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* currentTransMat[1][0] + oldValue[currentStateNo]* currentTransMat[1][1]) / survival);
						newValue[currentStateNo + 1] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
								* currentTransMat[2][0] + oldValue[currentStateNo + 1]* currentTransMat[2][2] ) / survival);
						/*
						 * NB disease with cured fraction can not be fatal at
						 * the same time
						 */
						currentStateNo++;
						currentStateNo++;
						survivalFraction *= survival;
					} else {
						/* cluster of dependent diseases */
						/* but this would work also for the previous cases 
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
							unconditionalNewValues[state1] = currentTransMat[state1][0]
									* currentHealthyState;
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += currentTransMat[state1][state2]
										* oldValue[state2 - 1 + currentStateNo];
						}
						/* calculate survival */

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

			}
			return newValue;
		} }catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by HealthStateMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ getCharacteristicIndex());
			e.printStackTrace();
			throw e;

		}

	}

	/**this method fills the transition rate matrix for a riskfactor-category with duration classes
	 * @param ageValue
	 * @param sexValue
	 * @param riskDurationValue
	 * @param c
	 * @return
	 */
	private double[][] fillRateMatrixForCluster(int ageValue, int sexValue,
			float riskDurationValue, int c) {
		int d;
		double incidence;
		double atMort;
		/* make transition rate matrix */
		double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];
		for (int dc = 0; dc < numberOfDiseasesInCluster[c]; dc++) {
			d = clusterStartsAtDiseaseNumber[c] + dc;
			incidence = calculateIncidence(riskDurationValue, ageValue, sexValue,
					d);
			atMort = attributableMortality[d][ageValue][sexValue];
			for (int loc = 0; loc < nCombinations[c] >> 1; loc++) {
				rateMatrix[incIndex[c][dc][loc]][incIndex[c][dc][loc]] += -incidence
						* RRdis[ageValue][sexValue][c][dc][loc];
				rateMatrix[incRowIndex[c][dc][loc]][incIndex[c][dc][loc]] += incidence
						* RRdis[ageValue][sexValue][c][dc][loc];
				rateMatrix[atIndex[c][dc][loc]][atIndex[c][dc][loc]] += -atMort;

			}// end loop locations of disease
		}// end loop over diseases in cluster

		/*
		 * now loop over the fatal diseases (separate, as this is only done if
		 * the disease is really fatal to save running time
		 */
		if (disFatalIndex[ageValue][sexValue][c][0] >= 0)
			for (int fataldis = 0; fataldis < disFatalIndex[ageValue][sexValue][c].length; fataldis++) {
				incidence = calculateFatalIncidence(riskDurationValue, ageValue,
						sexValue, fataldis + clusterStartsAtDiseaseNumber[c]);
				for (int loc = 0; loc < nCombinations[c]; loc++) {

					rateMatrix[disFatalIndex[ageValue][sexValue][c][loc]][disFatalIndex[ageValue][sexValue][c][loc]] += -incidence
							* RRdisFatal[ageValue][sexValue][c][fataldis][loc];

				} // end loop over locations
			} // end loop over fatal diseases
		return rateMatrix;
	}

	private double calculateIncidence(float riskDurationValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
				* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
						* Math.exp(-riskDurationValue
								* alfaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);

		return incidence;
	}

	private double calculateFatalIncidence(float riskDurationValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;
		incidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
				* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
						* Math.exp(-riskDurationValue
								* alfaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);

		return incidence;
	}

	private double calculateOtherCauseSurvival(float riskDurationValue,
			int ageValue, int sexValue) {
		double otherCauseSurvival = 0;

		otherCauseSurvival = Math
				.exp((-baselineOtherMort[ageValue][sexValue]
						*( (relRiskOtherMortBegin[ageValue][sexValue] - relRiskOtherMortEnd[ageValue][sexValue])

						* Math.exp(

						-alfaDuurOtherMort[ageValue][sexValue]
								* riskDurationValue) + relRiskOtherMortEnd[ageValue][sexValue]))
						* getTimeStep());

		return otherCauseSurvival;
	}

	private double calculateOtherCauseSurvival(int riskFactorValue,
			int ageValue, int sexValue) {
		double otherCauseSurvival = 0;
		otherCauseSurvival = Math
				.exp(-baselineOtherMort[ageValue][sexValue]
						* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue]
						* getTimeStep());
		return otherCauseSurvival;
	}

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
			/*
			 * NB: riskType should be set before handling the disease
			 * information
			 */
			handleNCat(configurationFileConfiguration);
			handleRefValueContinuous(configurationFileConfiguration);
			handleDurationClass(configurationFileConfiguration);
			handleNClusters(configurationFileConfiguration);
			handleOtherMort(configurationFileConfiguration);
			handleDiseaseData(rootNode);
			/* make matrixes with transition probabilities */
			MatrixExponential matExp = MatrixExponential.getInstance();

			atIndex = new int[nCluster][][];
			incIndex = new int[nCluster][][];
			incRowIndex = new int[nCluster][][];
			RRdis = new float[96][2][nCluster][][];
			RRdisFatal = new float[96][2][nCluster][][];
			disFatalIndex = new int[96][2][nCluster][];
			nonCuredRatio = new float[96][2][nCluster];
			for (int c = 0; c < nCluster; c++) {
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
								nonCuredRatio[a][g][c] = baselineIncidence[dd + 1][a][g]
										/ (baselineIncidence[dd][a][g] + baselineIncidence[dd + 1][a][g]);
						}
						if (nFatal > 0) {
							disFatalIndex[a][g][c] = new int[nFatal];
							RRdisFatal[a][g][c] = new float[nFatal][nCombinations[c]];
						} else {
							disFatalIndex[a][g][c] = new int[1];
							disFatalIndex[a][g][c][0] = -1;
						}
						int indexFatal = 0;
						for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {
							int dd = clusterStartsAtDiseaseNumber[c] + d;
							if (baselineFatalIncidence[dd][a][g] > 0)
								disFatalIndex[a][g][c][indexFatal] = d;

						}

					}
			}

			/*
			 * int[] numberOfDiseasesInCluster == array over clusters; // int[]
			 * clusterStartsAtDiseaseNumber == array over clusters; // int
			 * totalNumberOfDiseases; // int nCluster = -1; // int[]
			 * DiseaseNumberWithinCluster;== array over diseases
			 */

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
							 * first add fatal incidence irrespective of value
							 * of d
							 */

							for (int a = 0; a < 96; a++)
								for (int g = 0; g < 2; g++) {
									if (disFatalIndex[a][g][c][0] == -1) {

										for (int f = 0; f < disFatalIndex[a][g][c].length; f++)
											if (disFatalIndex[a][g][c][0] == d) {
												RRdisFatal[a][g][c][disFatalIndex[a][g][c][f]][f] = 1;
												for (int dCause = 0; dCause < getNDiseases(); dCause++)
													if ((column & (1 << dCause)) == (1 << dCause))
														RRdisFatal[a][g][c][disFatalIndex[a][g][c][f]][f] *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
											}
									}
								}

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

										for (int dCause = 0; dCause < getNDiseases(); dCause++)
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

					}// end loop over diseases within cluster

					currentStateNo += nCombinations[c] - 1;

				} // end if statement for cluster diseases

			} // end loop over clusters

			/*
			 * make transmat for all age/sex/risk factor combis with the
			 * exception of the duration class
			 */

			
			int d;
			double incidence;
			double incidence2;
			

			OtherMortalitySurvival = new float[96][2][nCat];
			transMat = new float[96][2][nCat][nCluster][][];/*
												 * index 3+4: riskfactor,
												 * cluster
												 */

			for (int a = 0; a < 96; a++) {
				for (int g = 0; g < 2; g++) {
					double[] attrMort = getAttributableMortality(a, g);
					for (int r = 0; r < relRiskCategorical[0][0][0].length; r++) {
						if (r != durationClass) {
							OtherMortalitySurvival[a][g][r] = (float) calculateOtherCauseSurvival(
									r, a, g);

							for (int c = 0; c < nCluster; c++) {

								if (numberOfDiseasesInCluster[c] == 1) {

									d = clusterStartsAtDiseaseNumber[c];

									double fatalIncidence = calculateFatalIncidence(
											r, a, g, d);
									incidence = calculateIncidence(r, a, g, d);
									/*
									 * make matrixes with transition
									 * probabilities
									 */

									transMat[a][g][r][c] = new float[2][2];

									transMat[a][g][r][c][0][0] = (float) Math
											.exp(-incidence - fatalIncidence);
									transMat[a][g][r][c][0][1] = 0;
									transMat[a][g][r][c][1][1] = (float) Math
											.exp(-attrMort[d] - fatalIncidence);

									if (Math.abs(incidence - attrMort[d]) > 1E-15)
										transMat[a][g][r][c][1][0] = (float) (Math
												.exp(-incidence - attrMort[d]
														- fatalIncidence)
												* incidence
												* (Math.exp(incidence) - Math
														.exp(attrMort[d])) / (incidence - attrMort[d]));
									else
										/*
										 * if incidence equal to attributable
										 * mortality, the denominator becomes
										 * zero and we need another formula
										 */
										transMat[a][g][r][c][1][0] = (float) (Math
												.exp(-incidence
														- fatalIncidence) * incidence);

									currentStateNo++;
								} else if (withCuredFraction[c]) {

									/*
									 * zeroth disease state=healthy first
									 * disease state = cured disease (d) second
									 * disease state = not cured disease (d+1)
									 */

									/*
									 * Officially it is not possible to have
									 * both fatal incidences >0 and with cured
									 * fraction, so fatal incidence is not
									 * included
									 */
									d = clusterStartsAtDiseaseNumber[c];
									incidence = calculateIncidence(r, a, g, d);
									incidence2 = calculateIncidence(r, a, g,
											d + 1)
											+ incidence;
									transMat[a][g][r][c] = new float[3][3];

									transMat[a][g][r][c][0][0] = (float) Math
											.exp(-incidence2);
									transMat[a][g][r][c][0][1] = 0;
									transMat[a][g][r][c][0][2] = 0;
									if ((incidence + incidence2) == 0)
										transMat[a][g][r][c][1][0] = 0;
									else
										transMat[a][g][r][c][1][0] = (float) ((float) (1 - Math
												.exp(-incidence2))
												* incidence / (incidence2));
									transMat[a][g][r][c][1][1] = 1;
									transMat[a][g][r][c][1][2] = 0;
									if (incidence2 == attrMort[d + 1])
										transMat[a][g][r][c][2][0] = (float) (Math
												.exp(-incidence2) * (incidence2 - incidence));
									else
										transMat[a][g][r][c][2][0] = (float) ((Math
												.exp(-attrMort[d + 1]) - Math
												.exp(-incidence2))
												* (incidence2 - incidence) / (incidence2 - attrMort[d + 1]));
									transMat[a][g][r][c][2][1] = 0;
									transMat[a][g][r][c][2][2] = (float) Math
											.exp(-attrMort[d + 1]);
									currentStateNo += 2;

								} else // cluster of dependent diseases

								{

									transMat[a][g][r][c] = new float[nCombinations[c]][nCombinations[c]];

									/* make transition rate matrix */
									double[][] rateMatrix = fillRateMatrixForCluster(
											a, g, r, c);

									transMat[a][g][r][c] = matExp
											.exponentiateFloatMatrix(rateMatrix);

								}
							} // end loop over clusters

							
						}
					}
				}
			} // end loop over age
			success = true;
			return success;
		
		} catch (NoSuchElementException e) {			
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
					+ this.nDiseasesLabel), configurationFile.getAbsolutePath());						
		} catch (DynamoUpdateRuleConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e, 
					configurationFile.getAbsolutePath());
		}
		success=false;
		return success;
	}

}
