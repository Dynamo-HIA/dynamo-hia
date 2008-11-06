package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W14.
 * The Observable contains a nonnegative Integer.
 */

import org.eclipse.core.databinding.observable.IObservable;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

public class OverallDALYWeightsObject extends AgeMap<SexMap<IObservable>>{

}
