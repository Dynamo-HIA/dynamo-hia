package nl.rivm.emi.cdm.population;

import java.util.ArrayList;
import java.util.Iterator;

import nl.rivm.emi.cdm.individual.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Population extends ArrayList<Individual> {

	Log log = LogFactory.getLog(getClass().getName());

	String elementName;

	String label = "Not initialized.";

	public static final String xmlElementName = "pop";
	public static final String xmlLabelElementName = "lb";
	public static final String populationFileNotWriteableMsg = "File %1$s could not be written.";

	private Iterator<Individual> iterator = null;

	public Population(String elementName, String label) {
		super();
		this.elementName = elementName;
		this.label = label;
	}

	public boolean addIndividual(Individual individual) {
		return add(individual);
	}

	/**
	 * Iterates over the Individuals in the Population. Restarts after the end
	 * has been reached (returns null once when it does). TODO Make a more
	 * robust implementation.
	 * 
	 * @return null if there is no Individual (left). the next Individual. The
	 *         first call returns the first individual.
	 */
	public Individual nextIndividual() {
		Individual result = null;
		if (iterator == null) {
			iterator = super.iterator();
		}
		if (iterator.hasNext()) {
			result = iterator.next();
		} else {
			iterator = null;
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getElementName() {
		return elementName;
	}

}
