package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Mean;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TransitionValueParameterDataPanel extends GenericParameterDataPanel {

	public TransitionValueParameterDataPanel(Composite parent,
			Text topNeighbour, TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			PanelMatrix panelMatrix) {
		super(parent, topNeighbour, lotsOfData, dataBindingContext, helpGroup,
				panelMatrix);
	}
	
	@Override
	protected void bindAbstractValue(TypedHashMap typedHashMap, int index, AtomicTypeBase myType) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		String convertedText = ((Mean)myType).convert4View(typedHashMap.get(index).toString());
		text.setText(convertedText);
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				TransitionValueParameterDataPanel.this.theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				TransitionValueParameterDataPanel.this.theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
																	// range.
			}

		});
		// Too early, see below.	text.addVerifyListener(new StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) typedHashMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((Mean)myType).getModelUpdateValueStrategy(), ((Mean)myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
	}

	@Override
	protected void bindAbstractString(TypedHashMap typedHashMap, int index,
			AtomicTypeBase myType) {
		// No need to implement this method, it is not used 		
	}
	
}