package nl.rivm.emi.cdm.simulation;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.rules.update.dynamo.AgeOneToOneUpdateRule;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;




public class ParameterFactory  {
	
	double [][][][] transitionMatrix	;

	
public	ParameterFactory() {}

	/*
	 * double p= new double[nCat]; int age=0; int gender=0; int nCat=3; int
	 * oldvalue=1; double Newvalue; double [] p = new double [nCat]; for (int
	 * c=0;c<nCat;c++){ p[c]=transitionMatrix[age][gender][oldvalue][c];}
	 * *Newvalue=DynamoLib.draw(p,randseed); return Newvalue;
	 * 
	 */


	


	
	


double [][][][] getParameters(File fln) 
{if (fln.exists()) {
	XMLConfiguration transitionConfiguration;}
	
	/* else throw new Exception("file "+fln+" not found");*/
	
	int nCat=3;
	double [][] [][]transitions=new double [96][2][nCat][nCat];
	
	try {
		XMLConfiguration transitionConfiguration = new XMLConfiguration(fln);
	

	List transList = transitionConfiguration.getList();
	nCat=(int) Math.sqrt(transList.size());
	
	
	for (int t=0;t<transList.size();t++){ 
		int a= Integer.parseInt(transitionConfiguration.getString("transition("+t+").age"));
		int g= Integer.parseInt(transitionConfiguration.getString("transition("+t+").gender"));
		int to= Integer.parseInt(transitionConfiguration.getString("transition("+t+").to"));
		int from= Integer.parseInt(transitionConfiguration.getString("transition("+t+").from"));
		transitions[a][g][from][to]= Double.parseDouble(transitionConfiguration.getString("transition("+t+").value"));}
	return transitions;
	} catch (ConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return transitions;
	}
		
	}



@Test
public void testrun(){
	 ParameterFactory f = new ParameterFactory();
	 File fln = new File("c:/hendriek/java/workspace/dynamo/dynamodata/transitions.xml");
	 f.transitionMatrix=getParameters(fln);}}
	 
	 
