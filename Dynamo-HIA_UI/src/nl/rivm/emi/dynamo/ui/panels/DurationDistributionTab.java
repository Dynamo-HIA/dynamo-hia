/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.ui.listeners.ScrollListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * Defines the nested risk factor tab
 * 
 * @author schutb
 * 
 */
public class DurationDistributionTab {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private int genderIndex;
	private DurationDistributionObject durationDistributionObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
//	private BaseNode selectedNode;

	private TabFolder tabFolder;
	private TabItem tabItem;

//	/**
//	 * Default constructor for allocating array only.
//	 */
//	public DurationDistributionTab() {
//	}

	/**
	 * @param tabfolder
	 * @param genderIndex
	 *            TODO
	 * @param output
	 * @throws ConfigurationException
	 */
	public DurationDistributionTab(TabFolder tabfolder, int genderIndex,
			DurationDistributionObject durationDistributionObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		this.tabFolder = tabfolder;
		this.genderIndex = genderIndex;
		this.dataBindingContext = dataBindingContext;
		this.durationDistributionObject = durationDistributionObject;
		this.helpGroup = helpGroup;
//		this.selectedNode = selectedNode;
		makeIt();
	}

	/**
	 * makes the tabfolder
	 * 
	 * @throws ConfigurationException
	 */
	public void makeIt() throws ConfigurationException {
		tabItem = new TabItem(this.tabFolder, SWT.NONE);
		tabItem.setText(BiGender.labels[genderIndex]);

		ScrolledComposite scrolledContainer = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormLayout formLayout4Scroll = new FormLayout();
		scrolledContainer.setLayout(formLayout4Scroll);
		scrolledContainer.setBackground(new Color(null, 0x00, 0x00, 0xee));
		DurationDistributionParameterDataPanel genderParameterDataPanel = new DurationDistributionParameterDataPanel(
				scrolledContainer, this.genderIndex,
				durationDistributionObject, dataBindingContext, helpGroup);

		FormData parameterFormData = new FormData();
		parameterFormData.top = new FormAttachment(0, 2);
		parameterFormData.right = new FormAttachment(100, -5);
		parameterFormData.left = new FormAttachment(0, 5);
		parameterFormData.bottom = new FormAttachment(100, -2);
		genderParameterDataPanel.setLayoutData(parameterFormData);
		scrolledContainer.setContent(genderParameterDataPanel);
		scrolledContainer.setExpandHorizontal(true);
		scrolledContainer.setExpandVertical(true);
		scrolledContainer.setMinSize(genderParameterDataPanel.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		Control[] controls = genderParameterDataPanel.getChildren();
		ScrollListener listener = new ScrollListener(scrolledContainer);
		for (int i = 0; i < controls.length; i++) {
			controls[i].addListener(SWT.Activate, listener);
		}
		genderParameterDataPanel
				.setBackground(new Color(null, 0xbb, 0xbb, 0xbb));
		tabItem.setControl(scrolledContainer);
	}

	/**
	 * Redraws the tab component
	 */
	public void redraw() {
		log.debug("REDRAW THIS");
		tabItem.getControl().redraw();
	}

}