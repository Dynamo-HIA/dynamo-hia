package nl.rivm.emi.dynamo.ui.treecontrol;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

public class Util {
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
			image_registry.put("folder", ImageDescriptor
					.createFromURL(newURL("file:images/tsuite.gif")));
			image_registry.put("file", ImageDescriptor
					.createFromURL(newURL("file:images/test.gif")));
			image_registry.put("error", ImageDescriptor
					.createFromURL(newURL("file:images/testerror.gif")));
		}
		return image_registry;
	}
}
