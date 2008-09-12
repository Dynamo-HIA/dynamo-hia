package nl.rivm.emi.dynamo.ui.parametercontrols.prototype;

import nl.rivm.emi.dynamo.databinding.converters.AgeIntegerToStringConverter;
import nl.rivm.emi.dynamo.databinding.converters.AgeStringToIntegerConverter;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AgeGenderDatabindingPrototype implements Runnable {
//	Log log = LogFactory.getLog(this.getClass());

	Composite container;

	public AgeGenderDatabindingPrototype(Composite container) {
		this.container = container;
	}

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		SingleAgeGenderComposite view = new SingleAgeGenderComposite(container,
				SWT.NONE);
		SingleAgeGenderComposite anotherView = new SingleAgeGenderComposite(container,
				SWT.NONE);
		DatabindableAgeGenderRow model = new DatabindableAgeGenderRow();
		hookupViewAndModel(view, model, dbc);
		hookupViewAndModel(anotherView, model, dbc);
		Button aButton = new Button(container, SWT.ABORT);
		aButton.setText("Abort");
		
//		addDebugListeners(model);
//		addDebugListeners(view);
	}

	private void hookupViewAndModel(SingleAgeGenderComposite view,
			DatabindableAgeGenderRow model, DataBindingContext dbc) {
		IObservableValue ageTextObservableValue = SWTObservables.observeText(
				view.getAgeText(), SWT.Modify);
		IObservableValue femaleTextObservableValue = SWTObservables.observeText(
				view.getFemaleText(), SWT.Modify);
		IObservableValue maleTextObservableValue = SWTObservables.observeText(
				view.getMaleText(), SWT.Modify);
		IObservableValue modelAgeObservableValue = (IObservableValue) model.getAgeValue();
		IObservableValue modelFemaleObservableValue = (IObservableValue) model.getFemaleValue();
		IObservableValue modelMaleObservableValue = (IObservableValue) model.getMaleValue();
		dbc.bindValue(ageTextObservableValue, modelAgeObservableValue,
				ModelUpdateValueStrategies.getStrategy(new Integer(1)),
				ViewUpdateValueStrategies.getStrategy(new Integer(1)));
		dbc.bindValue(femaleTextObservableValue, modelFemaleObservableValue,
				null,
				null);
		dbc.bindValue(maleTextObservableValue, modelMaleObservableValue,
				null,
				null);
	}

	private void addDebugListeners(DatabindableAgeGenderRow model) {
		((IObservableValue) model.getAgeValue()).addValueChangeListener(new IValueChangeListener() {

			public void handleValueChange(ValueChangeEvent event) {
				Integer newValue = (Integer) event.getObservableValue()
						.getValue();
//				log.debug("ValueChangeListener Model AgeValue changed to: "
//						+ newValue);
			}
		});
		((IObservableValue) model.getFemaleValue()).addValueChangeListener(
				new IValueChangeListener() {
					public void handleValueChange(ValueChangeEvent event) {
//						log
//								.debug("ValueChangeListener Model FemaleValue changed to: "
//										+ event.getObservableValue().getValue());
					}
				});
		((IObservableValue) model.getMaleValue()).addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(ValueChangeEvent event) {
//				log.debug("ValueChangeListener Model MaleValue changed to: "
//						+ event.getObservableValue().getValue());
			}
		});
	}


	private void addDebugListeners(SingleAgeGenderComposite target) {
		((IObservableValue) target.getAgeText()).addValueChangeListener(new IValueChangeListener() {

			public void handleValueChange(ValueChangeEvent event) {
				Integer newValue = (Integer) event.getObservableValue()
						.getValue();
//				log.debug("ValueChangeListener Model AgeValue changed to: "
//						+ newValue);
			}
		});
		((IObservableValue) target.getFemaleText()).addValueChangeListener(
				new IValueChangeListener() {
					public void handleValueChange(ValueChangeEvent event) {
//						log
//								.debug("ValueChangeListener Model FemaleValue changed to: "
//										+ event.getObservableValue().getValue());
					}
				});
		((IObservableValue) target.getMaleText()).addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(ValueChangeEvent event) {
//				log.debug("ValueChangeListener Model MaleValue changed to: "
//						+ event.getObservableValue().getValue());
			}
		});
	}
}
