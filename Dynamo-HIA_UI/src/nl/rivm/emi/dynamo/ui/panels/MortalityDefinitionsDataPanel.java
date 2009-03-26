package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.Unit;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
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

public class MortalityDefinitionsDataPanel extends Composite /* implements Runnable */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.MortalityDefinitionsDataPanel");
	IMortalityObject myMortalityObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType = new Unit();

	public MortalityDefinitionsDataPanel(Composite parent, Text topNeighbour,
			IMortalityObject iMortalityObject,
			DataBindingContext dataBindingContext, 
			HelpGroup helpGroup, String unitType) {
		super(parent, SWT.NONE);
		this.myMortalityObject = iMortalityObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label unitLabel = new Label(this, SWT.NONE);
		unitLabel.setText("Unit: " + unitType);
		Label acutelyFatalLabel = new Label(this, SWT.NONE);
		acutelyFatalLabel.setText("Acutely Fatal");
		Label curedFractionLabel = new Label(this, SWT.NONE);
		curedFractionLabel.setText("Cured Fraction");
		int numberOfAges = iMortalityObject.getNumberOfMortalities();
		for (int count = 0; count < numberOfAges; count++) {
			//WritableValue observableClassName = myMortalityObject.getObservableCategoryName(count);
//			if(observableClassName != null){
	//			Label label = new Label(this, SWT.NONE);
		//		label.setText(new Integer(count).toString());
		//		bindValue(observableClassName);
				
				myMortalityObject.putMortality(count, "");
				Label label = new Label(this, SWT.NONE);
				label.setText(new Integer(count).toString());
				bindValue(myMortalityObject.getObservableUnit(count));
				////TODO REACTIVATE other values bindValue(myMortalityObject.getObservableAcutelyFatal(count));
				////TODO REACTIVATE other values bindValue(myMortalityObject.getObservableCuredFraction(count));				
				
				/*
			} else {
				MessageBox box = new MessageBox(parent.getShell());
				box.setText("Error creating matrix value");
				box.setMessage("Matrix value at age " + count + " should not be empty.");
				box.open();
			}*/
		}
	}

	private void bindValue(WritableValue observableClassName) {
		Text text = createAndPlaceTextField();
		text.setText((String)observableClassName.doGetValue());
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
																	// range.
			}

		});
//	Too early, see below.	text.addVerifyListener(new StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				myType.getModelUpdateValueStrategy(), myType.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}


	public void handlePlacementInContainer(MortalityDefinitionsDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}
}
