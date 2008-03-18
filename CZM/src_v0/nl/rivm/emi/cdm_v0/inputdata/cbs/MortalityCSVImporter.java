package nl.rivm.emi.cdm_v0.inputdata.cbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import nl.rivm.emi.cdm_v0.exceptions.MantissaTooLongException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MortalityCSVImporter {
	static Log logger = LogFactory
			.getLog("nl.rivm.emi.cdm.inputdata.cbs.MortalityCVSImporter");
	static final int ageIndex = 1;
	static final int maleIndex = 5;
	static final int femaleIndex = 7;

	static public ArrayList<ArrayList> importFile(String filePath)
			throws IOException {
		ArrayList<ArrayList> dataContainer = new ArrayList<ArrayList>();
		ArrayList<Integer> theAges = new ArrayList<Integer>();
		dataContainer.add(theAges);
		ArrayList<Float> maleChances = new ArrayList<Float>();
		dataContainer.add(maleChances);
		ArrayList<Float> femaleChances = new ArrayList<Float>();
		dataContainer.add(femaleChances);
			File importFile = new File(filePath);
		FileReader fileReader = new FileReader(importFile);
		BufferedReader bReader = new BufferedReader(fileReader);
		logger.error("Log something.");	

		int numHeaderLines = 3;
		String line;

		for (int count = 0; count < numHeaderLines; count++) {
			line = bReader.readLine();
		}
		String[] stukkies;
		line = bReader.readLine();
		stukkies = line.split("\"");
		while (stukkies[1].indexOf("jaar") != -1) {
			int firstSpace = stukkies[1].indexOf(' ');
			String ageString = stukkies[1].substring(0, firstSpace);
			try {
				theAges.add(new Integer(ageString));
				
			} catch (NumberFormatException e) {
				logger.error(ageString + "cannot be parsed as an integer.");
			}
			try {
				stukkies[maleIndex] = stukkies[maleIndex].replace(',', '.');
				maleChances.add(new Float(stukkies[maleIndex]));
				
			} catch (NumberFormatException e) {
				logger.error(stukkies[maleIndex] + " cannot be parsed as a Float.");
			}
			try {
				stukkies[femaleIndex] = stukkies[femaleIndex].replace(',', '.');
				femaleChances.add(new Float(stukkies[femaleIndex]));
				
			} catch (NumberFormatException e) {
				logger.error(stukkies[femaleIndex] + " cannot be parsed as a Float.");
			}
			line = bReader.readLine();
			stukkies = line.split("\"");
		}
		for(int count = 0; count < theAges.size(); count++ ){
			logger.info("TheAges[" + count + "] =" + theAges.get(count) + " maleChance: " 
					+ maleChances.get(count) + " femaleChances: " + femaleChances.get(count));
		}
		return dataContainer;
	}

	static public ArrayList<ArrayList> importFile2IntegerChances(String filePath)
			throws IOException {
		ArrayList<ArrayList> dataContainer = new ArrayList<ArrayList>();
		ArrayList<Float> theAges = new ArrayList<Float>();
		dataContainer.add(theAges);
		ArrayList<Integer> maleChances = new ArrayList<Integer>();
		dataContainer.add(maleChances);
		ArrayList<Integer> femaleChances = new ArrayList<Integer>();
		dataContainer.add(femaleChances);
			File importFile = new File(filePath);
		FileReader fileReader = new FileReader(importFile);
		BufferedReader bReader = new BufferedReader(fileReader);
		logger.error("Log something.");	

		int numHeaderLines = 3;
		String line;

		for (int count = 0; count < numHeaderLines; count++) {
			line = bReader.readLine();
		}
		String[] stukkies;
		line = bReader.readLine();
		stukkies = line.split("\"");
		while (stukkies[1].indexOf("jaar") != -1) {
			int firstSpace = stukkies[1].indexOf(' ');
			String ageString = stukkies[1].substring(0, firstSpace);
			try {
				theAges.add(new Float(ageString));
				
			} catch (NumberFormatException e) {
				logger.error(ageString + "cannot be parsed as an integer.");
			}
			try {
				Integer tweakedMantissa = tweakMantissa2Integer(stukkies[maleIndex], 5);
				maleChances.add(tweakedMantissa);
				
			} catch (MantissaTooLongException e) {
			logger.error(e.getMessage());
			}
			catch (NumberFormatException e) {
				logger.error(stukkies[maleIndex] + " cannot be parsed as an Integer.");
			}
			try {
				Integer tweakedMantissa;
					tweakedMantissa = tweakMantissa2Integer(stukkies[femaleIndex], 5);
				femaleChances.add(tweakedMantissa);
			} catch (MantissaTooLongException e) {
			logger.error(e.getMessage());
			}
			 catch (NumberFormatException e) {
				logger.error(stukkies[femaleIndex] + " cannot be parsed as a Float.");
			}
			line = bReader.readLine();
			stukkies = line.split("\"");
		}
		for(int count = 0; count < theAges.size(); count++ ){
			logger.info("TheAges[" + count + "] =" + theAges.get(count) + " maleChance: " 
					+ maleChances.get(count) + " femaleChances: " + femaleChances.get(count));
		}
		return dataContainer;
	}

	private static Integer tweakMantissa2Integer(String origineel, int aantalCijfers) throws MantissaTooLongException {
		String naDeKomma = origineel.substring(origineel.indexOf(',') + 1, origineel.length());
		if(naDeKomma.length() > aantalCijfers){
			throw new MantissaTooLongException(naDeKomma.length(), aantalCijfers);
		}

		for(int count = naDeKomma.length(); count < aantalCijfers; count++){
			naDeKomma = naDeKomma + "0";
		}
		return new Integer(naDeKomma);
	}
}
