package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TransitionMatrixAgeGroup extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	Composite myParent;
	BaseNode mySelectedNode;
	TypedHashMap<Age> myModelObject;
	DataBindingContext myDataBindingContext;
	HelpGroup myHelpPanel;
	Group ageDropDownPanel;
	TransitionMatrixOneAgeAndSexParameterGroup[] transMatGroups;
	int currentAge = 0;
	boolean grow = false;
	private Point initialSize;

	public TransitionMatrixAgeGroup(Shell parent, int style,
			BaseNode selectedNode, TypedHashMap<?> modelObject,
			DataBindingContext dataBindingContext, HelpGroup helpPanel,
			GenericButtonPanel buttonPanel) {
		super(parent, style);
		myParent = parent;
		mySelectedNode = selectedNode;
		myModelObject = (TypedHashMap<Age>) modelObject;
		myDataBindingContext = dataBindingContext;
		myHelpPanel = helpPanel;
		FormData ageGroupLayoutData = new FormData();
		ageGroupLayoutData.top = new FormAttachment(0, 2);
		ageGroupLayoutData.left = new FormAttachment(0, 2);
		ageGroupLayoutData.right = new FormAttachment(helpPanel.getGroup(), -2);
		ageGroupLayoutData.bottom = new FormAttachment(buttonPanel, -2);
		setLayoutData(ageGroupLayoutData);
		FormLayout ageGroupLayout = new FormLayout();
		setLayout(ageGroupLayout);
		ageDropDownPanel = createAgeDropDownPanel(this, helpPanel);
		int minGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getIKnowWhatImDoingMIN_VALUE();
		int maxGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getMAX_VALUE();
		transMatGroups = new TransitionMatrixOneAgeAndSexParameterGroup[maxGender
				- minGender + 1];
		refreshTransitionMatrices(currentAge, ageDropDownPanel);
		parent.pack(true);
		Point groupSize = transMatGroups[0].theGroup.getSize();
		Point minAgeDropdownPanelSize = new Point(200,60);
		initialSize = (groupSize.x<minAgeDropdownPanelSize.x)?minAgeDropdownPanelSize:groupSize;
	}

	private Group createAgeDropDownPanel(Composite parent, HelpGroup helpPanel) {
		Group ageDropDownPanel = new Group(this, SWT.NONE);
		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 2);
		layoutData.left = new FormAttachment(0, 2);
		layoutData.right = new FormAttachment(100, -2);
		layoutData.bottom = new FormAttachment(0, 75);
		ageDropDownPanel.setLayoutData(layoutData);
		GridLayout ageDropDownLayout = new GridLayout();
		ageDropDownLayout.makeColumnsEqualWidth = true;
		ageDropDownLayout.numColumns = 4;
		ageDropDownPanel.setLayout(ageDropDownLayout);
		Label ageLabel = new Label(ageDropDownPanel, SWT.NONE);
		ageLabel.setText(XMLTagEntityEnum.AGE.getTheType().getXMLElementName()
				+ ": ");
		GridData ageLabelLayoutData = new GridData();
		ageLabelLayoutData.horizontalSpan = 1;
		ageLabel.setLayoutData(ageLabelLayoutData);
		Combo ageDropDown = new Combo(ageDropDownPanel, SWT.DROP_DOWN);
		int minAge = ((AbstractRangedInteger) XMLTagEntityEnum.AGE.getTheType())
				.getIKnowWhatImDoingMIN_VALUE();
		int maxAge = ((AbstractRangedInteger) XMLTagEntityEnum.AGE.getTheType())
				.getMAX_VALUE();
		for (int ageCount = minAge; ageCount <= maxAge; ageCount++) {
			ageDropDown.add(new Integer(ageCount).toString(), ageCount);
		}
		ageDropDown.select(42);
		// The select doesn't fire a modify-event.
		currentAge = 42;
		ModifyListener ageDropDownModifyListener = new AgeDropDownModifyListener();
		ageDropDown.addModifyListener(ageDropDownModifyListener);
		GridData ageDropDownLayoutData = new GridData();
		ageDropDownLayoutData.horizontalSpan = 1;
		ageDropDown.setLayoutData(ageDropDownLayoutData);
		Button copyButton = new Button(ageDropDownPanel, SWT.NONE);
		copyButton.setText("Apply current sheet to all higher ages");
		copyButton.addSelectionListener(new ApplyButtonSelectionListener());
		GridData copyButtonLayoutData = new GridData();
		copyButtonLayoutData.horizontalSpan = 2;
		// THIS DOESN'T WORK. Preventing half hidden button at low category counts.
