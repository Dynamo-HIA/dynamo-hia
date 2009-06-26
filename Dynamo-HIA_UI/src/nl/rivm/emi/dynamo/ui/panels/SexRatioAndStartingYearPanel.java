package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.SexRatio;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.StartingYearUpdateButtonSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractValueVerifyListener;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class SexRatioAndStartingYearPanel {

	private static final String SEX_RATIO = "Sex ratio";

	private HelpGroup theHelpGroup;
	private Composite myParent;
	private NewbornsObject newbornsObject;
	private DataBindingContext dataBindingContext;
	// private Button updateButton;
	// public Group groupStartingYear;
	public StartingYearFields startingYearFields;
	public Group group;
	private DataAndFileContainer modalParent;

	public SexRatioAndStartingYearPanel(Group parent,
			NewbornsObject newbornsObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			DataAndFileContainer modalParent)
			throws DynamoInconsistentDataException {
		this.myParent = parent;
		this.newbornsObject = newbornsObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.modalParent = modalParent;

		group = new Group(parent, SWT.NONE);
		// FormLayout formLayout = new FormLayout();
		// group.setLayout(formLayout);
		GridLayout gridLayout = new GridLayout(10, true);
		group.setLayout(gridLayout);
		String labelValue = SEX_RATIO;
		WritableValue observable = newbornsObject.getObservableSexRatio();
		Text text = bindHeaderValue(observable, labelValue, new SexRatio(),
				group);
		Label spaceLabel = new Label(group, SWT.NONE);
		GridData spaceLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		spaceLabelGridData.horizontalSpan = 2;
		spaceLabel.setLayoutData(spaceLabelGridData);
		startingYearFields = new StartingYearFields(group, SWT.NONE);
		Label space2Label = new Label(group, SWT.NONE);
		GridData space2LabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		space2LabelGridData.horizontalSpan = 3;
		space2Label.setLayoutData(space2LabelGridData);
	}

	public void putNextInContainer(Group topNeighbour, int height, Group group) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	private Text bindHeaderValue(WritableValue observable, String labelValue,
			AtomicTypeBase myType, Composite group) {
		Text text = null;
		if (observable != null) {
			Label label = new Label(group, SWT.NONE);
			label.setText(labelValue + ": ");
			text = bindValue(observable, myType, group, label);
			GridData textGridDate = new GridData(GridData.FILL_HORIZONTAL);
			text.setLayoutData(textGridDate);
		} else {
			MessageBox box = new MessageBox(this.myParent.getShell());
			box.setText("Class name error");
			box.setMessage("Value for " + labelValue + " should not be empty.");
			box.open();
		}
		return text;
	}

	protected Text bindValue(WritableValue observable, AtomicTypeBase myType,
			Composite group, Label label) {
		Text text = null;
		if (myType instanceof AbstractRangedInteger) {
			text = bindAbstractRangedInteger(observable, myType, group, label);
		} else if (myType instanceof AbstractValue) {
			text = bindAbstractValue(observable, myType, group, label);
		}
		return text;
	}

	protected Text bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType, Composite group, Label label) {
		Text text = getTextBinding(observableObject, myType, group, label);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(theHelpGroup.getTheModal(), myType));
		return text;
	}

	protected Text bindAbstractValue(WritableValue observableObject,
			AtomicTypeBase myType, Composite group, Label label) {
		Text text = getTextBinding(observableObject, myType, group, label);
		text.addVerifyListener(new AbstractValueVerifyListener(theHelpGroup.getTheModal(), myType));
		return text;
	}

	private Text getTextBinding(WritableValue observableObject,
			AtomicTypeBase myType, Composite group, Label label) {
		Text text = createAndPlaceTextField(group, label);
		text.setText((String) myType
				.convert4View(observableObject.doGetValue()));
		FocusListener focusListener = new TypedFocusListener(myType,theHelpGroup);
		text.addFocusListener(
		// new FocusListener() {
//			public void focusGained(FocusEvent arg0) {
//				theHelpGroup.getFieldHelpGroup().setHelpText("1");
//			}
//
//			public void focusLost(FocusEvent arg0) {
//				theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
//				// range.
//			}
//		}
		focusListener);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		return text;
	}

	private Text createAndPlaceTextField(Composite group, Label label) {
		final Text text = new Text(group, SWT.NONE);
		return text;
	}

	/**
	 * Handling starting-year field department.
	 */
	private class StartingYearFields {
		private static final String STARTING_YEAR = "Starting year";
		private static final String UPDATE = "Update";

		Composite theParent;
		Label label;
		Text startingYearText;
		AbstractRangedInteger startingYearType = (AbstractRangedInteger) XMLTagEntityEnum.STARTINGYEAR
				.getTheType();
		Button updateButton;

		public StartingYearFields(Composite parent, int style)
				throws DynamoInconsistentDataException {
			this.theParent = parent;
			setupLabel();
			setupText();
			setupUpdateButton();
		}

		private void setupLabel() {
			String labelValue = STARTING_YEAR;
			label = new Label(theParent, SWT.NONE);
			label.setText(labelValue + ": ");

		}

		private void setupText() throws DynamoInconsistentDataException {
			startingYearText = new Text(theParent, SWT.NONE);
			GridData startingYearTextGridData = new GridData(GridData.FILL_HORIZONTAL);
			startingYearText.setLayoutData(startingYearTextGridData);
			Integer startingYear = newbornsObject.getStartingYear();
			startingYearText.setText(startingYearType
					.convert4View(startingYear));
			startingYearText.addVerifyListener(startingYearType);
			FocusListener focusListener = new TypedFocusListener(XMLTagEntityEnum.STARTINGYEAR.getTheType(),theHelpGroup);
		startingYearText.addFocusListener(
//				new FocusListener() {
//				public void focusGained(FocusEvent arg0) {
//					theHelpGroup.getFieldHelpGroup().setHelpText("1");
//				}
//
//				public void focusLost(FocusEvent arg0) {
//					theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out
//					// of
//					// range.
//				}
//			}
			focusListener);
		}

		private void setupUpdateButton() {
			updateButton = new Button(theParent, SWT.PUSH);
			updateButton.setText(UPDATE);
			updateButton
					.addSelectionListener(new StartingYearUpdateButtonSelectionListener(
							modalParent, startingYearText, startingYearType));

		}

	}
}
