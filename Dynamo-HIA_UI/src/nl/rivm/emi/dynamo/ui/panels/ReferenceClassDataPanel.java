package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.CategoryIndexVerifyListener;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ReferenceClassDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	IReferenceClass myReferenceCategoryObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<Integer> myType = new ReferenceClass();

	public ReferenceClassDataPanel(Composite parent, Composite topNeighbour,
			IReferenceClass referenceCategoryObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myReferenceCategoryObject = referenceCategoryObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Referenceclass index:");
		WritableValue observableObject = referenceCategoryObject
				.getObservableReferenceClass();
		if (observableObject != null) {
			bindValue(observableObject);
		} else {
			MessageBox box = new MessageBox(parent.getShell());
			box.setText("Referenceclass error");
			box.setMessage("Referenceclass is absent.");
			box.open();
		}
	}

	private void bindValue(WritableValue observableObject) {
		Text text = createAndPlaceTextField();
		text.setText((String) myType.convert4View(observableObject.doGetValue()));
		FocusListener focusListener = new TypedFocusListener(myType,theHelpGroup);
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
				focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new CategoryIndexVerifyListener(theHelpGroup.getTheModal(), (NumberRangeTypeBase<Integer>)myType));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
}
