package nl.rivm.emi.cdm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream {
	private StringReader stringReader;

	/**
	 * The String wrapped by the reader.
	 */
	private String string;

	@SuppressWarnings("unused")
	private int position = -1;

	public StringInputStream(String string) {
		this.string = string;
		this.position = 0;
		this.stringReader = new StringReader(string);
	}

	public void close() {
		stringReader.close();
	}

	public void mark(int readLimit) {
		super.mark(readLimit);
	}

	/**
	 * The StringReader supports marking, but the IOException makes overriding
	 * tricky, so we don't.
	 */
	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		return stringReader.read();
	}

	public int read(byte[] buffer) throws IOException {
		throw new IOException("This read is not supported");
	}

	public int read(byte[] buffer, int offset, int length) throws IOException {
		super.read(buffer, offset, length);
		// Super has approved the parameters.
		byte[] stringContents = string.getBytes();
		int contentLength = stringContents.length;
		int numBytes2Read = Math.min(contentLength, length);
		for(int count = 0; count < numBytes2Read; count++){
			buffer[offset+count] = stringContents[count];
		}
		return numBytes2Read;
	}

	public void reset() throws IOException {
		stringReader.reset();
	}

	public long skip(long skippy) throws IOException {
		return stringReader.skip(skippy);
	}
}
