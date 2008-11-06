package nl.rivm.emi.dynamo.data.objects;

import org.eclipse.core.databinding.observable.IObservable;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

/**
 * Object to contain the data entered in W12.
 * The Observable contains a nonegative Float with eight decimals.
 */

public class OverallMortalityObject extends AgeMap<SexMap<IObservable>>{

}
