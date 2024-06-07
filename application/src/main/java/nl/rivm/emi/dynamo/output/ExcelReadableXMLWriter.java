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
public class ExcelReadableXMLWriter {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.ExcelReadableXMLWriter");

	/* object with the output data */
	private CDMOutputFactory output;

	private ScenarioParameters params;

	private boolean details = false;

	private double NDaly[][][][][];
	private double NDisease[][][][][];
	private double NOriDaly[][][][][];
	private double NoriDisease[][][][][];
	private double[][][][][][] nDiseaseByRiskClassByAge ;
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
	public ExcelReadableXMLWriter(CDMOutputFactory outputFactory,
			ScenarioParameters scenParams) {
		this.output = outputFactory;
		this.params = scenParams;
		makeDALYArrays();
		makeDiseaseArrays();
		this.nDiseaseByRiskClassByAge = this.output
		.getNDiseaseByRiskClassByAge();
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */

	}

	private void makeDiseaseArrays() {

		
		NoriDisease=new double [this.output.nScen + 1][this.output.nDim][this.output.nRiskFactorClasses][this.output.maxAgeInSimulation + 1][2];
		NDisease=new double [this.output.nScen + 1][this.output.stepsInRun+1][this.output.nRiskFactorClasses][96+ this.output.stepsInRun][2];
		
		double[][][][][] nOriPop = this.output.getNPopByOriRiskClassByOriAge();

		double[][][][][] probTotOriDisease = this.output
				.getPTotalDiseaseByOriRiskClassByOriAge();
		for (int scen = 0; scen < this.output.nScen + 1; scen++)
			for (int stepCount = 0; stepCount < this.output.nDim; stepCount++)
				for (int a = Math.max(0,this.output.minAgeInSimulation); a < this.output.maxAgeInSimulation + 1; a++)
					for (int s = 0; s <= 1; s++)
						for (int r = 0; r < this.output.nRiskFactorClasses; r++) {
							NoriDisease[scen][stepCount][r][a][s] = nOriPop[scen][stepCount][r][a][s]
									* probTotOriDisease[scen][stepCount][r][a][s];

						}
		
		double[][][][][] probTotDisease = this.output
		.getPTotalDiseaseByRiskClassByAge();
		for (int r = 0; r < this.output.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.output.nScen + 1; scen++)
				for (int a = 0; a < this.output.maxAgeInSimulation + 1 + this.output.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.output.stepsInRun + 1; stepCount++)

							NDisease[scen][stepCount][r][a][g] += output.nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* probTotDisease[scen][stepCount][r][a][g];

	}

