package nl.rivm.emi.dynamo.estimation;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import nl.rivm.emi.dynamo.output.DynamoOutputFactory;

/**
 * @author boshuizh
 *
 */
public class  OutputWritingRunnable implements Runnable {
  String fileName=null;
  DynamoOutputFactory output=null;
	/** write the object to a file "fileName" after calling the run method
	 * in a separate thread
	 * @param fileName
	 * @param outputObject
	 */
	public OutputWritingRunnable(String fileName, DynamoOutputFactory outputObject) {
		
		this.fileName=fileName;
		this.output=outputObject;
	}
	
	/**write the object in a separate thread
	 */
	public void run() {

		
		ObjectOutputStream out;
		try {
		out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(this.fileName)));
		
		// out = new ObjectOutputStream(
		// new FileOutputStream(resultFile));
		out.writeObject(output);

		out.flush();
		out.close();;
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		//this.displayErrorMessage(e, fileName);
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//this.displayErrorMessage(e, fileName);
		e.printStackTrace();
	}

}}
