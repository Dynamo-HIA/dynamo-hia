package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.verify.CategoryIndexVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.FloatValueVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.TypedFocusListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ReferenceValueDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	IReferenceValue myReferenceValueObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<Float> myType = new Value();

	public ReferenceValueDataPanel(Composite parent, Composite topNeighbour,
			IReferenceValue referenceValueObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myReferenceValueObject = referenceValueObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Reference value:");
		WritableValue observableObject = referenceValueObject
				.getObservableReferenceValue();
		if (observableObject != null) {
			bindValue(observableObject);
		} else {
			MessageBox box = new MessageBox(parent.getShell());
			box.setText("Reference value error");
			box.setMessage("Reference value is invalid.");
			box.open();
		}
	}

	private void bindValue(WritableValue observableObject) {
		Text text = createAndPlaceTextField();
		text.setText((String) myType
				.convert4View(observableObject.doGetValue()));
		FocusListener focusListener = new TypedFocusListener(myType,
				theHelpGroup);
		text.addFocusListener(
		// new FocusListener() {
				// public void focusGained(FocusEvent arg0) {
				// theHelpGroup.getFieldHelpGroup().setHelpText("1");
				// }
				//
				// public void focusLost(FocusEvent arg0) {
				// theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
				// // range.
				// }
				//
				// }
				focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new FloatValueVerifyListener(theHelpGroup.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}

	public void handlePlacementInContainer(ReferenceValueDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindTestValue(TypedHashMap sexMap, int index) {
		Text text = new Text(this, SWT.NONE);
		text.setText(sexMap.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue
						.getValueType()), ViewUpdateValueStrategies
						.getStrategy(modelObservableValue.getValueType()));
	}
}