	private void makeDALYArrays() {
		NDaly = this.output.getNDisabledByRiskClassByAge();
		NOriDaly=new double [this.output.nScen + 1][this.output.nDim][this.output.nRiskFactorClasses][this.output.maxAgeInSimulation + 1][2];
		
		for (int scen = 0; scen < this.output.nScen + 1; scen++)
			for (int stepCount = 0; stepCount < this.output.nDim; stepCount++)
				for (int a = Math.max(0,this.output.minAgeInSimulation); a < this.output.maxAgeInSimulation + 1; a++)
					for (int s = 0; s <= 1; s++)
						for (int r = 0; r < this.output.nRiskFactorClasses; r++){
						//	log.fatal(" scen a s r"+scen+" "+a+" "+s+" "+r);
							NOriDaly[scen][stepCount][r][a][s] = this.output
									.getNPopByOriRiskClassByOriAge()[scen][stepCount][r][a][s]
									* this.output
											.getPDisabilityByOriRiskClassByOriAge()[scen][stepCount][r][a][s];
						}
		// TODO Auto-generated method stub

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
		log.info("About to write output to " + fileName);
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
			writer.writeAttribute("ss:Name", "year "
					+ (this.output.startYear + year));
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
			//	writeCell(xmlWriter, "std_riskFactor");
			//	writeCell(xmlWriter, "skewness");
				writeCell(writer, "risk class");
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
			writeCell(writer, "disability");
			writeCell(writer, "with disease");
			writeCell(writer, "mortality");
			for (int col = 0; col < this.output.nDiseases ; col++) {
				writeCell(writer, "incidence of "+this.output.diseaseNames[col]);
				

			}
			
			writer.writeEndElement();// </row>

			/* write the data */
			/* each row is a risk-class / age combination */
			int astart=this.output.minAgeInSimulationAtStart+year;
			if (this.output.withNewborns) astart=this.output.minAgeInSimulationAtStart;
			for (int rClass = 0; rClass < this.output.nRiskFactorClasses; rClass++)
				
				for (int a =astart; a < Math.min(this.output.maxAgeInSimulation+year+1,this.output.nDim); a++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
				//	log.fatal("start row");
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
							
							/* here we add the same value nriskclass times to the array
							 * not very efficient, but it works
							 * 
							 * 
							 */
							for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

								toBeAveragedRef[r] = this.output
										.getMeanRiskByRiskClassByAge()[0][year][rClass][a][sex];
								toBeAveragedScen[r] = this.output
										.getMeanRiskByRiskClassByAge()[thisScen][year][rClass][a][sex];
								numbersRef[r] = this.output
										.getNPopByRiskClassByAge()[0][year][rClass][a][sex];
								numbersScen[r] = this.output
										.getNPopByRiskClassByAge()[thisScen][year][rClass][a][sex];
							}
							mean = this.params.applySuccesrateToMean(
									toBeAveragedRef, toBeAveragedScen,
									numbersRef, numbersScen, thisScen, year, a,
									sex);

						} else {
							double[][] toBeAveragedRef2 = new double[this.output.nRiskFactorClasses] [ 2];
							double[][] toBeAveragedScen2 = new double[this.output.nRiskFactorClasses][ 2];
							double[][] numbersRef2 = new double[this.output.nRiskFactorClasses][ 2];
							double[][] numbersScen2 = new double[this.output.nRiskFactorClasses][ 2];
							

								for (int r = 0; r < this.output.nRiskFactorClasses; r++) {

									toBeAveragedRef2[r] = this.output.meanRiskByRiskClassByAge[0][year][rClass][a];
									toBeAveragedScen2[r] = this.output.meanRiskByRiskClassByAge[thisScen][year][rClass][a];
									numbersRef2[r]= this.output.nPopByRiskClassByAge[0][year][rClass][a];
									numbersScen2[r ] = this.output.nPopByRiskClassByAge[thisScen][year][rClass][a];
								}
							mean = this.params.applySuccesrateToMean(
									toBeAveragedRef2, toBeAveragedScen2,
									numbersRef2, numbersScen2, thisScen, year, a
									);
						}
//log.fatal("write mean");
						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor TODO */

					if (this.output.riskType == 2 && !this.output.categorized) {

					//	writeCell(xmlWriter, rClass);
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

						writeCell(writer, mean);

						//log.fatal("write mean");
					}

					/* write age */

					writeCell(writer, a);

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
					writeCell(writer, data);

			//		log.fatal("write npop");
					/* write disease info */

