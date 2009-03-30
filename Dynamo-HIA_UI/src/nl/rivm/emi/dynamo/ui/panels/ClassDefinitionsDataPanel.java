package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.NameVerifyListener;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ClassDefinitionsDataPanel extends Composite /* implements Runnable */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	ICategoricalObject myCategoricalObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType = new Name();

	public ClassDefinitionsDataPanel(Composite parent, Text topNeighbour,
			ICategoricalObject iCategoricalObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.myCategoricalObject = iCategoricalObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Index");
		Label classNameLabel = new Label(this, SWT.NONE);
		classNameLabel.setText("Classname");
		int found = 0;
		int count = 1;
		int numberOfCategories = iCategoricalObject.getNumberOfCategories();
		for (; found < numberOfCategories && count <= numberOfCategories; count++) {
			WritableValue observableClassName = iCategoricalObject.getObservableCategoryName(count);
			if(observableClassName != null){
				found++;
				Label label = new Label(this, SWT.NONE);
				label.setText(new Integer(count).toString());
				bindValue(observableClassName);
			} else {
				MessageBox box = new MessageBox(parent.getShell());
				box.setText("Class name error");
				box.setMessage("Name at index " + count + " should not be empty.");
				box.open();
			}
		}
//		for (; count <= numberOfCategories; count++) {
//			myCategoricalObject.putCategory(count, "");
//			Label label = new Label(this, SWT.NONE);
//			label.setText(new Integer(count).toString());
//			bindValue(myCategoricalObject.getObservableCategoryName(count));
//		}
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
		text.addVerifyListener(new NameVerifyListener());
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}

//
//	public void handlePlacementInContainer(ClassDefinitionsDataPanel panel,
//			Label topNeighbour) {
//		FormData formData = new FormData();
//		formData.top = new FormAttachment(topNeighbour, 10);
//		formData.right = new FormAttachment(100, -10);
//		formData.bottom = new FormAttachment(100, -10);
//		formData.left = new FormAttachment(0, 10);
//		panel.setLayoutData(formData);
//	}
}
