package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.NameVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ClassDefinitionsDataPanel extends Composite /* implements Runnable */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	ICategoricalObject myCategoricalObject;
	final Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<String> myType = new Name();

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
		GridData labelLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridData dataLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataLayoutData.widthHint = 150;

		final Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText("Index");
		indexLabel.setLayoutData(labelLayoutData);
		final Label classNameLabel = new Label(this, SWT.NONE);
		classNameLabel.setText("Classname");
		classNameLabel.setLayoutData(dataLayoutData);
		int found = 1;
		int count = 1;
		int numberOfCategories = iCategoricalObject.getNumberOfCategories();
		for (; found <= numberOfCategories && count <= numberOfCategories; count++) {
			WritableValue observableClassName = iCategoricalObject
					.getObservableCategoryName(count);
			if (observableClassName != null) {
				found++;
				final Label label = new Label(this, SWT.NONE);
				label.setText(new Integer(count).toString());
				bindValue(observableClassName);
//				@SuppressWarnings("unused")
//				final Label anotherEmptyLabel = new Label(this, SWT.NONE);
			} else {
				final MessageBox box = new MessageBox(parent.getShell());
				box.setText("Class name error");
				box.setMessage("Name at index " + count
						+ " should not be empty.");
				box.open();
			}
		}
	}

	private void bindValue(WritableValue observableClassName) {
		final Text text = createAndPlaceTextField();
		text.setText((String) observableClassName.doGetValue());
//		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
//		layoutData.horizontalSpan = 3;
//		text.setLayoutData(layoutData);
		HelpTextListenerUtil.addHelpTextListeners(text, myType);
		//ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new NameVerifyListener(theHelpGroup
				.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		final Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		return text;
	}
}
