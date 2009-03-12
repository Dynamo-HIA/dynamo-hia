package nl.rivm.emi.dynamo.data.types;

import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Alpha;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.DynamoClass;
import nl.rivm.emi.dynamo.data.types.atomic.Classes;
import nl.rivm.emi.dynamo.data.types.atomic.Duration;
import nl.rivm.emi.dynamo.data.types.atomic.DurationClass;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.types.atomic.Mean;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceValue;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.atomic.Year;


public enum XMLTagEntityEnum {
	AGE((XMLTagEntity)new Age()),
	ALPHA((XMLTagEntity)new Alpha()),
	BEGIN((XMLTagEntity)new Begin()),
	CLASS((XMLTagEntity)new DynamoClass()),
	CLASSES((XMLTagEntity)new Classes()),
	DURATION((XMLTagEntity)new Duration()),
	DURATIONCLASS((XMLTagEntity)new DurationClass()),
	END((XMLTagEntity)new End()),
	HASNEWBORNS((XMLTagEntity)new HasNewborns()),
	INDEX((XMLTagEntity)new Index()),
	NAME((XMLTagEntity)new Name()),
	NUMBER((XMLTagEntity)new Number()),
	PERCENTAGE((XMLTagEntity) new Percent()),
	PROBABILITY((XMLTagEntity)new Probability()),
	REFERENCECLASS((XMLTagEntity)new ReferenceClass()),
	REFERENCEVALUE((XMLTagEntity)new ReferenceValue()),
	SEX((XMLTagEntity)new Sex()),
	STANDARDVALUE((XMLTagEntity)new Value()),
	TRANSITIONDESTINATION((XMLTagEntity)new TransitionDestination()),
	TRANSITIONSOURCE((XMLTagEntity)new TransitionSource()),
	UNIQUENAME((XMLTagEntity)new UniqueName()),
	//TODO Reactivate (9-3-2009): HASNEWBORNS((XMLTagEntity)new HasNewborns()),
	YEAR((XMLTagEntity)new Year()),
	MEAN((XMLTagEntity)new Mean());
	/*TREND((XMLTagEntity)new Trend()*/
	
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