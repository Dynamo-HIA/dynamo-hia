/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author boshuizh
 * 
 */
public class ExcelReadableXMLWriter  {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.DynamoOutputFactory");

	/* object with the output data */
	private CDMOutputFactory output;
	
	private ScenarioParameters params; 
	
	private boolean details=false;

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
	 * The constructor initializes the fields (arrays with all values==0), including the object that contais
	 * the output data and the object that holds the current successrate values
	 * 
	
	 * @param outputFactory:  object with aggregated output of the model
	 * @param scenParams:  object with the successrate etc of the scenarios
	 * @throws DynamoScenarioException
	 * @throws DynamoOutputException
	 */
	public ExcelReadableXMLWriter(CDMOutputFactory outputFactory, ScenarioParameters scenParams )
	{
		this.output = outputFactory;
		this.params=scenParams;
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */

	}
	/**
	 * makes a array of mortality by scenario, year, age and sex It is not
	 * possible to do so also by riskfactor or by disease In order to do so,
	 * this should be included as state in the update rule
	 * 
	 * @param numbers (boolean) : if true returns numbers, otherwise rates
	 * @param riskFactor : value of the riskfactor ; -1= total mortality (irrespective of risk factor)
	 * @return array of mortality by  scenario, year, age and sex 
	 */
	public double[][][][] getMortality(boolean numbers, int riskFactor) {
		double[][][][] mortality = null;
		int loopBegin = riskFactor;
		int loopEnd = riskFactor + 1;
		if (riskFactor < 0) {
			loopBegin = 0;
			loopEnd = this.output.nRiskFactorClasses;
		}
		if (this.output.stepsInRun > 0) {
			mortality = new double[this.output.nScen + 1][this.output.stepsInRun][96 + this.output.stepsInRun][2];
			/*
			 * number of persons now and after a year are calculated for all
			 * possible combinations of age and years, regardless of whether
			 * there are persons available at a particular age and year
			 * combination
			 */
			for (int scen = 0; scen < this.output.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.output.stepsInRun - 1; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.output.stepsInRun; stepCount++) {
							double denominator = 0;
							double nominator = 0;
							double personsAtnextAge = 0;
							for (int r = loopBegin; r < loopEnd; r++) {
								denominator += this.params.applySuccesrate(
										this.output.nPopByRiskClassByAge[0][stepCount][r][a][g],
										this.output.nPopByRiskClassByAge[scen][stepCount][r][a][g],
										scen, stepCount, a, g);

								personsAtnextAge += this.params.applySuccesrate(
										this.output.nPopByRiskClassByAge[0][stepCount + 1][r][a + 1][g],
										this.output.nPopByRiskClassByAge[scen][stepCount + 1][r][a + 1][g],
										scen, stepCount + 1, a + 1, g);
							}
							nominator = denominator - personsAtnextAge;

							if (denominator != 0 && !numbers
									&& personsAtnextAge > 0)
								mortality[scen][stepCount][a][g] = nominator
										/ denominator;
							if (denominator != 0 && personsAtnextAge > 0
									&& numbers)
								mortality[scen][stepCount][a][g] = nominator;
							if (denominator == 0 || personsAtnextAge == 0)
								mortality[scen][stepCount][a][g] = -1;
							/*
							 * also make mortality -1 for the highest age group
							 * as there is never a higher age available with
							 * which to calculate mortality
							 */
							if (a == (this.output.nDim - 2))
								mortality[scen][stepCount][this.output.nDim - 1][g] = -1;

						}
		}
		return mortality;
	}

	/**
	 * method writeOutput writes Excel-readable XML files The following files
	 * are produced: depending on user input (TODO) - separate workbooks for for
	 * men and/or women, or for the total population (sum of men and women) -
	 * separate workbooks per scenario - by choice each worksheet in the
	 * workbook is a calendar year or a birth cohort - for continuous variables
	 * the choice is either overall results (including riskfactor average and
	 * std) or a classification, or The files are written in the directory
	 * "simulationName\results\". Their names are:
	 * excel_cohort_(fe)male_scenario#.xml (men or women) or
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
	 * @throws XMLStreamException
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
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws DynamoOutputException
	 */
	public void writeWorkBookXMLbyYear(String fileName, int sex, int thisScen)
			throws FileNotFoundException, FactoryConfigurationError,
			XMLStreamException, DynamoOutputException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer;
		try {
			writer = factory.createXMLStreamWriter(out);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}

		/* in the current version all possbile notebooks are written */

