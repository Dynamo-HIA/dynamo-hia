package nl.rivm.emi.dynamo.ui.panels.output;

import java.io.Serializable;

import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

/**
 * @author boshuizh ScenarioParameters contains the parameters of the scenario
 *         that can still be change after running the simulation: succesrate,
 *         minimum and maximum age of the intervention and whether it is applied
 *         to men or women or both
 */
public class ScenarioParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean[] inMen;

	private boolean[] inWomen;

	private double[] succesrate;

	private int[] minAge;

	private int[] maxAge;

	/**
	 * ScenarioParameters contains the parameters of the scenario that can still
	 * be change after running the simulation: succesrate, minimum and maximum
	 * age of the intervention and whether it is applied to men or women or
	 * both. It is initialized using the object that contains the information
	 * given to the simulation by the user before running the simulation
	 * 
	 * @param scenInfo
	 *            object that contains the information given to the simulation
	 *            by the user before running the simulation
	 * 
	 */
	public ScenarioParameters(ScenarioInfo scenInfo) {
		super();
		this.inMen = scenInfo.getInMen();
		this.inWomen = scenInfo.getInWomen();
		this.maxAge = scenInfo.getMaxAge();
		this.minAge = scenInfo.getMinAge();
		/* in scenInfo the succes rate is in percentage */
		/* in this object it is as fraction (between 0 and 1) */
		/* in the output_screen again it is in percentage */
		this.setSuccesPercentage(scenInfo.getDoubleSuccesrate());

		/*
		 * scenInfo contains the information given to the simulation by the user
		 * before running the simulation
		 * 
		 * This is used for initialisation of the parameters
		 */

	}

	/**
	 * applies succesrates for men and women together.
	 * 
	 * @param inputRef
	 *            : array[2] with data for reference scenario for men and women
	 * @param inputRef
	 *            : array[2] with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied This should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	public double applySuccesrateToBothGenders(double[] inputRef,
			double[] inputScen, int thisScen, int year, int a) {
		double data = 0.0;

		if (thisScen == 0)

			for (int i = 0; i < 2; i++)
				data += inputRef[i];
		else if (a >= year) /* if not newborns */{
			if (this.getMinAge(thisScen - 1) > a - year
					|| this.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.getInMen(thisScen - 1))
					data += (1 - this.getSuccesrate(thisScen - 1))
							* inputRef[0] + (this.getSuccesrate(thisScen - 1))
							* inputScen[0];
				else
					data += inputRef[0];
				if (this.getInWomen(thisScen - 1))
					data += (1 - this.getSuccesrate(thisScen - 1))
							* inputRef[1] + (this.getSuccesrate(thisScen - 1))
							* inputScen[1];
				else
					data += inputRef[1];
			}
		} else /* if newborns */
		{
			if (this.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.getInMen()[thisScen - 1])
					data += (1 - this.getSuccesrate()[thisScen - 1])
							* inputRef[0]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[0];
				else
					data += inputRef[0];
				if (this.getInWomen()[thisScen - 1])
					data += (1 - this.getSuccesrate()[thisScen - 1])
							* inputRef[1]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[1];
				else
					data += inputRef[1];
			}
		}
		;
		return data;
	}
	
	/**
	 * applies the succesrate, minimum target age, maximum target age and target
	 * gender (todo) to the
	 * 
	 * @param inputRef
	 *            : value with data for reference scenario
	 * @param inputRef
	 *            : value with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied. This should be zero for "ori"
	 *            arrays
	 * @param a
	 *            : age: the current age of the cohort, or the age at start of
	 *            simulation (for "ori" arrays). In the last case, year above
	 *            should be zero
	 * @param gender
	 *            : gender (0 or 1 for men or women)
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * */

	public double applySuccesrate(double inputRef, double inputScen,
			int thisScen, int year, int a, int gender) {
		double data = 0.0;
		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.getInMen()[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.getInWomen()[thisScen - 1])
				doApply = false;

		}
		if (!doApply)
			data = inputRef;
		/* if not newborns */
		else if (a - year >= 0) {
			if (this.getMinAge(thisScen - 1) > a - year
					|| this.getMaxAge(thisScen - 1) < a - year)
				data = inputRef;
			else
				data = (1 - this.getSuccesrate()[thisScen - 1]) * inputRef
						+ (this.getSuccesrate()[thisScen - 1]) * inputScen;
		} else {
			if (this.getMinAge()[thisScen - 1] > 0)
				data = inputRef;
			else
				data = (1 - this.getSuccesrate()[thisScen - 1]) * inputRef
						+ (this.getSuccesrate()[thisScen - 1]) * inputScen;
		}
		;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome array after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, inMen and
	 * inWomen, so changing these fields will give different outputs If
	 * gender=2, the array should be of dimension [2], and inMen and InWomen are
	 * to applied
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario: all data are for the
	 *            same age and year
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario:
	 *            all data are for the same age and year
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step). Should be zero for the "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * @param gender
	 *            : gender: 0=men, 1=women, 2=both, inplying that the
	 *            application of succesrates to gender already took place
	 *            earlier, and only averaging over the current array is needed
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * @throws DynamoOutputException
	 */
	public double applySuccesrateToMean(double[] inputRef, double[] inputScen,
			double[] nInRef, double[] nInScen, int thisScen, int year, int a,
			int gender) throws DynamoOutputException {

		double data = 0.0;
		if (gender == 2) {
			data = applySuccesrateToMeanToBothGenders(inputRef, inputScen,
					nInRef, nInScen, thisScen, year, a);

		} else {

			double denominator = 0;
			double nominator = 0;
			int nToAdd = inputRef.length;
			boolean doApply = true;
			if (thisScen == 0)
				doApply = false;
			else {
				if (gender == 0 && !this.getInMen(thisScen - 1))
					doApply = false;
				if (gender == 1 && !this.getInWomen(thisScen - 1))
					doApply = false;

			}
			if (!doApply)
				for (int i = 0; i < nToAdd; i++) {
					if (nInRef[i] != 0)
						nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else if (a - year >= 0) /* if not newborns */{
				if (this.getMinAge()[thisScen - 1] > a - year
						|| this.getMaxAge(thisScen - 1) < a - year)
					for (int i = 0; i < nToAdd; i++) {
						if (nInRef[i] != 0)
							nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						if ((nInRef[i]
										* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
										&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i]) != 0))
							nominator += (1 - this.getSuccesrate()[thisScen - 1])
									* inputRef[i]
									* nInRef[i]
									+ (this.getSuccesrate()[thisScen - 1])
									* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i];
					}
				}
			} else /* if newborns */
			{
				if (this.getMinAge()[thisScen - 1] > 0)
					for (int i = 0; i < nToAdd; i++) {
						if (nInRef[i] != 0)
							nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						if ((nInRef[i]
										* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
										&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i]) != 0))
							nominator += (1 - this.getSuccesrate()[thisScen - 1])
									* inputRef[i]
									* nInRef[i]
									+ (this.getSuccesrate()[thisScen - 1])
									* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i];
					}
				}
			}
			;
			if (denominator != 0)
				data = nominator / denominator;
			else
				data = -99999;
		}
		return data;
	}

	/**
	 * Applies success rate to the input arrays containing both data of men and
	 * of women, and return a mean for both sexes together
	 * 
	 * @param inputRef
	 * @param inputScen
	 * @param nInRef
	 * @param nInScen
	 * @param thisScen
	 * @param year: years after the age to which apply the selections. Should be zero for "ori" arrays 
	 * @param a the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * @return
	 */
	public double applySuccesrateToMean(double[][] inputRef,
			double[][] inputScen, double[][] nInRef, double[][] nInScen,
			int thisScen, int year, int a) throws DynamoOutputException {

		double data = 0.0;

		double denominator = 0;
		double nominator = 0;
		int nToAdd = inputRef.length;
		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;

		if (!doApply)
			for (int i = 0; i < nToAdd; i++)
				for (int s = 0; s < 2; s++) {
					if (nInRef[i][s] != 0)
						nominator += inputRef[i][s] * nInRef[i][s];
					denominator += nInRef[i][s];
				}
		else if (a - year >= 0) /* if not newborns */{
			for (int s = 0; s < 2; s++) {
				boolean inThisGender = false;
				if (s == 0 && this.inMen[thisScen - 1])
					inThisGender = true;
				if (s == 1 && this.inWomen[thisScen - 1])
					inThisGender = true;
				if (this.getMinAge()[thisScen - 1] > a - year
						|| this.getMaxAge(thisScen - 1) < a - year
						|| !inThisGender)
					for (int i = 0; i < nToAdd; i++) {
						if (nInRef[i][s] != 0)
							nominator += inputRef[i][s] * nInRef[i][s];
						denominator += nInRef[i][s];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						if ((nInRef[i][s]
										* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
										&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][s]) != 0))
							nominator += (1 - this.getSuccesrate()[thisScen - 1])
									* inputRef[i][s]
									* nInRef[i][s]
									+ (this.getSuccesrate()[thisScen - 1])
									* inputScen[i][s] * nInScen[i][s];
						;
						denominator += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][s]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][s];
					}
				}
			}
		} else /* if newborns */
		{
			for (int s = 0; s < 2; s++) {
				boolean inThisGender = false;
				if (s == 0 && this.inMen[thisScen - 1])
					inThisGender = true;
				if (s == 1 && this.inWomen[thisScen - 1])
					inThisGender = true;
				if (this.getMinAge()[thisScen - 1] > 0 || !inThisGender)
					for (int i = 0; i < nToAdd; i++) {
						if (nInRef[i][s] != 0) nominator += inputRef[i][s] * nInRef[i][s];
						denominator += nInRef[i][s];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						if ((nInRef[i][s]
								* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
								&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][s]) != 0))
							nominator += (1 - this.getSuccesrate()[thisScen - 1])
									* inputRef[i][s]
									* nInRef[i][s]
									+ (this.getSuccesrate()[thisScen - 1])
									* inputScen[i][s] * nInScen[i][s];
						;
						denominator += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][s]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][s];
					}
				}
			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;

		return data;
	}
	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates, using data from both sexes. As this needs to be a weighted
	 * mean (weighted by the number of persons) it also needs the weights
	 * (numbers in each scenario) This method uses the fields: successrate,
	 * minage and maxage, so changing these fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step) after the year for which to apply the selections. Should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	public double applySuccesrateToMeanToBothGenders(double[] inputRef,
			double[] inputScen, double[] nInRef, double[] nInScen,
			int thisScen, int year, int a) throws DynamoOutputException {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;
		if (inputRef.length != 2)
			throw new DynamoOutputException(
					" (development error code) gender array has length "
							+ inputRef.length + " in stead of 2");
		if (thisScen == 0)

			for (int i = 0; i < 2; i++) {
				if (nInRef[i] != 0) nominator += inputRef[i] * nInRef[i];
				denominator += nInRef[i];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.getMinAge(thisScen - 1) > a - year
					|| this.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < 2; i++) {
					if (nInRef[i] != 0)nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.getInMen(thisScen - 1)) {
					if ((nInRef[0]
									* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
									&& ((this.getSuccesrate()[thisScen - 1] * nInScen[0]) != 0))
					nominator += (1 - this.getSuccesrate(thisScen - 1))
							* inputRef[0] * nInRef[0]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[0] * nInScen[0];
					;
					denominator += (1 - this.getSuccesrate()[thisScen - 1])
							* nInRef[0] + (this.getSuccesrate()[thisScen - 1])
							* nInScen[0];
				} else {
					if (nInRef[0] != 0) nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.getInWomen()[thisScen - 1]) {
					if ((nInRef[1]
								* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
								&& ((this.getSuccesrate()[thisScen - 1] * nInScen[1]) != 0))
					nominator += (1 - this.getSuccesrate()[thisScen - 1])
							* inputRef[1] * nInRef[1]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[1] * nInScen[1];
					;
					denominator += (1 - this.getSuccesrate()[thisScen - 1])
							* nInRef[1] + (this.getSuccesrate()[thisScen - 1])
							* nInScen[1];
				} else {
					if (nInRef[1] != 0) nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}

			}
		} else /* if newborns */
		{
			if (this.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++) {
					if (nInRef[i] != 0) nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.getInMen()[thisScen - 1]) {
				
					if ((nInRef[0]
								* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
								&& ((this.getSuccesrate()[thisScen - 1] * nInScen[0]) != 0))
					nominator += (1 - this.getSuccesrate()[thisScen - 1])
							* inputRef[0] * nInRef[0]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[0] * nInScen[0];
					;
					denominator += (1 - this.getSuccesrate()[thisScen - 1])
							* nInRef[0] + (this.getSuccesrate()[thisScen - 1])
							* nInScen[0];
				} else {
					if (nInRef[0] != 0) nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.getInWomen()[thisScen - 1]) {
					if ((nInRef[1]
								* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
								&& ((this.getSuccesrate()[thisScen - 1] * nInScen[1]) != 0))
					nominator += (1 - this.getSuccesrate()[thisScen - 1])
							* inputRef[1] * nInRef[1]
							+ (this.getSuccesrate()[thisScen - 1])
							* inputScen[1] * nInScen[1];
					;
					denominator += (1 - this.getSuccesrate()[thisScen - 1])
							* nInRef[1] + (this.getSuccesrate()[thisScen - 1])
							* nInScen[1];
				} else {
					if (nInRef[1] != 0)nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}
			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * success rates, using data from both sexes. As this needs to be a weighted
	 * mean (weighted by the number of persons) it also needs the weights
	 * (numbers in each scenario) This method uses the fields: success rate,
	 * minage and maxage, so changing these fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario, with indexes i (to sum over) and sex
	 * @param inputRef
	 *            : array with data for 100% successful alternative scenario, with indexes i (to sum over) and sex
	 * @param nInRef: : array with numbers for reference scenario, with indexes i (to sum over) and sex
	 * @param nInScen : array with numbers for alternative scenario, with indexes i (to sum over) and sex
	 * @param thisScen
	 *            : number of scenario,
	 * @param year
	 *            : year (step) after the year for which to apply the selections. Should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * 
	 * 
	 * @return the result for a scenario to which the success rates and
	 *         min-maximum age have been applied
	 */
	
	public double applySuccesrateToMeanToBothGenders(double[][] inputRef,
			double[][] inputScen, double[][] nInRef, double[][] nInScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		double denominator0 = 0;
		double nominator0 = 0;
		double denominator1 = 0;
		double nominator1 = 0;
		int nToAdd = inputRef.length;

		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++) {
				if (nInRef[i][0]!=0) nominator0 += inputRef[i][0] * nInRef[i][0];
				denominator0 += nInRef[i][0];
				if (nInRef[i][1]!=0)nominator1 += inputRef[i][1] * nInRef[i][1];
				denominator1 += nInRef[i][1];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.getMinAge(thisScen - 1) > a - year
					|| this.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < nToAdd; i++) {
					if (nInRef[i][0]!=0) nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					if (nInRef[i][1]!=0) nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.getInMen()[thisScen - 1]) {
						if ((nInRef[i][0]
									* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
									&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][0]) != 0))
						nominator0 += (1 - this.getSuccesrate()[thisScen - 1])
								* inputRef[i][0] * nInRef[i][0]
								+ (this.getSuccesrate()[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][0]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][0];
					} else {
						if (nInRef[i][0]!=0) nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.getInWomen()[thisScen - 1]) {
						if ((nInRef[i][1]
									* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
									&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][1]) != 0))
						nominator1 += (1 - this.getSuccesrate()[thisScen - 1])
								* inputRef[i][1] * nInRef[i][1]
								+ (this.getSuccesrate()[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][1]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][1];
					}

					else {
						if (nInRef[i][1]!=0) nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		} else /* if newborns */
		{
			if (this.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < nToAdd; i++) {
					if (nInRef[i][0]!=0) nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					if (nInRef[i][1]!=0)  nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.getInMen()[thisScen - 1]) {
						if ((nInRef[i][0]
										* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
										&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][0]) != 0))
						nominator0 += (1 - this.getSuccesrate()[thisScen - 1])
								* inputRef[i][0] * nInRef[i][0]
								+ (this.getSuccesrate()[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][0]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][0];
					} else {
						if (nInRef[i][0]!=0) nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.getInWomen()[thisScen - 1]) {
						if ((nInRef[i][1]
										* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
										&& ((this.getSuccesrate()[thisScen - 1] * nInScen[i][1]) != 0)) nominator1 += (1 - this.getSuccesrate()[thisScen - 1])
								* inputRef[i][1] * nInRef[i][1]
								+ (this.getSuccesrate()[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.getSuccesrate()[thisScen - 1])
								* nInRef[i][1]
								+ (this.getSuccesrate()[thisScen - 1])
								* nInScen[i][1];
					} else {
						if (nInRef[i][1]!=0) nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		}
		;
		if (denominator0 + denominator1 != 0)
			data = (nominator0 + nominator1) / (denominator0 + denominator1);
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, so changing these
	 * fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): should be zero for the "ori" arrays
	 * @param a
	 *            : the age for which to return the result = the age in the input arrays = age at start for "ori" arrays
	 * @param gender
	 *            : gender
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	public double applySuccesrateToMean(double inputRef, double inputScen,
			double nInRef, double nInScen, int thisScen, int year, int a,
			int gender) {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;

		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.getInMen()[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.getInWomen()[thisScen - 1])
				doApply = false;

		}
		if (!doApply) {

			if (nInRef!=0) nominator += inputRef * nInRef;
			denominator += nInRef;
		} else if (a - year >= 0)/* if not newborns */{
			if (this.getMinAge(thisScen - 1) > a - year
					|| this.getMaxAge(thisScen - 1) < a - year) {
				if (nInRef!=0) nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {
				if ((nInRef
								* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
								&& ((this.getSuccesrate()[thisScen - 1] * nInScen) != 0))
				nominator += (1 - this.getSuccesrate(thisScen - 1)) * inputRef
						* nInRef + (this.getSuccesrate()[thisScen - 1])
						* inputScen * nInScen;
				;
				denominator += (1 - this.getSuccesrate()[thisScen - 1])
						* nInRef + (this.getSuccesrate()[thisScen - 1])
						* nInScen;

			}
		} else { /* for newborns */
			if (this.getMinAge()[thisScen - 1] > 0) {
				if (nInRef!=0)nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {
				if ((nInRef
						* (1 - this.getSuccesrate()[thisScen - 1]) != 0)
						&& ((this.getSuccesrate()[thisScen - 1] * nInScen) != 0))
				nominator += (1 - this.getSuccesrate()[thisScen - 1])
						* inputRef * nInRef
						+ (this.getSuccesrate()[thisScen - 1]) * inputScen
						* nInScen;
				;
				denominator += (1 - this.getSuccesrate()[thisScen - 1])
						* nInRef + (this.getSuccesrate()[thisScen - 1])
						* nInScen;

			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * @param scen
	 *            scenario number
	 * @return maximum age targetted by the intervention
	 */
	public int getMaxAge(int scen) {
		return this.maxAge[scen];
	}

	/**
	 * @param maxAge
	 *            maximum age targetted by the intervention
	 */
	public void setMaxAge(int[] maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @param maxAge
	 *            maximum age targetted by the intervention scen
	 * @param scen
	 *            scenario number
	 */
	public void setMaxAge(int maxAge, int scen) {
		this.maxAge[scen] = maxAge;
	}

	/**
	 * @return minimum age targetted by the intervention
	 */
	public int[] getMinAge() {
		return this.minAge;
	}

	/**
	 * @return minimum age targetted by the intervention
	 */
	public int getMinAge(int scen) {
		return this.minAge[scen];
	}

	/**
	 * @param minAge
	 *            , minimum age targetted by the intervention
	 */
	public void setMinAge(int[] minAge) {
		this.minAge = minAge;
	}

	/**
	 * @return boolean []: array giving whether the intervention is applied to
	 *         women (index: scenario)
	 */
	public boolean[] getInWomen() {
		return this.inWomen;
	}

	/**
	 * @param scen
	 *            number of scenario
	 * @return boolean : array giving whether the intervention is applied to
	 *         women in scenario scen
	 */
	public boolean getInWomen(int scen) {
		return this.inWomen[scen];
	}

	/**
	 * @param inWomen
	 */
	public void setInWomen(boolean[] inWomen) {
		this.inWomen = inWomen;
	}

	/**
	 * @param scen
	 *            : number of the scenario
	 * @param inWomen
	 */
	public void setInWomen(int scen, boolean inWomen) {
		this.inWomen[scen] = inWomen;
	}

	/**
	 * @return boolean []: array giving whether the intervention is applied to
	 *         men (index: scenario)
	 */
	public boolean[] getInMen() {
		return this.inMen;
	}

	/**
	 * @param scen
	 *            number of scenario
	 * @return boolean : array giving whether the intervention is applied to men
	 *         in scenario scen
	 */
	public boolean getInMen(int scen) {
		return this.inMen[scen];
	}

	/**
	 * @param inMen
	 */
	public void setInMen(boolean[] inMen) {
		this.inMen = inMen;
	}

	/**
	 * @param scen
	 *            : scenario number
	 * @param inMen
	 */
	public void setInMen(int scen, boolean inMen) {
		this.inMen[scen] = inMen;
	}

	public void setMinAge(int value, int i) {
		this.minAge[i] = value;
	}

	/**
	 * @return success percentage of intervention
	 */
	public double[] getSuccesPercentage() {
		double[] returnvalue = new double[this.succesrate.length];
		for (int i = 0; i < this.succesrate.length; i++)
			returnvalue[i] = this.succesrate[i] * 100;
		return returnvalue;
	}

	/**
	 * @return success percentage of intervention i
	 */
	public double getSuccesPercentage(int i) {
		double returnvalue;

		returnvalue = this.succesrate[i] * 100;
		return returnvalue;
	}

	/**
	 * @param success
	 *            percentage for all scenarios
	 */
	public void setSuccesPercentage(double[] succesrateIn) {

		this.succesrate = new double[succesrateIn.length];
		for (int i = 0; i < succesrateIn.length; i++)
			this.succesrate[i] = succesrateIn[i] / 100;

	}

	/**
	 * @param succes
	 *            Percentage of scenario i
	 * @param i
	 *            scenario number
	 */
	public void setSuccesPercentage(double succesrate, int i) {
		this.succesrate[i] = succesrate / 100;

	}

	/**
	 * @return succesrate of intervention
	 */
	public double[] getSuccesrate() {
		double[] returnvalue = new double[this.succesrate.length];
		for (int i = 0; i < this.succesrate.length; i++)
			returnvalue[i] = this.succesrate[i];
		return returnvalue;
	}

	/**
	 * @return succesrate of intervention i
	 */
	public double getSuccesrate(int i) {
		double returnvalue;

		returnvalue = this.succesrate[i];
		return returnvalue;
	}

	/**
	 * @param array
	 *            with succesrates of scenarios
	 */
	public void setSuccesrate(double[] succesrateIn) {

		this.succesrate = new double[succesrateIn.length];
		for (int i = 0; i < succesrateIn.length; i++)
			this.succesrate[i] = succesrateIn[i];

	}

	/**
	 * @param succesrate
	 *            of scenario i
	 * @param i
	 *            scenario number
	 */
	public void setSuccesrate(double succesrate, int i) {
		this.succesrate[i] = succesrate;

	}

	public int[] getMaxAge() {
		return this.maxAge;
	}

}
