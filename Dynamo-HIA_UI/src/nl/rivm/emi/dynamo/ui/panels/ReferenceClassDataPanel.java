package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.ReferenceClassIndexComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

public class ReferenceClassDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	IReferenceClass myReferenceCategoryObject;
	Composite myParent = null;
	boolean open = false;
//	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<Integer> myType = new ReferenceClass();
	Combo dropDown = null;
	ReferenceClassIndexComboModifyListener referenceClassIndexComboModifyListener = null;

	public ReferenceClassDataPanel(Composite parent, Composite topNeighbour,
			IReferenceClass riskFactorConfigurationObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myParent = parent;
		this.myReferenceCategoryObject = riskFactorConfigurationObject;
//		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 120;
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Referenceclass index:");
		indexLabel.setLayoutData(gridData);
		WritableValue observableObject = riskFactorConfigurationObject
				.getObservableReferenceClass();
		if (observableObject != null) {
			// bindValue(observableObject);
			createAndHookupDropDown(observableObject,
					(ICategoricalObject)riskFactorConfigurationObject);
		} else {
			MessageBox box = new MessageBox(parent.getShell());
			box.setText("Referenceclass error");
			box.setMessage("Referenceclass is absent.");
			box.open();
		}
	}

	// private void bindValue(WritableValue observableObject) {
	// Text text = createAndPlaceTextField();
	// text.setText((String) myType
	// .convert4View(observableObject.doGetValue()));
	// HelpTextListenerUtil.addHelpTextListeners(text, myType);
	// // Too early, see below. text.addVerifyListener(new
	// // StandardValueVerifyListener());
	// IObservableValue textObservableValue = SWTObservables.observeText(text,
	// SWT.Modify);
	// dataBindingContext.bindValue(textObservableValue, observableObject,
	// myType.getModelUpdateValueStrategy(), myType
	// .getViewUpdateValueStrategy());
	// text.addVerifyListener(new CategoryIndexVerifyListener(theHelpGroup
	// .getTheModal(), (NumberRangeTypeBase<Integer>) myType));
	// }
	//
	// private Text createAndPlaceTextField() {
	// Text text = new Text(this, SWT.NONE);
	// GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	// gridData.widthHint = 50;
	// text.setLayoutData(gridData);
	// return text;
	// }

	private void createAndHookupDropDown(WritableValue writableValue,
			ICategoricalObject riskFactorConfig) {
		int numberOfClasses = riskFactorConfig.getNumberOfCategories();
		dropDown = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		HelpTextListenerUtil.addHelpTextListeners(dropDown, myType);
		GridData dropDownGridData = new GridData(GridData.FILL_HORIZONTAL);
		dropDown.setLayoutData(dropDownGridData);
		DropDownPropertiesSet selectableReferenceClassIndexPropertiesSet = new DropDownPropertiesSet();
		for (int count = 1; count <= numberOfClasses; count++) {
			selectableReferenceClassIndexPropertiesSet.add((new Integer(count)
					.toString()));
			dropDown.add((new Integer(count)).toString(), count - 1);
		}
		int initialIndex = 0;
		if (writableValue != null) {
			String initialValue = (String) (writableValue.doGetValue())
					.toString();
			if (selectableReferenceClassIndexPropertiesSet
					.contains(initialValue)) {
				initialIndex = selectableReferenceClassIndexPropertiesSet
						.getSelectedIndex(initialValue);
			}
		}
		this.referenceClassIndexComboModifyListener = new ReferenceClassIndexComboModifyListener(
				writableValue);
		dropDown.addModifyListener(referenceClassIndexComboModifyListener);
		dropDown.select(initialIndex);
	}
}
