/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.rivm.emi.cdm.util.log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;

/**
 * ConsoleAppender appends log events to <code>System.out</code> or
 * <code>System.err</code> using a layout specified by the user. The default
 * target is <code>System.out</code>.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 * @since 1.1
 */
public class SwingAppender extends WriterAppender {
	Log log = LogFactory.getLog(this.getClass().getSimpleName());
	/**
	 * The ConsoleAppender this Appender is cloned from used a Target. This one
	 * doesn't, but it is kept (for now) to avoid the risk of log4j
	 * configuration problems.
	 */
	String bogusTarget = null;

	/**
	 * Determines if the appender honors reassignments of System.out or
	 * System.err made after configuration.
	 */
	private boolean follow = false;

	private SwingLogOutputStream logStream = null;

	/**
	 * Constructs an unconfigured appender.
	 */
	public SwingAppender() {
	}

	/**
	 * Creates a configured appender.
	 * 
	 * @param layout
	 *            layout, may not be null.
	 */
	public SwingAppender(Layout layout) {
		this(layout, "bogus");

	}

	/**
	 * Creates a configured appender.
	 * 
	 * @param layout
	 *            layout, may not be null.
	 * @param target
	 *            target, either "System.err" or "System.out".
	 */
	public SwingAppender(Layout layout, String target) {
		setLayout(layout);
		setTarget(target);
		activateOptions();
	}

	/**
	 * Sets the value of the <b>Target</b> option. Recognized values are
	 * "System.out" and "System.err". Any other value will be ignored.
	 * */
	public void setTarget(String value) {
		bogusTarget = value;
	}

	/**
	 * Returns the current value of the <b>Target</b> property. The default
	 * value of the option is "System.out".
	 * 
	 * See also {@link #setTarget}.
	 * */
	public String getTarget() {
		return bogusTarget;
	}

	/**
	 * Sets whether the appender honors reassignments of System.out or
	 * System.err made after configuration.
	 * 
	 * @param newValue
	 *            if true, appender will use value of System.out or System.err
	 *            in force at the time when logging events are appended.
	 * @since 1.2.13
	 */
	public final void setFollow(final boolean newValue) {
		follow = newValue;
	}

	/**
	 * Gets whether the appender honors reassignments of System.out or
	 * System.err made after configuration.
	 * 
	 * @return true if appender will use value of System.out or System.err in
	 *         force at the time when logging events are appended.
	 * @since 1.2.13
	 */
	public final boolean getFollow() {
		return follow;
	}

	void targetWarn(String val) {
		LogLog.warn("[" + val + "] should be System.out or System.err.");
		LogLog.warn("Using previously set target, System.out by default.");
	}

	/**
	 * Prepares the appender for use.
	 */
	public void activateOptions() {
		logStream = new SwingLogOutputStream();
		setWriter(createWriter(logStream));
		Swing2LogConnectorSingleton connector = Swing2LogConnectorSingleton
				.getInstance();
		connector.setTheStream(logStream);
		super.activateOptions();
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void closeWriter() {
		if (follow) {
			super.closeWriter();
		}
	}

	/**
	 * An implementation of OutputStream that redirects to the current
	 * System.err.
	 * 
	 */
	static public class SwingLogOutputStream extends OutputStream {
		Log streamLog = LogFactory.getLog(getClass().getSimpleName());

		Queue<String> logQueue;

		public SwingLogOutputStream() {
			logQueue = new ConcurrentLinkedQueue<String>();
		}

		public void close() {
		}

		public void flush() {
		}

		public void write(final byte[] b) throws IOException {
			String string = new String(b);
			logQueue.add(string);
			streamLog.debug("Array: Added \""
					+ string.substring(0, string.length() - 2)
					+ "\" to queue, length: " + logQueue.size() + ".");
		}

		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			byte[] copyOfB = new byte[len];
			for (int count = 0; count < len; count++) {
				copyOfB[count] = b[off + count];
			}
			String string = new String(copyOfB);
			logQueue.add(string);
			streamLog.debug("Array(o,l): Added \""
					+ string.substring(0, string.length() - 2)
					+ "\" to queue, length: " + logQueue.size() + ".");
		}

		public void write(final int b) throws IOException {
			String string = Integer.toString(b);
			logQueue.add(string);
			streamLog.debug("Int: Added \"" + string + "\" to queue, length: "
					+ logQueue.size() + ".");
		}

		public String read() {
			String string = logQueue.poll();
			if (string != null) {
				streamLog.debug("Getting \"" + string
						+ "\" from queue, length: " + logQueue.size() + ".");
			}
			return string;
		}
	}
}
