package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.TypedFocusListener;

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

public class TransitionDriftNettoParameterDataPanel extends Composite {

	Log log = LogFactory.getLog(this.getClass().getName());
	private Composite myParent = null;
	protected DataBindingContext dataBindingContext = null;
	protected HelpGroup theHelpGroup;

	public TransitionDriftNettoParameterDataPanel(Composite parent,
			Text topNeighbour, TransitionDriftNettoObject lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup)
			throws DynamoConfigurationException {
		super(parent, SWT.NONE);
		myParent = parent;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		setLayout(gridLayout);
		Label label = new Label(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		label.setLayoutData(gridData);
		label.setText("Trend: ");
		WritableValue observableTrend = lotsOfData.getObservableTrend();
		bindAbstractValue(observableTrend,
				(AtomicTypeBase) XMLTagEntityEnum.TREND.getTheType());
	}

	protected void bindAbstractValue(WritableValue modelObservableValue,
			AtomicTypeBase myType) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		String convertedText = myType.convert4View(modelObservableValue);
		text.setText(convertedText);
		FocusListener focusListener = new TypedFocusListener(myType,
				theHelpGroup);
		text.addFocusListener(focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup
				.getTheModal()));
	}
}
