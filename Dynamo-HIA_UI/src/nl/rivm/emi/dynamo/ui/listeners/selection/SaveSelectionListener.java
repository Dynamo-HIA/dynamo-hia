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
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.panels.NewbornsDialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

			// Modalparent dependent functionality added.
			boolean doSave = true;
			SideEffectProcessor preProcessorSelectionListener = modalParent
					.getSavePreProcessor();
			SideEffectProcessor postProcessorSelectionListener = modalParent
					.getSavePostProcessor();
			if (preProcessorSelectionListener != null) {
				doSave = preProcessorSelectionListener.doIt();
			}
			// Allow vetoing by preprocessor.
			if (doSave) {
				String rootElementName = (String) modalParent
						.getRootElementName();
				log.debug("rootElementName" + rootElementName + " modelObject"
						+ modelObject + " configurationFile"
						+ configurationFile);
				if (modelObject instanceof TypedHashMap) {
					FileControlEnum fileControl = FileControlSingleton
							.getInstance().get(rootElementName);
					StAXAgnosticTypedHashMapWriter.produceFile(fileControl,
							(TypedHashMap) modelObject, configurationFile);
					if (postProcessorSelectionListener != null) {
						doSave = postProcessorSelectionListener.doIt();
					}
				} else {
					if (modelObject instanceof LinkedHashMap) {
						if (modelObject instanceof DynamoSimulationObject) {
							Map map = ((DynamoSimulationObject) modelObject)
									.getRelativeRiskConfigurations();
							Set<Integer> keys = map.keySet();
							for (Integer key : keys) {
								TabRelativeRiskConfigurationData conf = (TabRelativeRiskConfigurationData) map
										.get(key);
								log.error("conf.getName()"
										+ conf.getDataFileName());
								if (conf.getDataFileName() == null
										|| conf.getDataFileName().isEmpty()) {
									throw new DynamoConfigurationException(
											"The Relative Risk field"
													+ "is empty of Relative Risks is empty");
								}

								Map secondMap = ((DynamoSimulationObject) modelObject)
										.getRelativeRiskConfigurations();
								Set<Integer> secondKeys = secondMap.keySet();
								for (Integer secondKey : secondKeys) {
									TabRelativeRiskConfigurationData secondConf = (TabRelativeRiskConfigurationData) secondMap
											.get(secondKey);

									if (secondConf.getFrom().equals(
											conf.getFrom())
											&& secondConf.getTo().equals(
													conf.getTo())
											&& !secondConf.getIndex().equals(
													conf.getIndex())) {
										throw new DynamoConfigurationException(
												"Two Relative Risks cannot contain the same to and from values");
									}
								}
							}
						}
						StAXAgnosticGroupWriter.produceFile(rootElementName,
								(HashMap<String, Object>) modelObject,
								configurationFile);
						if (postProcessorSelectionListener != null) {
							doSave = postProcessorSelectionListener.doIt();
						}

						if (modelObject instanceof DynamoSimulationObject) {
							((SimulationModal) modalParent).getRunButtonGroup().runButton
									.setVisible(true);
						}

					} else {
						throw new DynamoConfigurationException(
								"SaveSelectionListener - Unsupported modelObjectType: "
										+ modelObject.getClass().getName());
					}
				}
			} else {
				MessageBox box = new MessageBox(this.modalParent.getShell(),
						SWT.ERROR_UNSPECIFIED);
				box
						.setText("The preprocessor had a problem, saving didn't happen.");
				box
						.setMessage("The preprocessor had a problem, saving didn't happen.");
				box.open();

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
