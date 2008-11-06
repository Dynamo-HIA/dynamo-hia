package nl.rivm.emi.dynamo.data.values;

import nl.rivm.emi.dynamo.data.types.atomic.Age;

import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr
 * 
 */
public class AgeWritableValue extends WritableValue {
	/**
	 * Default constructor, the only accessible one.
	 */
	public AgeWritableValue() {
//		super(null, Integer.class);
		super(new Integer(-1), Integer.class);
		}

	/**
	 * Private constructor to block direct access to the superclass one.
	 * 
	 * @param initialValue
	 * @param valueType
	 */
	private AgeWritableValue(Object initialValue, Object valueType) {
		super(initialValue, valueType);
	}

	public void doSetValue(Object value) {
		if (value instanceof Integer) {
			doSetValue((Integer) value);
		} else {
			throw new UnsupportedOperationException(
					"Integers only in AgeWritableValue");
		}
	}

	/**
	 * Flipflop an invalid value to get it to the view...
	 * 
	 * @param value
	 */
	public void doSetValue(Integer value) {
		IntegerValueDiff integerValueDiff = new IntegerValueDiff( (Integer)super.doGetValue(), value);
		if (!new Age().inRange(value)) {
			super.fireValueChange(integerValueDiff);
		} else {
			super.doSetValue(value);
		}
	}

	static class IntegerValueDiff extends ValueDiff {
		private Integer oldValue;
		private Integer newValue;

		public IntegerValueDiff(Integer oldValue, Integer newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public Object getNewValue() {
			return newValue;
		}

		@Override
		public Object getOldValue() {
			return oldValue;
		}
	}

}
