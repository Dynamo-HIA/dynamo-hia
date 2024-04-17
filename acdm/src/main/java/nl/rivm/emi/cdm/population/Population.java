package nl.rivm.emi.cdm.population;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import nl.rivm.emi.cdm.individual.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Population extends ArrayList<Individual> {
	/* implements java.io.Serializable */
	Log log = LogFactory.getLog(getClass().getName());

	String elementName;

	String label = "Not initialized.";

	public static final String xmlElementName = "pop";
	public static final String xmlLabelAttributeName = "lb";
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

	/*
	 * added by Hendriek :
	 */

	public void addAll(Population populationToAdd) {
		Iterator<Individual> individualIterator = populationToAdd.iterator();
		while (individualIterator.hasNext()) {
			this.add(individualIterator.next());

		}

	}

	/*
	 * added by Hendriek : deep copy method (gejat van internet, nog niet
	 * getest!!!!!!!!!!!!!!!!)
	 */
	public Population deepCopy() {

		return (Population) Population.copy(this);
	}

	/**
	 * Utility for making deep copies (vs. clone()'s shallow copies) of objects.
	 * Objects are first serialized and then deserialized. Error checking is
	 * fairly minimal in this implementation. If an object is encountered that
	 * cannot be serialized (or that references an object that cannot be
	 * serialized) an error is printed to System.err and null is returned.
	 * Depending on your specific application, it might make more sense to have
	 * copy(...) re-throw the exception.
	 * 
	 * NOT YET TESTED!!!!!!!!!!!!!!!!
	 */

	/**
	 * Returns a copy of the object, or null if the object cannot be serialized.
	 */
	public static Object copy(Object orig) {
		Object obj = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return obj;
	}
	/* added by Hendriek */
	/**
	 * resets the iterator, so it starts at the beginning of the population
	 */
	public void resetIterator(){ this.iterator=null;}

}
