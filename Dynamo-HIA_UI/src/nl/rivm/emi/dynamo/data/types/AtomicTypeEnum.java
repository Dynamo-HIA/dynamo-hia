package nl.rivm.emi.dynamo.data.types;

import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.types.atomic.Duration;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.Value;


public enum AtomicTypeEnum {
	AGE((AtomicTypeBase)new Age()),
	SEX((AtomicTypeBase)new Sex()),
	NUMBER((AtomicTypeBase)new Number()),
	CATEGORY((AtomicTypeBase)new Category()),
	DURATION((AtomicTypeBase)new Duration()),
	TRANSITIONSOURCE((AtomicTypeBase)new TransitionSource()),
	TRANSITIONDESTINATION((AtomicTypeBase)new TransitionDestination()),
	PERCENTAGE((AtomicTypeBase) new Percent()),
	PROBABILITY((AtomicTypeBase)new Probability()),
	STANDARDVALUE((AtomicTypeBase)new Value());
	
	private final AtomicTypeBase theType;

	private AtomicTypeEnum(AtomicTypeBase type) {
		this.theType = type;
	}

	public AtomicTypeBase getTheType() {
		return theType;
	}

	public String getElementName() {
		return theType.getXMLElementName();
	}
}