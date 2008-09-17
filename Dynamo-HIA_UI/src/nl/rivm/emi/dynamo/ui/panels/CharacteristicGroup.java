package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class CharacteristicGroup{
	Group theGroup;

	public CharacteristicGroup(Shell shell, AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData, DataBindingContext dataBindingContext) {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		CharacteristicNamePanel characteristicNameGroup = new CharacteristicNamePanel(theGroup);
		characteristicNameGroup.handlePlacementInContainer();
		ParameterGroup parameterGroup = new ParameterGroup(theGroup, lotsOfData, dataBindingContext);
		parameterGroup.handlePlacementInContainer(characteristicNameGroup.group);
}

 public void setFormData(Composite rightNeighbour, Composite lowerNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(rightNeighbour, -2);
		formData.bottom = new FormAttachment(lowerNeighbour, -5);
		theGroup.setLayoutData(formData);
	}

}
