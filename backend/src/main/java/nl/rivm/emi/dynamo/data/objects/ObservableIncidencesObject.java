package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 * Object to contain the data entered in W32.
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractAge;

public class ObservableIncidencesObject extends TypedHashMap<Age> implements ObservableObjectMarker{
	private static final long serialVersionUID = 3842282209503745605L;

	public ObservableIncidencesObject(AbstractAge theType) {
		super((Age)XMLTagEntityEnum.AGE.getTheType());
	}
}
