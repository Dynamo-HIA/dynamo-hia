package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.ICutoffs;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class CutoffDefinitionsDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory.getLog(this.getClass().getName());
	ICutoffs myCutoffsObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<Float> myType = new Value();

	public CutoffDefinitionsDataPanel(Composite parent, Text topNeighbour,
			ICutoffs cutoffsObject, DataBindingContext dataBindingContext,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myCutoffsObject = cutoffsObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Index");
		Label classNameLabel = new Label(this, SWT.NONE);
		classNameLabel.setText("Cutoff-value");
		int found = 1;
		int count = 1;
		int numberOfCutoffs = cutoffsObject.getNumberOfCutoffs();
		for (; found <= numberOfCutoffs && count <= numberOfCutoffs; count++) {
			WritableValue observableCutoffValue = cutoffsObject
					.getObservableCutoffValue(count);
			if (observableCutoffValue != null) {
				found++;
				Label label = new Label(this, SWT.NONE);
				label.setText(new Integer(count).toString());
				bindValue(observableCutoffValue);
			} else {
				MessageBox box = new MessageBox(parent.getShell());
				box.setText("Class name error");
				box.setMessage("Name at index " + count
						+ " should not be empty.");
				box.open();
			}
		}
	}

	private void bindValue(WritableValue observableClassName) {
		Text text = createAndPlaceTextField();
		text.setText(myType.convert4View(observableClassName.doGetValue()));
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}

		});
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
//		text.addVerifyListener(new ValueVerifyListener());
		text.addVerifyListener(new ValueVerifyListener());
		}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
}
