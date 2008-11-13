package nl.rivm.emi.dynamo.datahandling;

import java.io.File;
import java.util.ArrayList;

import nl.rivm.emi.dynamo.estimation.DiseaseClusterStructure;
import nl.rivm.emi.dynamo.estimation.ModelParameters;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationFileFactory {
	
	
	
	
	/**
	 * @param M: object containing modelparameters
	 * @param Basedir: root of directory structure
	 */
	public void manufactureSimulationConfigurationFile(DynamoConfigurationData config ,ModelParameters param) {
		
		
		/** This method writes 2 main configuration XML files and a collection of supporting XML files per characteristic;
		 * there are separate methods for the supporting XML files
		 * 
		 * the first XML files give the form of the characterics in the simulation and looks like
		 * 
		 * <?xml version="1.0" encoding="UTF-8"?>
			<characteristics>
			<ch>
		<id>1</id>
		<lb>age</lb>
		<type>numericalcontinuous</type>
		
			</ch>
			<ch>
		<id>2</id>
		<lb>sex</lb>
		<type>categorical</type>
		<possiblevalues>
			<vl>0</vl>
			<vl>1</vl>		
			
		</possiblevalues>
			</ch> .......
			</characteristics>
		 * 
		 * 
		 * the second main XLM file gives the running information and the updaterules and looks like
		<?xml version="1.0" encoding="UTF-8"?>
		<sim>
		<lb>eersteproeve</lb>
		<timestep>1</timestep>
		<runmode>longitudinal</runmode>
		<stepsbetweensaves>1</stepsbetweensaves>
		<stepsinrun>100</stepsinrun>
		   <stoppingcondition/>
		   <pop>c:/hendriek/java/workspace/dynamo/dynamodata/pop_hb.xml</pop>
		   <updaterules>
		      <updaterule>
		         <characteristicid>1</characteristicid>
		         <classname>nl.rivm.emi.cdm.rules.update.dynamo.AgeOneToOneUpdateRule</classname>
		      </updaterule> ....
		      <updaterule>
		         <characteristicid>10</characteristicid>
		         <classname>nl.rivm.emi.cdm.rules.update.dynamo.AgeOneToOneUpdateRule</classname>
		      </updaterule>
		   </updaterules>
		</sim>
		*
		*  files will be written both at the same time to prevent that they are not synchronised

*/
		
		// first read DynamoConfigurationData from xml;
		// TODO: put results in class DynamoConfigurationData
		
		HierarchicalConfiguration configSimulation = new HierarchicalConfiguration();
		// write <sim>
		// write <lb> met tekst: "getSimulationName()
		// write <timestep> getTimeStep </timestep>
		// <runmode>longitudinal</runmode>
		//<stepsbetweensaves>1</stepsbetweensaves>
		//	<stepsinrun>105</stepsinrun>
		//  <stoppingcondition/>
		//<pop> name initial population: InitialPopulation.XML </pop>
		//<updaterules>
		
		// write: TO 1
		// <characteristics>
		//	<ch>
		//<id>1</id>
		//<lb>age</lb>
		//<type>numericalcontinuous</type>
		 /* write: TO 2
		<updaterule>
        <characteristicid>1</characteristicid>
        <classname>nl.rivm.emi.cdm.rules.update.dynamo.AgeOneToOneUpdateRule</classname>
     </updaterule> 
     
     // write: TO 1
     <ch>
		<id>2</id>
		<lb>sex</lb>
		<type>categorical</type>
		<possiblevalues>
			<vl>0</vl>
			<vl>1</vl>		
			
		</possiblevalues>
			</ch>
      
      // write: TO 2
     <updaterule>
     <characteristicid>2</characteristicid>
     <classname>nl.rivm.emi.cdm.rules.update.dynamo.SexOneToOneUpdateRule</classname>
  </updaterule>
  <updaterule>
  */
		if (config.getRiskFactor().getType()=="categorical") {
			
			/* get number of possible values from prevalence data */
			
			int nValues=param.getBaselinePrevalenceOdds().length;
			
			/* write: TO 1
		//     <ch>
		//		<id>3</id>
		//		<lb>config.getRiskFactor().getName()</lb>
		//		<type>categorical</type>
		//		<possiblevalues>*/
				for (int n=0;n<nValues;n++){
					// write <vl>n</vl>
			
				}
					
			/*	</possiblevalues>
					</ch> 
					*/
			
			
			
			
			// write: TO 2
			// <updaterule>
			 //<characteristicid>3</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule
			// </classname>
	        //
			//<configurationfile> BASEDIR/SIMULATIONS/"+getSimulationName()+"/configurationFiles/riskFactorConfig.xml</configurationfile>
		    //  </updaterule>
			
			
		}
		if (config.getRiskFactor().getType()=="continuous") {
			
			/* write: TO 1
			//     <ch>
			//		<id>3</id>
			//		<lb>config.getRiskFactor().getName()</lb>
			//		<type>numericalcontinuous</type>
			//		</ch>
			
			 // write: TO 2
			// <updaterule>
			 //<characteristicid>3</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.ContinuousRiskFactorMultiToOneUpdateRule
			// </classname>
				 //
			//<configurationfile> BASEDIR/SIMULATIONS/"+getSimulationName()+"/configurationFiles/riskFactorConfig.xml</configurationfile>
		    //  </updaterule>
			
		*/	
		}
		int currentChar=4;
		if (config.getRiskFactor().getType()=="compound") {
			
			
	/* get number of possible values from prevalence data */
			
			int nValues=param.getBaselinePrevalenceOdds().length;
			
			/* write: TO 1
		//     <ch>
		//		<id>3</id>
		//		<lb>config.getRiskFactor().getName()</lb>
		//		<type>categorical</type>
		//		<possiblevalues>*/
				for (int n=0;n<nValues;n++){
					// write <vl>n</vl>
				}
					
					
			/*	</possiblevalues>
					</ch> 
					*/
			 // write: TO 2
			// <updaterule>
			 //<characteristicid>3</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule
			// </classname>
			//<configurationfile> BASEDIR/SIMULATIONS/"+getSimulationName()+"/configurationFiles/riskFactorConfig.xml</configurationfile>
	        //  </updaterule>
				
				
				/* write: TO 1
				//     <ch>
				//		<id>4</id>
				//		<lb>"config.getRiskFactor().getName()"+"_duration"</lb>
				//		<type>numericalcontinuous</type>
				//		</ch>
             // write: TO 2
			// <updaterule>
			 //<characteristicid>4</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.RiskFactorDurationMultiToOneUpdateRule
			// </classname>
	        //
		   //  </updaterule>
			currentChar=5;	
			*/
		}
		
		
		
	/* now the disease part */	
		/* this only uses ModelParameters */
		
		/* for running the model it is essential that the order here corresponds with the configuration data written later */
		/* ordering follows the ordering in ModelParameters, but cancers (=diseases with cured fraction) are now
		 * given 2 numbers in stead of one */
		/* some form of checking would be nice but not yet implemented */
		/* also writting the configuration data simultaneously would make things better */
		 
		
		for (int c=0;c<param.getNCluster();c++){
			
		DiseaseClusterStructure structure = param.getClusterStructure()[c];
		int NDiseases=structure.getNinCluster();	
		int  DiseaseNumber=structure.getDiseaseNumber()[0];
		if (!structure.isWithCuredFraction()&& NDiseases==1)// single disease
			 
		{
			
			/* write: TO 1
			//     <ch>
			//		<id>currentChar</id>
			//		<lb>"structure.DiseaseName()[0]"</lb>
			//		<type>numericalcontinuous</type>
			//		</ch>
			
			
			// write: TO 2
			// <updaterule>
		
			 //<characteristicid>currentChar</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.SingleDiseaseMultiToOneUpdateRule
			// </classname>
			//<configurationfile> BASEDIR"/SIMULATIONS/"+getSimulationName()+"/configurationFiles/disease"+currentChar+"Config.xml</configurationfile>
	        */
			
			currentChar++;}
		if (structure.isWithCuredFraction())
			
			
		{ 
			
			/* write: TO 1
			//     <ch>
			//		<id>currentChar</id>
			//		<lb>structure.DiseaseName()[0]+"_cured"</lb>
			//		<type>numericalcontinuous</type>
			//		</ch>
			
			// write: TO 2
			// <updaterule>
		 //<characteristicid>currentChar</characteristicid>
		// <classname>
		// nl.rivm.emi.cdm.rules.update.dynamo.TwoDiseasesMultiToOneUpdateRule
		// </classname>
		//<configurationfile> BASEDIR"/SIMULATIONS/"+getSimulationName()+"/configurationFiles/disease"+currentChar+"Config.xml</configurationfile>
        */
			
		currentChar++;
		
		/* write: TO 1
		//     <ch>
		//		<id>currentChar</id>
		//		<lb>structure.DiseaseName()[0]+"_notcured"</lb>
		//		<type>numericalcontinuous</type>
		//		</ch>
		 // write: TO 2
		// <updaterule>
		 //<characteristicid>currentChar</characteristicid>
		// <classname>
		// nl.rivm.emi.cdm.rules.update.dynamo.TwoDiseasesDiseaseMultiToOneUpdateRule
		// </classname>
		//<configurationfile> BASEDIR"/SIMULATIONS/"+getSimulationName()+"/configurationFiles/disease"+currentChar+"Config.xml</configurationfile>
       */
		currentChar++;
		}
		
		if (!structure.isWithCuredFraction()&& NDiseases>1){
			for (int combi=1;combi<Math.pow(2,structure.getNinCluster());combi++){
				
			String charName=Integer.toBinaryString(combi);
			/* add 0's at the beginning to represent all diseases */
			while (charName.length()<structure.getNinCluster())
			{charName="0"+charName;}
			charName="Cluster_"+c+"_"+charName;
				/* write: TO 1
				//     <ch>
				//		<id>currentChar</id>
				//		<lb>charName</lb>
				//		<type>numericalcontinuous</type>
				//		</ch>
				 // write: TO 2
			// <updaterule>
			 //<characteristicid>currentChar</characteristicid>
			// <classname>
			// nl.rivm.emi.cdm.rules.update.dynamo.ClusterDiseaseMultiToOneUpdateRule
			// </classname>
			//<configurationfile> BASEDIR"/SIMULATIONS/"+getSimulationName()+"/configurationFiles/disease"+currentChar+"Config.xml</configurationfile>
	        */
				
			currentChar++;}
		}
		
	}// end cluster loop;
		
		/* lastly write the update rule for survival */
		
		/* write: TO 1
		//     <ch>
		//		<id>currentChar</id>
		//		<lb>structure.DiseaseName()[0]+"_notcured"</lb>
		//		<type>numericalcontinuous</type>
		//		</ch>
		 * 
		 *  // write: TO 2
	
		// <updaterule>
		 //<characteristicid>currentChar</characteristicid>
		// <classname>
		// nl.rivm.emi.cdm.rules.update.dynamo.SurvivalMultiToOneUpdateRule
		// </classname>
		//<configurationfile> BASEDIR"/SIMULATIONS/"+getSimulationName()+"/configurationFiles/disease"+currentChar+"Config.xml</configurationfile>
       */	
	
	
	}
		
		
		
	
	
	
	
	
	public void manufactureRiskFactorConfiguration(ModelParameters M, String Basedir, int index){}
		
	public void manufactureDiseaseConfiguration(ModelParameters M, String Basedir, int index){}
	public void manufactureSurvivalConfiguration(ModelParameters M, String Basedir, int index){}
	
}
