package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.Transition;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

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

public class TransitionMatrixParameterPanel extends Composite /*
															 * implements
															 * Runnable
															 */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.MortalityDefinitionsDataPanel");
	TypedHashMap<TransitionSource> transitionFromClassToClassObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	final private int NUMCOLUMNS = 10;

	// AtomicTypeBase myType = new Unit();

	public TransitionMatrixParameterPanel(Composite parent, Text topNeighbour,
			TypedHashMap<TransitionSource> transitionFromClassToClassObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.transitionFromClassToClassObject = transitionFromClassToClassObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = NUMCOLUMNS;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label spaceLabel = new Label(this, SWT.NONE);
		spaceLabel.setText(" ");
		Label[] topLabels = new Label[9];
		for (int count = 1; count < NUMCOLUMNS; count++) {
			topLabels[count-1] = new Label(this, SWT.NONE);
			topLabels[count-1].setText("T" + new Integer(count).toString());
		}
		int numberOfSources = transitionFromClassToClassObject.size();
		for (int sourceCount = 1; sourceCount <= numberOfSources; sourceCount++) {
			Label label = new Label(this, SWT.NONE);
			label.setText("S" + new Integer(sourceCount).toString());
			TypedHashMap<TransitionDestination> destinationMap = (TypedHashMap<TransitionDestination>) transitionFromClassToClassObject
					.get(sourceCount);
			int numberOfDestinations = destinationMap.size();
			for (int destinationCount = 1; destinationCount <= numberOfDestinations; destinationCount++) {
				ArrayList<AtomicTypeObjectTuple> parameterList = (ArrayList<AtomicTypeObjectTuple>) destinationMap
				.get(destinationCount);
				WritableValue observableValue = (WritableValue) parameterList.get(0).getValue();
				AtomicTypeBase<Float> theType = (AtomicTypeBase<Float>) parameterList.get(0).getType();
				bindValue(observableValue, theType);
			}
			}
		/*
		 * } else { MessageBox box = new MessageBox(parent.getShell());
		 * box.setText("Error creating matrix value");
		 * box.setMessage("Matrix value at age " + count +
		 * " should not be empty."); box.open(); }
		 */
	}

	private void bindValue(WritableValue observableClassName,
			AtomicTypeBase<Float> theType) {
		Text text = new Text(this, SWT.NONE);  // createAndPlaceTextField();
		text.setText("Bla"); // theType.convert4View(observableClassName.doGetValue()));
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}

		});
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener());
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
}