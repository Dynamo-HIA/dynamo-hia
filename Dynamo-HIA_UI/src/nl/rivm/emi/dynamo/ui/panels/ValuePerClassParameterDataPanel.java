package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;

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

public class ValuePerClassParameterDataPanel extends Composite /*
																 * implements
																 * Runnable
																 */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;

	public ValuePerClassParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData, DataBindingContext dataBindingContext,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"value");
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label classLabel = new Label(this, SWT.NONE);
		classLabel.setText("Class");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		for (int ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			TypedHashMap oneAgeMap = (TypedHashMap) lotsOfData.get(ageCount);
			TypedHashMap femaleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.FEMALE_INDEX);
			TypedHashMap maleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.MALE_INDEX);
			for (int classCount = 1; classCount <= femaleClassHMap.size(); classCount++) {
				Label ageCellLabel = new Label(this, SWT.NONE);
				if (classCount == 1) {
					ageCellLabel.setText(new Integer(ageCount).toString());
				}
				Label classCellLabel = new Label(this, SWT.NONE);
				classCellLabel.setText(new Integer(classCount).toString());
				bindValue(femaleClassHMap, classCount);
				bindValue(maleClassHMap, classCount);
			}
		}
	}

	public void handlePlacementInContainer(
			ValuePerClassParameterDataPanel panel, Label topNeighbour) {
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
		String convertedText = ((Value) myType).convert4View(typedHashMap.get(
				index).toString());
		text.setText(convertedText);
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}

		});
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) typedHashMap
				.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((Value) myType).getModelUpdateValueStrategy(),
				((Value) myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
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
