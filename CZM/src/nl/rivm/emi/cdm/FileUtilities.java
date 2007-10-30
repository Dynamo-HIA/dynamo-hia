package nl.rivm.emi.cdm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;

public class FileUtilities {

		/**
		 * @param args
		 */
		public static void main(String[] args) {
				if (args.length != 1) {
					System.err.println("Invalid number of arguments: "
							+ args.length);
				} else {
						// YAGNI ArrayList<Integer> resultAR =
						// findDivIndexes(fileContent);
						int startIndex = findContentStart(fileContent);
						int endIndex = findContentEnd(fileContent, startIndex);
						String interestingContent = fileContent.substring(
								startIndex, endIndex);
						interestingContent = processTerug(interestingContent);
						interestingContent = processContent(interestingContent);
						interestingContent = processASPXPopups(interestingContent);
						interestingContent = processJavaScriptPopups(interestingContent);
						interestingContent = processNaarBoven(interestingContent);
						interestingContent = processContentSomeMore(interestingContent);
						System.out.println("Result>>>\n" + interestingContent
								+ "\n<<<");
						String outputFileName = args[0].replace(".html", ".frag");
						File outputFile = new File(outputFileName);
						writeStringIntoFile(outputFile, interestingContent);
						System.out.println("Result>>>\n" + interestingContent
								+ "\n<<<");
					}
				}
				System.out.println("Processing finished.");
		}

		public String readFileContentIntoString(String fileName)
				throws FileNotFoundException, IOException {
			StringBuffer resultSB = new StringBuffer();
			try {
			File file = new File(fileName);
			if (file != null) {
			InputStream is = new FileInputStream(file);
			InputStreamReader iStreamReader = new InputStreamReader(is);
			CharBuffer charBuffer = CharBuffer.allocate(4096);
			int readCount = 0;
			while ((readCount = iStreamReader.read(charBuffer)) > 0) {
				resultSB.append(charBuffer.array(), 0, readCount);
				charBuffer.clear();
			}
			is.close();
			}
			}catch (Exception e) {
				System.out.println("Processing aborted.");
				System.out.println("Processing aborted.");

				e.printStackTrace();
			}
			return resultSB.toString();
		}
}