//		copyButtonLayoutData.grabExcessHorizontalSpace = true;
//		copyButtonLayoutData.minimumWidth = 200;
        //		
		copyButton.setLayoutData(copyButtonLayoutData);
		return ageDropDownPanel;
	}

	public void refreshTransitionMatrices(int age, Group topNeighbour) {
		int minGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getIKnowWhatImDoingMIN_VALUE();
		int maxGender = ((AbstractRangedInteger) XMLTagEntityEnum.SEX
				.getTheType()).getMAX_VALUE();
		for (int genderCount = minGender; genderCount <= maxGender; genderCount++) {
			refreshTransitionMatrixGroup(this, age, genderCount, topNeighbour);
		}
		layoutRefreshedTransitionMatrixGroups(this, age, topNeighbour);
		Shell modalShell = myHelpPanel.getModalShell();
		Point oldSize = modalShell.getSize();
		int sizeDelta = -1;
		if (grow) {
			sizeDelta = 1;
		}
		grow = !grow;
		Point newSize = new Point(oldSize.x + sizeDelta, oldSize.y);
		modalShell.setSize(newSize);
		modalShell.redraw();
		modalShell.update();
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
	}

	private void layoutRefreshedTransitionMatrixGroups(Composite parent,
			int age, Composite topNeighbour) {
		FormData femaleLayoutData = new FormData();
		femaleLayoutData.top = new FormAttachment(topNeighbour, 2);
		femaleLayoutData.left = new FormAttachment(0, 2);
		femaleLayoutData.right = new FormAttachment(transMatGroups[1]
				.getGroup(), -2);
		femaleLayoutData.bottom = new FormAttachment(100, -2);
		transMatGroups[0].getGroup().setLayoutData(femaleLayoutData);
		FormData maleLayoutData = new FormData();
		maleLayoutData.top = new FormAttachment(topNeighbour, 2);
		maleLayoutData.left = new FormAttachment(transMatGroups[0].getGroup(),
				2);
		maleLayoutData.right = new FormAttachment(100, -2);
		maleLayoutData.bottom = new FormAttachment(100, -2);
		transMatGroups[1].getGroup().setLayoutData(maleLayoutData);
	}

	class AgeDropDownModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent arg0) {
			log.debug("Dropdown modified");
			currentAge = ((Combo) arg0.widget).getSelectionIndex();
			refreshTransitionMatrices(currentAge, ageDropDownPanel);
		}
	}

	class ApplyButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			((Button) arg0.widget).setText("Default");
		}

		/**
		 * This method copies the data from the age that is selected at the time
		 * of clicking to all higher ages.
		 * 
		 */
		public void widgetSelected(SelectionEvent arg0) {
			try {
				((Button) arg0.widget).setText("Apply current sheet to all higher ages");
				TypedHashMap<TransitionSource>[] startAgeData = new TypedHashMap[2];
				for (int genderCount = 0; genderCount <= 1; genderCount++) {
					startAgeData[genderCount] = (TypedHashMap<TransitionSource>) ((TypedHashMap<Sex>) myModelObject
							.get(currentAge)).get(genderCount);
				}
				int maxAge = ((AbstractRangedInteger) XMLTagEntityEnum.AGE
						.getTheType()).getMAX_VALUE();
				for (int ageCount = currentAge + 1; ageCount <= maxAge; ageCount++) {
					for (int genderCount = 0; genderCount <= 1; genderCount++) {
						TypedHashMap<TransitionSource> newMap = new TypedHashMap<TransitionSource>(
								startAgeData[genderCount]);
						 ((TypedHashMap<Sex>)
						 myModelObject.get(ageCount)).put(genderCount, newMap);
//						((Button) arg0.widget).setText("Working on age: " + ageCount);
					}
				}
//			((Button) arg0.widget).setText("Ready");

			} catch (DynamoConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				((Button) arg0.widget).setText("Exception caught.");
			}
		}

		/**
		 * Testversion, copies the female data to the male data of the same age.
		 * Tests ok.
		 */
		// public void widgetSelected(SelectionEvent arg0) {
		// try {
		// ((Button) arg0.widget).setText("Testing");
		// TypedHashMap<Sex> currentAgeMap = (TypedHashMap<Sex>) myModelObject
		// .get(currentAge);
		// TypedHashMap<TransitionSource> femaleMap =
		// (TypedHashMap<TransitionSource>) currentAgeMap
		// .get(0);
		// TypedHashMap<TransitionSource> newMaleMap = new
		// TypedHashMap<TransitionSource>(femaleMap);
		// currentAgeMap.put(1, newMaleMap);
		// refreshTransitionMatrices(currentAge, ageDropDownPanel);
		// ((Button) arg0.widget).setText("Tested");
		// } catch (DynamoConfigurationException e) {
		// e.printStackTrace();
		// ((Button) arg0.widget).setText("Exception caught.");
		// }
		// }
	}

	public Point getInitialSize() {
		return initialSize;
	}
}
