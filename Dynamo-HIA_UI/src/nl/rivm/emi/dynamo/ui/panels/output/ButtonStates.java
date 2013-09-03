/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

/**
 * @author boshuizh
 *
 */
public class ButtonStates {
	int genderChoice;
	int currentScen;
	int currentDisease;
	 int currentYear;
	 int currentAge;
	 int plotType;
	 int riskClassChoice;
	 boolean differencePlot;
	 boolean axisIsAge;
	 boolean numbers;
	 boolean survival;
	 boolean population;
	 boolean Sullivan;
	 boolean oldSullivan=false;
	// String [] availlableAges;
	 int maxAge;
	 int minAge;
	 boolean newborns;
	 boolean blackAndWhite;
	int cumulative;
	/*
	 * cumulative is 0 for no cumulative data, 1 for cumulative, and 2 for single age
	 */
	 public ButtonStates(){}
}
