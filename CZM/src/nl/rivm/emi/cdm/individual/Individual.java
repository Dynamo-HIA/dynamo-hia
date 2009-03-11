package nl.rivm.emi.cdm.individual;

import java.util.ArrayList;
import java.util.Iterator;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simplest individual that can be used in a simulation.
 * 
 * @author mondeelr
 * 
 */


public class Individual extends ArrayList<CharacteristicValueBase> {
	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Name of the tag that represents an Individual in configuration XML-files.
	 * 
	 */
	private String elementName;

	private String label = "Not initialized.";

	public static final String xmlElementName = "ind";

	private Long randomNumberGeneratorSeed = null;

		public Long getRandomNumberGeneratorSeed() {
		return randomNumberGeneratorSeed;
	}

	public void setRandomNumberGeneratorSeed(Long randomNumberGeneratorSeed) {
		this.randomNumberGeneratorSeed = randomNumberGeneratorSeed;
	}

	/* hendriek has added newborn and the generation of the newborn 
	 * as fields and as part of a new constructor */
	/* standard this is set to the value false and 0, and then everything acts like before */	
		private boolean newborn=false;
		private int generation=0;
		/* generation gives the number of timesteps (thus not years) 
		 * in which  this newborn enters the population
		 * characteristics at timesteps before this moment are empty */
		
		
		public Individual(String elementName, String label, boolean isNewborn, int generation2) {
			super();
			this.elementName = elementName;
			this.label = label;
			this.newborn =isNewborn;
			this.generation=generation2;
		}	
	
	
	public Individual(String elementName, String label) {
		super();
		this.elementName = elementName;
		this.label = label;
	}

	public String getElementName() {
		return elementName;
	}

	public int getCurrentIntCharacteristicValue(int characteristicIndex)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof IntCharacteristicValue)) {
				throw new CDMRunException(
						"No IntCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		return ((IntCharacteristicValue) cv).getCurrentValue();
	}

	public float getCurrentFloatCharacteristicValue(int characteristicIndex)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof FloatCharacteristicValue)) {
				throw new CDMRunException(
						"No FloatCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		return ((FloatCharacteristicValue) cv).getCurrentValue();
	}
	
/* added by Hendriek */
	public Float[] getCurrentCompoundCharacteristicValue(int characteristicIndex)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof FloatCharacteristicValue)) {
				throw new CDMRunException(
						"No FloatCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		return ((CompoundCharacteristicValue) cv).getCurrentValue();
	}
	
	
	/* added by Hendriek */
	public float[] getCurrentUnwrappedCompoundCharacteristicValue(int characteristicIndex)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof FloatCharacteristicValue)) {
				throw new CDMRunException(
						"No FloatCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		return ((CompoundCharacteristicValue) cv).getCurrentWrapperlessValue();
	}
	

	public void updateIntCharacteristicValue(int characteristicIndex, int newValue)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof IntCharacteristicValue)) {
				throw new CDMRunException(
						"No IntCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		((IntCharacteristicValue)cv).appendValue(newValue);
	}

	public void updateFloatCharacteristicValue(int characteristicIndex, float newValue)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof FloatCharacteristicValue)) {
				throw new CDMRunException(
						"No FloatCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		((FloatCharacteristicValue)cv).appendValue(newValue);
	}

	
	
/* added by Hendriek 
	

	public boolean isNewborn() {
		return newborn;
	}
	
	public int getGeneration() {
		return generation;
	}*/
	
	public void updateCompoundCharacteristicValue(int characteristicIndex, float [] newValue)
			throws CDMRunException {
		CharacteristicValueBase cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CDMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		} else {
			if (!(cv instanceof FloatCharacteristicValue)) {
				throw new CDMRunException(
						"No FloatCharacteristicValue found for index: "
								+ characteristicIndex);
			}
		}
		((CompoundCharacteristicValue)cv).appendValue(newValue);
	}
	
	/* end addition */

	/**
	 * Append / replace the value at index.
	 */
	public CharacteristicValueBase luxeSet(int index,
			CharacteristicValueBase value) {
		CharacteristicValueBase result = null;
		if (index >= this.size()) {
			int count = this.size();
			// Fill up.
			for (; count < index; count++) {
				add(count, null);
			}
			add(count, value);
		} else {
			log.warn("Replacing CharacteristicValue.");
			result = this.get(index);
			set(index, value);
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

	public Iterator<CharacteristicValueBase> iterator() {
		return new CharacteristicValueIterator();
	}

	


	class CharacteristicValueIterator implements
			Iterator<CharacteristicValueBase> {
		/**
		 * Some status bookkeeping.
		 */
		int currentIndex = -1;

		int nextIndex = -1;

		int lastReturnedIndex = -1;

		public boolean hasNext() {
			nextIndex = -1; // Invalidate.
			for (int count = currentIndex + 1; count < size(); count++) {
				if (get(count) != null) {
					nextIndex = count;
					break;
				}
			}
			return (nextIndex != -1);
		}

		public CharacteristicValueBase next() {
			CharacteristicValueBase found = null;
			// Sanity check.
			if (-1 < nextIndex && nextIndex < size()) {
				currentIndex = nextIndex;
				found = get(currentIndex);
				if (found != null) {
					lastReturnedIndex = currentIndex;
				}
			}
			return found;
		}

		public void remove() {
			set(lastReturnedIndex, null);
		}
	}

}
