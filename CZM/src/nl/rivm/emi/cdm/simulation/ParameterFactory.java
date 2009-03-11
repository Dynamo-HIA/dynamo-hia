package nl.rivm.emi.cdm.simulation;

import java.io.File;

import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class ParameterFactory {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	double[][][][] transitionMatrix;

	public ParameterFactory() {
	}

	/*
	 * double p= new double[nCat]; int age=0; int gender=0; int nCat=3; int
	 * oldvalue=1; double Newvalue; double [] p = new double [nCat]; for (int
	 * c=0;c<nCat;c++){ p[c]=transitionMatrix[age][gender][oldvalue][c];}
	 * Newvalue=DynamoLib.draw(p,randseed); return Newvalue;
	 */

	double[][][][] getParameters(File fln) throws ConfigurationException {
		if (fln.exists()) {
			XMLConfiguration transitionConfiguration;
		}

		/* else throw new Exception("file "+fln+" not found"); */

		int nCat = 3;
		double[][][][] transitions = new double[96][2][nCat][nCat];

		try {
			XMLConfiguration transitionConfiguration = new XMLConfiguration(fln);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			transitionConfiguration.clear();

			// Validate the xml by xsd schema
			transitionConfiguration.setValidating(true);
			transitionConfiguration.load();

			// Retrieve the xml root node
			ConfigurationNode configurationNode = transitionConfiguration
					.getRootNode();

			// Get the Children from the xml root node and determine its size
			// Number of ages is 96, number of sexes is 2
			int listSize = configurationNode.getChildren().size() / (96 * 2);

			// Calculate the number of categories
			nCat = (int) Math.sqrt(listSize);

			// Retrieve the values from the categories of the xml
			for (int t = 0; t < listSize; t++) {
				int a = Integer.parseInt(transitionConfiguration
						.getString("transition(" + t + ").age"));
				int g = Integer.parseInt(transitionConfiguration
						.getString("transition(" + t + ").gender"));
				int to = Integer.parseInt(transitionConfiguration
						.getString("transition(" + t + ").to"));
				int from = Integer.parseInt(transitionConfiguration
						.getString("transition(" + t + ").from"));
				transitions[a][g][from][to] = Double
						.parseDouble(transitionConfiguration
								.getString("transition(" + t + ").value"));
			}
			return transitions;
		} catch (ConfigurationException e) {			
			ErrorMessageUtil.handleErrorMessage(this.log, "", e, 
					fln.getAbsolutePath());
			return transitions;
		}

	}


	@Test
	public void testrun() {
		ParameterFactory f = new ParameterFactory();
		File fln = new File(
				"c:/hendriek/java/workspace/dynamo/dynamodata/transitions.xml");
		try {
			f.transitionMatrix = getParameters(fln);
		} catch (ConfigurationException e) {
			this.log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
