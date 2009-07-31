package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DurationDistributionTabsPanel {

	private Log log = LogFactory.getLog(this.getClass().getName());

	protected DurationDistributionObject durationDistributionObject;
//	final private Composite myParent;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	final private TabFolder tabFolder;
	
	public DurationDistributionTabsPanel(Composite parent,
			BaseNode selectedNode,
			DurationDistributionObject durationDistributionObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup)
			throws ConfigurationException {
//		this.myParent = parent;
		tabFolder = new TabFolder(parent, SWT.FILL);
		tabFolder.setLayout(new FillLayout());
		this.durationDistributionObject = durationDistributionObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeTabs(parent);
	}

	public void makeTabs(Composite parent) throws ConfigurationException {

		log.debug("durationDistributionObject: " + durationDistributionObject);


		final DurationDistributionTab[] genderTabs = new DurationDistributionTab[2];
		genderTabs[BiGender.FEMALE_INDEX] = new DurationDistributionTab(
				tabFolder, BiGender.FEMALE_INDEX, durationDistributionObject,
				dataBindingContext, selectedNode, theHelpGroup);

		genderTabs[BiGender.MALE_INDEX] = new DurationDistributionTab(
				tabFolder, BiGender.MALE_INDEX, durationDistributionObject,
				dataBindingContext, selectedNode, theHelpGroup);

		tabFolder.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TabItem item = (TabItem) event.item;
				String tabId = item.getText();
				log.debug("TabText: " + tabId);
				if (BiGender.labels[BiGender.FEMALE_INDEX].equals(tabId)) {
					genderTabs[BiGender.FEMALE_INDEX].redraw();
				} else {
					if (BiGender.labels[BiGender.MALE_INDEX].equals(tabId)) {
						genderTabs[BiGender.MALE_INDEX].redraw();
					} else {

					}
				}
			}

//			private void handleErrorMessage(Exception e) {
//				e.printStackTrace();
//				MessageBox box = new MessageBox(
//						DurationDistributionTabsPanel.this.myParent.getShell(),
//						SWT.ERROR_UNSPECIFIED);
//				box.setText("Error occured during creation of a new tab "
//						+ e.getMessage());
//				box.setMessage(e.getMessage());
//				box.open();
//			}

		});

	}
	public void putInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		tabFolder.setLayoutData(formData);
	}

}
