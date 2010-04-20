package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IDurationClass;
import nl.rivm.emi.dynamo.data.types.atomic.DurationClass;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.DurationClassIndexComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

public class DurationClassDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	IDurationClass myDurationClassObject;
	final Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<Integer> myType = new DurationClass();
	Combo dropDown = null;
	DurationClassIndexComboModifyListener durationClassIndexComboModifyListener = null;

	public DurationClassDataPanel(Composite parent, Composite topNeighbour,
			IDurationClass durationClassObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myDurationClassObject = durationClassObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 120;
		final Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Duration class index:");
		indexLabel.setLayoutData(gridData);
		// 20100415 The durationClass index is no longer changeable.
		//
		// WritableValue observableObject = referenceCategoryObject
		// .getObservableDurationClass();
		// if (observableObject != null) {
		// createAndHookupDropDown(observableObject,
		// (ICategoricalObject)referenceCategoryObject);
		// } else {
		// MessageBox box = new MessageBox(parent.getShell());
		// box.setText("Duration Class error");
		// box.setMessage("Duration Class is absent.");
		// box.open();
		// }
		Integer durationClassIndex = durationClassObject.getDurationClass();
		Label durationClassIndexLabel = new Label(this, SWT.NONE);
		durationClassIndexLabel.setText(durationClassIndex.toString());
	}

	// 20100415 The durationClass index is no longer changeable.
	//
	// private void createAndHookupDropDown(WritableValue writableValue,
	// ICategoricalObject riskFactorConfig) {
	// int numberOfClasses = riskFactorConfig.getNumberOfCategories();
	// dropDown = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
	// HelpTextListenerUtil.addHelpTextListeners(dropDown, myType);
	// GridData dropDownGridData = new GridData(GridData.FILL_HORIZONTAL);
	// dropDown.setLayoutData(dropDownGridData);
	// DropDownPropertiesSet selectableReferenceClassIndexPropertiesSet = new
	// DropDownPropertiesSet();
	// for (int count = 1; count <= numberOfClasses; count++) {
	// selectableReferenceClassIndexPropertiesSet.add((new Integer(count)
	// .toString()));
	// dropDown.add((new Integer(count)).toString(), count - 1);
	// }
	// int initialIndex = 0;
	// if (writableValue != null) {
	// String initialValue = (String) (writableValue.doGetValue())
	// .toString();
	// if (selectableReferenceClassIndexPropertiesSet
	// .contains(initialValue)) {
	// initialIndex = selectableReferenceClassIndexPropertiesSet
	// .getSelectedIndex(initialValue);
	// }
	// }
	// this.durationClassIndexComboModifyListener = new
	// DurationClassIndexComboModifyListener(
	// writableValue);
	// dropDown.addModifyListener(durationClassIndexComboModifyListener);
	// dropDown.select(initialIndex);
	// }

	public void handlePlacementInContainer(DurationClassDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}
}
