package nl.rivm.emi.dynamo.ui.panels;

// extra toegevoegd 15-2-2015 omdat trend ook negatief moet kunnen zijn
// dus Value as type werkt niet

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.TrendVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
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
		gridLayout.makeColumnsEqualWidth = false;
		setLayout(gridLayout);
		Label label = new Label(this, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		label.setLayoutData(gridData);
		label.setText("Trend: ");
		WritableValue observableTrend = lotsOfData.getObservableTrend();
		bindAbstractValue(observableTrend,
				(AtomicTypeBase) XMLTagEntityEnum.TREND.getTheType());
	}

	protected void bindAbstractValue(WritableValue modelObservableValue,
			AtomicTypeBase myType) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 50;
		text.setLayoutData(gridData);
		String convertedText = myType.convert4View(modelObservableValue);
		text.setText(convertedText);
		HelpTextListenerUtil.addHelpTextListeners(text, myType);
		
		// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new TrendVerifyListener(theHelpGroup
				.getTheModal()));
	}
}
