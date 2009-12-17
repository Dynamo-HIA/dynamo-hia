package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesSingleton;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FileTreeLabelProvider extends LabelProvider {
	Log log = LogFactory.getLog(this.getClass().getName());

	Pattern matchPattern = Pattern.compile("^.*\\.xml$");

	public String getText(Object element) {
		return ((BaseNode) element).toString();
	}

	@SuppressWarnings("finally")
	public Image getImage(Object element) {
		Image image = null;
		try {
			if (element instanceof DirectoryNode) {
				image = Util.getImageRegistry()
						.get(Util.imageRegistryFolderKey);
			} else {
				if (element instanceof FileNode) {
					FileNode theNode = (FileNode) element;
					File physicalStorage = theNode.physicalStorage;
					String fileName = physicalStorage.getName();
					if (hasXMLExtension(fileName)) {
						String rootElementName;
						rootElementName = ConfigurationFileUtil
								.justExtractRootElementName(physicalStorage);
						RootElementNamesSingleton singleton = RootElementNamesSingleton
								.getInstance();
						RootElementNamesEnum renEnum = singleton
								.get(rootElementName.toLowerCase());
						if (renEnum != null) {
							boolean locationOK = renEnum.isLocationOK(theNode);
							log.debug("RootElementName: " + rootElementName
									+ " location OK: " + locationOK);
							if (locationOK) {
								image = handleRightRootRightPlace(physicalStorage);
							} else {
								image = Util
										.getImageRegistry()
										.get(
												Util.imageRegistrySupportedXMLFileWrongPlaceKey);
							}
						} else {
							image = Util.getImageRegistry().get(
									Util.imageRegistryUnsupportedXMLFileKey);
						}
					} else {
						image = Util.getImageRegistry().get(
								Util.imageRegistryFileKey);
					}
				} else {
					image = Util.getImageRegistry().get(
							Util.imageRegistryErrorKey);
				}
			}
		} catch (DynamoConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (image == null) {
				image = Util.getImageRegistry().get(Util.imageRegistryErrorKey);
			}
			return image;
		}
	}

	private Image handleRightRootRightPlace(File physicalStorage)
			throws DynamoConfigurationException {
		Image image;
		try {
			ConfigurationFileUtil
					.extractRootElementNameIncludingSchemaCheck(physicalStorage);
			image = Util.getImageRegistry().get(
					Util.imageRegistrySupportedXMLFileRightPlaceKey);
			return image;
		} catch (ConfigurationException e) {
			log.warn("Schema check failed, exception: " + e.getClass().getSimpleName()
					+ " thrown, cause: " + e.getCause() + " message: "
					+ e.getMessage());
			image = Util.getImageRegistry().get(
					Util.imageRegistrySupportedXMLFileWrongPlaceKey);
			return image;
		}
	}

	public boolean hasXMLExtension(String fileName) {
		Matcher numericalMatcher = matchPattern.matcher(fileName);
		boolean match = numericalMatcher.matches();
		return match;
	}

}
