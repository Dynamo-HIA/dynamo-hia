package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.interfaces.IParameterTypeObject;
import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MortalityDefinitionsDataPanel extends Composite /*
															 * implements
															 * Runnable
															 */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.MortalityDefinitionsDataPanel");
	IMortalityObject myMortalityObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;

	// AtomicTypeBase myType = new Unit();

	@SuppressWarnings("unchecked")
	public MortalityDefinitionsDataPanel(Composite parent, Text topNeighbour,
			IMortalityObject iMortalityObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			UnitTypeComboModifyListener unitTypeModifyListener) {
		super(parent, SWT.NONE);
		this.myMortalityObject = iMortalityObject;
		// 20100409
		boolean acutelyFaltalChosen = ExcessMortalityObject.ParameterTypeHelperClass.ACUTELY_FATAL
				.equals(((IParameterTypeObject) myMortalityObject)
						.getParameterType());
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		// layout.numColumns = 7;
		layout.numColumns = 5;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		// Second line
		@SuppressWarnings("unused")
		Label filler1Label = new Label(this, SWT.NONE);
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		@SuppressWarnings("unused")
		Label filler2Label = new Label(this, SWT.NONE);
		// Label filler3Label = new Label(this, SWT.NONE);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		@SuppressWarnings("unused")
		Label filler4Label = new Label(this, SWT.NONE);
		// Label filler5Label = new Label(this, SWT.NONE);
		// Second line
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label unitLabel = new Label(this, SWT.NONE);
		String unitText = unitTypeModifyListener.registerLabel(unitLabel);
		unitLabel.setText(unitText);
		// Label acutelyFatalLabel = new Label(this, SWT.NONE);
		// acutelyFatalLabel.setText("Acutely Fatal");
		// Label curedFractionLabel = new Label(this, SWT.NONE);
		// curedFractionLabel.setText("Cured Fraction");
		Label parameterLabel = new Label(this, SWT.NONE);
		if (acutelyFaltalChosen) {
			parameterLabel
					.setText(ExcessMortalityObject.ParameterTypeHelperClass.ACUTELY_FATAL);
		} else {
			parameterLabel
					.setText(ExcessMortalityObject.ParameterTypeHelperClass.CURED_FRACTION);
		}
		Label femaleUnitLabel = new Label(this, SWT.NONE);
		unitText = unitTypeModifyListener.registerLabel(femaleUnitLabel);
		femaleUnitLabel.setText(unitText);
		Label femaleParameterLabel = new Label(this, SWT.NONE);
		if (acutelyFaltalChosen) {
			femaleParameterLabel
					.setText(ExcessMortalityObject.ParameterTypeHelperClass.ACUTELY_FATAL);
		} else {
			femaleParameterLabel
					.setText(ExcessMortalityObject.ParameterTypeHelperClass.CURED_FRACTION);
		}
		// Data panel.
		// int numberOfAges = iMortalityObject.getNumberOfMortalities();
		TypedHashMap<Age> ageMap = iMortalityObject.getMortalities();
		int numberOfAges = ageMap.size();
		for (int AgeCount = 0; AgeCount < numberOfAges; AgeCount++) {
			Label label = new Label(this, SWT.NONE);
			label.setText(Integer.valueOf(AgeCount).toString());
			TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(AgeCount);
			int numberOfSexes = sexMap.size();
			for (int sexCount = 0; sexCount < numberOfSexes; sexCount++) {
				ArrayList<AtomicTypeObjectTuple> arrayList = (ArrayList<AtomicTypeObjectTuple>) sexMap
						.get(sexCount);
				for (int paramCount = 0; paramCount < arrayList.size(); paramCount++) {
					if (!((acutelyFaltalChosen && (paramCount == 2)) || (!acutelyFaltalChosen && (paramCount == 1)))) {
						AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
						@SuppressWarnings("rawtypes")
						WritableValue observableClassName = (WritableValue) tuple
								.getValue();
						XMLTagEntity theType = tuple.getType();
						bindValue(observableClassName,
								(AtomicTypeBase<Float>) theType);
					}
				}
			}

			/*
			 * } else { MessageBox box = new MessageBox(parent.getShell());
			 * box.setText("Error creating matrix value");
			 * box.setMessage("Matrix value at age " + count +
			 * " should not be empty."); box.open(); }
			 */
		}
	}

	@SuppressWarnings("unchecked")
	private void bindValue(@SuppressWarnings("rawtypes") WritableValue observableClassName,
			AtomicTypeBase<Float> theType) {
		Text text = createAndPlaceTextField();
		text.setText(theType.convert4View(observableClassName.doGetValue()));
		// FocusListener focusListener = new TypedFocusListener(theType,
		// theHelpGroup);
		// text.addFocusListener(focusListener);
		HelpTextListenerUtil.addHelpTextListeners(text, theType);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		@SuppressWarnings("rawtypes")
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);

		@SuppressWarnings("rawtypes")
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup
				.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
}
