package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFlexibleUpperLimitInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

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
import org.eclipse.swt.widgets.Text;

public class DurationDistributionParameterDataPanel extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	DurationDistributionObject lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;
	int durationClassIndex;

	public DurationDistributionParameterDataPanel(Composite parent,
			int genderIndex, DurationDistributionObject lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		log.debug("Constructing DurationDistributionParameterDataPanel");
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"value");
		this.durationClassIndex = durationClassIndex;
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		// Top line.
		createHeader();
		log.debug("Number of columns: " + Integer.toString(layout.numColumns));
		for(int ageCount = 0; ageCount < lotsOfData.size(); ageCount++){
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			Label ageLabel = new Label(this, SWT.NONE);
			ageLabel.setText(Integer.toString(ageCount));
			ageLabel.setLayoutData(gridData);
			log.debug("Handling Age: " + Integer.toString(ageCount));
			TypedHashMap oneAgeMap = (TypedHashMap) lotsOfData.get(ageCount);
			TypedHashMap<ArrayList<AtomicTypeObjectTuple>> oneGenderMap = (TypedHashMap<ArrayList<AtomicTypeObjectTuple>>) oneAgeMap
					.get(genderIndex);
			for (int durationCount = 1; durationCount <= oneGenderMap.size(); durationCount++) {
				ArrayList<AtomicTypeObjectTuple> genderList = (ArrayList<AtomicTypeObjectTuple>) oneGenderMap
						.get(durationCount);
//				log.debug("Aantal durations: " + genderList.size());
				bindDurationValue(genderList);
			}
		}
	}

	private void bindDurationValue(ArrayList<AtomicTypeObjectTuple> tupleList) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) tupleList.get(0);
		bindTuple(gridData, tuple);
	}

	private void bindTuple(GridData gridData, AtomicTypeObjectTuple tuple) {
		WritableValue modelObservableValue = (WritableValue) tuple.getValue();
		AtomicTypeBase modelType = (AtomicTypeBase) tuple.getType();
		Text text = new Text(this, SWT.NONE);
		text.setLayoutData(gridData);
		String convertedText = modelType.convert4View(modelObservableValue
				.getValue());
		text.setText(convertedText);
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
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				modelType.getModelUpdateValueStrategy(), modelType
						.getViewUpdateValueStrategy());
		// text.addVerifyListener(new ValueVerifyListener());
	}

	private void createHeader() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		Label ageHeader = new Label(this, SWT.NONE);
		ageHeader.setText("Age");
		ageHeader.setLayoutData(gridData);
		((GridLayout) getLayout()).numColumns++;
		int maxDuration = ((AbstractFlexibleUpperLimitInteger) XMLTagEntityEnum.DURATION
				.getTheType()).getMAX_VALUE();
		log.debug("maxDuration: " + maxDuration);
		for (int durationCount = 1; durationCount <= maxDuration; durationCount++) {
			Label durationHeader = new Label(this, SWT.NONE);
			String durationHeaderText = Integer.toString(durationCount);
			log.debug("DurationHeaderText: " + durationHeaderText);
			durationHeader.setText(durationHeaderText);
			((GridLayout) getLayout()).numColumns++;
		}
	}
}
