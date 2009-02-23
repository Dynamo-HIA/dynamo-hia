package nl.rivm.emi.dynamo.estimation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.rules.update.base.ManyToManyUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRules4Simulation;
import nl.rivm.emi.cdm.simulation.RunModes;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.stax.StAXEntryPoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class DynamoSimulation extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	private Simulation simulation;

	public DynamoSimulation() {
		super();
	}

	public DynamoSimulation(Simulation simulation) {
		super();
		this.simulation = simulation;
	}

	/*
	 * public static void runDynamo () { Display display = new Display (); Shell
	 * shell = new Shell (display); ProgressBar bar = new ProgressBar (shell,
	 * SWT.SMOOTH); bar.setBounds (10, 10, 200, 32); shell.open (); for (int
	 * i=0; i<=bar.getMaximum (); i++) { try {Thread.sleep (100);} catch
	 * (Throwable th) {} bar.setSelection (i); } while (!shell.isDisposed ()) {
	 * if (!display.readAndDispatch ()) display.sleep (); } display.dispose ();
	 * }
	 */

	public void runDynamo(int scennum) throws Exception {

		Population population = simulation.getPopulation();
		int stepsInRun = 105;
	

		int size = population.size();

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Simulation of scenario " + scennum + " running ....");
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		ProgressBar bar = new ProgressBar(shell, SWT.NULL);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);

		shell.open();

		int step = (int) Math.floor(size / 50);
		if (step < 1)
			step = 1;
		int currentIndividual = 0;
		int currentProgressIndicator = 0;
		bar.setMaximum(size / step);
		Iterator<Individual> individualIterator = population.iterator();
		while (individualIterator.hasNext()) {
			currentIndividual++;
			Individual individual = individualIterator.next();
			log.debug("Longitudinal: Processing individual "
					+ individual.getLabel());
			for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
				/* check if the simulation for this person can be ended */	
				CharacteristicValueBase charValBase =	individual.get(1);
				
					if (charValBase instanceof IntCharacteristicValue) {
						if (((IntCharacteristicValue) charValBase).isFull()) break; 
					}
						
					 else {
						if (charValBase instanceof FloatCharacteristicValue) 
							if (((FloatCharacteristicValue) charValBase).isFull()) break;
					 }
                 /* if not, simulate */
								
				simulation.processCharVals(individual);
				
			}
			if (currentIndividual > step * currentProgressIndicator) {
				bar.setSelection(currentProgressIndicator);
				currentProgressIndicator++;
			}
		}
		/*
		 * while (!shell.isDisposed ()) { if (!display.readAndDispatch ())
		 * display.sleep (); }
		 */
		shell.close();
		display.dispose();
	}
}
