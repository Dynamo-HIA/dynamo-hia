/*******************************************************************************
 * Copyright (c) 2007 Brad Reynolds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 ******************************************************************************/

package nl.rivm.emi.dynamo.ui.parametercontrols.prototype;

import nl.rivm.emi.dynamo.databinding.converters.AgeIntegerToStringConverter;
import nl.rivm.emi.dynamo.databinding.converters.AgeStringToIntegerConverter;
import nl.rivm.emi.dynamo.databinding.validators.AfterGetFromViewAgeValidator;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Snippet that validates values across multiple bindings on change of each
 * observable. If the values of the target observables are not equal the model
 * is not updated. When the values are equal they will be written to sysout.
 * 
 * @author Brad Reynolds
 */
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
//		addDebugListeners(model);
//		addDebugListeners(view);
	}

	private void hookupViewAndModel(SingleAgeGenderComposite view,
			DatabindableAgeGenderRow model, DataBindingContext dbc) {
		IObservableValue ageTextObserveWidget = SWTObservables.observeText(
				view.getAge(), SWT.Modify);
		IObservableValue femaleTextObserveWidget = SWTObservables.observeText(
				view.getFemaleText(), SWT.Modify);
		IObservableValue maleTextObserveWidget = SWTObservables.observeText(
				view.getMaleText(), SWT.Modify);
		IObservableValue modelAge = (IObservableValue) model.getAgeValue();
		IObservableValue modelFemaleValue = (IObservableValue) model.getFemaleValue();
		IObservableValue modelMaleValue = (IObservableValue) model.getMaleValue();
//		UpdateValueStrategy viewAgeUpdateValueStrategy = assembleViewAgeValueUpdateStrategy();
//		UpdateValueStrategy modelAgeUpdateValueStrategy = assembleModelAgeValueUpdateStrategy();
//		dbc.bindValue(ageTextObserveWidget, modelAge,
//				modelAgeUpdateValueStrategy,
//				viewAgeUpdateValueStrategy);
		dbc.bindValue(ageTextObserveWidget, modelAge,
				assembleSimpleModelAgeValueUpdateStrategy(dbc),
				assembleSimpleViewAgeValueUpdateStrategy());
		dbc.bindValue(femaleTextObserveWidget, modelFemaleValue,
				null,
				null);
		dbc.bindValue(maleTextObserveWidget, modelMaleValue,
				null,
				null);
	}

//	private UpdateValueStrategy assembleViewAgeValueUpdateStrategy() {
//		UpdateValueStrategy ageUpdateValueStrategy = new UpdateValueStrategy();
//		ageUpdateValueStrategy.setConverter(new AgeIntegerToStringConverter(
//				"ViewAge"));
//		ageUpdateValueStrategy.setAfterGetValidator(new AfterGetFromViewAgeValidator(
//				null));
//		ageUpdateValueStrategy.setBeforeSetValidator(new AfterGetFromViewAgeValidator(
//				null));
//		ageUpdateValueStrategy.setAfterConvertValidator(new AfterGetFromViewAgeValidator(
//				null));
//		return ageUpdateValueStrategy;
//	}
	private UpdateValueStrategy assembleSimpleViewAgeValueUpdateStrategy() {
		UpdateValueStrategy ageUpdateValueStrategy = new UpdateValueStrategy();
		ageUpdateValueStrategy.setConverter(new AgeIntegerToStringConverter(
				"ViewAge"));
//		ageUpdateValueStrategy.setAfterGetValidator(new AgeValidator(
//				"ViewAge After get"));
//		ageUpdateValueStrategy.setBeforeSetValidator(new AgeValidator(
//				"ViewAge Before set"));
//		ageUpdateValueStrategy.setAfterConvertValidator(new AgeValidator(
//				"ViewAge After convert"));
		return ageUpdateValueStrategy;
	}

//	private UpdateValueStrategy assembleModelAgeValueUpdateStrategy() {
//		UpdateValueStrategy ageUpdateValueStrategy = new UpdateValueStrategy();
//		ageUpdateValueStrategy.setConverter(new AgeStringToIntegerConverter(
//				"ModelAge"));
//		ageUpdateValueStrategy.setAfterGetValidator(new AfterGetFromViewAgeValidator(
//				null));
//		ageUpdateValueStrategy.setBeforeSetValidator(new AfterGetFromViewAgeValidator(
//				null));
//		ageUpdateValueStrategy.setAfterConvertValidator(new AfterGetFromViewAgeValidator(
//				null));
//		return ageUpdateValueStrategy;
//	}


	private UpdateValueStrategy assembleSimpleModelAgeValueUpdateStrategy(DataBindingContext dbc) {
		UpdateValueStrategy ageUpdateValueStrategy = new UpdateValueStrategy();
		ageUpdateValueStrategy.setConverter(new AgeStringToIntegerConverter(
				"ModelAge"));
//		ageUpdateValueStrategy.setAfterGetValidator(new AfterGetFromViewAgeValidator(
//				dbc));

		//		ageUpdateValueStrategy.setBeforeSetValidator(new AgeValidator(
//				"ModelAge Before set"));
//		ageUpdateValueStrategy.setAfterConvertValidator(new AgeValidator(
//				"ModelAge After convert"));
		return ageUpdateValueStrategy;
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
		((IObservableValue) target.getAge()).addValueChangeListener(new IValueChangeListener() {

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
	/**
	 * @since 3.2
	 * 
	 */
	private static final class CrossFieldValidator implements IValidator {
		/**
		 * 
		 */
		private final IObservableValue other;

		/**
		 * @param model
		 */
		private CrossFieldValidator(IObservableValue other) {
			this.other = other;
		}

		public IStatus validate(Object value) {
			// System.out.println("Validating, other: " +
			// (other.getValue()).toString() + " value: " +value.toString());
			if (!value.equals(other.getValue())) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error("values cannot be the same");
		}
	}
}
