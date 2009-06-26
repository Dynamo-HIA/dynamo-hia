package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
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

public class CategoricalValueParameterDataPanel extends Composite {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;

	public CategoricalValueParameterDataPanel(Composite parent,
			Text topNeighbour, TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"value");
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label categoryLabel = new Label(this, SWT.NONE);
		categoryLabel.setText("Class");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		for (int count = 0; count < lotsOfData.size(); count++) {
			TypedHashMap tHMap = (TypedHashMap) lotsOfData.get(count);
			for (int classCount = 1; classCount < lotsOfData.size(); classCount++) {
				TypedHashMap cHMap = (TypedHashMap) tHMap.get(classCount);
				Label label = new Label(this, SWT.NONE);
				if (classCount == 1) {
					label.setText(new Integer(count).toString());
				}
				Label classLabel = new Label(this, SWT.NONE);
				classLabel.setText(new Integer(classCount).toString());
				bindValue(cHMap, BiGender.FEMALE_INDEX);
				bindValue(cHMap, BiGender.MALE_INDEX);
			}
		}
	}

	private void bindValue(TypedHashMap typedHashMap, int index) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		String convertedText = ((Value) myType).convert4View(typedHashMap.get(
				index).toString());
		text.setText(convertedText);
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
		WritableValue modelObservableValue = (WritableValue) typedHashMap
				.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((Value) myType).getModelUpdateValueStrategy(),
				((Value) myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup.getTheModal()));
	}
}
