package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.Calendar;
import java.util.Date;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class StartingYear extends AbstractRangedInteger implements PayloadType<Integer>{
	static final protected String XMLElementName = "startingYear";

	public StartingYear(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * The default year will be the current year
	 */
	@Override
	public Integer getDefaultValue() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return Integer.valueOf(calendar.get(Calendar.YEAR));
	}
	
}
