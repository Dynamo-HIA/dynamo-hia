package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesSingleton;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FileTreeLabelProvider extends LabelProvider {

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
								.extractRootElementName(physicalStorage);
						RootElementNamesSingleton singleton = RootElementNamesSingleton.getInstance();
						RootElementNamesEnum renEnum = singleton.get(rootElementName);
						if (renEnum != null) {
							boolean locationOK = renEnum.isLocationOK(theNode);
							if(locationOK){
							image = Util
									.getImageRegistry()
									.get(
											Util.imageRegistrySupportedXMLFileRightPlaceKey);
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

	public boolean hasXMLExtension(String fileName) {
		Matcher numericalMatcher = matchPattern.matcher(fileName);
		boolean match = numericalMatcher.matches();
		return match;
	}

}
