package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;

public class StartingYear extends AbstractRangedInteger {
	static final protected String XMLElementName = "startingyear";

	public StartingYear(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
}
