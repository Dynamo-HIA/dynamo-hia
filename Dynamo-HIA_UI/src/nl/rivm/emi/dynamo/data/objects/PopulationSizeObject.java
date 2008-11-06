package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W11.
 * The Observable contains a nonegative Integer.
 */
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

import org.eclipse.core.databinding.observable.IObservable;

public class PopulationSizeObject extends AgeMap<SexMap<IObservable>>{

}
