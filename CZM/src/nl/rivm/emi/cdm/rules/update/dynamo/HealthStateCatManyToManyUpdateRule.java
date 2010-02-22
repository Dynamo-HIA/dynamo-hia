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

				newValue = new float[oldValue.length];
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
				double survival = 0;
				double survivalFraction = OtherMortalitySurvival[ageValue][sexValue][riskFactorValue];

				float[][] currentTransMat;
				for (int c = 0; c < nCluster; c++) {
					currentTransMat = transMat[ageValue][sexValue][riskFactorValue][c];
					
					/* update single diseases */
					
					if (numberOfDiseasesInCluster[c] == 1) {

						int d = clusterStartsAtDiseaseNumber[c];

						survival = (1 - oldValue[currentStateNo])
								* (currentTransMat[0][0]+currentTransMat[1][0])
								+ oldValue[currentStateNo]
								* ( currentTransMat[1][1]);

						newValue[currentStateNo] = (float) (  (
								 (1 - oldValue[currentStateNo])	* currentTransMat[1][0] 
								      + oldValue[currentStateNo]* currentTransMat[1][1])
								      / survival);

						survivalFraction *= survival;
						currentStateNo++;
						/* update diseases with cured fraction */
					} else if (withCuredFraction[c]) {
						double currentHealthyState = (1 - oldValue[currentStateNo] - oldValue[currentStateNo + 1]);
						survival = currentHealthyState
								* (currentTransMat[0][0]
										+ currentTransMat[1][0] + currentTransMat[2][0])
								+ oldValue[currentStateNo]
								* currentTransMat[1][1]
								+ oldValue[currentStateNo + 1]
								* currentTransMat[2][2];
						double unconditionalNewValues0 = currentTransMat[1][0]
								* currentHealthyState
								+ oldValue[currentStateNo]
								* currentTransMat[1][1];
						double unconditionalNewValues1 = currentTransMat[2][0]
								* currentHealthyState
								+ oldValue[currentStateNo + 1]
								* currentTransMat[2][2];
						newValue[currentStateNo] = (float) (unconditionalNewValues0 / survival);
						newValue[currentStateNo + 1] = (float) (unconditionalNewValues1 / survival);

						survivalFraction *= survival;
						currentStateNo++;
						currentStateNo++;

					} /* update cluster diseases */
					else {

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
							unconditionalNewValues[state1] = currentTransMat[state1][0]
									* currentHealthyState;
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += currentTransMat[state1][state2]
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
			
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);


			/**
			TODO: VALIDATION IS FOR FUTURE USE 
			NICE TO HAVE FEATURE
			KEEP IT IN THE CODE
			The following schemas are not be validated:
			updateRuleConfiguration.xsd
			
			*/
			if (!"updateRuleConfiguration".equals(configurationFileConfiguration.getRootElementName())) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
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
								int d = clusterStartsAtDiseaseNumber[c];
								transMat[a][g][r][c][0][0] = (float) Math
										.exp(-incidence[d] - fatalIncidence[d]);
								transMat[a][g][r][c][0][1] = 0;
								transMat[a][g][r][c][1][1] = (float) Math
										.exp(-atMort[d] - fatalIncidence[d]);

								if (Math.abs(incidence[d] - atMort[d]) > 1E-15)
									transMat[a][g][r][c][1][0] = (float) (Math
											.exp( - fatalIncidence[d])
											* incidence[d]
											* (Math.exp(- atMort[d]) - Math
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
								 * zeroth disease state=healthy 
								 * first disease state = cured disease (d) 
								 * second disease state = not cured disease (d+1)
								 * 
								 */

								/*
								 * Officially it is not possible to have both
								 * fatal incidences >0 and with cured fraction,
								 * However, fatal incidence is also included here in the formulae
								 * for possibly future use, under the assumption
								 * that the incidence of fatal disease is the same in 
								 * healthy, with cured and with non-cured disease
								 * This implies that all transition rates are 
								 * multiplied with a term:
								 *     Math.exp( - fatalIncidence[d])
								 * where we assume the fatalincidence to independent of having both
								 * the cured and uncured disease
								 */
								transMat[a][g][r][c] = new float[3][3];
								int d = clusterStartsAtDiseaseNumber[c];
								transMat[a][g][r][c][0][0] = (float) Math
										.exp(-incidence[d] - incidence[d + 1]- fatalIncidence[d]);
								transMat[a][g][r][c][0][1] = 0;
								transMat[a][g][r][c][0][2] = 0;
								if ((incidence[d] + incidence[d + 1]) == 0)
									transMat[a][g][r][c][1][0] = 0;
								else
									transMat[a][g][r][c][1][0] = (float) (Math
									.exp( - fatalIncidence[d])* (1 - Math
											.exp(-incidence[d]
													- incidence[d + 1]))
											* incidence[d] / (incidence[d] + incidence[d + 1]));
								transMat[a][g][r][c][1][1] = (float) Math
								.exp( - fatalIncidence[d]);
								transMat[a][g][r][c][1][2] = 0;
								if (incidence[d] + incidence[d + 1] == atMort[d + 1])
									transMat[a][g][r][c][2][0] = (float) (Math
											.exp(-incidence[d]
													- incidence[d + 1] - fatalIncidence[d]) * incidence[d + 1]);
								else
									transMat[a][g][r][c][2][0] = (float) (((Math
											.exp(-atMort[d + 1]) - Math
											.exp(-incidence[d]
													- incidence[d + 1])) * incidence[d + 1])* Math
													.exp( - fatalIncidence[d])/ (incidence[d]
											+ incidence[d + 1] - atMort[d + 1]));
								transMat[a][g][r][c][2][1] = 0;
								transMat[a][g][r][c][2][2] = (float) Math
										.exp(-atMort[d + 1]-fatalIncidence[d]);
								currentStateNo += 2;

							} else // cluster of dependent diseases
							{
								double[][] rateMatrix = new double[nCombinations[c]][nCombinations[c]];

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
												for (int dCause = 0; dCause < getNDiseases(); dCause++)
													/* if dCause==1 in row=column) */
													if ((row & (1 << dCause)) == (1 << dCause))
														RR *= this.relativeRiskDiseaseOnDisease[c][a][g][dCause][d];

												rateMatrix[row][column] -= (RR * fatalIncidence[clusterStartsAtDiseaseNumber[c]
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

								transMat[a][g][r][c] = matExp
										.exponentiateFloatMatrix(rateMatrix);

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
					+ this.nDiseasesLabel), configurationFile.getAbsolutePath());
		} catch (DynamoUpdateRuleConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e, 
					configurationFile.getAbsolutePath());
		}
		return success;
	}


	
	
}
