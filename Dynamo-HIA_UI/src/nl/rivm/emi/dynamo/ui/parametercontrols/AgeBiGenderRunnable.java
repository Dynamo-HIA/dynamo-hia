package nl.rivm.emi.dynamo.ui.parametercontrols;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AgeBiGenderRunnable implements Runnable {
	Composite parent;
	AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData;

	public AgeBiGenderRunnable(Composite parent, int style,
			AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData) {
		this.parent = parent;
		this.lotsOfData = lotsOfData;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
	}

	public void putAndHookData() {
		DataBindingContext dataBindingContext = new DataBindingContext();
		Label ageLabel = new Label(parent, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(parent, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(parent, SWT.NONE);
		maleLabel.setText("Male");
		for (int count = 0; count < lotsOfData.size(); count++) {
			BiGenderSteppedContainer<Integer> bgsc = lotsOfData.get(count);
			Label label = new Label(parent, SWT.NONE);
			label.setText(new Float(count * lotsOfData.getAgeStepSize())
					.toString());
			bindValue(dataBindingContext, bgsc, BiGender.FEMALE_INDEX);
			bindValue(dataBindingContext, bgsc, BiGender.MALE_INDEX);
		}
	}

	private void bindValue(DataBindingContext dataBindingContext,
			BiGenderSteppedContainer<Integer> bgsc, int index) {
		Text text = new Text(parent, SWT.NONE);
		text.setText(bgsc.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		Object theValue = bgsc.get(index);
		IObservableValue modelObservableValue = (IObservableValue) new WritableValue(
				theValue, theValue);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(theValue),
				ViewUpdateValueStrategies.getStrategy(theValue));
	}

	public void run() {
		putAndHookData();
	}
}
