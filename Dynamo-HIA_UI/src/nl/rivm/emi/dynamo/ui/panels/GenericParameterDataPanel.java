package nl.rivm.emi.dynamo.ui.panels;

import java.util.Set;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

// TODO: First implementation for Transition Drift!
public abstract class GenericParameterDataPanel<T> extends Composite /* implements Runnable */{
	Log log = LogFactory
			.getLog(this.getClass().getName());
	//private TypedHashMap<T> lotsOfData;
	private Composite myParent = null;
	private boolean open = false;
	protected DataBindingContext dataBindingContext = null;
	protected HelpGroup theHelpGroup;
	//protected AtomicTypeBase<T>[] myTypes;

	public GenericParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			PanelMatrix<T> panelMatrix
	) {
		super(parent, SWT.NONE);
		//this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		//this.myTypes = atomicTypeBases;
		//this.myTypes = atomicTypeBases;

		GridLayout layout = new GridLayout();
		layout.numColumns = panelMatrix.size();
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
				
		// Process each panel matrix item
		// Get the available keys and copy them into an array
		Set<String> keys = panelMatrix.keySet();
		String[] keysArray = (String[]) keys.toArray(new String[keys.size()]);
		
		// Iterate through the keys of the COLUMNS
		for (String key: keysArray) {
			
			// Get the PanelMatrixItem
			PanelMatrixItem panelMatrixItem = panelMatrix.get(key);
			
			Label label = new Label(this, SWT.NONE);
			label.setText(panelMatrixItem.getColumnHeader());

			// The first column values (e.g. age, index) are not bound
			if (key != null && !key.equals(keysArray[0])) {
				// ROWS
				for (int count = 0; count < lotsOfData.size(); count++) {
					TypedHashMap tHMap = (TypedHashMap)lotsOfData.get(count);
					Label labelRow = new Label(this, SWT.NONE);
					labelRow.setText(new Integer(count).toString());
					bindValue(tHMap, panelMatrixItem.getColumnIndex(), panelMatrixItem.getMyType());					
				}				
			}
	
		}
		
		
		/*
		for (int i = 0; i < columNames.length; i++) {
			Label ageLabel = new Label(this, SWT.NONE);
			ageLabel.setText(columNames[i]);
		}*/
		
		// ROWS
		/*
		for (int count = 0; count < lotsOfData.size(); count++) {
			TypedHashMap tHMap = (TypedHashMap)lotsOfData.get(count);
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(count).toString());
			// COLUMNS
			for (int i = 0; i < columnIndexes.length; i++) {		
				bindValue(tHMap, columnIndexes[i], myTypes[]);
			}				
		}*/
	}
	
	// TODO: is not abstract anymore: see ExcessMortality
	// Binds values that are subclass types of AbstractValue  
	protected abstract void bindAbstractValue(TypedHashMap typedHashMap, int index, 
			AtomicTypeBase myType);/* {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		String convertedText = ((AbstractValue)myType).convert4View(typedHashMap.get(index).toString());
		text.setText(convertedText);
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				GenericParameterDataPanel.this.theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}
			public void focusLost(FocusEvent arg0) {
				GenericParameterDataPanel.this.theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of																	// range.
			}

		});
		//	Too early, see below.	text.addVerifyListener(new StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) typedHashMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				((AbstractValue)myType).getModelUpdateValueStrategy(), ((AbstractValue)myType).getViewUpdateValueStrategy());
		text.addVerifyListener(new MeanVerifyListener());
	}*/

	// Binds values that are subclass types of AbstractString
	protected abstract void bindAbstractString(TypedHashMap typedHashMap, int index, 
			AtomicTypeBase myType);/* {
		// TODO: bind method for Unit in ExcessMortality		
	}*/
	
	// TODO: check instanceof AbstractValue and AbstractString
	// TODO: create factories
	// TODO: Unit has to be varied
	// and other basetypes
	// Call the other  
	// Look to Matrix and others
	private void bindValue(TypedHashMap typedHashMap, int index, 
			AtomicTypeBase myType) {
		if (myType instanceof AbstractValue) {
			bindAbstractValue(typedHashMap, index, myType);
		} else
		if (myType instanceof AbstractString) {
			bindAbstractString(typedHashMap, index, myType);
		}		
	}
	
	private void bindTestValue(TypedHashMap sexMap, int index) {
		Text text = new Text(this, SWT.NONE);
		text.setText(sexMap.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue
						.getValueType()), ViewUpdateValueStrategies
						.getStrategy(modelObservableValue.getValueType()));
	}
}
