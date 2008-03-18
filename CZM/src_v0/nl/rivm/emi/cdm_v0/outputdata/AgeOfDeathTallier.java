package nl.rivm.emi.cdm_v0.outputdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgeOfDeathTallier {
	Log logger = LogFactory.getLog(getClass().getName());

	int[] deathCountByIndex;
	int totalDeaths;
	public AgeOfDeathTallier(int numberOfBuckets){
		deathCountByIndex = new int[numberOfBuckets];
		for(int count = 0; count < numberOfBuckets; count++){
			deathCountByIndex[count] = 0;
		}
		totalDeaths = 0;
	}
	public void tallyOneDeath(int index){
		deathCountByIndex[index] = deathCountByIndex[index]+1;
		totalDeaths += 1;
	}

	public void dumpSurvivalTable(){
		int survivorCount = totalDeaths;
		logger.info("Born were " + survivorCount + " individuals.");
		int numberOfBuckets = deathCountByIndex.length;
		for( int count = 0; count<numberOfBuckets; count++){
			survivorCount -= deathCountByIndex[count];
			logger.info(deathCountByIndex[count] + " individuals died at age " 
					+ count + ", " + survivorCount + " survived.");
			logger.warn("\""+ count + "\";\"" + survivorCount + "\"");
		}
	}
}
