 
package nl.rivm.emi.cdm.characteristic.types;
import java.util.regex.Pattern;
/**
 * @author Hendriek
 *
 */
abstract public class AbstractCompoundCharacteristicType   extends
			AbstractCharacteristicType {

		Pattern matchPattern;

		Float lowerLimit;

		Float upperLimit;
		
		int nElements;
		
		int nElementsFilled=0;

		protected AbstractCompoundCharacteristicType(String type) {
			super(type);
		}

		public void setLowerLimit(String lowerLimit) {
			this.lowerLimit = Float.valueOf(lowerLimit);
		}

		public void setUpperLimit(String upperLimit) {
			this.upperLimit = Float.valueOf(upperLimit);
		}

		public void setLowerLimit(Float lowerLimit) {
			this.lowerLimit = lowerLimit;
		}

		public void setUpperLimit(Float upperLimit) {
			this.upperLimit = upperLimit;
		}

		abstract public boolean setLimits(Float lowerLimit, Float upperLimit);

		public Float getLowerLimit() {
			return lowerLimit;
		}

		public Float getUpperLimit() {
			return upperLimit;
		}
		@Override
		public boolean isCategoricalType() {
			return false;
		}
		@Override
		public boolean isCompoundType() {
			return true;
		}
		@Override
		abstract public String humanReadableReport();

		public int getNElementsFilled() {
			return nElementsFilled;
		}

		public void setNElementsFilled(int elementsFilled) {
			nElementsFilled = elementsFilled;
		}

		public int getNElements() {
			return nElements;
		}

		public void setNElements(int elements) {
			nElements = elements;
		}
	}


