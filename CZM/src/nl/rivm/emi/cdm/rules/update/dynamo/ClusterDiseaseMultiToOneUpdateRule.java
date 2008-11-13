package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClusterDiseaseMultiToOneUpdateRule extends
		DynamoManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = { "updateRuleConfiguration", "age", "sex", "charID" };
	int nDiseases;

	float relRiskContinous[][][] = null;
	float referenceValueContinous;
	float relRiskCategorical[][][][] = null;
	float relRiskEnd[][][] = null;
	float relRiskBegin[][][] = null;
	float alfaDuur[][][] = null;
	float attributableMortality[][][] = null;
	
	float baselineIncidence[][][] = null;
	float relRiskDiseaseOnDisease[][][][] = null;
	
	int startIndex = -1;

	String relRiskCatFileName = null;
	String relRiskContFileName = null;
	String relRiskEndFileName = null;
	String relRiskBeginFileName = null;
	String alfaDuurFileName = null;
	String attributableMortalityFileName = null;
	String baselineIncidenceFileName = null;

	private Random randomGenerator = null;

	public ClusterDiseaseMultiToOneUpdateRule(String configFileName)
			throws ConfigurationException {
		// constructor fills the parameters with data
		// filenames of the parameter files are given in the file name with
		// configFileName
		relRiskCatFileName = null; // ConfigurationFactory.getRiskCatFileName(configFileName);
		relRiskContFileName = null;
		relRiskEndFileName = null;
		relRiskBeginFileName = null;
		alfaDuurFileName = null;
		attributableMortalityFileName = null;
		baselineIncidenceFileName = null;

		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
		if (!success)
			throw new ConfigurationException(
					"loading of configuration file failed for updateRule ClusterDiseaseMultiToOneUpdateRule");

	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		double timestep = 1;

		try {
			int ageValue = (int) getFloat(currentValues, ageIndex);
			if (ageValue>95) ageValue=95;
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

			float[] atMort = attributableMortality[ageValue][sexValue];
			// other mort is not needed!!
			
			double[] incidence = calculateIncidence(currentValues, ageValue,
					sexValue);
			// make transitionrate matrix

			// plus 1 for total matrix = including state of dead
			double[][] rateMatrix = new double[nCombinations ][nCombinations ];
			/*
			 * first= changed state (row) second :sources of
			 * change(change=number in second state entry in matrix
			 */
			Arrays.fill(rateMatrix, 0);
			for (int row = 0; row < nCombinations; row++)
				for (int column = 0; column < nCombinations; column++)
					/*
					 * Matrix entry from each disease is formed as:
					 * 
					 * if value first=0 and value second=1: incidence second
					 * product of RRdiseaseOnDisease over other(=all) diseases
					 * that are 1 on second disease if value first=0 and
					 * first=second: - sum incidence to all other diseases
					 * (including RR's as above plus mortality due to cluster diseases: 
					 * - attributable Mortality for each disease that is 1 in
					 
					 */

					if (row == column)
					/*
					 * Matrix entry is formed as: / - all mortality: - otherMort
					 * - attributable Mortality for each disease that is 1 in
					 * combi
					 * 
					 * - sum incidence to all other disease that are 0 in
					 * combi(including RR's as above
					 */
					{
						
						for (int d = 0; d < nDiseases; d++)
							if ((row & (1 << d)) != (1 << d)) {
								double RR = 1;
								for (int dCause = 0; dCause < nDiseases; dCause++)
									if ((row & (1 << dCause)) == (1 << dCause))
										RR *= relRiskDiseaseOnDisease[ageIndex][sexIndex][dCause][d];

								rateMatrix[row][column] -= timestep
										* (RR * incidence[d] + attributableMortality[ageIndex][sexIndex][d]);
							}
					} else
						for (int d1 = 0; d1 < nDiseases; d1++)
							for (int d2 = 0; d2 < nDiseases; d2++)
								if (((row & (1 << d1)) == (1 << d1))
										&& ((column & (1 << d2)) != (1 << d2))) {
									double RR = 1;
									for (int dCause = 0; dCause < nDiseases; dCause++)
										if ((column & (1 << dCause)) == (1 << dCause))
											RR *= relRiskDiseaseOnDisease[ageIndex][sexIndex][dCause][d1];

									rateMatrix[row][column] += timestep
											* (RR * incidence[d1]);
								}
			;
			

			/* Exponentiale the matrix */

			MatrixExponential matExp = MatrixExponential.getInstance();
			double[][] TransitionProbabilities = new double[nCombinations ][nCombinations];
			TransitionProbabilities = matExp.exponentiateMatrix(rateMatrix);

			/* Multiply the matrix with the old values (column vector) */
			double unconditionalNewValues[] = new double[nCombinations ];

			for (int state1 = 0; state1 < nCombinations ; state1++) // row
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
			double survival=0;
			for (int state = 0; state < nCombinations; state++){survival+=unconditionalNewValues[state];}
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
		double[] incidence = null;
		Arrays.fill(incidence, -1);
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			for (int d = 0; d < nDiseases; d++)
				incidence[d] = baselineIncidence[ageValue][sexValue][d]
						* relRiskCategorical[ageValue][sexValue][riskFactorValue][d];

		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			for (int d = 0; d < nDiseases; d++)
				incidence[d] = baselineIncidence[ageValue][sexValue][d]
						* Math.pow((riskFactorValue - referenceValueContinous),
								relRiskContinous[ageValue][sexValue][d]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);
				for (int d = 0; d < nDiseases; d++)
					incidence[d] = baselineIncidence[ageValue][sexValue][d]
							* ((relRiskBegin[ageValue][sexValue][d] - relRiskEnd[ageValue][sexValue][d])
									* Math.exp(-riskDurationValue
											* alfaDuur[ageValue][sexValue][d]) + relRiskEnd[ageValue][sexValue][d]);

			} else
				for (int d = 0; d < nDiseases; d++)
					incidence[d] = baselineIncidence[ageValue][sexValue][d]
							* relRiskCategorical[ageValue][sexValue][riskFactorValue][d];
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
		double diseaseValues[] = null;
		Arrays.fill(diseaseValues, -1);
		for (int d = 0; d < Ntot; d++)
			diseaseValues[d] = (double) getFloat(currentValues, Nstart + d);
		return diseaseValues;
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return false;
	}
}
