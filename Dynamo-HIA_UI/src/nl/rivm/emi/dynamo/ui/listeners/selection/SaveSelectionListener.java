package nl.rivm.emi.dynamo.ui.listeners.selection;

//TODO(mondeelr) Hacked to ErrorLessNess.
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

public class SaveSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
	DataAndFileContainer modalParent;

	public SaveSelectionListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		String filePath = modalParent.getConfigurationFilePath();
		File configurationFile = new File(filePath);
		try {
			Object modelObject = modalParent.getData();

			String rootElementName = (String) modalParent.getRootElementName();
			log.debug("rootElementName" + rootElementName + " modelObject"
					+ modelObject + " configurationFile" + configurationFile);
			if (modelObject instanceof TypedHashMap) {
				FileControlEnum fileControl = FileControlSingleton
						.getInstance().get(rootElementName);
				StAXAgnosticTypedHashMapWriter.produceFile(fileControl,
						(TypedHashMap) modelObject, configurationFile);
			} else {
				if (modelObject instanceof LinkedHashMap) {

					/**
					 * TODO REMOVE: LOGGING BELOW
					 */
					/*
					 * Map map = ((DynamoSimulationObject)
					 * modelObject).getDiseaseConfigurations(); Set<String> keys
					 * = map.keySet(); for (String key : keys) {
					 * ITabDiseaseConfiguration conf =
					 * (ITabDiseaseConfiguration) map.get(key);
					 * log.error("conf.getName()" + conf.getName());
					 * log.error("conf.getPrevalenceFileName()" +
					 * conf.getPrevalenceFileName());
					 * log.error("conf.getIncidenceFileName()" +
					 * conf.getIncidenceFileName());
					 * log.error("conf.getExcessMortalityFileName()" +
					 * conf.getExcessMortalityFileName());
					 * log.error("conf.getDalyWeightsFileName()" +
					 * conf.getDalyWeightsFileName()); }
					 */

					if (modelObject instanceof DynamoSimulationObject) {
						Map map = ((DynamoSimulationObject) modelObject)
								.getRelativeRiskConfigurations();
						Set<Integer> keys = map.keySet();
						for (Integer key : keys) {
							TabRelativeRiskConfigurationData conf = (TabRelativeRiskConfigurationData) map
									.get(key);
							log
									.error("conf.getName()"
											+ conf.getDataFileName());
							if (conf.getDataFileName() == null
									|| conf.getDataFileName().isEmpty()) {
								throw new DynamoConfigurationException(
										"The Relative Risk field"
												+ "is empty of Relative Risks is empty");
							}
						}
					}
					/**
					 * TODO REMOVE: LOGGING ABOVE
					 */

					StAXAgnosticGroupWriter.produceFile(rootElementName,
							(HashMap<String, Object>) modelObject,
							configurationFile);
				} else {
					throw new DynamoConfigurationException(
							"SaveSelectionListener - Unsupported modelObjectType: "
									+ modelObject.getClass().getName());
				}
			}
		} catch (XMLStreamException e) {
			this.handleErrorMessage(e);
		} catch (UnexpectedFileStructureException e) {
			this.handleErrorMessage(e);
		} catch (IOException e) {
			this.handleErrorMessage(e);
		} catch (DynamoOutputException e) {
			this.handleErrorMessage(e);
		} catch (DynamoConfigurationException e) {
			this.handleErrorMessage(e);
		}
	}

	private void handleErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(this.modalParent.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during save " + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

}