		writer.writeStartDocument();
		writer.writeStartElement("Workbook");
		/*
		 * write information for excel might be partially unnecessary, but this
		 * works
		 */
		writer.writeAttribute("xmlns",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:o",
				"urn:schemas-microsoft-com:office:office");
		writer.writeAttribute("xmlns:x",
				"urn:schemas-microsoft-com:office:excel");
		writer.writeAttribute("xmlns:ss",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:html", "http://www.w3.org/TR/REC-html40");

		/* make one worksheet per calendar year */
		for (int year = 0; year < this.output.stepsInRun + 1; year++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "year " + (this.output.startYear + year));
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3 || this.output.categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (this.output.riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "age");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.getNDiseaseStates() + 3; col++) {
					writeCell(writer, this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					writeCell(writer, this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.writeEndElement();// </row>

			/* write the data */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

				for (int a = 0; a < 96; a++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (this.output.riskType == 1 || this.output.riskType == 3
							|| this.output.categorized) {

						writeCell(writer, this.output.riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (this.output.riskType == 2 && !this.output.categorized) {

						/*
						 * Calculate the average over all risk factor classes
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

								toByAveragedRef[r] = this.output.getMeanRiskByRiskClassByAge()[0][year][rClass][a][sex];
								toByAveragedScen[r] = this.output.getMeanRiskByRiskClassByAge()[thisScen][year][rClass][a][sex];
								numbersRef[r] = this.output.getNPopByRiskClassByAge()[0][year][rClass][a][sex];
								numbersScen[r] = this.output.getNPopByRiskClassByAge()[thisScen][year][rClass][a][sex];
							}
							mean = this.params.applySuccesrateToMean(toByAveragedRef,
									toByAveragedScen, numbersRef, numbersScen,
									thisScen, year, a, sex);

						} else {
							toByAveragedRef = new double[this.output.nRiskFactorClasses * 2];
							toByAveragedScen = new double[this.output.nRiskFactorClasses * 2];
							numbersRef = new double[this.output.nRiskFactorClasses * 2];
							numbersScen = new double[this.output.nRiskFactorClasses * 2];
							for (int s = 0; s < 2; s++)

								for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

									toByAveragedRef[r + s
											* this.output.nRiskFactorClasses] = this.output.meanRiskByRiskClassByAge[0][year][rClass][a][s];
									toByAveragedScen[r + s
											* this.output.nRiskFactorClasses] = this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][s];
									numbersRef[r + s * this.output.nRiskFactorClasses] = this.output.nPopByRiskClassByAge[0][year][rClass][a][s];
									numbersScen[r + s * this.output.nRiskFactorClasses] = this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][s];
								}
							mean = this.params.applySuccesrateToMean(toByAveragedRef,
									toByAveragedScen, numbersRef, numbersScen,
									thisScen, year, a, 2);
						}

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (this.output.riskType == 2 && !this.output.categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((this.output.riskType == 2 && this.output.categorized)
							|| this.output.riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = this.params.applySuccesrateToMean(
									this.output.meanRiskByRiskClassByAge[0][year][rClass][a][sex],
									this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
									this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
									this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
									thisScen, year, a, sex);

						} else {
							mean = this.params.applySuccesrateToMean(
									this.output.meanRiskByRiskClassByAge[0][year][rClass][a],
									this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a],
									this.output.nPopByRiskClassByAge[0][year][rClass][a],
									this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
									thisScen, year, a, sex);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, a);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = this.params.applySuccesrate(
								this.output.nPopByRiskClassByAge[0][year][rClass][a][sex],
								this.output.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
								thisScen, year, a, sex);

					} else {

						data = this.params.applySuccesrateToBothGenders(
								this.output.nPopByRiskClassByAge[0][year][rClass][a],
								this.output.nPopByRiskClassByAge[thisScen][year][rClass][a],
								thisScen, year, a);

					}
					writeCell(writer, data);
					/* write disease info */

					if (this.details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = this.params.applySuccesrate(
										this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a, sex);

							} else {

								data = this.params.applySuccesrateToBothGenders(
										this.output.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a],
										this.output.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a],
										thisScen, year, a);

							}
							writeCell(writer, data);
						}

					} else { /*
							 * if details is false: then write the data of
							 * diseases
							 */
						/* make summary array */
						double[][][][][][] nDiseaseByRiskClassByAge = this.output.getNDiseaseByRiskClassByAge();

						for (int col = 4; col < this.output.nDiseases + 4; col++) {

							if (sex < 2) {
								data = this.params.applySuccesrate(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a, sex);

							} else {

								data = this.params.applySuccesrateToBothGenders(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a],
										thisScen, year, a);

							}
							writeCell(writer, data);
						}

					}

					writer.writeEndElement();// </row>
				}// end risk class and age loop

			writer.writeEndElement();
			writer.writeEndElement();// end table and worksheet

		}// end loop over years
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		try {
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}

