package nl.rivm.emi.dynamo.data.types;

import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Alpha;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.Categories;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.types.atomic.Duration;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceValue;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;


public enum XMLTagEntityEnum {
	AGE((XMLTagEntity)new Age()),
	SEX((XMLTagEntity)new Sex()),
	NUMBER((XMLTagEntity)new Number()),
	NAME((XMLTagEntity)new Name()),
	UNIQUENAME((XMLTagEntity)new UniqueName()),
	CATEGORY((XMLTagEntity)new Category()),
	DURATION((XMLTagEntity)new Duration()),
	TRANSITIONSOURCE((XMLTagEntity)new TransitionSource()),
	TRANSITIONDESTINATION((XMLTagEntity)new TransitionDestination()),
	PERCENTAGE((XMLTagEntity) new Percent()),
	PROBABILITY((XMLTagEntity)new Probability()),
	CLASSES((XMLTagEntity)new Categories()),
//	REFERENCECLASS((XMLTagEntity)new ReferenceClass()),
//	DURATIONCLASS((XMLTagEntity)new DurationClass()),
	REFERENCEVALUE((XMLTagEntity)new ReferenceValue()),
	STANDARDVALUE((XMLTagEntity)new Value()),
	BEGIN((XMLTagEntity)new Begin()),
	ALPHA((XMLTagEntity)new Alpha()),
	END((XMLTagEntity)new End());
	
	private final XMLTagEntity theType;

	private XMLTagEntityEnum(XMLTagEntity type){
		this.theType = type;
	}

	public XMLTagEntity getTheType() {
		return theType;
	}

	public String getElementName() {
		return theType.getXMLElementName();
	}
}