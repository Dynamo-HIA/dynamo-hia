package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
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
import org.eclipse.swt.widgets.Text;

public class ValueParameterDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;

	public ValueParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData, DataBindingContext dataBindingContext,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"value");
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		for (int count = 0; count < lotsOfData.size(); count++) {
			TypedHashMap tHMap = (TypedHashMap) lotsOfData.get(count);
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(count).toString());
			bindValue(tHMap, BiGender.FEMALE_INDEX);
			bindValue(tHMap, BiGender.MALE_INDEX);
		}
	}

	public void handlePlacementInContainer(ValueParameterDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindValue(TypedHashMap typedHashMap, int index) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) typedHashMap
				.get(index);
		WritableValue modelObservableValue = (WritableValue) ((AtomicTypeObjectTuple) list
				.get(0)).getValue();
		AtomicTypeBase<Float> theType = (AtomicTypeBase<Float>) list.get(0)
				.getType();
		String convertedText = theType.convert4View(modelObservableValue
				.doGetValue());
		text.setText(convertedText);
		FocusListener focusListener = new TypedFocusListener(theType,
				theHelpGroup);
		text.addFocusListener(focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((Value) myType).getModelUpdateValueStrategy(),
				((Value) myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup
				.getTheModal()));
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
