package nl.rivm.emi.dynamo.ui.panels;

import java.io.File;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.SomethingPerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ParameterDataPanel extends Composite /* implements Runnable */ {
static Log log = LogFactory.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	
	public ParameterDataPanel(Composite parent, Text topNeighbour,
			AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData, DataBindingContext dataBindingContext) {
		super(parent, SWT.NONE);
			this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		GridLayout layout = new GridLayout();
// Testing		layout.numColumns = 3;
		layout.numColumns = 5;
				layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label femaleTestLabel = new Label(this, SWT.NONE);
		femaleTestLabel.setText("FemaleTest");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		Label maleTestLabel = new Label(this, SWT.NONE);
		maleTestLabel.setText("MaleTest");
		for (int count = 0; count < lotsOfData.size(); count++) {
			BiGenderSteppedContainer<IObservable> bgsc = lotsOfData.get(
					count);
			Label label = new Label(this, SWT.NONE);
			label.setText(new Float(count
					* lotsOfData.getAgeStepSize()).toString());
			bindValue(bgsc,
					BiGender.FEMALE_INDEX);
			bindTestValue(bgsc,
					BiGender.FEMALE_INDEX);
			bindValue(bgsc,
					BiGender.MALE_INDEX);
			bindTestValue(bgsc,
					BiGender.MALE_INDEX);
		}
	}

	public void handlePlacementInContainer(ParameterDataPanel panel, Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	public void setLotsOfData(
			AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData) {
		this.lotsOfData = lotsOfData;
	}

	public AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> getLotsOfData() {
		return lotsOfData;
	}

	public AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> manufactureModel(
			String configurationFilePath) {
		File configurationFile = new File(configurationFilePath);
		// log.fatal(configurationFile.getAbsolutePath());
		AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> testModel = SomethingPerAgeDataFromXMLFactory
				.manufacture(configurationFile);
		return testModel;
	}

	private void bindValue(BiGenderSteppedContainer<IObservable> bgsc, int index) {
		Text text = new Text(this, SWT.NONE);
		text.setText(bgsc.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) bgsc.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue.getValueType()),
				ViewUpdateValueStrategies.getStrategy(modelObservableValue.getValueType()));
	}

	private void bindTestValue(BiGenderSteppedContainer<IObservable> bgsc, int index) {
		Text text = new Text(this, SWT.NONE);
		text.setText(bgsc.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
//		Object theValue = bgsc.get(index);
//		IObservableValue modelObservableValue = (IObservableValue) new WritableValue(
//				theValue, theValue);
		WritableValue modelObservableValue = (WritableValue) bgsc.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue.getValueType()),
				ViewUpdateValueStrategies.getStrategy(modelObservableValue.getValueType()));
	}
}
