
/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.estimation.DynSimRunPRInterface;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * writes output files with life expectancies in the form of csv files.
 * 
 * @author boshuizh
 * 
 */
public class CSVLEwriter implements Runnable  {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.CSVLEWriter");

	/* object with the output data */
	private CDMOutputFactory output;
	private String delimiter = ",";
	private DynamoPlotFactory factory;
    private String filename;
    private DynSimRunPRInterface pr;
    
	FileWriter writer ;
    public FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename)  {
		this.filename = filename;
		
	}

	public boolean isSullivan() {
		return sullivan;
	}

	public void setSullivan(boolean sullivan) {
		this.sullivan = sullivan;
	}

	private boolean sullivan;
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	private ScenarioParameters params;

	

	
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

	public CSVLEwriter(CDMOutputFactory output, ScenarioParameters params, DynSimRunPRInterface pr) {
		super();
		this.params = params;
		this.output = output;
		this.factory = new DynamoPlotFactory(output, params);
		this.pr=pr;
	
	}

	public void run()
			 {

		    
			
			
/* we use a second string for the data of females, as they are generate at the same time as those
 * for men, but need to be put on another line
 */
			StringBuilder toWriteCSVString = new StringBuilder();
			StringBuilder toWriteCSVString2 = new StringBuilder();

			
			/* write column headings */
			if (sullivan) toWriteCSVString.append("year" + this.delimiter);
			toWriteCSVString.append("scenario" + this.delimiter);
			toWriteCSVString.append("gender" + this.delimiter);
			toWriteCSVString.append("age" + this.delimiter);

			/* total number */
			toWriteCSVString.append("total life expectancy" + this.delimiter);
			
			/* disease info */
			toWriteCSVString.append("expectancy with disability"+ this.delimiter);
			toWriteCSVString.append("expectancy with all diseases");

			for (int col = 0; col < this.output.nDiseases; col++) {
				toWriteCSVString.append(this.delimiter + "life expectany with "
						+ this.output.diseaseNames[col]);

			}
			
			/* the end of the line */
			toWriteCSVString.append("\n");// </row>
			
			/* now write the data */
			int endyear=this.output.stepsInRun;
			if (!sullivan) endyear=1;
			for (int year = 0; year < endyear ; year++) {
				int year2= year+this.output.getStartYear();
				//log.fatal(" start loop year " +year);
				for (int thisScen = 0; thisScen < this.output.nScen + 1; thisScen++)

					/* write the data */
					/* each row is a year / risk-class / age /sex combination */

					for (int a = 0; a < 96; a++) {

						
						if (sullivan) toWriteCSVString.append(year2 + this.delimiter);
						toWriteCSVString.append(this.output.scenarioNames[thisScen] + this.delimiter);
						toWriteCSVString.append("males" + this.delimiter);
						toWriteCSVString.append(a );
						if (sullivan) toWriteCSVString2.append(year2 + this.delimiter);
						toWriteCSVString2.append(this.output.scenarioNames[thisScen] + this.delimiter);
						toWriteCSVString2.append("females" + this.delimiter);
						toWriteCSVString2.append(a );
						/* write risk factor info */
						for (int d = -2; d < output.nDiseases; d++) {

							/*
							 * return array [sex][type] where type=0 gives total
							 * life expectancy and type=1 gives the healthy life
							 * expectancy and type=2 the starting population;
							 * [0][0] is made -1 if calculation fails
							 */
						//	log.fatal(" start loop age " +a+ "  diseaseno "+d);
							double[][] LE;
							if (sullivan)
								LE = factory.calculateSullivanLifeExpectancy(
										year, a,  d, thisScen);
							else
								LE = factory.calculateCohortHealthExpectancy(a,
										 thisScen, d);
						//	log.fatal(" le calculated ");
							/*
							 * first write the total life expectancy, needs to
							 * be done only once ( for d=-2)
							 */

							if (d == -2) {
								toWriteCSVString.append( this.delimiter+LE[0][0]
										);
								toWriteCSVString2.append(this.delimiter+LE[1][0]
										);
							}
							/* write the health life expectancy */
							toWriteCSVString.append(this.delimiter + LE[0][1]  );
							toWriteCSVString2.append(this.delimiter+LE[1][1]);

						}
                     
						toWriteCSVString.append("\n");// end of </row>
						toWriteCSVString2.append("\n");// end of </row>
					
					
						
					}
			}
			// end loop over years, scenarios and gender
			try {
				this.writer.append(toWriteCSVString);
				this.writer.append(toWriteCSVString2);
				this.writer.flush();
				this.writer.close();
				
			} catch (IOException e) {	
				((DynSimRunPRInterface) pr).usedToBeErrorMessageWindow("file " + this.filename
						+ " can not be written. \nPlease make sure that"
						+ " this file is not in use by another program.");
				
				
				
					
					e.printStackTrace();
				}
				
			}
			

		

	}



