package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 * Object to contain the data entered in W32.
 */
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

import org.eclipse.core.databinding.observable.IObservable;

public class ObservableIncidencesObject extends AgeMap<SexMap<IObservable>> implements ObservableObjectMarker{

}
