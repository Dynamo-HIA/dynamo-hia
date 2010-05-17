package nl.rivm.emi.cdm.log4j.appender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

public class AntBuildListener4Log4J implements BuildListener {
	Log log = LogFactory.getLog(getClass().getSimpleName());

	public void buildFinished(BuildEvent event) {
		log.info("Build \"" + event.getProject() + "\" finished.");
	}

	public void buildStarted(BuildEvent event) {
		log.info("Build started.");
	}

	public void messageLogged(BuildEvent event) {
		log.info("Message \"" + event.getMessage() + "\" logged.");
	}

	public void targetFinished(BuildEvent event) {
		log.info("Target \"" + event.getTarget() + "\" finished.");
	}

	public void targetStarted(BuildEvent event) {
		log.info("Target \"" + event.getTarget() + "\" started.");
	}

	public void taskFinished(BuildEvent event) {
		log.info("Task \"" + event.getTask() + "\" finished.");
	}

	public void taskStarted(BuildEvent event) {
		log.info("Task \"" + event.getTask() + "\" started.");
	}

}
