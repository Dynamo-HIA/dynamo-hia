package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.PercentVerifyListener;
import nl.rivm.emi.dynamo.ui.main.RelativeRisksDiseaseOnDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
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

public class RelativeRisksMatrixParameterPanel extends Composite /*
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
	/**
	 * Field that controls the width and breadth of the matrix. Initialized to
	 * something painfull, so things should blow up when proper initialization
	 * doesn't happen.
	 */
	private int numberOfCategories = -1;

	// AtomicTypeBase myType = new Unit();

	public RelativeRisksMatrixParameterPanel(Composite parent, Text topNeighbour,
			TypedHashMap<TransitionSource> transitionFromClassToClassObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.transitionFromClassToClassObject = transitionFromClassToClassObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		numberOfCategories = transitionFromClassToClassObject.size();
		layout.numColumns = numberOfCategories + 1;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		Label fromLabel = new Label(this, SWT.NONE);
		fromLabel.setText("To:");
		Label[] topLabels = new Label[numberOfCategories];
		for (int count = 1; count <= numberOfCategories; count++) {
			topLabels[count - 1] = new Label(this, SWT.NONE);
			topLabels[count - 1].setText(new Integer(count).toString());
		}
		Label toLabel = new Label(this, SWT.NONE);
		toLabel.setText("From");
		Label[] blankLabels = new Label[numberOfCategories];
		for (int count = 1; count <= numberOfCategories; count++) {
			blankLabels[count - 1] = new Label(this, SWT.NONE);
			blankLabels[count - 1].setText(" ");
		}
		for (int sourceCount = 1; sourceCount <= numberOfCategories; sourceCount++) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(sourceCount).toString());
			TypedHashMap<TransitionDestination> destinationMap = (TypedHashMap<TransitionDestination>) transitionFromClassToClassObject
					.get(sourceCount);
			int numberOfDestinations = destinationMap.size();
			for (int destinationCount = 1; destinationCount <= numberOfDestinations; destinationCount++) {
				ArrayList<AtomicTypeObjectTuple> parameterList = (ArrayList<AtomicTypeObjectTuple>) destinationMap
						.get(destinationCount);
				WritableValue observableValue = null;
				if ((sourceCount == destinationCount)
						&& (((RelativeRisksDiseaseOnDiseaseModal) helpGroup.getTheModal())
								.isHasDefaultObject())) {
					observableValue = (WritableValue) parameterList.get(0)
							.getValue();
					/**
					 * Set the diagonal to 100 on the diagonal.
					 */
					observableValue.doSetValue(new Float(100F));
				} else {
					observableValue = (WritableValue) parameterList.get(0)
							.getValue();
				}
				AtomicTypeBase<Float> theType = (AtomicTypeBase<Float>) parameterList
						.get(0).getType();
				bindValue(observableValue, theType);
			}
			// Once should be enough.
			((RelativeRisksDiseaseOnDiseaseModal) helpGroup.getTheModal()).setHasDefaultObject(false);
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
		Text text = new Text(this, SWT.NONE); // createAndPlaceTextField();
		text.setText("Bla"); // theType.convert4View(observableClassName.doGetValue()));
		// FocusListener focusListener = new
		// TypedFocusListener(theType,theHelpGroup);
		// text.addFocusListener(
		// // new FocusListener() {
		// // public void focusGained(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("1");
		// // }
		// //
		// // public void focusLost(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
		// // // range.
		// // }
		// //
		// // }
		// focusListener);
		HelpTextListenerUtil.addHelpTextListeners(text, theType);
		GridData textLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		textLayoutData.minimumWidth = 35;
		textLayoutData.horizontalAlignment = GridData.END;
		text.setLayoutData(textLayoutData);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		
		
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		// text.addVerifyListener(new ValueVerifyListener());
		text.addVerifyListener(new PercentVerifyListener(theHelpGroup
				.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gridData);
		return text;
	}
}
