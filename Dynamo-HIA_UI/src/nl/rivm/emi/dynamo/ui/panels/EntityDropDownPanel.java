package nl.rivm.emi.dynamo.ui.panels;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EntityDropDownPanel {

	Log log = LogFactory.getLog(this.getClass().getName());

	private Group theGroup;
	private Text freePart;
	private Combo dropDown;
	private HelpGroup theHelpGroup;
	private Map selectableExcessMortalityPropertiesMap;

	private int selectedIndex;
	
	public EntityDropDownPanel(Composite parent, String entityLabel,
			Map selectableExcessMortalityPropertiesMap) {
		this.selectableExcessMortalityPropertiesMap =
			selectableExcessMortalityPropertiesMap;
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		Label label = new Label(theGroup, SWT.LEFT);
		label.setText(entityLabel + ":");
		
		
		freePart = new Text(theGroup, SWT.BORDER);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(0, 15);
		textFormData.right = new FormAttachment(100, -15);
		textFormData.top = new FormAttachment(0, 10);
		freePart.setLayoutData(textFormData);
		dropDown = new Combo(theGroup, SWT.DROP_DOWN);
		Set<String> keys = this.selectableExcessMortalityPropertiesMap.keySet();
		int index = 0;
		for (String item : keys) {
			dropDown.add(item, index);
			index ++;
		}
		dropDown.addSelectionListener(new SelectionAdapter() {
			// In case the user does not select anything
			public void widgetDefaultSelected(SelectionEvent e) {
				EntityDropDownPanel.this.selectedIndex = dropDown.getSelectionIndex();
			}
			// In case the user makes the selection
			public void widgetSelected(SelectionEvent e) {
				EntityDropDownPanel.this.selectedIndex = dropDown.getSelectionIndex();
			}
		});
		dropDown.select(0);
		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(0, 15);
		comboFormData.right = new FormAttachment(100, -15);
		comboFormData.top = new FormAttachment(freePart, 10);
		dropDown.setLayoutData(comboFormData);		
	}

	public String getUnitType() {		
		return (String) 
			this.selectableExcessMortalityPropertiesMap.get(this.selectedIndex);
	}

	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		theGroup.setLayoutData(formData);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;		
	}

	/**
	 * 
	 * Place the first group in the container
	 * 
	 * @param height
	 */
	public void putFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		theGroup.setLayoutData(formData);
	}
	
	public void putMiddleInContainer(Group topNeighbour, int height) {
		// TODO Auto-generated method stub
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(6, 5 + height);
		theGroup.setLayoutData(formData);		
	}	
	
}