	/**
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws DynamoOutputException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 */
	public void writeWorkBookXMLbyCohort(String fileName, int sex, int thisScen)
			throws FactoryConfigurationError, XMLStreamException,
			DynamoOutputException, FileNotFoundException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		XMLStreamWriter writer = null;
		try {
			writer = factory.createXMLStreamWriter(out);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
		/* in the current version all possbile notebooks are written */

		writer.writeStartDocument();
		writer.writeStartElement("Workbook");
		/*
		 * write information for excel might be partially unnecessary, but this
		 * works
		 */
		writer.writeAttribute("xmlns",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:o",
				"urn:schemas-microsoft-com:office:office");
		writer.writeAttribute("xmlns:x",
				"urn:schemas-microsoft-com:office:excel");
		writer.writeAttribute("xmlns:ss",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:html", "http://www.w3.org/TR/REC-html40");
		// for (int year = 0; year < stepsInRun; year++)
		/* make one worksheet per cohort */
		for (int cohort = 0; cohort < 96; cohort++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "age " + cohort + "in "
					+ this.output.startYear);
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3 || this.output.categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (this.output.riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (this.output.riskType == 2 && this.output.categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "year");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
					writeCell(writer, this.output.getStateNames()[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.output.nDiseases + 4; col++) {
					writeCell(writer, this.output.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.writeEndElement();// </row>

			/*
			 * 
			 * 
			 * write the data
			 */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)

				for (int year = 0; year < this.output.nDim - cohort - 1; year++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (this.output.riskType == 1 || this.output.riskType == 3
							|| this.output.categorized) {

						writeCell(writer, this.output.riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (this.output.riskType == 2 && !this.output.categorized) {

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
								toBeAveragedScen[r] = this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][cohort][sex];
								numbersRef[r] = this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
								numbersScen[r] = this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
							}

							mean = this.params.applySuccesrateToMean(toBeAveragedRef,
									toBeAveragedScen, numbersRef, numbersScen,
									thisScen, 0, cohort, sex);
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
							mean = this.params.applySuccesrateToMeanToBothGenders(
									toBeAveragedRef2, toBeAveragedScen2,
									numbersRef2, numbersScen2, thisScen, 0,
									cohort);
						}
						// TODO veranderen

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (this.output.riskType == 2 && !this.output.categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((this.output.riskType == 2 && this.output.categorized)
							|| this.output.riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = this.params.applySuccesrateToMean(
									this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
									this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
									this.output.nPopByRiskClassByAge[0][year][rClass][cohort][sex],
									this.output.nPopByRiskClassByAge[thisScen][year][rClass][cohort][sex],
									thisScen, 0, cohort, sex);

						} else {
							mean = this.params.applySuccesrateToMeanToBothGenders(
									this.output.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort],
									this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
									this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									thisScen, 0, cohort);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, this.output.startYear + year);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = this.params.applySuccesrate(
								this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
								this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
								thisScen, 0, cohort, sex);

					} else {

						data = this.params.applySuccesrateToBothGenders(
								this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
								this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
								thisScen, 0, cohort);

					}
					writeCell(writer, data);
					/* write disease info */

					if (this.details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < this.output.nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = this.params.applySuccesrate(
										this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

							} else {

								data = this.params.applySuccesrateToBothGenders(
										this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
										thisScen, 0, cohort);

							}
							writeCell(writer, data);
						}

					} else {
						/* if details is false: then write the data of diseases */
						for (int col = 4; col < this.output.nDiseases + 4; col++) {
							if (sex < 2) {
								data = this.params.applySuccesrate(
										this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

							} else {

								data = this.params.applySuccesrateToBothGenders(
										this.output.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										this.output.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
										thisScen, 0, cohort);

							}
							writeCell(writer, data);
						}

					}

					writer.writeEndElement();// </row>
				}// end risk class and age loop

			writer.writeEndElement();
			writer.writeEndElement();// end table and worksheet

		}// end loop over years
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		try {
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}



	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, int toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Integer) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, float toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Float) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, double toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Double) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, String toWrite)
			throws XMLStreamException {

		writer.writeStartElement("Cell");
		writer.writeStartElement("Data");
		writer.writeAttribute("ss:Type", "String");
		writer.writeCharacters(toWrite);
		writer.writeEndElement();
		writer.writeEndElement();
	}


}
