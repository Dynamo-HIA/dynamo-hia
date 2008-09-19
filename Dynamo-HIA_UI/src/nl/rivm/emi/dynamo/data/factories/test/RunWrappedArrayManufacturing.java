package nl.rivm.emi.dynamo.data.factories.test;

import java.io.File;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.AgeGenderIncidenceArrayFromFlatXMLFactory;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.SingleAgeGenderComposite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class RunWrappedArrayManufacturing implements Runnable {
	Log log = LogFactory.getLog(this.getClass().getName());

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "incidence1.xml";
		File configurationFile = new File(configurationFilePath);
		log.debug(configurationFile.getAbsolutePath());
		int[][] theArray = AgeGenderIncidenceArrayFromFlatXMLFactory
				.manufactureArray(configurationFile);
	}

}
