package nl.rivm.emi.cdm.population;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import nl.rivm.emi.cdm.individual.CSVIndividualWriter;
import nl.rivm.emi.cdm.individual.Individual;




public abstract class CSVPopulationWriter {

	Log log = LogFactory.getLog(getClass().getName());

	public CSVPopulationWriter() {
		super();
	}

public static void writePopulation(String fileName, Population population, int firstStep, int lastStep,String headers) throws IOException {
	  FileWriter writer = new FileWriter(fileName+".csv"); 
	  if (headers !="" && headers!=null)writer.append(headers+"\n");
	  for (int stepNumber=firstStep;stepNumber<=lastStep;stepNumber++)
	  writeStepToCSVFile(writer, population,  stepNumber);
	  writer.flush();
	  writer.close();
}

public static void writePopulation(String fileName, Population population, int stepNumber) throws IOException {
	  FileWriter writer = new FileWriter(fileName+".csv");
	  writeStepToCSVFile(writer, population,  stepNumber);
	  writer.flush();
	  writer.close();
	  
}


public static void writeStepToCSVFile(FileWriter writer, Population population, int stepNumber) throws IOException{
  
Individual individual;
population.resetIterator();
while ((individual = population.nextIndividual()) != null) {	
  String individualString=   CSVIndividualWriter.generateCSVrecord(individual, stepNumber);
  /* check if this step is present in the individual */
  if (individualString!="") {
	  writer.append(((Integer)stepNumber).toString()+";");
	  writer.append(individualString);
	  writer.append('\n');}
  }
 

}
}


