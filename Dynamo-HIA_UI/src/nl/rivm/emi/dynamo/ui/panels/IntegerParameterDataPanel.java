package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.IntegerVerifyListener;

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

public class IntegerParameterDataPanel extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;

	public IntegerParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData, DataBindingContext dataBindingContext,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		// this.setSize(300, 300);
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		// layout.numColumns = 5;
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		// Label femaleTestLabel = new Label(this, SWT.NONE);
		// femaleTestLabel.setText("FemaleTest");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		// Label maleTestLabel = new Label(this, SWT.NONE);
		// maleTestLabel.setText("MaleTest");
		for (int count = 0; count < lotsOfData.size(); count++) {
			TypedHashMap tHMap = (TypedHashMap) lotsOfData.get(count);
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(count).toString());
			bindValue(tHMap, BiGender.FEMALE_INDEX);
			// bindTestValue(sexMap, BiGender.FEMALE_INDEX);
			bindValue(tHMap, BiGender.MALE_INDEX);
			// bindTestValue(sexMap, BiGender.MALE_INDEX);
		}
	}

	public void handlePlacementInContainer(IntegerParameterDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindValue(TypedHashMap sexMap, int index) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		text.setText(sexMap.get(index).toString());
		FocusListener focusListener = new TypedFocusListener(XMLTagEntityEnum.NUMBER.getTheType(),theHelpGroup);
		text.addFocusListener(
//				new FocusListener() {
//			public void focusGained(FocusEvent arg0) {
//				theHelpGroup.getFieldHelpGroup().setHelpText("1");
//			}
//
//			public void focusLost(FocusEvent arg0) {
//				theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
//				// range.
//			}
//
//		}
		focusListener		);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
// 		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
	AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) ((ArrayList)sexMap.get(index)).get(0);
		WritableValue modelObservableValue = (WritableValue)tuple.getValue();
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue
						.getValueType()), ViewUpdateValueStrategies
						.getStrategy(modelObservableValue.getValueType()));
		text.addVerifyListener(new IntegerVerifyListener(theHelpGroup.getTheModal()));
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
		text.addVerifyListener(new IntegerVerifyListener(theHelpGroup.getTheModal()));
	}
}
