package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 * Object to contain the data entered in W22 for categorical 
 * and the categorical part of the compound prevalences.
 * 
 * @author mondeelr
 *
 */
import org.eclipse.core.databinding.observable.IObservable;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.containers.CategoryMap;

public class PrevalencesCategoricalObject extends AgeMap<SexMap<CategoryMap<IObservable>>> implements PrevalencesMarker{

}
