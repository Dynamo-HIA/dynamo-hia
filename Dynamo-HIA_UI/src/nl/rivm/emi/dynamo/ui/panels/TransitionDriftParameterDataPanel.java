package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Mean;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TransitionDriftParameterDataPanel extends Composite {
	private Composite myParent = null;
	private boolean open = false;
	protected DataBindingContext dataBindingContext = null;
	protected HelpGroup theHelpGroup;
	protected TypedHashMap<?> modelObject;

	public TransitionDriftParameterDataPanel(Composite parent,
			Text topNeighbour, TypedHashMap<?> lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		myParent = parent;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.modelObject = lotsOfData;
		GridLayout layout = new GridLayout();
		layout.numColumns = 3; 
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		GridData labelGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING );
		GridData dataGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataGridData.widthHint = 50;
		Label spaceLabel = new Label(this, SWT.NONE);
		spaceLabel.setLayoutData(labelGridData);
		Label maleHeaderLabel = new Label(this, SWT.NONE);
		maleHeaderLabel.setText("Male");
		maleHeaderLabel.setLayoutData(dataGridData);
		Label femaleHeaderLabel = new Label(this, SWT.NONE);
		femaleHeaderLabel.setText("Female");
		femaleHeaderLabel.setLayoutData(dataGridData);
		Label ageHeaderLabel = new Label(this, SWT.NONE);
		// ageHeaderLabel.setLayoutData(labelLayoutData);
		ageHeaderLabel.setText("Age");
		Label femaleMeanHeaderLabel = new Label(this, SWT.NONE);
		// femaleMeanHeaderLabel.setLayoutData(labelLayoutData);
		femaleMeanHeaderLabel.setText("Mean");
		Label maleMeanHeaderLabel = new Label(this, SWT.NONE);
		// maleMeanHeaderLabel.setLayoutData(labelLayoutData);
		maleMeanHeaderLabel.setText("Mean");
		// ROWS
		for (int ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			TypedHashMap<?> tHMap = (TypedHashMap<?>) lotsOfData.get(ageCount);
			Label labelRow = new Label(this, SWT.NONE);
			labelRow.setText(new Integer(ageCount).toString());
			// labelRow.setLayoutData(labelLayoutData);
			for (int genderCount = 0; genderCount < tHMap.size(); genderCount++) {
				ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) tHMap
						.get(genderCount);
				AtomicTypeObjectTuple tuple = list.get(0);
				WritableValue modelObservableValue = (WritableValue) tuple
						.getValue();
				AtomicTypeBase<Float> type = (AtomicTypeBase<Float>) tuple
						.getType();
				bindAbstractValue(modelObservableValue, type);
			}
		}
	}

	protected void bindAbstractValue(WritableValue modelObservableValue,
			AtomicTypeBase<Float> type) {
		Text text = new Text(this, SWT.NONE);
		GridData textLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		textLayoutData.widthHint = 50;
		text.setLayoutData(textLayoutData);
		String convertedText = ((Mean) type).convert4View(modelObservableValue
				.doGetValue());
		text.setText(convertedText);
		HelpTextListenerUtil.addHelpTextListeners(text, type);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				type.getModelUpdateValueStrategy(), type
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup
				.getTheModal()));
	}

	protected void bindAbstractString(TypedHashMap<?> typedHashMap, int index,
			AtomicTypeBase<?> myType) {
		// No need to implement this method, it is not used
	}

}
