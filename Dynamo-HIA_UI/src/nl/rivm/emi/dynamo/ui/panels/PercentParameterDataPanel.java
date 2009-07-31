package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.PercentVerifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PercentParameterDataPanel extends Composite /* implements Runnable */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	TypedHashMap<?> lotsOfData;
//	final Composite myParent;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<?> myType;

	@SuppressWarnings("unchecked")
	public PercentParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap<?> lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase<?>) XMLTagEntitySingleton.getInstance().get("percent");
		GridLayout layout = new GridLayout();
//		layout.numColumns = 5;
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		final Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		final Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
//		Label femaleTestLabel = new Label(this, SWT.NONE);
//		femaleTestLabel.setText("FemaleTest");
		final Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		for (int count = 0; count < lotsOfData.size(); count++) {
			TypedHashMap<?> tHMap = (TypedHashMap<?>)lotsOfData.get(count);
			final Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(count).toString());
			for(int sexCount = 0; sexCount < 2; sexCount++ ){
				ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) tHMap.get(sexCount);
				WritableValue thePercentageObservable = (WritableValue) list.get(0).getValue();  
			bindValue(thePercentageObservable);
//			bindTestValue(tHMap, BiGender.FEMALE_INDEX);
			}
		}
	}

	public void handlePlacementInContainer(PercentParameterDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindValue(WritableValue thePercentage) {
		final Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		String convertedText = ((Percent)myType).convert4View(thePercentage);
		text.setText(convertedText);
		HelpTextListenerUtil.addHelpTextListeners(text, myType);

		//	Too early, see below.	text.addVerifyListener(new StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, thePercentage,
				((Percent)myType).getModelUpdateValueStrategy(), ((Percent)myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new PercentVerifyListener(theHelpGroup.getTheModal()));
	}

//	private void bindTestValue(TypedHashMap sexMap, int index) {
//		Text text = new Text(this, SWT.NONE);
//		text.setText(sexMap.get(index).toString());
//		IObservableValue textObservableValue = SWTObservables.observeText(text,
//				SWT.Modify);
//		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
//		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
//				ModelUpdateValueStrategies.getStrategy(modelObservableValue
//						.getValueType()), ViewUpdateValueStrategies
//						.getStrategy(modelObservableValue.getValueType()));
//	}
}
