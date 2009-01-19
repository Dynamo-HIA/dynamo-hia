package nl.rivm.emi.dynamo.data.objects.layers;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

/**
 * BaseClass for Dynamo-HIA configuration Object s/parts. 
 * They can contain either Observables for use behind
 * the User Interface or unwrapped Objects for use in the model.
 * 
 * @author mondeelr
 * 
 */
public abstract class DualModeObjectBase {
	protected boolean observable;

	protected DualModeObjectBase(boolean observable) {
		super();
		this.observable = observable;
	}

	/**
	 * Does this Object contains it's parameters wrapped in Observable Objects?
	 * @return
	 */
	public boolean isObservable() {
		return observable;
	}
}
