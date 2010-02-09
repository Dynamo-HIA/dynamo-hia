/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * writes the output files in the form of csv files; one file per scenario.
 * 
 * @author boshuizh
 * 
 */
public class CSVWriter {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.DynamoOutputFactory");

	/* object with the output data */
	private CDMOutputFactory output;
	private String delimiter = ";";

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

	public void setDetails(boolean details) {
		this.details = details;
	}

	/**
	 * 
	 * 
	 * 
	 * The constructor initializes the fields (arrays with all values==0),
	 * including the object that contais the output data and the object that
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
	}

	

	public void writeBatchOutputCSV(String fileName)
			throws FactoryConfigurationError, XMLStreamException,
			DynamoOutputException {

		try {
			FileWriter writer = new FileWriter(fileName + ".csv");

			// TOADD writer.flush();
			// writer.close();

			/* make one worksheet per calendar year */

			/* write column headings */
			writer.append("year" + this.delimiter);
			writer.append("scenario" + this.delimiter);
			writer.append("gender" + this.delimiter);
			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				writer.append("riskClass" + this.delimiter);
			} else {
				writer.append("mean_riskFactor" + this.delimiter);
				writer.append("std_riskFactor" + this.delimiter);
				writer.append("skewness" + this.delimiter);

			}
			if (this.output.riskType == 3) {

				writer.append("mean duration" + this.delimiter);

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				writer.append("mean riskFactor" + this.delimiter);

			}

			/* age */
			writer.append("age" + this.delimiter);

			/* total number */
			writer.append("total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 0; col < this.output.getNDiseaseStates() - 1; col++) {
					writer.append(this.delimiter
							+ this.output.getStateNames()[col]);

				}
			} else {
				for (int col = 0; col < this.output.nDiseases - 1; col++) {
					writer.append(this.delimiter
							+ this.output.diseaseNames[col]);

				}
			}
			/* the end of the line */
			writer.append("\n");// </row>

			/* now write the data */
			for (int year = 0; year < this.output.stepsInRun + 1; year++)
				for (int thisScen = 0; thisScen < this.output.nScen + 1; thisScen++)
					for (int sex = 0; sex < 2; sex++) {

						/* write the data */
						/* each row is a year / risk-class / age combination */

						for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

							for (int a = 0; a < 96; a++) {
								writer.append(year + this.delimiter);
								writer.append(thisScen + this.delimiter);
								writer.append(sex + this.delimiter);
								/* write risk factor info */
								if (this.output.riskType == 1
										|| this.output.riskType == 3
										|| this.output.categorized) {

									writer
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
									double mean;

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

									writer.append(mean + this.delimiter);
								}
								/*
								 * write the standard deviation of the
								 * continuous riskfactor
								 */

								if (this.output.riskType == 2
										&& !this.output.categorized) {

									writer.append(rClass + this.delimiter);
									// TODO vervangen door std risk factor

									writer.append(rClass + this.delimiter);

								}

								/*
								 * write the mean value of the continuous
								 * riskfactor within a category of a riskfactor
								 */
								if ((this.output.riskType == 2 && this.output.categorized)
										|| this.output.riskType == 3) {

									double mean = 0;

									mean = this.params
											.applySuccesrateToMean(
													this.output.meanRiskByRiskClassByAge[0][year][rClass][a][sex],
													this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
													this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
													this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
													thisScen, year, a, sex);

									writer.append(mean + this.delimiter);

								}

								/* write age */

								writer.append(a + this.delimiter);

								/* write total numbers in group(row) */
								double data = 0;

								data = this.params
										.applySuccesrate(
												this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
												this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
												thisScen, year, a, sex);

								writer.append(data + this.delimiter);
								/* write disease info */

								if (this.details) {
									/*
									 * last diseaseState is the survival, that
									 * is already part of the file thus not
									 * needed here; if +3 is made +4 then also
									 * survival is in the output
									 */
									for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {

										data = this.params
												.applySuccesrate(
														this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
														this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
														thisScen, year, a, sex);

										if (col == this.output.nDiseases + 3)
											writer.append(((Double) data)
													.toString());
										else
											writer
													.append(data
															+ this.delimiter);
									}

								} else { /*
										 * if details is false: then write the
										 * data of diseases
										 */
									/* make summary array */
									double[][][][][][] nDiseaseByRiskClassByAge = this.output
											.getNDiseaseByRiskClassByAge();

									for (int col = 4; col < this.output.nDiseases + 4; col++) {

										data = this.params
												.applySuccesrate(
														nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
														nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
														thisScen, year, a, sex);

										if (col == this.output.nDiseases + 3)
											writer.append(((Double) data)
													.toString());
										else
											writer
													.append(data
															+ this.delimiter);
									}

								}
								// writeEndElement
								writer.append("\n");// </row>
							}// end risk class and age loop

					}// end loop over years, scenarios and gender

			writer.flush();
			writer.close();

		} catch (IOException e) {
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. \nPlease make sure that"
					+ " this file is not in use by another program.");
		}

	}

	/**
	 * method writeOutput writes CSV files The following files are produced:
	 * depending on user input - separate workbooks for for men and/or women, or
	 * for the total population (sum of men and women) - separate workbooks per
	 * scenario - by choice each worksheet in the workbook is a calendar year or
	 * a birth cohort - for continuous variables the choice is either overall
	 * results (including riskfactor average and std) or a classification, or
	 * The files are written in the directory "simulationName\results\". Their
	 * names are: excel_cohort_(fe)male_scenario#.xml (men or women) or
	 * excel_cohort_scenario#.xml (both combined)
	 * excel_year_(fe)male_scenario#.xml (men or women) or
	 * excel_year_scenario#.xml (both combined) * where # is the scenario number
	 * Either prevalences per disease are written (field details=false) or the
	 * originally calculated diseases states are written (field details=true)
	 * 
	 * @param scenInfo
	 *            : (ScenarioInfo) object with information on scenarios
	 * @param simulationName
	 *            (String): name of the simulation
	 * @throws IOException
	 * 
	 * @throws FactoryConfigurationError
	 */

	/**
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws DynamoOutputException
	 * @throws IOException
	 */
	public void writeWorkBookCSVbyYear(String fileName, int sex, int thisScen)
			throws FactoryConfigurationError, XMLStreamException,
			DynamoOutputException {

		log.fatal("output written to " + fileName);
		try {
			FileWriter writer = new FileWriter(fileName + ".csv");

			// TOADD writer.flush();
			// writer.close();

			/* make one worksheet per calendar year */

			/* write column headings */
			writer.append("year" + this.delimiter);
			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				writer.append("riskClass" + this.delimiter);
			} else {
				writer.append("mean_riskFactor" + this.delimiter);
				writer.append("std_riskFactor" + this.delimiter);
				writer.append("skewness" + this.delimiter);

			}
			if (this.output.riskType == 3) {

				writer.append("mean duration" + this.delimiter);

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				writer.append("mean riskFactor" + this.delimiter);

			}

			/* age */
			writer.append("age" + this.delimiter);

			/* total number */
			writer.append("total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.getNDiseaseStates() + 3; col++) {
					writer.append(this.delimiter
							+ this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					writer.append(this.delimiter
							+ this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			/* the end of the line */
			writer.append("\n");// </row>

			/* now write the data */

			for (int year = 0; year < this.output.stepsInRun + 1; year++) {
				/* write the data */
				/* each row is a year / risk-class / age combination */

				for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

					for (int a = 0; a < 96; a++) {
						writer.append(year + this.delimiter);
						/* write risk factor info */
						if (this.output.riskType == 1
								|| this.output.riskType == 3
								|| this.output.categorized) {

							writer.append(this.output.riskClassnames[rClass]
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

							writer.append(mean + this.delimiter);
						}
						/*
						 * write the standard deviation of the continuous
						 * riskfactor
						 */

						if (this.output.riskType == 2
								&& !this.output.categorized) {

							writer.append(rClass + this.delimiter);
							// TODO vervangen door std risk factor

							writer.append(rClass + this.delimiter);

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

							writer.append(mean + this.delimiter);

						}

						/* write age */

						writer.append(a + this.delimiter);

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
						writer.append(data + this.delimiter);
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
								if (col == this.output.nDiseases + 3)
									writer.append(((Double) data).toString());
								else
									writer.append(data + this.delimiter);
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
									writer.append(((Double) data).toString());
								else
									writer.append(data + this.delimiter);
							}

						}
						// writeEndElement
						writer.append("\n");// </row>
					}// end risk class and age loop

			}// end loop over years

			writer.flush();
			writer.close();

		} catch (IOException e) {
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. \nPlease make sure that"
					+ " this file is not in use by another program.");
		}

	}

	/**
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
	 */
	public void writeWorkBookXMLbyCohort(String fileName, int sex, int thisScen)
			throws DynamoOutputException, FileNotFoundException

	{
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		try {
			FileWriter writer = new FileWriter(fileName + ".csv");

			// TODO Auto-generated catch block

			/* write column headings */
			writer.append("age in startyear" + this.delimiter);

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				writer.append("riskClass" + this.delimiter);
			} else {
				writer.append("mean_riskFactor" + this.delimiter);
				writer.append("std_riskFactor" + this.delimiter);
				writer.append("skewness" + this.delimiter);

			}
			if (this.output.riskType == 3) {

				writer.append("mean duration" + this.delimiter);

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				writer.append("mean riskFactor" + this.delimiter);

			}

			/* age */
			writer.append("year" + this.delimiter);

			/* total number */
			writer.append("total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
					writer.append(this.delimiter
							+ this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					writer.append(this.delimiter
							+ this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.append("\n"); // end header row

			/*
			 * 
			 * 
			 * write the data
			 */
			for (int cohort = 0; cohort < 96; cohort++) {

				writer.append(((Integer) (cohort + this.output.startYear))
						.toString());

				/* each row is a cohort / risk-class / age combination */
				for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

					for (int year = 0; year < this.output.nDim - cohort - 1; year++) {

						/* write risk factor info */
						if (this.output.riskType == 1
								|| this.output.riskType == 3
								|| this.output.categorized) {

							writer.append(this.delimiter
									+ this.output.riskClassnames[rClass]);

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
									toBeAveragedScen[r] = this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][cohort][sex];
									numbersRef[r] = this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
									numbersScen[r] = this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
								}

								mean = this.params.applySuccesrateToMean(
										toBeAveragedRef, toBeAveragedScen,
										numbersRef, numbersScen, thisScen, 0,
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
										numbersRef2[r][s] = this.output.nPopByRiskClassByAge[0][year][rClass][cohort][s];
										numbersScen2[r][s] = this.output.nPopByRiskClassByAge[thisScen][year][rClass][cohort][s];

										;
									}
								mean = this.params
										.applySuccesrateToMeanToBothGenders(
												toBeAveragedRef2,
												toBeAveragedScen2, numbersRef2,
												numbersScen2, thisScen, 0,
												cohort);
							}
							// TODO veranderen

							writer.append(this.delimiter + mean);
						}
						/*
						 * write the standard deviation of the continuous
						 * riskfactor
						 */

						if (this.output.riskType == 2
								&& !this.output.categorized) {

							writer.append(this.delimiter + rClass);
							// TODO vervangen door std risk factor

							writer.append(this.delimiter + rClass);

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
												this.output.nPopByRiskClassByAge[0][year][rClass][cohort][sex],
												this.output.nPopByRiskClassByAge[thisScen][year][rClass][cohort][sex],
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

							writer.append(this.delimiter + mean);

						}

						/* write age */

						writer.append(this.delimiter + this.output.startYear
								+ year);

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
						writer.append(this.delimiter + data);
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
								writer.append(this.delimiter + data);
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
								writer.append(this.delimiter + data);
							}

						}

						writer.append("\n");// </row>
					}// end risk class and age loop

			}// end loop over cohorts

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

}
