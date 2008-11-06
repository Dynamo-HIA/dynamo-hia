package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 */
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

import org.eclipse.core.databinding.observable.IObservable;

public class IncidencesObject extends AgeMap<SexMap<IObservable>>{

}
