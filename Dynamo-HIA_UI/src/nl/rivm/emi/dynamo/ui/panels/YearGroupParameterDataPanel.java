package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Defines the Year parameter group in Newborns (screen W13)
 * 
 * @author schutb
 *
 */

public class YearGroupParameterDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory
			.getLog(this.getClass().getName());
	NewbornsObject lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;

	public YearGroupParameterDataPanel(Composite parent, Text topNeighbour,
			NewbornsObject newbornsObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = newbornsObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get("number");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label yearLabel = new Label(this, SWT.NONE);
		yearLabel.setText("Year");
		Label numberLabel = new Label(this, SWT.NONE);
		numberLabel.setText("Number");
		
		int numberOfNumbers = newbornsObject.getNumberOfNumbers();

		WritableValue observableObject = newbornsObject.getObservableStartingYear();		
		int startingYear = ((Integer) observableObject.doGetValue()).intValue();		

		// Start year is the first key of the hashmap
		for (int yearCount = startingYear; yearCount < (numberOfNumbers + startingYear); yearCount++) {
			log.debug("yearCount" + yearCount);
			// Set the year label
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(yearCount).toString());				
			
			WritableValue observableClassName = newbornsObject.getObservableNumber(yearCount);
			if(observableClassName != null){
				bindAbstractRangedInteger(observableClassName, myType);
			} else {
				MessageBox box = new MessageBox(parent.getShell());
				box.setText("Class name error");
				box.setMessage("Name at year " + yearCount + " should not be empty.");
				box.open();
			}
		}					
	}

	protected void bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(myType));
	}

	private Text getTextBinding(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = createAndPlaceTextField();
		text.setText((String) myType
				.convert4View(observableObject.doGetValue()));
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
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		return text;
	}

	
	private Text createAndPlaceTextField() {
		final Text text = new Text(this, SWT.NONE);		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
	
	
	
}
