package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AgeGroup4TransitionMatrix extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	Composite myParent;
	BaseNode mySelectedNode;
	TypedHashMap<Age> myModelObject;
	DataBindingContext myDataBindingContext;
	HelpGroup myHelpPanel;
	TransitionMatrixOneAgeAndSexParameterGroup[] transMatGroups;
	int currentAge = 0;
	boolean grow = false;

	public AgeGroup4TransitionMatrix(Composite parent, int style,
			BaseNode selectedNode, TypedHashMap<?> modelObject,
			DataBindingContext dataBindingContext, HelpGroup helpPanel) {
		super(parent, style);
		myParent = parent;
		mySelectedNode = selectedNode;
		myModelObject = (TypedHashMap<Age>) modelObject;
		myDataBindingContext = dataBindingContext;
		myHelpPanel = helpPanel;
		FormData ageGroupLayoutData = new FormData();
		ageGroupLayoutData.top = new FormAttachment(0, 2);
		ageGroupLayoutData.left = new FormAttachment(0, 2);
		ageGroupLayoutData.right = new FormAttachment(100, -2);
		ageGroupLayoutData.bottom = new FormAttachment(0, 75);
		setLayoutData(ageGroupLayoutData);
		GridLayout ageGroupLayout = new GridLayout();
		ageGroupLayout.makeColumnsEqualWidth = true;
		ageGroupLayout.numColumns = 8;
		setLayout(ageGroupLayout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText(XMLTagEntityEnum.AGE.getTheType().getXMLElementName()
				+ ": ");
		Combo ageDropDown = new Combo(this, SWT.DROP_DOWN);
		int minAge = ((AbstractRangedInteger) XMLTagEntityEnum.AGE.getTheType())
				.getIKnowWhatImDoingMIN_VALUE();
		int maxAge = ((AbstractRangedInteger) XMLTagEntityEnum.AGE.getTheType())
				.getMAX_VALUE();
		for (int ageCount = minAge; ageCount <= maxAge; ageCount++) {
			ageDropDown.add(new Integer(ageCount).toString(), ageCount);
		}
		ageDropDown.select(42);
		ageDropDown.addModifyListener(new AgeDropDownModifyListener());
		Button button = new Button(this, SWT.NONE);
		button.setText("Apply current sheet to all higher ages");
		button.addSelectionListener(new ApplyButtonSelectionListener());
		int minGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getIKnowWhatImDoingMIN_VALUE();
		int maxGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getMAX_VALUE();
		transMatGroups = new TransitionMatrixOneAgeAndSexParameterGroup[maxGender
				- minGender + 1];
		refreshTransitionMatrices(currentAge);
	}

	public void refreshTransitionMatrices(int age) {
		int minGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getIKnowWhatImDoingMIN_VALUE();
		int maxGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getMAX_VALUE();
		for (int genderCount = minGender; genderCount <= maxGender; genderCount++) {
			refreshTransitionMatrixGroup(myParent, age, genderCount, this);
		}
	}

	private void refreshTransitionMatrixGroup(Composite parent, int age,
			int gender, Composite topNeighbour) {
		// remove if present.
		if (transMatGroups[gender] != null) {
			transMatGroups[gender].theGroup.dispose();
		}
		transMatGroups[gender] = new TransitionMatrixOneAgeAndSexParameterGroup(
				parent,
				(TypedHashMap<TransitionSource>) ((TypedHashMap<Sex>) myModelObject
						.get(age)).get(gender), myDataBindingContext,
				mySelectedNode, myHelpPanel);
		FormData layoutData2 = new FormData();
		layoutData2.top = new FormAttachment(topNeighbour, 5);
		layoutData2.left = new FormAttachment(50 * gender, 2);
		layoutData2.right = new FormAttachment(50 * gender + 50, -2);
		layoutData2.bottom = new FormAttachment(100, -5);
		transMatGroups[gender].getGroup().setLayoutData(layoutData2);
	}

	class AgeDropDownModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent arg0) {
			log.debug("Dropdown modified");
			currentAge = ((Combo) arg0.widget).getSelectionIndex();
			refreshTransitionMatrices(currentAge);
			// Control[] controls = new Control[2];
			// controls[0] = transMatGroups[0].theGroup;
			// controls[1] = transMatGroups[1].theGroup;
			 Shell modalShell = myHelpPanel.getModalShell();
			  Point oldSize = modalShell.getSize();
			  int sizeDelta = -1;
			  if(grow){
				  sizeDelta = 1;
			  }
			  grow = !grow;
			 Point newSize = new Point(oldSize.x+sizeDelta,oldSize.y);
//			  Point newSize = modalShell.computeSize(oldSize.x+1, oldSize.y+1, true);
			  modalShell.setSize(newSize);
			// modalShell.changed(controls);
			 modalShell.redraw();
			// controls[0].redraw();
			// modalShell.layout(true);
			 modalShell.update();
		}
	}

	class ApplyButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			((Button) arg0.widget).setText("Default");
		}

		public void widgetSelected(SelectionEvent arg0) {
			((Button) arg0.widget).setText("Applied");
			// transMatGroups[0].theGroup.dispose();
		}
	}
}
