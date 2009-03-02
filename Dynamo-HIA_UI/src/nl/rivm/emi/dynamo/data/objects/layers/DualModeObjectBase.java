package nl.rivm.emi.dynamo.data.objects.layers;

<<<<<<< .mine
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
=======
>>>>>>> .r294

/**
 * BaseClass for Dynamo-HIA configuration Object s/parts. 
 * They can contain either Observables for use behind
 * the User Interface or unwrapped Objects for use in the model.
 * 
 * @author mondeelr
 * 
 */
public abstract class DualModeObjectBase extends XMLTagEntity{
	protected boolean observable;

<<<<<<< .mine
	protected DualModeObjectBase(boolean observable, String tagName) {
		super(tagName);
=======
	protected DualModeObjectBase(boolean observable) {
>>>>>>> .r294
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
