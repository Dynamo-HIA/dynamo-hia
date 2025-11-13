package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractDataPanel extends Composite {

	private DataBindingContext dataBindingContext = null;

	public AbstractDataPanel(Composite parent, int style, 
			DataBindingContext dataBindingContext) {
		super(parent, SWT.NONE);
		this.dataBindingContext = dataBindingContext;
		
	}
	
	// and other basetypes
	// Call the other  
	// Look to Matrix and others
	protected void bindValue(TypedHashMap<?> typedHashMap, int index, 
			AtomicTypeBase<?> myType) {
		if (myType instanceof AbstractValue) {
			bindAbstractValue(typedHashMap, index, myType);
		} else
		if (myType instanceof AbstractString) {
			bindAbstractString(typedHashMap, index, myType);
		}		
	}
	
	@SuppressWarnings("unchecked")
	protected void bindTestValue(TypedHashMap<?> sexMap, int index) {
		final Text text = new Text(this, SWT.NONE);
		text.setText(sexMap.get(index).toString());

		// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		@SuppressWarnings("rawtypes")
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		
		@SuppressWarnings("rawtypes")
		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
		this.dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue
						.getValueType()), ViewUpdateValueStrategies
						.getStrategy(modelObservableValue.getValueType()));
	}
	
	protected abstract void bindAbstractValue(TypedHashMap<?> typedHashMap, int index, 
			AtomicTypeBase<?> myType);
	
	// Binds values that are subclass types of AbstractString
	protected abstract void bindAbstractString(TypedHashMap<?> typedHashMap, int index, 
			AtomicTypeBase<?> myType);

	// Binds values that are subclass types of AbstractBoolean
	protected void bindAbstractBoolean(TypedHashMap<?> typedHashMap, int index, 
			AtomicTypeBase<?> myType) {
		
	}
	
}
