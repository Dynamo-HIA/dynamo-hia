package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.types.atomic.SexRatio;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.selection.StartingYearModifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractValueVerifyListener;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class SexRatioAndStartingYearPanel {

	private static final String SEX_RATIO = "Sex ratio";
	private static final String STARTING_YEAR = "Starting year";
	private static final String UPDATE = "Update";
	
	private HelpGroup theHelpGroup;
	private StartingYearModifyListener startingYearModifyListener;
	private Composite myParent;
	private NewbornsObject newbornsObject;
	private DataBindingContext dataBindingContext;
	private Button updateButton;
	public Group groupStartingYear;
	public Group group;
	private DataAndFileContainer modalParent;

	public SexRatioAndStartingYearPanel(Group parent,
			NewbornsObject newbornsObject, 
			DataBindingContext dataBindingContext, 
			HelpGroup helpGroup,
			DataAndFileContainer modalParent) {
		this.myParent = parent;
		this.newbornsObject = newbornsObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.modalParent = modalParent;
		
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		
		String labelValue = SEX_RATIO;
		WritableValue observable = newbornsObject.getObservableSexRatio();
		Text text = bindHeaderValue(observable, labelValue, new SexRatio(), group);
		
		groupStartingYear = new Group(parent, SWT.NONE);
		FormLayout formLayoutStartYear = new FormLayout();
		groupStartingYear.setLayout(formLayoutStartYear);

		this.putNextInContainer(group, 10, groupStartingYear);
		
		labelValue = STARTING_YEAR;
		observable = newbornsObject.getObservableStartingYear();
		text = bindHeaderValue(observable, labelValue, new StartingYear(), groupStartingYear);				
				
		updateButton = putUpdateButton(groupStartingYear, text);
		setModifyListener();
	}
		
	public void setModifyListener() {
		this.startingYearModifyListener = new StartingYearModifyListener(
				this.modalParent);		
		updateButton
				.addSelectionListener(this.startingYearModifyListener);
	}

	static private Button putUpdateButton(Composite group, 
			Text text) {
		Button updateButton = new Button(group, SWT.PUSH);
		updateButton.setText(UPDATE);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(text, 20);
		formData.right = new FormAttachment(50, -5);
		formData.top = new FormAttachment(0, 2);
		formData.bottom = new FormAttachment(100, -5);
		updateButton.setLayoutData(formData);
		return updateButton;
	}

	

	public void putNextInContainer(Group topNeighbour, int height, Composite group) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public StartingYearModifyListener getStartingYearModifyListener() {
		return startingYearModifyListener;
	}
		
	private Text bindHeaderValue(WritableValue observable, String labelValue,
			AtomicTypeBase myType, Composite group) {
		Text text = null;
		if (observable != null) {
			Label label = new Label(group, SWT.NONE);
			label.setText(labelValue + ": ");
			
			FormData labelFormData = new FormData();
			labelFormData.top = new FormAttachment(0, 2);
			labelFormData.left = new FormAttachment(0, 5);
			labelFormData.right = new FormAttachment(0, 100);
			labelFormData.bottom = new FormAttachment(100, -5);
			label.setLayoutData(labelFormData);
			
			text = bindValue(observable, myType, group, label);
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
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(myType));
		return text;
	}

	protected Text bindAbstractValue(WritableValue observableObject,
			AtomicTypeBase myType, Composite group, Label label) {
		Text text = getTextBinding(observableObject, myType, group, label);
		text.addVerifyListener(new AbstractValueVerifyListener(myType));
		return text;
	}

	
	private Text getTextBinding(WritableValue observableObject,
			AtomicTypeBase myType, Composite group, Label label) {
		Text text = createAndPlaceTextField(group, label);
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
	
	
	
	private Text createAndPlaceTextField(Composite group, Label label) {
		final Text text = new Text(group, SWT.NONE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 2);
		formData.left = new FormAttachment(label, 0);
		formData.right = new FormAttachment(30, -20);
		formData.bottom = new FormAttachment(100, -5);
		text.setLayoutData(formData);
		return text;
	}	
}