					if (this.details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
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
							writeCell(writer, data);

							//log.fatal("write diseasestate");
						}

					} else { /*
							 * if details is false: then write the data of
							 * diseases
							 */
						/* make summary array */
						

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
							writeCell(writer, data);
						//	log.fatal("write diseasestate");
						}

					}

					if (sex < 2) {
						data = this.params.applySuccesrate(
								NDaly[0][year][rClass][a][sex],
								NDaly[thisScen][year][rClass][a][sex], thisScen,
								year, a,sex);
						writeCell(writer, data);


					//	log.fatal("write daly");

						data = this.params.applySuccesrate(
								NDisease[0][year][rClass][a][sex],
								NDisease[thisScen][year][rClass][a][sex], thisScen,
								year, a,sex);
						writeCell(writer, data);
						
						
						data = this.params.applySuccesrate(
								this.output.getMortalityByRiskClassByAge()[0][year][rClass][a][sex],
								this.output.getMortalityByRiskClassByAge()[thisScen][year][rClass][a][sex], thisScen,
								year, a,sex);
						writeCell(writer, data);

					//	log.fatal("write nDisease");
					} else {
						data = this.params.applySuccesrateToBothGenders(
								NDaly[0][year][rClass][a],
								NDaly[thisScen][year][rClass][a], thisScen,
								year, a);
						writeCell(writer, data);

					//	log.fatal("write daly");

						data = this.params.applySuccesrateToBothGenders(
								NDisease[0][year][rClass][a],
								NDisease[thisScen][year][rClass][a], thisScen,
								year, a);
						writeCell(writer, data);
						
						data = this.params.applySuccesrateToBothGenders(
								this.output.getMortalityByRiskClassByAge()[0][year][rClass][a],
								this.output.getMortalityByRiskClassByAge()[thisScen][year][rClass][a], thisScen,
								year, a);
						writeCell(writer, data);
					//	log.fatal("write nDisease");
					}
					
					
					for (int col = 0; col < this.output.nDiseases ; col++) {

						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output.getNewCasesByRiskClassByAge()[0][year][col][rClass][a][sex],
											this.output.getNewCasesByRiskClassByAge()[thisScen][year][col][rClass][a][sex],
											thisScen, year, a, sex);

						} else {

							data = this.params
									.applySuccesrateToBothGenders(
											this.output.getNewCasesByRiskClassByAge()[0][year][col][rClass][a],
											this.output.getNewCasesByRiskClassByAge()[thisScen][year][col][rClass][a],
											thisScen, year, a);

						}
						writeCell(writer, data);
					//	log.fatal("write diseasestate");
					}

					writer.writeEndElement();

					// </row>
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
		log.info("About to write output to: " + fileName);
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
		for (int cohort = this.output.minAgeInSimulationAtStart; cohort < this.output.maxAgeInSimulation+1; cohort++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "age " + cohort + " in "
					+ this.output.startYear);
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.output.riskType == 1 || this.output.riskType == 3
					|| this.output.categorized) {
				writeCell(writer, "risk class in "+this.output.startYear);
			} else {
				writeCell(writer, "mean_riskFactor");
				//writeCell(xmlWriter, "std_riskFactor");
				//writeCell(xmlWriter, "skewness");

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
			writeCell(writer, "disability");
			writeCell(writer, "with disease");
			writeCell(writer, "mortality");
			for (int col = 0; col < this.output.nDiseases ; col++) {
			writeCell(writer, "incidence of "+this.output.diseaseNames[col]);
				

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
								toBeAveragedScen[r] = this.output.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
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
									numbersRef2[r][s] = this.output.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][s];
									numbersScen2[r][s] = this.output.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][s];

									;
								}
							mean = this.params
									.applySuccesrateToMeanToBothGenders(
											toBeAveragedRef2,
											toBeAveragedScen2, numbersRef2,
											numbersScen2, thisScen, 0, cohort);
						}
						// TODO veranderen

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

			//		if (this.output.riskType == 2 && !this.output.categorized) {

				//		writeCell(xmlWriter, rClass);
						// TODO vervangen door std risk factor

				//		writeCell(xmlWriter, rClass);

				//	}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
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

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, this.output.startYear + year);

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
							writeCell(writer, data);
						}

					} else {
						/* if details is false: then write the data of diseases */
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
							writeCell(writer, data);
						}

					}

					if (sex < 2) {
						data = this.params
								.applySuccesrate(
										this.NOriDaly[0][year][rClass][cohort][sex],
										this.NOriDaly[thisScen][year][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

					} else {

						data = this.params
								.applySuccesrateToBothGenders(
										this.NOriDaly[0][year][rClass][cohort],
										this.NOriDaly[thisScen][year][rClass][cohort],
										thisScen, 0, cohort);

					}
					writeCell(writer, data);
					
					if (sex < 2) {
						data = this.params
								.applySuccesrate(
										this.NoriDisease[0][year][rClass][cohort][sex],
										this.NoriDisease[thisScen][year][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

					} else {

						data = this.params
								.applySuccesrateToBothGenders(
										this.NoriDisease[0][year][rClass][cohort],
										this.NoriDisease[thisScen][year][rClass][cohort],
										thisScen, 0, cohort);

					}
					writeCell(writer, data);
					
					if (sex < 2) {
						data = this.params
								.applySuccesrate(
										this.output.getMortalityByOriRiskClassByOriAge(0, year, rClass, cohort, sex),
										this.output.getMortalityByOriRiskClassByOriAge(thisScen, year, rClass, cohort, sex),
										thisScen, 0, cohort, sex);

					} else {

						data = this.params
								.applySuccesrateToBothGenders(
										this.output.getMortalityByOriRiskClassByOriAge()[0][year][rClass][cohort],
										this.output.getMortalityByOriRiskClassByOriAge()[thisScen][year][rClass][cohort],
										thisScen, 0, cohort);

					}
					writeCell(writer, data);
					
					for (int col = 0; col < this.output.nDiseases ; col++) {
						if (sex < 2) {
							data = this.params
									.applySuccesrate(
											this.output.getNewCasesByOriRiskClassByOriAge()[0][year][col ][rClass][cohort][sex],
											this.output.getNewCasesByOriRiskClassByOriAge()[thisScen][year][col ][rClass][cohort][sex],
											thisScen, 0, cohort, sex);

						} else {

							data = this.params
									.applySuccesrateToBothGenders(
											this.output.getNewCasesByOriRiskClassByOriAge()[0][year][col ][rClass][cohort],
											this.output.getNewCasesByOriRiskClassByOriAge()[thisScen][year][col ][rClass][cohort],
											thisScen, 0, cohort);

						}
						writeCell(writer, data);
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
	 * @param xmlWriter
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
	 * @param xmlWriter
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
	 * @param xmlWriter
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
	 * @param xmlWriter
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
