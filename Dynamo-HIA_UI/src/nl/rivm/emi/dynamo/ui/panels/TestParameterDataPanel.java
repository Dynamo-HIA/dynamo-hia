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
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TestParameterDataPanel extends ScrolledComposite /*
																 * implements
																 * Runnable
																 */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;

	public TestParameterDataPanel(Composite parent, Text topNeighbour,
			AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData) {
		super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		// dataBindingContext = new DataBindingContext();
		setBackground(new Color(null, 0xee, 0x00, 0x00));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		GridData gridData = new GridData(20, 20);
		ageLabel.setLayoutData(gridData);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		pack();
	}

	public void handlePlacementInContainer(TestParameterDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	public AgeSteppedContainer<BiGenderSteppedContainer<Integer>> manufactureModel(
			String configurationFilePath) {
		File configurationFile = new File(configurationFilePath);
		// log.fatal(configurationFile.getAbsolutePath());
		AgeSteppedContainer<BiGenderSteppedContainer<Integer>> testModel = SomethingPerAgeDataFromXMLFactory
				.manufacture(configurationFile);
		return testModel;
	}

	private void bindValue(Composite composite,
			DataBindingContext dataBindingContext,
			BiGenderSteppedContainer<Integer> bgsc, int index) {
		Text text = new Text(composite, SWT.NONE);
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
}
