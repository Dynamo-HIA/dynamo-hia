package nl.rivm.emi.dynamo.ui.treecontrol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Util {

	public static final String PLUGIN_ID = "CZM_Main";
	
	static public String[] deriveEntityLabelAndValueFromRiskSourceNode(BaseNode selectedNode) {
		BaseNode startNode = selectedNode;
		if (selectedNode instanceof FileNode) {
			startNode = (BaseNode) ((ChildNode) selectedNode).getParent();
		}
		String startLabel = ((BaseNode) startNode).toString();
		ParentNode containerNode = ((ChildNode) startNode).getParent();
		String containerLabel = ((BaseNode) containerNode).toString();
		containerLabel = containerLabel.substring(0,
				containerLabel.length() - 1);
		String[] result = new String[2];
		result[0] = containerLabel;
		result[1] = startLabel;
		return result;
	}

	static public String[] deriveEntityLabelAndValueFromSelectedNode(
			BaseNode selectedNode) throws ConfigurationException {
		String foundName = null;
		BaseNode riskSourceInstanceNode = null;
		if (selectedNode instanceof FileNode) {
			RiskSourcePropertiesMap map = RiskSourcePropertiesMapFactory
					.make(selectedNode);
			String selectedNodeLabel = selectedNode.deriveNodeLabel();
			Set<String> labelKeys = map.keySet();
			for (String key : labelKeys) {
				int index = selectedNodeLabel.indexOf(key);
				if ((index != -1)
						&& (index == (selectedNodeLabel.length() - key.length()))) {
					foundName = key;
					RiskSourceProperties riskSourceProperties = map.get(key);
					riskSourceInstanceNode = riskSourceProperties.getRiskSourceNode();
					break;
				}
			}
		}
		String riskSourceInstanceLabel = ((BaseNode) riskSourceInstanceNode).toString();
		ParentNode riskSourceTypeNode = ((ChildNode) riskSourceInstanceNode).getParent();
		String riskSourceTypeLabel = ((BaseNode) riskSourceTypeNode).toString();
		riskSourceTypeLabel = riskSourceTypeLabel.substring(0,
				riskSourceTypeLabel.length() - 1);
		String[] result = new String[2];
		result[0] = riskSourceTypeLabel;
		result[1] = riskSourceInstanceLabel;
		return result;
	}

	static public String[] deriveGrandParentEntityLabelAndValue(
			BaseNode selectedNode) {
		BaseNode startNode = selectedNode;
		// The modals have dual use, for new and existing files. This corrects
		// the level.
		if (selectedNode instanceof FileNode) {
			startNode = (BaseNode) ((ChildNode) selectedNode).getParent();
		}
		ParentNode parentNode = ((ChildNode) startNode).getParent();
		String parentLabel = ((BaseNode) parentNode).toString();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		String grandParentLabel = ((BaseNode) grandParentNode).toString();
		String[] result = new String[2];
		result[0] = grandParentLabel;
		result[1] = parentLabel;
		return result;
	}

	private static ImageRegistry image_registry;

	public static URL newURL(String url_name) {
		try {
			return new URL(url_name);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL " + url_name, e);
		}
	}

	public static ImageRegistry getImageRegistry() {
		if (image_registry == null) {
			image_registry = new ImageRegistry();
			
			ImageDescriptor folderImageDesriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Util.PLUGIN_ID, "/images/tsuite.gif");
			image_registry.put("folder", folderImageDesriptor);
			ImageDescriptor fileImageDesriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Util.PLUGIN_ID, "/images/test.gif");
			image_registry.put("file", fileImageDesriptor);
			ImageDescriptor errorImageDesriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Util.PLUGIN_ID, "/images/testerror.gif");			
			image_registry.put("error", errorImageDesriptor);
		}
		return image_registry;
	}
}
