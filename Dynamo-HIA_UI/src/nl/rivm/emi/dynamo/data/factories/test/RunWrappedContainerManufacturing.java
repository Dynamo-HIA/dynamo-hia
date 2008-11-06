package nl.rivm.emi.dynamo.data.factories.test;

import java.io.File;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.notinuse.IncidenceIntegerFactory;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.SingleAgeGenderComposite;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class RunWrappedContainerManufacturing implements Runnable {
	Log log = LogFactory.getLog(this.getClass().getName());

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "incidence1.xml";
		File configurationFile = new File(configurationFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			AgeMap<SexMap<IObservable>> testContainer = IncidenceIntegerFactory
					.manufactureFromFlatXML(configurationFile);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
