package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

	public MortalityDefinitionsDataPanel(Composite parent, Text topNeighbour,
			IMortalityObject iMortalityObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			UnitTypeComboModifyListener unitTypeModifyListener) {
		super(parent, SWT.NONE);
		this.myMortalityObject = iMortalityObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label unitLabel = new Label(this, SWT.NONE);
		String unitText = unitTypeModifyListener.registerLabel(unitLabel);
		unitLabel.setText(unitText);
		Label acutelyFatalLabel = new Label(this, SWT.NONE);
		acutelyFatalLabel.setText("Acutely Fatal");
		Label curedFractionLabel = new Label(this, SWT.NONE);
		curedFractionLabel.setText("Cured Fraction");
		Label maleUnitLabel = new Label(this, SWT.NONE);
		unitText = unitTypeModifyListener.registerLabel(maleUnitLabel);
		maleUnitLabel.setText(unitText);
		Label maleAcutelyFatalLabel = new Label(this, SWT.NONE);
		maleAcutelyFatalLabel.setText("Acutely Fatal");
		Label maleCuredFractionLabel = new Label(this, SWT.NONE);
		maleCuredFractionLabel.setText("Cured Fraction");
		// int numberOfAges = iMortalityObject.getNumberOfMortalities();
		TypedHashMap<Age> ageMap = iMortalityObject.getMortalities();
		int numberOfAges = ageMap.size();
		for (int AgeCount = 0; AgeCount < numberOfAges; AgeCount++) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(AgeCount).toString());
			TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(AgeCount);
			int numberOfSexes = sexMap.size();
			for (int sexCount = 0; sexCount < numberOfSexes; sexCount++) {
				ArrayList<AtomicTypeObjectTuple> arrayList = (ArrayList<AtomicTypeObjectTuple>) sexMap
						.get(sexCount);
				for (int paramCount = 0; paramCount < arrayList.size(); paramCount++) {
					AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
					WritableValue observableClassName = (WritableValue) tuple
							.getValue();
					XMLTagEntity theType = tuple.getType();
					bindValue(observableClassName,
							(AtomicTypeBase<Float>) theType);
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

	private void bindValue(WritableValue observableClassName,
			AtomicTypeBase<Float> theType) {
		Text text = createAndPlaceTextField();
		text.setText(theType.convert4View(observableClassName.doGetValue()));
		FocusListener focusListener = new TypedFocusListener(theType,theHelpGroup);
text.addFocusListener(
//		new FocusListener() {
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
		focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}

//	public void handlePlacementInContainer(MortalityDefinitionsDataPanel panel,
//			Label topNeighbour) {
//		FormData formData = new FormData();
//		formData.top = new FormAttachment(topNeighbour, 10);
//		formData.right = new FormAttachment(100, -10);
//		formData.bottom = new FormAttachment(100, -10);
//		formData.left = new FormAttachment(0, 10);
//		panel.setLayoutData(formData);
//	}
}
