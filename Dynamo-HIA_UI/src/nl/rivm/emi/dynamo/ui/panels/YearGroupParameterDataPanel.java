package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;

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
import org.eclipse.swt.widgets.Text;

/**
 * TODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODO
 * TODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODO
 * TODO: THIS CLASS IS NOT FINISHED YET
 * TODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODO
 * TODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODO
 * @author schutb
 *
 */

public class YearGroupParameterDataPanel extends Composite /* implements Runnable */{
	Log log = LogFactory
			.getLog(this.getClass().getName());
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;

	public YearGroupParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get("number");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label yearLabel = new Label(this, SWT.NONE);
		yearLabel.setText("Year");
		Label numberLabel = new Label(this, SWT.NONE);
		numberLabel.setText("Number");
		log.debug("lotsOfData" + lotsOfData);
		log.debug("lotsOfData.size()" + lotsOfData.size());
		// TODO: replace start year with the first key of the hashmap
		for (int count = 2009; count < lotsOfData.size() + 2009; count++) {
			TypedHashMap tHMap = (TypedHashMap)lotsOfData.get(count);
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(count).toString());
			////log.debug("tHMap" + tHMap);
			// TODO: implement databinding validation
			//bindValue(tHMap, NewBorn.YEAR); // TODO: If the user changes the start year, the binding will change automatically too!!!
			//////bindValue(tHMap, 1);
		}
	}

	public void handlePlacementInContainer(YearGroupParameterDataPanel panel,
			Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindValue(TypedHashMap typedHashMap, int index) {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		log.debug("myType" + myType);
		log.debug("typedHashMap" + typedHashMap);
		log.debug("typedHashMap.get(index)" + typedHashMap.get(index));
		
		String convertedText = ((Number)myType).convert4View(typedHashMap.get(index).toString());
		text.setText(convertedText);
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
																	// range.
			}

		});
//	Too early, see below.	text.addVerifyListener(new StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) typedHashMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((Number)myType).getModelUpdateValueStrategy(), ((Number)myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
	}
}
