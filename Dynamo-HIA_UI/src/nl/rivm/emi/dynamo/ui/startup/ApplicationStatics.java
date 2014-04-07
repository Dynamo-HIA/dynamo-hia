package nl.rivm.emi.dynamo.ui.startup;

import java.io.File;

import javax.swing.JFileChooser;

public class ApplicationStatics {
	public static final String APPBASENAME = "DYNAMO-HIA";

	public static final String RELEASE_TAG = "Version 2.0.3, Release date: 20140408, build 1240";
	/*
	 * 
	 * NB the following statement is not platform independent, so needs changing in case of linux versions
	 * 
	 */
//	public static final String DEFAULTWORKDATADIRECTORY = "%CSIDL_PERSONAL%"+File.separator+"DYNAMO-HIA"
	       // +File.separator+"Country_data"+File.separator+"Netherlands";
	/*
	 * 
	 * This should be platform independent
	 * 
	 */
	public static final String DEFAULTWORKDATADIRECTORY = "Tutorial_Data";
	// volgende stond er in 2013 maar is waarschijnlijk verkeerd overgenomen toen daaronderstaande verandering weer weg is gehaald
	//public static final String DEFAULTWORKDATADIRECTORY = "C:"+File.separator+"DYNAMO-HIA"+File.separator+"Netherlands";
/* volgende is poging om my documents/dynamo-hia als default te zetten maar werkt niet
	public static final String DEFAULTWORKDATADIRECTORY = new JFileChooser().getFileSystemView().getDefaultDirectory()+File.separator+"DYNAMO-HIA"
    +File.separator+"Country_data"; */
}