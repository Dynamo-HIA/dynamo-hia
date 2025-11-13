/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.global.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * writes the output files in the form of csv files; all information in one
 * single file.
 * 
 * @author boshuizh
 * 
 */
public class CSVWriter {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.CSVWriter");

	/* object with the output data */
	private CDMOutputFactory output;
	private String delimiter = ",";

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	private ScenarioParameters params;

	private boolean details = false;

	public boolean isDetails() {
		return details;
	}

	private double[][][][][] nDisabledByRiskClassByAge;
	private double[][][][][] nTotDiseaseByRiskClassByAge;

	public void setDetails(boolean details) {
		this.details = details;
	}

	private boolean cohort = false;

	public boolean isCohort() {
		return cohort;
	}

	public void setCohort(boolean cohort) {
		this.cohort = cohort;
	}

	/**
	 * 
	 * 
	 * 
	 * The constructor initializes the fields (arrays with all values==0),
	 * including the object that contains the output data and the object that
	 * holds the current successrate values
	 * 
	 * 
	 * @param outputFactory
	 *            : object with aggregated output of the model
	 * @param scenParams
	 *            : object with the successrate etc of the scenarios
	 * @throws DynamoScenarioException
	 * @throws DynamoOutputException
	 */

	public CSVWriter(CDMOutputFactory output, ScenarioParameters params) {
		super();
		this.params = params;
		this.output = output;
		this.nDisabledByRiskClassByAge = this.output
				.getNDisabledByRiskClassByAge();
		this.nTotDiseaseByRiskClassByAge = this.output
				.getNTotalDiseaseByRiskClassByAge();
	}

	/**
	 * Writes a batch file containing all information in a single file
	 * 
	 * @param fileName
	 * @throws DynamoOutputException
	 */
	public void writeBatchOutputCSV(String fileName, boolean separateGenders)
			throws DynamoOutputException {

		try {
			FileWriter writer = new FileWriter(fileName);

			// TOADD writer.flush();
			// writer.close();
			// StringBuilder CSVstring = new StringBuilder() is used as
			// appending directly to the FileWriter is
			// very slow;

			StringBuilder toWriteCSVString = new StringBuilder();

			writeHeaders(toWriteCSVString, true);
			writer.append(toWriteCSVString);
			writer.flush();

			/* now write the data */
			int endsex = 2;
			if (!separateGenders)
				endsex = 1;
			int maxAgeAtStart=this.output.getMaxAgeInSimulation();
			for (int year = 0; year < this.output.stepsInRun + 1; year++) {
				toWriteCSVString = new StringBuilder();
				int year2 = year + this.output.startYear;

				for (int thisScen = 0; thisScen < this.output.nScen + 1; thisScen++)
					for (int sex = 0; sex < endsex; sex++) {

						/* write the data */
						/* each row is a year / risk-class / age combination */

						for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

							for (int a = 0; a < Math.min( maxAgeAtStart+1 + this.output
									.getStepsInRun(), this.output.getNDim()); a++) {
								toWriteCSVString.append(year2 + this.delimiter);
								toWriteCSVString.append(thisScen
										+ this.delimiter);
								if (separateGenders)
									toWriteCSVString.append(sex
											+ this.delimiter);
								else
									toWriteCSVString.append("both genders"
											+ this.delimiter);
								/* write risk factor info */
								if (this.output.riskType == 1
										|| this.output.riskType == 3
										|| this.output.categorized) {

									toWriteCSVString
											.append(this.output.riskClassnames[rClass]
													+ this.delimiter);

								}

								/*
								 * write the mean value of the continuous risk
								 * factor or the duration
								 */
								if (this.output.riskType == 2
										&& !this.output.categorized) {

									/*
									 * Calculate the average over all risk
									 * factor classes
									 * 
									 * / make arrays with the data needed to be
									 * averaged
									 */
									double[] toByAveragedRef;
									double[] toByAveragedScen;
									double[] numbersRef;
									double[] numbersScen;
									double[][] toByAveragedRef2;
									double[][] toByAveragedScen2;
									double[][] numbersRef2;
									double[][] numbersScen2;

									double mean;

									toByAveragedRef = new double[this.output.nRiskFactorClasses];
									toByAveragedScen = new double[this.output.nRiskFactorClasses];
									numbersRef = new double[this.output.nRiskFactorClasses];
									numbersScen = new double[this.output.nRiskFactorClasses];
									toByAveragedRef2 = new double[this.output.nRiskFactorClasses][2];
									toByAveragedScen2 = new double[this.output.nRiskFactorClasses][2];
									numbersRef2 = new double[this.output.nRiskFactorClasses][2];
									numbersScen2 = new double[this.output.nRiskFactorClasses][2];
									for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

										toByAveragedRef[r] = this.output
												.getMeanRiskByRiskClassByAge()[0][year][rClass][a][sex];
										toByAveragedScen[r] = this.output
												.getMeanRiskByRiskClassByAge()[thisScen][year][rClass][a][sex];
										numbersRef[r] = this.output
												.getNPopByRiskClassByAge()[0][year][rClass][a][sex];
										numbersScen[r] = this.output
												.getNPopByRiskClassByAge()[thisScen][year][rClass][a][sex];
										if (!separateGenders)
											for (int s = 0; s < 2; s++) {
												toByAveragedRef2[r][s] = this.output
														.getMeanRiskByRiskClassByAge()[0][year][rClass][a][s];
												toByAveragedScen2[r][s] = this.output
														.getMeanRiskByRiskClassByAge()[thisScen][year][rClass][a][s];
												numbersRef2[r][s] = this.output
														.getNPopByRiskClassByAge()[0][year][rClass][a][s];
												numbersScen2[r][s] = this.output
														.getNPopByRiskClassByAge()[thisScen][year][rClass][a][s];

											}
									}
									// log.fatal(" write mean risk");
									if (separateGenders){
									mean = this.params.applySuccesrateToMean(
											toByAveragedRef, toByAveragedScen,
											numbersRef, numbersScen, thisScen,
											year, a, sex);}
									else{
										mean = this.params.applySuccesrateToMeanToBothGenders(
												toByAveragedRef2, toByAveragedScen2,
												numbersRef2, numbersScen2, thisScen,
												year, a);
									}

									toWriteCSVString.append(mean
											+ this.delimiter);
								}
								/*
								 * write the standard deviation of the
								 * continuous riskfactor
								 */

								if (this.output.riskType == 2
										&& !this.output.categorized) {

									toWriteCSVString.append(rClass
											+ this.delimiter);
									// TODO vervangen door std risk factor

									toWriteCSVString.append(rClass
											+ this.delimiter);

								}

								/*
								 * write the mean value of the continuous
								 * riskfactor within a category of a riskfactor
								 */
								if ((this.output.riskType == 2 && this.output.categorized)
										|| this.output.riskType == 3) {

									double mean = 0;
									if (separateGenders) {
										mean = this.params
												.applySuccesrateToMean(
														this.output.meanRiskByRiskClassByAge[0][year][rClass][a][sex],
														this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
														this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
														this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
														thisScen, year, a, sex);
									} else {

										mean = this.params
												.applySuccesrateToMeanToBothGenders(
														this.output.meanRiskByRiskClassByAge[0][year][rClass][a],
														this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a],
														this.output.nPopByRiskClassByAge[0][year][rClass][a],
														this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
														thisScen, year, a);
									}

									toWriteCSVString.append(mean
											+ this.delimiter);

								}

								/* write age */

								toWriteCSVString.append(a + this.delimiter);

								/* write total numbers in group(row) */
								double data = 0;
								// log.fatal(" write population total");
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
													this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
													thisScen, year, a, sex);
								} else {
									data = this.params
											.applySuccesrateToBothGenders(
													this.output.nPopByRiskClassByAge[0][year][rClass][a],
													this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
													thisScen, year, a);
								}
/* from now, put delimitor before as data can be missing */
								toWriteCSVString.append(data );
								/* write disease info */

								if (this.details) {
									/*
									 * last diseaseState is the survival, that
									 * is already part of the file thus not
									 * needed here; if +3 is made +4 then also
									 * survival is in the output
									 */
									for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
										// log.fatal(" write diseasestate "+col);
										if (separateGenders) {
											data = this.params
													.applySuccesrate(
															this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
															this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
															thisScen, year, a,
															sex);
										} else {
											data = this.params
													.applySuccesrateToBothGenders(
															this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a],
															this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a],
															thisScen, year, a);
										}

