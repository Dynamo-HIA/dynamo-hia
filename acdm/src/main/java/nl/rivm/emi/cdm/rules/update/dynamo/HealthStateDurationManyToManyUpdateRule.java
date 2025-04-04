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

extends HealthStateCatManyToManyUpdateRule  {

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
			double[] currentHealthyState = new double[nCluster];
			float[] oldValue = getValues(currentValues,
					getCharacteristicIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {

				int riskFactorValue = getInteger(currentValues,
						riskFactorIndex1);
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);
				// TODO dit beter oplossen: op een of ander manier wordt deze
				// steeds
				// -1;
				// wellicht doordat setter in andere classe dan deze??
				if (getCharacteristicIndex() > 0)
					setCharacteristicIndex(5);

				newValue = new float[oldValue.length];
				float[] currentDiseaseStateValues = new float[oldValue.length];

				if (riskFactorValue != durationClass) {

					return updatedHealthStates(currentValues, ageValue,
							currentHealthyState, oldValue);
				} else { // durationclass
					int sexValue = getInteger(currentValues, getSexIndex());
					if (ageValue > 95)
						ageValue = 95;

					int currentStateNo = 0;

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
						double survival = 0;
						/* update single diseases */
						if (numberOfDiseasesInCluster[c] == 1) {

							d = clusterStartsAtDiseaseNumber[c];
							atMort = attributableMortality[d][ageValue][sexValue];
							incidence = calculateIncidence(riskDurationValue,
									ageValue, sexValue, d);
							/*
							 * for faster execution for results that are used
							 * multiple times: only perform Math.exp once and
							 * save results
							 */
							expA = Math.exp(-atMort * timeStep);
							expI = Math.exp(-incidence * timeStep);
							if (Math.abs(incidence - atMort) > 1E-15)
								expAI = expA / expI;
							else
								expAI = 1;
							// finci = ((p0 * em - i) * exp((i - em) * time) + i
							// *
							// (1 -
							// p0))
							// / ((p0 * em - i) * exp((i - em) * time) + em * (1
							// -
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
							 * denominator becomes zero and we need another
							 * formula
							 */
							if (disFatalIndex[ageValue][sexValue][c][0] == 0
									&& expAI != 1)
								survivalFraction *= Math.exp(-getTimeStep()
										* calculateFatalIncidence(
												riskDurationValue, ageValue,
												sexValue, d))
										* (atMort
												* (1 - oldValue[currentStateNo])
												* expI + (atMort
												* oldValue[currentStateNo] - incidence)
												* expA) / (atMort - incidence);
							else if (expAI != 1)
								survivalFraction *= (atMort
										* (1 - oldValue[currentStateNo]) * expI + (atMort
										* oldValue[currentStateNo] - incidence)
										* expA)
										/ (atMort - incidence);
							else if (disFatalIndex[ageValue][sexValue][c][0] != 0)
								survivalFraction *= expA
										* (incidence
												* (1 - oldValue[currentStateNo]) + 1);
							else
								survivalFraction *= Math.exp(-getTimeStep()
										* calculateFatalIncidence(
												riskDurationValue, ageValue,
												sexValue, d))
										* expA
										* (incidence
												* (1 - oldValue[currentStateNo]) + 1);

							currentStateNo++;
							/* update diseases with cured fraction */
						} else if (withCuredFraction[c]) {
							d = clusterStartsAtDiseaseNumber[c];

							atMort = attributableMortality[d + 1][ageValue][sexValue];
							incidence2 = calculateIncidence(riskDurationValue,
									ageValue, sexValue, d + 1);
							/*
							 * non cured ratio can be only zero when either
							 * incidence d+1= zero, or both are zero, so the
							 * last option can not occur
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
							 * multipletimes: only perform Math.exp once and
							 * save results
							 */

							expI = Math.exp(-incidence * timeStep);
							expA = Math.exp(-atMort * timeStep);
							double transMat10;
							double transMat20;
							if (incidence != 0)
								transMat10 = (1 - expI)
										* (incidence - incidence2) / incidence;
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
							 * NB disease with cured fraction can not be fatal
							 * at the same time
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
							double unconditionalNewValues[] = new double[nCombinations[c]];

							/* calculate the healthy state */
							currentHealthyState[c] = 1;
							for (int state = currentStateNo; state < currentStateNo
									+ nCombinations[c] - 1; state++)
								currentHealthyState[c] -= oldValue[state];

							/*
							 * NB the unconditional new state starts at 0 with
							 * the healthy state, so oldvalue[1] belongs with
							 * unconditionalnewstate[0]
							 */

							double[][] rateMatrix = fillRateMatrixForCluster(
									ageValue, sexValue, riskDurationValue, c);
							if (ageValue == 46) {
								int stop = 0;
								stop++;

							}
							float[][] transMat = matExp
									.exponentiateFloatMatrix(rateMatrix);

							for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
							{ /* transitionProbabilities are [to][from] */
								unconditionalNewValues[state1] = transMat[state1][0]
										* currentHealthyState[c];
								for (int state2 = 1; state2 < nCombinations[c]; state2++)
									// column=from

									unconditionalNewValues[state1] += transMat[state1][state2]
											* oldValue[state2 - 1
													+ currentStateNo];
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
					/*
					 * copied from HenthStateContMany met alleen RiskValue verandert in RiskDuration
					 * 
					 * //
					 * *******************************************************
					 * *************************
					 */
					int currentStateNo2 = 0; /*
											 * this is the disease-state index,
											 * while currentStateNo is the state
											 * in the output-array (newvalues).
											 * i.o.w. CurrentStateNo is the
											 * index for the place where the
											 * incidence data are stored
											 */
					for (int c = 0; c < nCluster; c++) {

						/* update single diseases */
						float incidenceC = 0F;
						float fatalIncidenceC = 0F;
						if (numberOfDiseasesInCluster[c] == 1) {
							currentStateNo++;
							d = clusterStartsAtDiseaseNumber[c];

							/*  hier aangepast aan duration */
							incidenceC = (float) calculateIncidence(
									riskDurationValue, ageValue, sexValue, d);
							fatalIncidenceC = (float) calculateFatalIncidence(
									riskDurationValue, ageValue, sexValue, d);

							/*
							 * incidence = incidence(in disease free) * fraction
							 * of personyears free of disease + fatal incidence;
							 */
							/*
							 * person years with disease= average of prevalence
							 * at beginning and end
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
							double newHealthyState = 1
									- newValue[currentStateNo2]
									- newValue[currentStateNo2 + 1];
							double oldHealthyState = 1
									- oldValue[currentStateNo2]
									- oldValue[currentStateNo2 + 1];
							incidenceC = (float) calculateIncidence(
									riskDurationValue, ageValue, sexValue, d);
							fatalIncidenceC = (float) calculateFatalIncidence(
									riskDurationValue, ageValue, sexValue, d);
							// incidence =
							// calculateIncidence(riskFactorValue,ageValue,
							// sexValue, d);
							newValue[currentStateNo] = (float) ((0.5
									* newHealthyState * survivalFraction + 0.5 * oldHealthyState)
									* incidenceC + fatalIncidenceC
									* (0.5 + 0.5 * survivalFraction));
							currentStateNo++;
							incidenceC = (float) calculateIncidence(
									riskDurationValue, ageValue, sexValue, d + 1);
							fatalIncidenceC = (float) calculateFatalIncidence(
									riskDurationValue, ageValue, sexValue, d + 1);

							newValue[currentStateNo] = (float) ((0.5
									* newHealthyState * survivalFraction + 0.5 * oldHealthyState)
									* incidenceC + fatalIncidenceC
									* (0.5 + 0.5 * survivalFraction));
							currentStateNo2++;
							currentStateNo2++;
						}

						/* update cluster diseases */
						else {

												
							int dInCluster = 0;
							/*
							 * calculate the healthy state at the end of the
							 * time period
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
								 * first add incidence from healthy state
								 * including fatal incidence
								 */

								/*
								 * NB: first index flags whether this is
								 * incidence (0) or fatal incidence (1)
								 * 
								 * second index: disease for which this is the incidence
								 */
							
								double[][][] incidenceInStateForDuration = calculateIncidenceInState(
										riskDurationValue, ageValue, sexValue, c);

								double incidenceD = 0.5
										* (currentHealthyState[c] + newHealthyState
												* survivalFraction)
										* (incidenceInStateForDuration[0][numberInCluster][0] + incidenceInStateForDuration[1][numberInCluster][0]);
								/* add incidence from the non-healthy states */
								for (int state = 1; state < nCombinations[c]; state++) {

									if ((state & (1 << numberInCluster)) != (1 << numberInCluster))
										incidenceD += (0.5
												* newValue[startState + state
														- 1] * survivalFraction + 0.5 * oldValue[startState
												+ state - 1])
												* incidenceInStateForDuration[0][numberInCluster][state];
									/* fatal incidence is for all */
									incidenceD += (0.5
											* newValue[startState + state - 1]
											* survivalFraction + 0.5 * oldValue[startState
											+ state - 1])
											* incidenceInStateForDuration[1][numberInCluster][state];
								}

								newValue[currentStateNo] = (float) incidenceD;

							} /* end loop over diseases in cluster */
							currentStateNo2 += nCombinations[c] - 1;

						} // end if statement for cluster diseases

					} // end loop over clusters

					return newValue;

				}

			}
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by HealthStateDurationMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ getCharacteristicIndex());
			e.printStackTrace();
			throw e;

		}

	}

	public Object update_old(Object[] currentValues)
			throws CDMUpdateRuleException {

		float[] newValue = null;

		try {
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			float[] oldValue = getValues(currentValues,
					getCharacteristicIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {
				int sexValue = getInteger(currentValues, getSexIndex());
				if (ageValue > 95)
					ageValue = 95;
				int riskFactorValue = getInteger(currentValues,
						riskFactorIndex1);
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);
				// TODO dit beter oplossen: op een of ander manier wordt deze
				// steeds
				// -1;
				// wellicht doordat setter in andere classe dan deze??
				if (getCharacteristicIndex() > 0)
					setCharacteristicIndex(5);

				newValue = new float[oldValue.length];
				float[] currentDiseaseStateValues = new float[oldValue.length];

				/*
				 * UPDATE OF DURATION CLASS
				 */

				// NB All transitions are [to][from]
				if (riskFactorValue == durationClass) {
					int currentStateNo = 0;

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
						double survival = 0;
						/* update single diseases */
						if (numberOfDiseasesInCluster[c] == 1) {

							d = clusterStartsAtDiseaseNumber[c];
							atMort = attributableMortality[d][ageValue][sexValue];
							incidence = calculateIncidence(riskDurationValue,
									ageValue, sexValue, d);
							/*
							 * for faster execution for results that are used
							 * multiple times: only perform Math.exp once and
							 * save results
							 */
							expA = Math.exp(-atMort * timeStep);
							expI = Math.exp(-incidence * timeStep);
							if (Math.abs(incidence - atMort) > 1E-15)
								expAI = expA / expI;
							else
								expAI = 1;
							// finci = ((p0 * em - i) * exp((i - em) * time) + i
							// *
							// (1 -
							// p0))
							// / ((p0 * em - i) * exp((i - em) * time) + em * (1
							// -
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
							 * denominator becomes zero and we need another
							 * formula
							 */
							if (disFatalIndex[ageValue][sexValue][c][0] == 0
									&& expAI != 1)
								survivalFraction *= Math.exp(-getTimeStep()
										* calculateFatalIncidence(
												riskDurationValue, ageValue,
												sexValue, d))
										* (atMort
												* (1 - oldValue[currentStateNo])
												* expI + (atMort
												* oldValue[currentStateNo] - incidence)
												* expA) / (atMort - incidence);
							else if (expAI != 1)
								survivalFraction *= (atMort
										* (1 - oldValue[currentStateNo]) * expI + (atMort
										* oldValue[currentStateNo] - incidence)
										* expA)
										/ (atMort - incidence);
							else if (disFatalIndex[ageValue][sexValue][c][0] != 0)
								survivalFraction *= expA
										* (incidence
												* (1 - oldValue[currentStateNo]) + 1);
							else
								survivalFraction *= Math.exp(-getTimeStep()
										* calculateFatalIncidence(
												riskDurationValue, ageValue,
												sexValue, d))
										* expA
										* (incidence
												* (1 - oldValue[currentStateNo]) + 1);

							currentStateNo++;
							/* update diseases with cured fraction */
						} else if (withCuredFraction[c]) {
							d = clusterStartsAtDiseaseNumber[c];

							atMort = attributableMortality[d + 1][ageValue][sexValue];
							incidence2 = calculateIncidence(riskDurationValue,
									ageValue, sexValue, d + 1);
							/*
							 * non cured ratio can be only zero when either
							 * incidence d+1= zero, or both are zero, so the
							 * last option can not occur
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
							 * multipletimes: only perform Math.exp once and
							 * save results
							 */

							expI = Math.exp(-incidence * timeStep);
							expA = Math.exp(-atMort * timeStep);
							double transMat10;
							double transMat20;
							if (incidence != 0)
								transMat10 = (1 - expI)
										* (incidence - incidence2) / incidence;
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
							 * NB disease with cured fraction can not be fatal
							 * at the same time
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
							double unconditionalNewValues[] = new double[nCombinations[c]];

							/* calculate the healthy state */
							double currentHealthyState = 1;
							for (int state = currentStateNo; state < currentStateNo
									+ nCombinations[c] - 1; state++)
								currentHealthyState -= oldValue[state];

							/*
							 * NB the unconditional new state starts at 0 with
							 * the healthy state, so oldvalue[1] belongs with
							 * unconditionalnewstate[0]
							 */

							double[][] rateMatrix = fillRateMatrixForCluster(
									ageValue, sexValue, riskDurationValue, c);
							if (ageValue == 46) {
								int stop = 0;
								stop++;

							}
							float[][] transMat = matExp
									.exponentiateFloatMatrix(rateMatrix);

							for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
							{ /* transitionProbabilities are [to][from] */
								unconditionalNewValues[state1] = transMat[state1][0]
										* currentHealthyState;
								for (int state2 = 1; state2 < nCombinations[c]; state2++)
									// column=from

									unconditionalNewValues[state1] += transMat[state1][state2]
											* oldValue[state2 - 1
													+ currentStateNo];
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

				}

				/*
				 * UPDATE OF CATEGORIES WITHOUT DURATION
				 */

				/* for categories without duration */
				else {
					int currentStateNo = 0;

					double survivalFraction = OtherMortalitySurvival[ageValue][sexValue][riskFactorValue];

					float[][] currentTransMat;
					for (int c = 0; c < nCluster; c++) {
						double survival = 0;
						currentTransMat = transMat[ageValue][sexValue][riskFactorValue][c];
						if (numberOfDiseasesInCluster[c] == 1) {

							/*
							 * for a single disease we do not need the do loop
							 * (although it would work)
							 */

							survival = (1 - oldValue[currentStateNo])
									* (currentTransMat[1][0] + currentTransMat[0][0])
									+ oldValue[currentStateNo]
									* (currentTransMat[1][1]);

							newValue[currentStateNo] = (float) (((1 - oldValue[currentStateNo])
									* currentTransMat[1][0] + oldValue[currentStateNo]
									* currentTransMat[1][1]) / survival);

							survivalFraction *= survival;
							currentStateNo++;
						} else if (withCuredFraction[c]) {

							/*
							 * as some elements are zero here, this is faster
							 * than using the more general implementation for
							 * clusterdiseases
							 */

							survival = (1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
									* (currentTransMat[2][0]
											+ currentTransMat[1][0] + currentTransMat[0][0])

									+ oldValue[currentStateNo]
									* (currentTransMat[1][1])

									+ oldValue[currentStateNo + 1]
									* (currentTransMat[2][2]);

							newValue[currentStateNo] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
									* currentTransMat[1][0] + oldValue[currentStateNo]
									* currentTransMat[1][1]) / survival);
							newValue[currentStateNo + 1] = (float) (((1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1])
									* currentTransMat[2][0] + oldValue[currentStateNo + 1]
									* currentTransMat[2][2]) / survival);
							/*
							 * NB disease with cured fraction can not be fatal
							 * at the same time
							 */
							currentStateNo++;
							currentStateNo++;
							survivalFraction *= survival;
						} else {
							/* cluster of dependent diseases */
							/*
							 * but this would work also for the previous cases
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
							 * NB the unconditional new state starts at 0 with
							 * the healthy state, so oldvalue[1] belongs with
							 * unconditionalnewstate[0]
							 */

							for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
							{ /* transitionProbabilities are [to][from] */
								unconditionalNewValues[state1] = currentTransMat[state1][0]
										* currentHealthyState;
								for (int state2 = 1; state2 < nCombinations[c]; state2++)
									// column=from

									unconditionalNewValues[state1] += currentTransMat[state1][state2]
											* oldValue[state2 - 1
													+ currentStateNo];
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

				} // end of update for not duration class
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
	 * this method fills the transition rate matrix for a riskfactor-category
	 * with duration classes
	 * 
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
			incidence = calculateIncidence(riskDurationValue, ageValue,
					sexValue, d);
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
			for (int fataldisnr = 0; fataldisnr < disFatalIndex[ageValue][sexValue][c].length; fataldisnr++) {
				incidence = calculateFatalIncidence(riskDurationValue,
						ageValue, sexValue,
						disFatalIndex[ageValue][sexValue][c][fataldisnr]
								+ clusterStartsAtDiseaseNumber[c]);
				for (int loc = 0; loc < nCombinations[c]; loc++) {

					rateMatrix[loc][loc] += -incidence
							* RRdisFatal[ageValue][sexValue][c][fataldisnr][loc];
					;

				} // end loop over locations
			} // end loop over fatal diseases
		return rateMatrix;
	}

	/**
	 * this method fills the transition rate matrix for a riskfactor-category
	 * WITHOUT duration classes
	 * 
	 * @param ageValue
	 * @param sexValue
	 * @param riskDurationValue
	 * @param c
	 * @return
	 */
	private double[][] fillRateMatrixForCluster(int ageValue, int sexValue,
			int riskCat, int c) {
		int d;
		double incidence;
		double atMort;
		/* make transition rate matrix */
		double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];
		for (int dc = 0; dc < numberOfDiseasesInCluster[c]; dc++) {
			d = clusterStartsAtDiseaseNumber[c] + dc;
			incidence = calculateIncidence(riskCat, ageValue, sexValue, d);
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
			for (int fataldisnr = 0; fataldisnr < disFatalIndex[ageValue][sexValue][c].length; fataldisnr++) {
				incidence = calculateFatalIncidence(riskCat, ageValue,
						sexValue,
						disFatalIndex[ageValue][sexValue][c][fataldisnr]
								+ clusterStartsAtDiseaseNumber[c]);
				for (int loc = 0; loc < nCombinations[c]; loc++) {

					rateMatrix[loc][loc] += -incidence
							* RRdisFatal[ageValue][sexValue][c][fataldisnr][loc];
					;

				} // end loop over locations
			} // end loop over fatal diseases
		return rateMatrix;
	}

	/**
	 * this method fills the incidence in states matrix for a riskfactor-category
	 * WITHOUT duration classes
	 * 
	 * @param ageValue
	 * @param sexValue
	 * @param riskDurationValue
	 * @param c
	 * @return
	 */
	private float[][] fillIncidenceForCluster(int ageValue, int sexValue,
			int riskCat, int c) {

		/* make transition rate matrix */
		float[][] incidenceMatrix = new float[this.numberOfDiseasesInCluster[c]][nCombinations[c]];

		for (int row = 0; row < nCombinations[c]; row++)

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
				

				if ((row & (1 << d)) != (1 << d))

				/*
				 * d is 0, thus incidence should be added
				 */
				{
					double RR = 1;
					for (int dCause = 0; dCause < getNDiseases(); dCause++)
						if ((row & (1 << dCause)) == (1 << dCause))
							RR *= relativeRiskDiseaseOnDisease[c][ageValue][sexValue][dCause][d];

					
						incidenceMatrix[d][row] = (float) (RR* calculateIncidence(
								riskCat, ageValue, sexValue,
								clusterStartsAtDiseaseNumber[c] + d));
					// or d=1, then atmort
					// should be added
				}
			}
		}

		/*
		 * first add fatal incidence irrespective of value of d
		 */
		

		return incidenceMatrix;
	}

	/**
	 * this method fills the Fatal Incidence in States matrix for a
	 * riskfactor-category WITHOUT duration classes
	 * 
	 * @param ageValue
	 * @param sexValue
	 * @param riskDurationValue
	 * @param c
	 * @return
	 */
	private float[][] fillFatalIncidenceForCluster(int ageValue, int sexValue,
			int riskCat, int c) {

		/* make transition rate matrix */
		float[][] incidenceMatrix = new float[this.numberOfDiseasesInCluster[c]][nCombinations[c]];

		for (int row = 0; row < nCombinations[c]; row++)

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
					if ((row & (1 << dCause)) == (1 << dCause))
						RR *= this.relativeRiskDiseaseOnDisease[c][ageValue][sexValue][dCause][d];
				}

				incidenceMatrix[d][row] = (float) (RR
						* calculateFatalIncidence(riskCat, ageValue, sexValue,
								clusterStartsAtDiseaseNumber[c] + d));

			}
		}

	

		return incidenceMatrix;
	}

	/**
	 * Calculates the Incidence for riskfactor with durationValue equal to
	 * riskDurationValue
	 * 
	 * @param riskDurationValue
	 * @param ageValue
	 * @param sexValue
	 * @param diseaseNumber
	 * @return
	 */
	private double calculateIncidence(float riskDurationValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
				* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
						* Math.exp(-riskDurationValue
								* alphaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);

		return incidence;
	}

	/**
	 * Calculates the Incidence for riskfactor with Value equal to riskCat
	 * (where this is not the category with the duration
	 * 
	 * @param riskCat
	 * @param ageValue
	 * @param sexValue
	 * @param diseaseNumber
	 * @return
	 */
	private double calculateIncidence(int riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;

		incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
				* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		return incidence;
	}

	/**
	 * Calculates the FATAL Incidence for riskfactor with duration state, for
	 * the duration state being equal to riskDuration value
	 * 
	 * @param riskDurationValue
	 * @param ageValue
	 * @param sexValue
	 * @param diseaseNumber
	 * @return
	 */
	private double calculateFatalIncidence(float riskDurationValue,
			int ageValue, int sexValue, int diseaseNumber) {
		double incidence = 0;
		incidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
				* ((relRiskBegin[diseaseNumber][ageValue][sexValue] - relRiskEnd[diseaseNumber][ageValue][sexValue])
						* Math.exp(-riskDurationValue
								* alphaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);

		return incidence;
	}

	/**
	 * Calculates the FATAL Incidence for riskfactor with Value equal to riskCat
	 * (where this is not the category with the duration
	 * 
	 * @param riskFactorValue
	 * @param ageValue
	 * @param sexValue
	 * @param diseaseNumber
	 * @return
	 */
	private double calculateFatalIncidence(int riskFactorValue, int ageValue,
			int sexValue, int diseaseNumber) {
		double incidence = 0;
		incidence = baselineFatalIncidence[diseaseNumber][ageValue][sexValue]
				* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];

		return incidence;
	}

	private double calculateOtherCauseSurvival(float riskDurationValue,
			int ageValue, int sexValue) {
		double otherCauseSurvival = 0;

		otherCauseSurvival = Math
				.exp((-baselineOtherMort[ageValue][sexValue] * ((relRiskOtherMortBegin[ageValue][sexValue] - relRiskOtherMortEnd[ageValue][sexValue])

						* Math.exp(

						-alphaDuurOtherMort[ageValue][sexValue]
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
			/*
			 * XMLConfiguration configurationFileConfiguration = new
			 * XMLConfiguration( configurationFile); OUD vervangen door volgende
			 * regels
			 */

			XMLConfiguration configurationFileConfiguration = new XMLConfiguration();
			configurationFileConfiguration.setDelimiterParsingDisabled(true);
			configurationFileConfiguration.load(configurationFile);

			// Validate the xml by xsd schema
			// TODO put schema's in again but these do not work
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			// configurationFileConfiguration.clear();

			// Validate the xml by xsd schema
			// configurationFileConfiguration.setValidating(true);
			// configurationFileConfiguration.load();

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
			/* make the arrays necessary for the transition rate matrixes */
			/*
			 * for the duration class they are indicator arrays that will be
			 * used to calculate the rate matrix later
			 */
			/*
			 * for the other classes the rate matrix is made, its exponential is
			 * taken which yeilds the transition probabilities matrix, and this
			 * is stored for later use
			 */
			atIndex = new int[nCluster][][];
			incIndex = new int[nCluster][][];
			incRowIndex = new int[nCluster][][];
			RRdis = new float[96][2][nCluster][][];
			RRdisFatal = new float[96][2][nCluster][][];
			disFatalIndex = new int[96][2][nCluster][];
			nonCuredRatio = new float[96][2][nCluster];
			incidenceInState = new float[96][2][nCat][nCluster][][];
			fatalIncidenceInState = new float[96][2][nCat][nCluster][][];

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

			for (int c = 0; c < nCluster; c++) {
				if (numberOfDiseasesInCluster[c] == 1) {

				} else if (withCuredFraction[c]) {

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
															 * index 3+4:
															 * riskfactor,
															 * cluster
															 */

			for (int a = 0; a < 96; a++) {
				for (int g = 0; g < 2; g++) {
					double[] attrMort = getAttributableMortality(a, g);
					for (int r = 0; r < this.nCat; r++) {
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
									incidenceInState[a][g][r][c]= new float [1][1];
									fatalIncidenceInState[a][g][r][c]= new float [1][1];
									incidenceInState[a][g][r][c][0][0] = (float) incidence;
									fatalIncidenceInState[a][g][r][c][0][0] = (float) fatalIncidence;

									if (Math.abs(incidence - attrMort[d]) > 1E-15)
										transMat[a][g][r][c][1][0] = (float) ((Math
												.exp(-incidence
														- fatalIncidence) - Math
												.exp(-attrMort[d]
														- fatalIncidence))
												* incidence / (attrMort[d] - incidence));
									else
										/*
										 * if incidence equal to attributable
										 * mortality, the denominator becomes
										 * zero and we need another formula
										 */
										transMat[a][g][r][c][1][0] = (float) (Math
												.exp(-incidence
														- fatalIncidence) * incidence);

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
									 * 
									 * This has not be consistently done through
									 * the code
									 * 
									 * in the similar code for the categorical risk factor this is included
								
								
								
									 * 
									 * 
									 */
									d = clusterStartsAtDiseaseNumber[c];
									incidence = calculateIncidence(r, a, g, d);
									incidence2 = calculateIncidence(r, a, g,
											d + 1)
											+ incidence;
									transMat[a][g][r][c] = new float[3][3];
									incidenceInState[a][g][r][c] = new float[2][3];
									fatalIncidenceInState[a][g][r][c] = new float[2][3];

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
									// incidence 2 is the total incidence, not that of disease 2
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

									incidenceInState[a][g][r][c][0][0] = (float) incidence;
									incidenceInState[a][g][r][c][0][1] = 0;
									incidenceInState[a][g][r][c][0][2] = 0;
									// incidence 2 is the total incidence, not that of disease 2
									incidenceInState[a][g][r][c][1][0] = (float) (incidence2-incidence);
									incidenceInState[a][g][r][c][1][1] = 0;
									incidenceInState[a][g][r][c][1][2] = 0;
									/*
									 * no fatal incidence together with cured
									 * fraction, so it is 0
									 */
									fatalIncidenceInState[a][g][r][c][0][0] = 0;

								} else // cluster of dependent diseases

								{

									transMat[a][g][r][c] = new float[nCombinations[c]][nCombinations[c]];
									incidenceInState[a][g][r][c] = fillIncidenceForCluster(
											a, g, r, c);;
									fatalIncidenceInState[a][g][r][c] = fillFatalIncidenceForCluster(
											a, g, r, c);

									/* make transition rate matrix */
									double[][] rateMatrix = fillRateMatrixForCluster(
											a, g, r, c);
									

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

								}
							} // end loop over clusters

						}
					}
				}
			} // end loop over age
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
		success = false;
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
	 *            : risk duration  value (float)
	 * @param c
	 *            : cluster number
	 * @return 3-dimensional array with indexes: indicator (0=incidence, 1=fatal
	 *         incidence) ; disease (number within cluster) ; state
	 */
	private double[][][] calculateIncidenceInState(float r, int a, int g, int c) {
		/* copied from HealthStateContMany.... */
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
