package nl.rivm.emi.dynamo.ui.parametercontrols;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AgeBiGenderList extends Composite implements Runnable {

	public AgeBiGenderList(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		setLayout(gridLayout);
	}

	public void putTestLabel(Composite parent) {
		Text label = new Text(parent, SWT.BORDER);
		label.setText("Test-tekst");
		label.setBackground(new Color(null, 0xee, 0x00, 0x00));
		GridData layoutData = new GridData();
		layoutData.heightHint = 20;
		layoutData.widthHint = 100;
		label.setLayoutData(layoutData);
	}

	public void putAndHookData(AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData,
			DataBindingContext dataBindingContext, UpdateValueStrategy updateModelStrategy,
			UpdateValueStrategy updateViewStrategy) {
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");

		for (int count = 0; count < lotsOfData.size(); count++){
				BiGenderSteppedContainer<Integer> bgsc = lotsOfData.get(count);
				Label label = new Label(this, SWT.NONE);
			label.setText(new Float(count*lotsOfData.getAgeStepSize()).toString());
			Text femaleText = new Text(this, SWT.NONE);
			femaleText.setText(bgsc.get(BiGender.FEMALE_INDEX).toString());
			IObservableValue femaleTextObservableValue = SWTObservables
					.observeText(femaleText, SWT.Modify);
			Text maleText = new Text(this, SWT.NONE);
			maleText.setText(bgsc.get(BiGender.MALE_INDEX).toString());
			IObservableValue maleTextObservableValue = SWTObservables
					.observeText(maleText, SWT.Modify);

			IObservableValue modelFemaleObservableValue = (IObservableValue) row
					.getFemaleValue();
			IObservableValue modelMaleObservableValue = (IObservableValue) row
					.getMaleValue();
			// dbc.bindValue(ageTextObservableValue, modelAgeObservableValue,
			// assembleSimpleModelAgeValueUpdateStrategy(dbc),
			// assembleSimpleViewAgeValueUpdateStrategy());
			dataBindingContext.bindValue(femaleTextObservableValue,
					modelFemaleObservableValue, null, null);
			dataBindingContext.bindValue(maleTextObservableValue, modelMaleObservableValue,
					null, null);

		}
	}

	public void run() {
		// TODO Auto-generated method stub

	}
}
