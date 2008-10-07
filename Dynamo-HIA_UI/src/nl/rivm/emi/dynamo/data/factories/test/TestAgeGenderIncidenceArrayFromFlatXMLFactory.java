package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.IncidenceIntegerFactory;
import nl.rivm.emi.dynamo.data.factories.FourDimFloatArrayFromFlatXMLFactory;
import nl.rivm.emi.dynamo.data.transition.DestinationsByOriginMap;
import nl.rivm.emi.dynamo.data.transition.ValueByDestinationMap;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.AgeGenderDatabindingPrototype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgeGenderIncidenceArrayFromFlatXMLFactory {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	Composite container = null;
	boolean killScreen = false;

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturing() {
		RunWrappedContainerManufacturing conManRunner = new RunWrappedContainerManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				conManRunner);
	}

	@Test
	public void testArrayManufacturing() {
		RunWrappedArrayManufacturing arrManRunner = new RunWrappedArrayManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				arrManRunner);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				TestAgeGenderIncidenceArrayFromFlatXMLFactory.class);
	}
}
