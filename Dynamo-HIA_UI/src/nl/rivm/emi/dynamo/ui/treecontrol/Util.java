package nl.rivm.emi.dynamo.ui.treecontrol;

import java.net.MalformedURLException;
import java.net.URL;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.main.DynamoPlugin;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jfree.util.Log;
//import org.jfree.util.Log;

public class Util {

	private static final String RISK_FACTOR = "Risk_Factor";
	private static final String TRANSITION = "Transition";

	static final public String imageRegistryFolderKey = "folder";
	static final public String imageRegistryFileKey = "file";
	static final public String imageRegistryUnsupportedXMLFileKey = "unsupportedXMLfile";
	static final public String imageRegistrySupportedXMLFileRightPlaceKey = "supportedfile_rightplace";
	static final public String imageRegistrySupportedXMLFileWrongPlaceKey = "supportedfile_wrongplace";
	static final public String imageRegistryErrorKey = "error";

	static public String[] deriveEntityLabelAndValueFromRiskSourceNode(
			BaseNode selectedNode) throws DynamoConfigurationException {
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

	/**
	 * New functionality that is needed to handle the fact that the
	 * configuration-files for RiskFactors have been moved down a level. They
	 * now reside in a directory, so multiple files are possible instead of the
	 * single instance that was foreseen before.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws DynamoConfigurationException
	 */
	static public String[] deriveEntityLabelAndValueFromRiskSourceNode_Directories(
			BaseNode selectedNode) throws DynamoConfigurationException {
		BaseNode startNode = selectedNode;
		if (selectedNode instanceof FileNode) {
			startNode = (BaseNode) ((ChildNode) selectedNode).getParent();
		}
		// Extra step.
		startNode = (BaseNode) ((ChildNode) startNode).getParent();
		// Extra step ends.
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

	/**
	 * 
	 * Retrieves the Risk Factor name (grand parent) and Transition name
	 * (parent) of the selected node
	 * 
	 * The selected node has to be a Transition (i.e. Drift, Matrix, Drift
	 * Netto)
	 * 
	 * @param selectedNode
	 * @return String []
	 * @throws DynamoConfigurationException
	 */
	static public String[] deriveEntityLabelAndValueFromTransitionSourceNode(
			BaseNode selectedNode) throws DynamoConfigurationException {
		BaseNode startNode = selectedNode;
		if (selectedNode instanceof FileNode) {
			startNode = (BaseNode) ((ChildNode) selectedNode).getParent();
		}
		String transitionLabel = ((BaseNode) startNode).toString();
		ParentNode containerNode = ((ChildNode) startNode).getParent();
		String riskFactorLabel = ((BaseNode) containerNode).toString();

		String[] result = new String[4];
		result[0] = RISK_FACTOR;
		result[1] = riskFactorLabel;
		result[2] = TRANSITION;
		result[3] = transitionLabel;
		return result;
	}

	static public String[] deriveRiskSourceTypeAndLabelFromSelectedNode(
			BaseNode selectedNode) throws ConfigurationException,
			DynamoInconsistentDataException {
		Log.debug("selectedNode" + selectedNode.deriveNodeLabel());
//		String foundName = null;
//		BaseNode riskSourceInstanceNode = null;
		if (selectedNode instanceof FileNode) {
			/*
			 * RiskSourcePropertiesMap map = RiskSourcePropertiesMapFactory
			 * 
			 * .makeMap4OneRiskSourceType(selectedNode); String
			 * selectedNodeLabel = selectedNode.deriveNodeLabel(); Set<String>
			 * labelKeys = map.keySet(); for (String key : labelKeys) { int
			 * index = selectedNodeLabel.indexOf(key); if ((index != -1) &&
			 * (index == (selectedNodeLabel.length() - key.length()))) {
			 * foundName = key; RiskSourceProperties riskSourceProperties =
			 * map.get(key); riskSourceInstanceNode = riskSourceProperties
			 * .getRiskSourceNode(); break; } } } if (riskSourceInstanceNode !=
			 * null) { String riskSourceInstanceLabel = ((BaseNode)
			 * riskSourceInstanceNode) .toString(); ParentNode
			 * riskSourceTypeNode = ((ChildNode) riskSourceInstanceNode)
			 * .getParent(); String riskSourceTypeLabel = ((BaseNode)
			 * riskSourceTypeNode) .toString(); riskSourceTypeLabel =
			 * riskSourceTypeLabel.substring(0, riskSourceTypeLabel.length() -
			 * 1);
			 */
			RiskSourceProperties props = RiskSourcePropertiesMapFactory
					.getProperties((FileNode) selectedNode);
			String riskSourceTypeLabel = props.getRiskSourceLabel().replace('_', ' ');
			String riskSourceInstanceLabel = props.getRiskSourceName();

			
			String[] result = new String[2];
			result[0] = riskSourceTypeLabel;
			result[1] = riskSourceInstanceLabel;
			return result;
		} else {
			throw new DynamoInconsistentDataException(
					"RiskSource not found for filename: \""
							+ selectedNode.deriveNodeLabel() + "\"");
		}
	}

	/**
	 * Shortended the result at index 0 by one to remove the "s".
	 * 
	 * @param selectedNode
	 * @return
	 */
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
		String inBetween = ((BaseNode) grandParentNode).deriveNodeLabel();
		String grandParentLabel = inBetween
				.substring(0, inBetween.length() - 1);
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

	/**
	 * 
	 * Returns the imageRegistry of this plugin
	 * 
	 * @return ImageRegistry
	 */
	public static ImageRegistry getImageRegistry() {
		if (image_registry == null) {
			image_registry = new ImageRegistry();

			if (ResourcesPlugin.getPlugin() != null) {
				// Test if the application is a (standalone) plug-in, if so load
				// images by AbstractUIPlugin
				// folder
				ImageDescriptor folderImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/tsuite.gif");
				image_registry
						.put(imageRegistryFolderKey, folderImageDesriptor);
				// Just a non-XML file.
				ImageDescriptor fileImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/test.gif");
				image_registry.put(imageRegistryFileKey, fileImageDesriptor);
				// An unsupported xml-file.
				ImageDescriptor unsupportedXMLFileImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/testfail.gif");
				image_registry.put(imageRegistryUnsupportedXMLFileKey,
						unsupportedXMLFileImageDesriptor);
				// Supported xml-file at the right place.
				ImageDescriptor supportedFileRightPlaceImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/testok.gif");
				image_registry.put(imageRegistrySupportedXMLFileRightPlaceKey,
						supportedFileRightPlaceImageDesriptor);
				// Supported xml-file at the wrong place.
				ImageDescriptor supportedFileWrongPlaceImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/testerr.gif");
				image_registry.put(imageRegistrySupportedXMLFileWrongPlaceKey,
						supportedFileWrongPlaceImageDesriptor);
				// Error
				ImageDescriptor errorImageDesriptor = AbstractUIPlugin
						.imageDescriptorFromPlugin(DynamoPlugin.PLUGIN_ID,
								"/images/tsuiteerror.gif");
				image_registry.put("error", errorImageDesriptor);
			} else {
				// The application is not a standalone plugin;
				// The images are loaded by URL
				image_registry = new ImageRegistry();
				image_registry.put(imageRegistryFolderKey, ImageDescriptor
						.createFromURL(newURL("file:images/tsuite.gif")));
				image_registry.put(imageRegistryFileKey, ImageDescriptor
						.createFromURL(newURL("file:images/test.gif")));
				image_registry
						.put(
								imageRegistryUnsupportedXMLFileKey,
								ImageDescriptor
										.createFromURL(newURL("file:images/testfail.gif")));
				image_registry
						.put(
								imageRegistrySupportedXMLFileRightPlaceKey,
								ImageDescriptor
										.createFromURL(newURL("file:images/testok.gif")));
				image_registry
						.put(
								imageRegistrySupportedXMLFileWrongPlaceKey,
								ImageDescriptor
										.createFromURL(newURL("file:images/testerr.gif")));
				image_registry.put("error", ImageDescriptor
						.createFromURL(newURL("file:images/tsuiteerror.gif")));
			}
		}
		return image_registry;
	}
}