										/*
										 * if (col == this.output.nDiseaseStates
										 * + 3) writer.append(((Double) data)
										 * .toString()); else
										 */
										toWriteCSVString.append(this.delimiter + data
												 );
									}

								} else { /*
										 * if details is false: then write the
										 * data of diseases
										 */
									/* make summary array */
									double[][][][][][] nDiseaseByRiskClassByAge = this.output
											.getNDiseaseByRiskClassByAge();

									for (int col = 4; col < this.output.nDiseases + 4; col++) {
										if (separateGenders) {
											data = this.params
													.applySuccesrate(
															nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
															nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
															thisScen, year, a,
															sex);
										} else {
											data = this.params
													.applySuccesrateToBothGenders(
															nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a],
															nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a],
															thisScen, year, a);

										}
										/*
										 * if (col == this.output.nDiseases + 3)
										 * writer.append(((Double) data)
										 * .toString()); else
										 */
										toWriteCSVString.append(this.delimiter +data
												);
									}

								}
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													nDisabledByRiskClassByAge[0][year][rClass][a][sex],
													nDisabledByRiskClassByAge[thisScen][year][rClass][a][sex],
													thisScen, year, a, sex);
								} else {
									data = this.params
											.applySuccesrateToBothGenders(
													nDisabledByRiskClassByAge[0][year][rClass][a],
													nDisabledByRiskClassByAge[thisScen][year][rClass][a],
													thisScen, year, a);
								}
								// log.fatal(" write disabled");
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString()
										);
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													nTotDiseaseByRiskClassByAge[0][year][rClass][a][sex],
													nTotDiseaseByRiskClassByAge[thisScen][year][rClass][a][sex],
													thisScen, year, a, sex);
								} else {
									data = this.params
											.applySuccesrateToBothGenders(
													nTotDiseaseByRiskClassByAge[0][year][rClass][a],
													nTotDiseaseByRiskClassByAge[thisScen][year][rClass][a],
													thisScen, year, a);
								}

								// log.fatal(" write total disease");
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString()
										);
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output
															.getMortalityByRiskClassByAge()[0][year][rClass][a][sex],
													this.output
															.getMortalityByRiskClassByAge()[thisScen][year][rClass][a][sex],
													thisScen, year, a, sex);
								} else {
									data = this.params
											.applySuccesrateToBothGenders(
													this.output
															.getMortalityByRiskClassByAge()[0][year][rClass][a],
													this.output
															.getMortalityByRiskClassByAge()[thisScen][year][rClass][a],
													thisScen, year, a);
								}

								// log.fatal(" write total disease");
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString()
										);
								double[][][][][][] incByRiskClassByAge = this.output
										.getNewCasesByRiskClassByAge();

								for (int col = 0; col < this.output.nDiseases; col++) {
									if (separateGenders) {
										data = this.params
												.applySuccesrate(
														incByRiskClassByAge[0][year][col][rClass][a][sex],
														incByRiskClassByAge[thisScen][year][col][rClass][a][sex],
														thisScen, year, a, sex);
									} else {
										data = this.params
												.applySuccesrateToBothGenders(
														incByRiskClassByAge[0][year][col][rClass][a],
														incByRiskClassByAge[thisScen][year][col][rClass][a],
														thisScen, year, a);
									}

									/*
									 * if (col == this.output.nDiseases + 3)
									 * writer.append(((Double) data)
									 * .toString()); else
									 */
									toWriteCSVString.append(this.delimiter +((Double) data)
											.toString()
											);
								}

								// writeEndElement
								toWriteCSVString.append("\n");// </row>
							}// end risk class and age loop
						// log.fatal("writing year "+ year + " for gender "
						// +sex+ " scenario " + thisScen);
					}
				writer.append(toWriteCSVString);
				writer.flush();
				toWriteCSVString = new StringBuilder();
			}// end loop over years, scenarios and gender

			writer.close();

		} catch (IOException e) {
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. \nPlease make sure that"
					+ " this file is not in use by another program.");
		}

	}

	private void writeHeaders(StringBuilder toWriteCSVString,
			boolean writedetails) throws IOException {
		/* write column headings */
		toWriteCSVString.append("year" + this.delimiter);
		if (writedetails)
			toWriteCSVString.append("scenario" + this.delimiter);
		if (writedetails)
			toWriteCSVString.append("gender" + this.delimiter);
		/* risk factor info */
		if (this.output.riskType == 1 || this.output.riskType == 3
				|| this.output.categorized) {
			toWriteCSVString.append("riskClass" + this.delimiter);
		} else {
			toWriteCSVString.append("mean_riskFactor" + this.delimiter);
			/*
			 * toWriteCSVString.append("std_riskFactor" + this.delimiter);
			 * toWriteCSVString.append("skewness" + this.delimiter);
			 */

		}
		if (this.output.riskType == 3) {

			toWriteCSVString.append("mean duration" + this.delimiter);

		}
		if (this.output.riskType == 2 && this.output.categorized) {

			toWriteCSVString.append("mean riskFactor" + this.delimiter);

		}

		/* age */
		toWriteCSVString.append("age" + this.delimiter);

		/* total number */
		toWriteCSVString.append("total number");

		/* disease info */
		if (this.details) {
			/*
			 * last disease state is survival and is equal to total numbers so
			 * not included
			 */
			for (int col = 0; col < this.output.getNDiseaseStates() - 1; col++) {
				toWriteCSVString.append(this.delimiter
						+ this.output.getStateNames()[col]);

			}
		} else {
			for (int col = 0; col < this.output.nDiseases; col++) {
				toWriteCSVString.append(this.delimiter
						+ this.output.diseaseNames[col]);

			}
		}
		toWriteCSVString.append(this.delimiter + "with disability");
		toWriteCSVString.append(this.delimiter + "with disease");
		toWriteCSVString.append(this.delimiter + "mortality");
		for (int col = 0; col < this.output.nDiseases; col++) {
			toWriteCSVString.append(this.delimiter + "incidence of "
					+ this.output.diseaseNames[col]);

		}

		/* the end of the line */
		toWriteCSVString.append("\n");// </row>

	}

	/**
	 * write csv files by year for a single scenario;
	 * 
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * @throws DynamoOutputException
	 */
	public void writeWorkBookCSVbyYear(String fileName, int sex, int thisScen)
			throws DynamoOutputException {

		log.info("About to write CSV output to " + fileName);
		try {
			FileWriter writer = new FileWriter(fileName);
			// StringBuilder CSVstring = new StringBuilder() is used as
			// appending directly to the FileWriter is
			// very slow;

			StringBuilder toWriteCSVString = new StringBuilder();
			// TOADD writer.flush();
			// writer.close();

			/* make one worksheet per calendar year */

			/* write column headings */
			writeHeaders(toWriteCSVString, false);

			/* now write the data */
			int maxAgeAtStart=this.output.getMaxAgeInSimulation();

			for (int year = 0; year < this.output.stepsInRun + 1; year++) {
				/* write the data */
				/* each row is a year / risk-class / age combination */
				int year2 = year + this.output.startYear;

				for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

					for (int a = 0; a <  maxAgeAtStart+1; a++) {

						toWriteCSVString.append(year2 + this.delimiter);
						/* write risk factor info */
						if (this.output.riskType == 1
								|| this.output.riskType == 3
								|| this.output.categorized) {

							toWriteCSVString
									.append(this.output.riskClassnames[rClass]
											+ this.delimiter);

						}

						/*
						 * write the mean value of the continuous risk factor or
						 * the duration
						 */
						if (this.output.riskType == 2
								&& !this.output.categorized) {

							/*
							 * Calculate the average over all risk factor
							 * classes
							 * 
							 * / make arrays with the data needed to be averaged
							 */
							double[] toByAveragedRef;
							double[] toByAveragedScen;
							double[] numbersRef;
							double[] numbersScen;
							double mean;
							if (sex < 2) {
								toByAveragedRef = new double[this.output.nRiskFactorClasses];
								toByAveragedScen = new double[this.output.nRiskFactorClasses];
								numbersRef = new double[this.output.nRiskFactorClasses];
								numbersScen = new double[this.output.nRiskFactorClasses];
								for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

									toByAveragedRef[r] = this.output
											.getMeanRiskByRiskClassByAge()[0][year][rClass][a][sex];
									toByAveragedScen[r] = this.output
											.getMeanRiskByRiskClassByAge()[thisScen][year][rClass][a][sex];
									numbersRef[r] = this.output
											.getNPopByRiskClassByAge()[0][year][rClass][a][sex];
									numbersScen[r] = this.output
											.getNPopByRiskClassByAge()[thisScen][year][rClass][a][sex];
								}
								mean = this.params.applySuccesrateToMean(
										toByAveragedRef, toByAveragedScen,
										numbersRef, numbersScen, thisScen,
										year, a, sex);

							} else {
								toByAveragedRef = new double[this.output.nRiskFactorClasses * 2];
								toByAveragedScen = new double[this.output.nRiskFactorClasses * 2];
								numbersRef = new double[this.output.nRiskFactorClasses * 2];
								numbersScen = new double[this.output.nRiskFactorClasses * 2];
								for (int s = 0; s < 2; s++)

									for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

										toByAveragedRef[r
												+ s
												* this.output.nRiskFactorClasses] = this.output.meanRiskByRiskClassByAge[0][year][rClass][a][s];
										toByAveragedScen[r
												+ s
												* this.output.nRiskFactorClasses] = this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][s];
										numbersRef[r
												+ s
												* this.output.nRiskFactorClasses] = this.output.nPopByRiskClassByAge[0][year][rClass][a][s];
										numbersScen[r
												+ s
												* this.output.nRiskFactorClasses] = this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][s];
									}
								mean = this.params.applySuccesrateToMean(
										toByAveragedRef, toByAveragedScen,
										numbersRef, numbersScen, thisScen,
										year, a, 2);
							}

							toWriteCSVString.append(mean + this.delimiter);
						}
						/*
						 * write the standard deviation of the continuous
						 * riskfactor
						 */

						if (this.output.riskType == 2
								&& !this.output.categorized) {

							toWriteCSVString.append(rClass + this.delimiter);
							// TODO vervangen door std risk factor

							toWriteCSVString.append(rClass + this.delimiter);

						}

						/*
						 * write the mean value of the continuous riskfactor
						 * within a category of a riskfactor
						 */
						if ((this.output.riskType == 2 && this.output.categorized)
								|| this.output.riskType == 3) {

							double mean = 0;

							if (sex < 2) {

								mean = this.params
										.applySuccesrateToMean(
												this.output.meanRiskByRiskClassByAge[0][year][rClass][a][sex],
												this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
												this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
												this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
												thisScen, year, a, sex);

							} else {
								mean = this.params
										.applySuccesrateToMean(
												this.output.meanRiskByRiskClassByAge[0][year][rClass][a],
												this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a],
												this.output.nPopByRiskClassByAge[0][year][rClass][a],
												this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
												thisScen, year, a, sex);

							}

							toWriteCSVString.append(mean + this.delimiter);

						}

						/* write age */

						toWriteCSVString.append(a + this.delimiter);

						/* write total numbers in group(row) */
						double data = 0;
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
											this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
											thisScen, year, a, sex);

						} else {

							data = this.params
									.applySuccesrateToBothGenders(
											this.output.nPopByRiskClassByAge[0][year][rClass][a],
											this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
											thisScen, year, a);

						}
						toWriteCSVString.append(data );
						/* write disease info */

						if (this.details) {
							/*
							 * last diseaseState is the survival, that is
							 * already part of the file thus not needed here; if
							 * +3 is made +4 then also survival is in the output
							 */
							for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
								if (sex < 2) {
									data = this.params
											.applySuccesrate(
													this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
													this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
													thisScen, year, a, sex);

								} else {

									data = this.params
											.applySuccesrateToBothGenders(
													this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a],
													this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a],
													thisScen, year, a);

								}
								
									toWriteCSVString.append(this.delimiter +data
											+ this.delimiter);
							}

						} else { /*
								 * if details is false: then write the data of
								 * diseases
								 */
							/* make summary array */
							double[][][][][][] nDiseaseByRiskClassByAge = this.output
									.getNDiseaseByRiskClassByAge();

							for (int col = 4; col < this.output.nDiseases + 4; col++) {

								if (sex < 2) {
									data = this.params
											.applySuccesrate(
													nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
													nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
													thisScen, year, a, sex);

								} else {

									data = this.params
											.applySuccesrateToBothGenders(
													nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a],
													nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a],
													thisScen, year, a);

								}
								if (col == this.output.nDiseases + 3)
									toWriteCSVString.append(this.delimiter +((Double) data)
											.toString());
								else
									toWriteCSVString.append(this.delimiter +data
											);
							}

						}

						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											nDisabledByRiskClassByAge[0][year][rClass][a][sex],
											nDisabledByRiskClassByAge[thisScen][year][rClass][a][sex],
											thisScen, year, a, sex);
						} else {
							data = this.params
									.applySuccesrateToBothGenders(
											nDisabledByRiskClassByAge[0][year][rClass][a],
											nDisabledByRiskClassByAge[thisScen][year][rClass][a],
											thisScen, year, a);

						}
						// log.fatal(" write disabled");
						toWriteCSVString.append(this.delimiter +((Double) data).toString()
								);
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											nTotDiseaseByRiskClassByAge[0][year][rClass][a][sex],
											nTotDiseaseByRiskClassByAge[thisScen][year][rClass][a][sex],
											thisScen, year, a, sex);
						} else {
							data = this.params
									.applySuccesrateToBothGenders(
											nTotDiseaseByRiskClassByAge[0][year][rClass][a],
											nTotDiseaseByRiskClassByAge[thisScen][year][rClass][a],
											thisScen, year, a);

						}
						// log.fatal(" write total disease");
						toWriteCSVString.append(this.delimiter +((Double) data).toString()
								);
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output
													.getMortalityByRiskClassByAge()[0][year][rClass][a][sex],
											this.output
													.getMortalityByRiskClassByAge()[thisScen][year][rClass][a][sex],
											thisScen, year, a, sex);
						} else {
							data = this.params
									.applySuccesrateToBothGenders(
											this.output
													.getMortalityByRiskClassByAge()[0][year][rClass][a],
											this.output
													.getMortalityByRiskClassByAge()[thisScen][year][rClass][a],
											thisScen, year, a);

						}
						// log.fatal(" write total disease");
						toWriteCSVString.append(this.delimiter +((Double) data).toString()
								);
						double[][][][][][] incByRiskClassByAge = this.output
								.getNewCasesByRiskClassByAge();

						for (int col = 0; col < this.output.nDiseases; col++) {
							if (sex < 2) {
								data = this.params
										.applySuccesrate(
												incByRiskClassByAge[0][year][col][rClass][a][sex],
												incByRiskClassByAge[thisScen][year][col][rClass][a][sex],
												thisScen, year, a, sex);
							} else {
								data = this.params
										.applySuccesrateToBothGenders(
												incByRiskClassByAge[0][year][col][rClass][a],
												incByRiskClassByAge[thisScen][year][col][rClass][a],
												thisScen, year, a);

							}
							/*
							 * if (col == this.output.nDiseases + 3)
							 * writer.append(((Double) data) .toString()); else
							 */
							toWriteCSVString.append(this.delimiter +((Double) data).toString()
									);
						}

						// writeEndElement
						toWriteCSVString.append("\n");// </row>
					}// end risk class and age loop

			}// end loop over years
			writer.append(toWriteCSVString);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. \nPlease make sure that"
					+ " this file is not in use by another program.");
		}

	}

	/**
	 * write the data by cohort (by the original value of the risk-factor at the
	 * start of simulation) for a single scenario
	 * 
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * 
	 * @throws DynamoOutputException
	 *             : when file can not be written
	 * @throws FileNotFoundException
	 * 
	 */
	public void writeWorkBookCSVbyCohort(String fileName, int sex, int thisScen)
			throws DynamoOutputException

	{

		// log.fatal("About to write CSV output to " + fileName);
		try {
			FileWriter writer = new FileWriter(fileName);
			// StringBuilder CSVstring = new StringBuilder() is used as
			// appending directly to the FileWriter is
			// very slow;

			StringBuilder toWriteCSVString = new StringBuilder();
			// TODO Auto-generated catch block

			/* write column headings */
			toWriteCSVString.append("age in startyear" + this.delimiter);

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				toWriteCSVString.append("riskClass" + this.delimiter);
			} else {
				toWriteCSVString.append("mean_riskFactor" + this.delimiter);
				toWriteCSVString.append("std_riskFactor" + this.delimiter);
				toWriteCSVString.append("skewness" + this.delimiter);

			}
			if (this.output.riskType == 3) {

				toWriteCSVString.append("mean duration" + this.delimiter);

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				toWriteCSVString.append("mean riskFactor" + this.delimiter);

			}

			/* age */
			toWriteCSVString.append("year" + this.delimiter);

			/* total number */
			toWriteCSVString.append("total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
					toWriteCSVString.append(this.delimiter
							+ this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					toWriteCSVString.append(this.delimiter
							+ this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}

			toWriteCSVString.append(this.delimiter + "with disability");
			toWriteCSVString.append(this.delimiter + "with disease");
			toWriteCSVString.append(this.delimiter + "mortality");
			for (int col = 0; col < this.output.nDiseases; col++) {
				toWriteCSVString.append(this.delimiter + "incidence of "
						+ this.output.diseaseNames[col]);

			}
			toWriteCSVString.append("\n"); // end header row

			/*
			 * 
			 * 
			 * write the data
			 */
			int maxAgeAtStart=this.output.getMaxAgeInSimulation();
			for (int cohort = 0; cohort <  maxAgeAtStart+1; cohort++) {

				/* each row is a cohort / risk-class / age combination */
				for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

					for (int year = 0; year < this.output.nDim - cohort - 1; year++) {
						toWriteCSVString
								.append(((Integer) (cohort)).toString());
						int year2 = year + this.output.startYear;
						/* write risk factor info */
						if (this.output.riskType == 1
								|| this.output.riskType == 3
								|| this.output.categorized) {

							toWriteCSVString.append(this.delimiter
									+ this.output.riskClassnames[rClass]);

						}

						/*
						 * write the mean value of the continuous risk factor or
						 * the duration
						 */
						// log.fatal("write  mean values");
						if (this.output.riskType == 2
								&& !this.output.categorized) {

							double mean = calculateMean(sex, thisScen, cohort,
									rClass, year);

							toWriteCSVString.append(this.delimiter + mean);
						}
						/*
						 * write the standard deviation of the continuous
						 * riskfactor
						 */

						if (this.output.riskType == 2
								&& !this.output.categorized) {

							toWriteCSVString.append(this.delimiter + rClass);
							// TODO vervangen door std risk factor

							toWriteCSVString.append(this.delimiter + rClass);

						}

						/*
						 * write the mean value of the continuous riskfactor
						 * within a category of a riskfactor
						 */
						if ((this.output.riskType == 2 && this.output.categorized)
								|| this.output.riskType == 3) {

							double mean = 0;

							if (sex < 2) {

								mean = this.params
										.applySuccesrateToMean(
												this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
												this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
												this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
												this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
												thisScen, 0, cohort, sex);

							} else {
								mean = this.params
										.applySuccesrateToMeanToBothGenders(
												this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort],
												this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
												this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
												this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
												thisScen, 0, cohort);

							}

							toWriteCSVString.append(this.delimiter + mean);

						}

						/* write age */

						toWriteCSVString.append(this.delimiter + year2);
						// log.fatal("write  nPOP");
						/* write total numbers in group(row) */
						double data = 0;
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
											this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
											thisScen, 0, cohort, sex);

						} else {

							data = this.params
									.applySuccesrateToBothGenders(
											this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
											this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
											thisScen, 0, cohort);

						}
						toWriteCSVString.append( this.delimiter+data);
						/* write disease info */
						// log.fatal("write  ziekten");
						if (this.details) {
							/*
							 * last diseaseState is the survival, that is
							 * already part of the file thus not needed here; if
							 * +3 is made +4 then also survival is in the output
							 */
							for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
								if (sex < 2) {
									data = this.params
											.applySuccesrate(
													this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
													this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
													thisScen, 0, cohort, sex);

								} else {

									data = this.params
											.applySuccesrateToBothGenders(
													this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
													this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
													thisScen, 0, cohort);

								}
								toWriteCSVString.append(this.delimiter + data);
							}

						} else {
							/*
							 * if details is false: then write the data of
							 * diseases
							 */
							for (int col = 4; col < this.output.nDiseases + 4; col++) {
								if (sex < 2) {
									data = this.params
											.applySuccesrate(
													this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
													this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
													thisScen, 0, cohort, sex);

								} else {

									data = this.params
											.applySuccesrateToBothGenders(
													this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
													this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
													thisScen, 0, cohort);

								}
								toWriteCSVString.append(this.delimiter  + data);
							}

						}
						
						// log.fatal("write  disability");
						// this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output
													.getPDisabilityByOriRiskClassByOriAge()[0][year][rClass][cohort][sex]
													* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
											this.output
													.getPDisabilityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex]
													* this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
											thisScen, 0, cohort, sex);
						} else {

							double[] inputRef = new double[2];
							double[] inputScen = new double[2];
							for (int i = 0; i < 2; i++) {
								inputRef[i] = this.output
										.getPDisabilityByOriRiskClassByOriAge()[0][year][rClass][cohort][i]
										* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
								inputScen[i] = this.output
										.getPDisabilityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][i]
										* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
							}
							data = this.params.applySuccesrateToBothGenders(
									inputRef, inputScen, thisScen, 0, cohort);

						}
						toWriteCSVString.append(this.delimiter +((Double) data).toString());
						
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output
													.getPTotalDiseaseByOriRiskClassByOriAge()[0][year][rClass][cohort][sex]
													* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
											this.output
													.getPTotalDiseaseByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex]
													* this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
											thisScen, 0, cohort, sex);
							// log.fatal("write mortality");
							// log.fatal(" write total disease");
						} else {
							double[] inputRef = new double[2];
							double[] inputScen = new double[2];
							for (int i = 0; i < 2; i++) {
								inputRef[i] = this.output
										.getPTotalDiseaseByOriRiskClassByOriAge()[0][year][rClass][cohort][i]
										* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
								inputScen[i] = this.output
										.getPTotalDiseaseByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][i]
										* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
							}
							data = this.params.applySuccesrateToBothGenders(
									inputRef, inputScen, thisScen, 0, cohort);

						}
						toWriteCSVString.append(this.delimiter +data );
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output
													.getMortalityByOriRiskClassByOriAge()[0][year][rClass][cohort][sex],
											this.output
													.getMortalityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex],
											thisScen, 0, cohort, sex);
						} else {
							data = this.params
									.applySuccesrateToBothGenders(
											this.output
													.getMortalityByOriRiskClassByOriAge()[0][year][rClass][cohort],
											this.output
													.getMortalityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort],
											thisScen, 0, cohort);
							// data = this.params todo
							// .applySuccesrateToBothGenders(
						}
						// log.fatal(" write total disease");
						toWriteCSVString.append(this.delimiter +((Double) data).toString()
								);
						double[][][][][][] incByRiskClassByAge = this.output
								.getNewCasesByOriRiskClassByOriAge();
						// log.fatal("write  incidences");
						for (int col = 0; col < this.output.nDiseases; col++) {
							if (sex < 2) {
								data = this.params
										.applySuccesrate(
												incByRiskClassByAge[0][year][col][rClass][cohort][sex],
												incByRiskClassByAge[thisScen][year][col][rClass][cohort][sex],
												thisScen, 0, cohort, sex);
							} else {
								data = this.params
										.applySuccesrateToBothGenders(
												incByRiskClassByAge[0][year][col][rClass][cohort],
												incByRiskClassByAge[thisScen][year][col][rClass][cohort],
												thisScen, 0, cohort);
								// data = this.params todo
								// .applySuccesrateToBothGenders(
							}
							/*
							 * if (col == this.output.nDiseases + 3)
							 * writer.append(((Double) data) .toString()); else
							 */
							toWriteCSVString.append(this.delimiter +data );
						}

						toWriteCSVString.append("\n");// </row>
					}// end risk class and age loop

			}// end loop over cohorts
			writer.append(toWriteCSVString);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}

	/**
	 * write the data by cohort (by the original value of the risk-factor at the
	 * start of simulation) for all scenarios and genders in a single file
	 * 
	 * @param fileName
	 *            : name to write to
	 * @param separateGenders
	 *            : sex to write: true= write men and women separate, false:
	 *            write the sum
	 * @throws DynamoOutputException
	 *             : when file can not be written
	 * @throws FileNotFoundException
	 * 
	 */
	/**
	 * @param fileName
	 * @param separateGenders
	 * @throws DynamoOutputException
	 */
	public void writeWorkBookCSVbyCohort(String fileName,
			boolean separateGenders) throws DynamoOutputException

	{

		// log.fatal("About to write CSV output to " + fileName);
		try {
			FileWriter writer = new FileWriter(fileName);
			// StringBuilder CSVstring = new StringBuilder() is used as
			// appending directly to the FileWriter is
			// very slow;

			StringBuilder toWriteCSVString = new StringBuilder();
			// TODO Auto-generated catch block

			/* write column headings */
			toWriteCSVString.append("age in startyear" + this.delimiter);
			toWriteCSVString.append("scenario" + this.delimiter);
			toWriteCSVString.append("gender" + this.delimiter);

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				toWriteCSVString.append("riskClass" + this.delimiter);
			} else {
				toWriteCSVString.append("mean_riskFactor" + this.delimiter);
				toWriteCSVString.append("std_riskFactor" + this.delimiter);
				toWriteCSVString.append("skewness" + this.delimiter);

			}
			if (this.output.riskType == 3) {

				toWriteCSVString.append("mean duration" + this.delimiter);

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				toWriteCSVString.append("mean riskFactor" + this.delimiter);

			}

			/* year */
			toWriteCSVString.append("year" + this.delimiter);

			/* total number */
			toWriteCSVString.append("total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
					toWriteCSVString.append(this.delimiter
							+ this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					toWriteCSVString.append(this.delimiter
							+ this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}

			toWriteCSVString.append(this.delimiter + "with disability");
			toWriteCSVString.append(this.delimiter + "with disease");
			toWriteCSVString.append(this.delimiter + "mortality");
			for (int col = 0; col < this.output.nDiseases; col++) {
				toWriteCSVString.append(this.delimiter + "incidence of "
						+ this.output.diseaseNames[col]);

			}
			toWriteCSVString.append("\n"); // end header row

			/*
			 * 
			 * 
			 * write the data
			 */
			int endsex = 2;
			if (!separateGenders)
				endsex = 1;
			int maxAgeAtStart=this.output.getMaxAgeInSimulation();
			for (int thisScen = 0; thisScen < this.output.nScen + 1; thisScen++) {
				for (int sex = 0; sex < endsex; sex++)
					for (int cohort = 0; cohort <  maxAgeAtStart+1; cohort++) {
						/* cohort is age at start of simulation */

						/* each row is a cohort / risk-class / age combination */
						for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

							for (int year = 0; year < this.output.nDim - cohort
									- 1; year++) {
								int year2 = year + this.output.startYear;
								/* write age at start */
								toWriteCSVString.append(((Integer) (cohort))
										.toString()
										+ this.delimiter);
								/* write scenario */
								toWriteCSVString.append(((Integer) (thisScen)+this.delimiter)
										.toString()
										);
								/* write sex */
								if (separateGenders) toWriteCSVString.append( ((Integer) (sex))
										.toString());
								else toWriteCSVString.append("both genders");
								/* write risk factor info */
								if (this.output.riskType == 1
										|| this.output.riskType == 3
										|| this.output.categorized) {

									toWriteCSVString
											.append(this.delimiter
													+ this.output.riskClassnames[rClass]);

								}

								/*
								 * write the mean value of the continuous risk
								 * factor or the duration
								 */
								if (this.output.riskType == 2
										&& !this.output.categorized) {

									double mean = calculateMean(sex, thisScen,
											cohort, rClass, year);

									toWriteCSVString.append(this.delimiter
											+ mean);
								}
								/*
								 * write the standard deviation of the
								 * continuous riskfactor
								 */

								if (this.output.riskType == 2
										&& !this.output.categorized) {

									toWriteCSVString.append(this.delimiter
											+ rClass);
									// TODO vervangen door std risk factor

									toWriteCSVString.append(this.delimiter
											+ rClass);

								}

								/*
								 * write the mean value of the continuous
								 * riskfactor within a category of a riskfactor
								 */

								if ((this.output.riskType == 2 && this.output.categorized)
										|| this.output.riskType == 3) {

									double mean = 0;

									if (separateGenders) {

										mean = this.params
												.applySuccesrateToMean(
														this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
														this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
														this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
														this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
														thisScen, 0, cohort,
														sex);

									} else {
										mean = this.params
												.applySuccesrateToMeanToBothGenders(
														this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort],
														this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
														this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
														this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
														thisScen, 0, cohort);

									}

									toWriteCSVString.append(this.delimiter
											+ mean);

								}

								/* write year */

								toWriteCSVString.append(this.delimiter + year2);

								/* write total numbers in group(row) */
								double data = 0;
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
													this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
													thisScen, 0, cohort, sex);

								} else {

									data = this.params
											.applySuccesrateToBothGenders(
													this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
													this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
													thisScen, 0, cohort);

								}
								toWriteCSVString.append(this.delimiter + data);
								/* write disease info */

								if (this.details) {
									/*
									 * last diseaseState is the survival, that
									 * is already part of the file thus not
									 * needed here; if +3 is made +4 then also
									 * survival is in the output
									 */
									for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
										if (separateGenders) {
											data = this.params
													.applySuccesrate(
															this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
															this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
															thisScen, 0,
															cohort, sex);

										} else {

											data = this.params
													.applySuccesrateToBothGenders(
															this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
															this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
															thisScen, 0, cohort);

										}
										toWriteCSVString.append(this.delimiter
												+ data);
									}

								} else {
									/*
									 * if details is false: then write the data
									 * of diseases
									 */
									for (int col = 4; col < this.output.nDiseases + 4; col++) {
										if (separateGenders) {
											data = this.params
													.applySuccesrate(
															this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
															this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
															thisScen, 0,
															cohort, sex);

										} else {

											data = this.params
													.applySuccesrateToBothGenders(
															this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
															this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
															thisScen, 0, cohort);

										}
										toWriteCSVString.append(this.delimiter
												+ data);
									}

								}
								
								// this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output
															.getPDisabilityByOriRiskClassByOriAge()[0][year][rClass][cohort][sex]
															* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
													this.output
															.getPDisabilityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex]
															* this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
													thisScen, 0, cohort, sex);
								} else {
									double[] inputScen = new double[2];
									double[] inputRef = new double[2];
									for (int i = 0; i < 2; i++) {

										inputRef[i] = this.output
												.getPDisabilityByOriRiskClassByOriAge()[0][year][rClass][cohort][i]
												* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];

										inputScen[i] = this.output
												.getPDisabilityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][i]
												* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
									}
									data = this.params
											.applySuccesrateToBothGenders(
													inputRef, inputScen,
													thisScen, 0, cohort);
								}
								// log.fatal(" write disabled");
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString());
								

								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output
															.getPTotalDiseaseByOriRiskClassByOriAge()[0][year][rClass][cohort][sex]
															* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
													this.output
															.getPTotalDiseaseByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex]
															* this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
													thisScen, 0, cohort, sex);
								} else {
									double[] inputScen = new double[2];
									double[] inputRef = new double[2];
									for (int i = 0; i < 2; i++) {

										inputRef[i] = this.output
												.getPTotalDiseaseByOriRiskClassByOriAge()[0][year][rClass][cohort][i]
												* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];

										inputScen[i] = this.output
												.getPTotalDiseaseByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][i]
												* this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][i];
									}

									data = this.params
											.applySuccesrateToBothGenders(
													inputRef, inputScen,
													thisScen, 0, cohort);

								}
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString());
								

								if (separateGenders) {
									data = this.params
											.applySuccesrate(
													this.output
															.getMortalityByOriRiskClassByOriAge()[0][year][rClass][cohort][sex],
													this.output
															.getMortalityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort][sex],
													thisScen, 0, cohort, sex);
								} else {
									data = this.params
											.applySuccesrateToBothGenders(
													this.output
															.getMortalityByOriRiskClassByOriAge()[0][year][rClass][cohort],
													this.output
															.getMortalityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort],
													thisScen, 0, cohort);
								}

								// log.fatal(" write total disease");
								toWriteCSVString.append(this.delimiter +((Double) data)
										.toString());
								
								double[][][][][][] incByRiskClassByAge = this.output
										.getNewCasesByOriRiskClassByOriAge();

								for (int col = 0; col < this.output.nDiseases; col++) {

									if (separateGenders) {
										data = this.params
												.applySuccesrate(
														incByRiskClassByAge[0][year][col][rClass][cohort][sex],
														incByRiskClassByAge[thisScen][year][col][rClass][cohort][sex],
														thisScen, 0, cohort,
														sex);
									} else {
										data = this.params
												.applySuccesrateToBothGenders(
														incByRiskClassByAge[0][year][col][rClass][cohort],
														incByRiskClassByAge[thisScen][year][col][rClass][cohort],
														thisScen, 0, cohort);
									}

									/*
									 * if (col == this.output.nDiseases + 3)
									 * writer.append(((Double) data)
									 * .toString()); else
									 */
									toWriteCSVString.append(this.delimiter +data
											);
								}

								toWriteCSVString.append("\n");// </row>
							}// end risk class and age loop

					}// end loop over cohorts
			} // end loop over scenarios
			writer.append(toWriteCSVString);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}

	private double calculateMean(int sex, int thisScen, int cohort, int rClass,
			int year) throws DynamoOutputException {
		/*
		 * Calculate the average over all risk factor classes
		 * 
		 * / make arrays with the data needed to be averaged
		 */
		double[] toBeAveragedRef;
		double[] toBeAveragedScen;
		double[] numbersRef;
		double[] numbersScen;
		double mean;

		if (sex < 2) {
			toBeAveragedRef = new double[this.output.nRiskFactorClasses];
			toBeAveragedScen = new double[this.output.nRiskFactorClasses];
			numbersRef = new double[this.output.nRiskFactorClasses];
			numbersScen = new double[this.output.nRiskFactorClasses];
			for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

				toBeAveragedRef[r] = this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
				toBeAveragedScen[r] = this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
				numbersRef[r] = this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
				numbersScen[r] = this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
			}

			mean = this.params.applySuccesrateToMean(toBeAveragedRef,
					toBeAveragedScen, numbersRef, numbersScen, thisScen, 0,
					cohort, sex);
		} else {
			double[][] toBeAveragedRef2 = new double[this.output.nRiskFactorClasses * 2][2];
			double[][] toBeAveragedScen2 = new double[this.output.nRiskFactorClasses * 2][2];
			double[][] numbersRef2 = new double[this.output.nRiskFactorClasses * 2][2];
			double[][] numbersScen2 = new double[this.output.nRiskFactorClasses * 2][2];
			for (int r = 0; r < this.output.nRiskFactorClasses; r++)

				for (int s = 0; s < 2; s++) {
					toBeAveragedScen2[r][s] = this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][s];
					toBeAveragedRef2[r][s] = this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][s];
					numbersRef2[r][s] = this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][s];
					numbersScen2[r][s] = this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][s];

					;
				}
			mean = this.params.applySuccesrateToMeanToBothGenders(
					toBeAveragedRef2, toBeAveragedScen2, numbersRef2,
					numbersScen2, thisScen, 0, cohort);
		}
		return mean;
	}

}
