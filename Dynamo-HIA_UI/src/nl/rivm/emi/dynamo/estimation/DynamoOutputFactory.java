/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.cdm.characteristic.types.CompoundCharacteristicType;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import java.awt.Font;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jfree.chart.ChartPanel; 

public class DynamoOutputFactory {

	double pPop[][][]; /* percentage of survivors by scenario, time and sex */
	double pPopByAge[][][][];/*
							 * percentage of survivors by scenario, age, time, and
							 * sex
							 */
	double pRiskClass[][][][]; /*
								 * prevalence of each risk class by
								 * scenario,risk class time and sex
								 */
	double pDisease[][][][];/*prevalence of disease ; indexes are
							 * scenario,disease, time and
							 * sex
							 */
	double pPopPerRiskClass[][][][]; /*
									 * percentage of survivors by scenario, time,
									 * risk class and sex
									 */
	double pDiseasePerRiskClass[][][][][];/*
										 * prevalence of disease ; indexes are
										 * scenario,disease, time, risk class
										 * and sex
										 */
	double pDiseasePerRiskClassByAge[][][][][][];/*
												 * prevalence of disease ; indexes are
												 * scenario,disease, risk class
												 * age, time, and sex
												 *  */
	double [][][][][]	pRiskClassByAge	;		/*
	 * prevalence of risk class by Age; indexes are:
	 * scenario, risk class
	 * age, time, and sex
	 *  */							

	DiseaseClusterStructure[] structure;
	int riskType;
	int nScen;
	int stepsInRun;
	int nDiseases;
	int nRiskFactorClasses;
	int nDiseaseStates;
	

	public DynamoOutputFactory(int nScen, int riskType, int nRiskFactorClasses,
			int stepsInRun, DiseaseClusterStructure[] structure) {
		super();
		setRiskType(riskType);
		setNScen(nScen);
		setStepsInRun(stepsInRun);
		setStructure(structure);
		setNDiseases(structure);
		setNDiseaseStates(structure);
		if (getRiskType() == 1 || getRiskType() == 3)
			setNRiskFactorClasses(nRiskFactorClasses);

		pPop = new double[nScen + 1][stepsInRun][2];
		pPopByAge = new double[nScen + 1][96 + stepsInRun][stepsInRun][2];
		pRiskClass = new double[nScen + 1][getNRiskFactorClasses()][stepsInRun][2];
		pRiskClassByAge = new double[nScen + 1][getNRiskFactorClasses()][96 + stepsInRun][stepsInRun][2];
		pDisease = new double[nScen + 1][nDiseases][stepsInRun][2];
		pDiseasePerRiskClassByAge = new double[nScen + 1][nDiseases][getNRiskFactorClasses()][96 + stepsInRun][stepsInRun][2];
		pDiseasePerRiskClass = new double[nScen + 1][nDiseases][getNRiskFactorClasses()][stepsInRun][2];
		for (int i = 0; i <= nScen; i++)
			for (int sex = 0; sex < 2; sex++)
				for (int steps = 0; steps < stepsInRun; steps++) {
					pPop[i][steps][sex] = 0;

					for (int age = 0; age < stepsInRun + 96; age++) {
						pPopByAge[i][age][steps][sex] = 0;
					}
					for (int nDis = 0; nDis < nDiseases; nDis++) {
						pDisease[i][nDis][steps][sex] = 0;
					}
					for (int r = 0; r < nRiskFactorClasses; r++) {
						pRiskClass[i][r][steps][sex] = 0;
						
						for (int age = 0; age < stepsInRun + 96; age++)
						pRiskClassByAge[i][r][age][steps][sex]=0;
						for (int nDis = 0; nDis < nDiseases; nDis++) {
							pDiseasePerRiskClass[i][nDis][r][steps][sex] = 0;
							for (int age = 0; age < stepsInRun + 96; age++)

							{
								pDiseasePerRiskClassByAge[i][nDis][r][age][steps][sex] = 0;

							}
						}
					}
				}

	}

	public void makeOutput(Population [] pop) {

		int nPopulations=pop.length;
		for (int thisScen=0;thisScen<nPopulations;thisScen++){
		int sexIndex = 0;
		int ageIndex = 0;

		int[] nInSimulation = new int[2];
		int[][] nInSimulationByAge = new int[96 + stepsInRun][2];
		
		
		float[] compoundData;
		for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {

			Iterator<Individual> individualIterator = pop[thisScen].iterator();

			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				ageIndex = (int) Math.round(((Float) individual.get(1)
						.getValue(stepCount)));
				sexIndex = (int) (Integer) individual.get(2)
						.getValue(stepCount);
				int riskFactor = (int) (Integer) individual.get(3).getValue(
						stepCount);

				if (riskType == 2)
					compoundData = ((CompoundCharacteristicValue) individual
							.get(5)).getUnwrappedValue(stepCount);
				else
					compoundData = ((CompoundCharacteristicValue) individual
							.get(4)).getUnwrappedValue(stepCount);

				pPop[thisScen][stepCount][sexIndex] += compoundData[getNDiseaseStates() - 1];
				nInSimulation[sexIndex]++;
				nInSimulationByAge[ageIndex][sexIndex]++;
				
				pPopByAge[thisScen][ageIndex][stepCount][sexIndex] += compoundData[getNDiseaseStates() - 1];
				pRiskClass[thisScen][riskFactor][stepCount][sexIndex] += compoundData[getNDiseaseStates() - 1];
				pRiskClassByAge[thisScen][riskFactor][ageIndex][stepCount][sexIndex]+=compoundData[getNDiseaseStates() - 1];
				int currentDisease = 0;
				int currentState = 0;
				for (int c = 0; c < structure.length; c++) {
					for (int d = 0; d <= structure[c].getNinCluster(); d++) {
						for (int s = 1; s < Math.pow(2, structure[c]
								.getNinCluster()); s++) {
							if ((s & (1 << d)) == (1 << d)) {
								pDisease[thisScen][currentDisease + d][stepCount][sexIndex] += compoundData[currentState
										+ s - 1]*compoundData[getNDiseaseStates() - 1];
								pDiseasePerRiskClassByAge[thisScen][currentDisease
										+ d][riskFactor][ageIndex][stepCount][sexIndex] += compoundData[currentState
										+ s - 1]*compoundData[getNDiseaseStates() - 1];
								pDiseasePerRiskClass[thisScen][currentDisease
										+ d][riskFactor][stepCount][sexIndex] += compoundData[currentState
										+ s - 1]*compoundData[getNDiseaseStates() - 1];
							}
						}
					}
					currentDisease += structure[c].getNinCluster();
					currentState += Math.pow(2, structure[c].getNinCluster()) - 1;
				}

				// TODO for with cured fraction
				// float [] disease = (float[]) individual.get(4)
				// .getValue(stepCount);

				// simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
				// simulatedDiseasePrevalence[stepCount][ageIndex][sexIndex] +=
				// disease[0];
				// simulatedSurvival[stepCount][ageIndex][sexIndex] +=
				// disease[3];

			}// end loop over individuals

			for (int s = 0; s < 2; s++) {

				
					

					for (int d = 0; d < nDiseases; d++) {
						if(pPop[thisScen][stepCount][s] != 0)
						pDisease[thisScen][d][stepCount][s] = pDisease[thisScen][d][stepCount][s]
								/ pPop[thisScen][stepCount][s];
						else pDisease[thisScen][d][stepCount][s]=0;

						for (int r = 0; r <  nRiskFactorClasses; r++) {
						if(pRiskClass[thisScen][r][stepCount][s]!=0)	pDiseasePerRiskClass[thisScen][d][r][stepCount][s] = pDiseasePerRiskClass[thisScen][d][r][stepCount][s]
									/pRiskClass[thisScen][r][stepCount][s];
						else pDiseasePerRiskClass[thisScen][d][r][stepCount][s] = 0;						                                                                      									
							for (int a = 0; a < 96; a++)
								if(pRiskClassByAge[thisScen][r][a][stepCount][s]!=0)
							pDiseasePerRiskClassByAge[thisScen][d][r][a][stepCount][s] = pDiseasePerRiskClassByAge[thisScen][d][r][a][stepCount][s]
										/pRiskClassByAge[thisScen][r][a][stepCount][s];
								else pDiseasePerRiskClassByAge[thisScen][d][r][a][stepCount][s] = 0;

						}
					}
					for (int r = 0; r <  nRiskFactorClasses; r++) {
						if (pPop[thisScen][stepCount][s] != 0) 	
							pRiskClass[thisScen][r][stepCount][s] = pRiskClass[thisScen][r][stepCount][s]
								/ pPop[thisScen][stepCount][s];
						else pRiskClass[thisScen][r][stepCount][s] =0;
						for (int a = 0; a < 96; a++)
							if(	 pPopByAge[thisScen][a][stepCount][s]!=0)
								pRiskClassByAge[thisScen][r][a][stepCount][s] =
									pRiskClassByAge	[thisScen][r][a][stepCount][s]
										/ pPopByAge[thisScen][a][stepCount][s];
							else pRiskClassByAge[thisScen][r][a][stepCount][s]= 0;
					}
					if (nInSimulation[s] != 0)  pPop[thisScen][stepCount][s] = pPop[thisScen][stepCount][s]
					                              							/ nInSimulation[s];
					else pPop[thisScen][stepCount][s]=0;
					for (int a = 0; a < 96; a++)
						if(	 nInSimulationByAge[a][s]!=0)
							pPopByAge[thisScen][a][stepCount][s] = pPopByAge[thisScen][a][stepCount][s]
									/ nInSimulationByAge[a][s];
						else pPopByAge[thisScen][a][stepCount][s] = 0;
					
					
					
					                              							
				}
			}
		}// end stepCount
	}//	end loop over populations			

	

	public JFreeChart makeSurvivalPlot(String outcomeName, int thisScen) {
		/*
		 * Note: Multiple series (lines) can be added to one data set. Use this
		 * feature if you want more than one line to appear on the same graph.
		 */
		XYSeries menSeries = new XYSeries("Survival rates men");
		XYSeries womenSeries = new XYSeries("Survival rates women");

		for (int steps = 0; steps < stepsInRun; steps++) {
			menSeries.add((double) steps, pPop[thisScen][steps][0]);
			womenSeries.add((double) steps, pPop[thisScen][steps][1]);
			
		}

		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);

		JFreeChart chart = ChartFactory.createXYLineChart(outcomeName,
				"years of simulation", "survival", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);
		try {

			ChartUtilities.saveChartAsJPEG(new File(
					"C:\\hendriek\\java\\survivalplot_scen"+thisScen+".jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart.");
		}
		return chart;
	}

	public void makePrevalencePlots(int thisScen) {
		for (int c = 0; c < structure.length; c++) {
			for (int d = 0; d < structure[c].getNinCluster(); d++) {
		
		XYSeries menSeries = new XYSeries(structure[c].diseaseName.get(d)+" prevalence in men");
		XYSeries womenSeries = new XYSeries(structure[c].diseaseName.get(d)+" prevalence in women");
        int dNumber=structure[c].diseaseNumber[d];
		for (int steps = 0; steps < stepsInRun; steps++) {
			menSeries.add((double) steps, pDisease[thisScen][dNumber][steps][0]);
			womenSeries.add((double) steps, pDisease[thisScen][dNumber][steps][1]);
			
		}
		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);

		JFreeChart chart = ChartFactory.createXYLineChart(structure[c].diseaseName.get(d),
				"years of simulation", "prevalence rate", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		final ChartPanel chartPanel = new ChartPanel(chart); 
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
		try {

			ChartUtilities.saveChartAsJPEG(new File(
					"C:\\hendriek\\java\\chartPrevalence"+dNumber+".jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart. for diseasenumber" + dNumber);
		}
		
			}	
		
	}}


	public void makePrevalenceByRiskFactorPlots(int thisScen) {
		XYDataset xyDataset =null;
		for (int c = 0; c < structure.length; c++) {
			for (int d = 0; d < structure[c].getNinCluster(); d++) {
				 int dNumber=structure[c].diseaseNumber[d];
				for (int r = 0; r < nRiskFactorClasses; r++) {
		XYSeries menSeries = new XYSeries(structure[c].diseaseName.get(d)+" prevalence in men, risk factor class "+r);
		XYSeries womenSeries = new XYSeries(structure[c].diseaseName.get(d)+" prevalence in women, risk factor class "+r);
	
       
		for (int steps = 0; steps < stepsInRun; steps++) {
			menSeries.add((double) steps, pDiseasePerRiskClass[thisScen][dNumber][r][steps][0]);
			womenSeries.add((double) steps,pDiseasePerRiskClass[thisScen][dNumber][r][steps][1]);
			
		}
		if (r==0)  xyDataset = new XYSeriesCollection(menSeries);else
			((XYSeriesCollection) xyDataset).addSeries(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
				}
		JFreeChart chart = ChartFactory.createXYLineChart(structure[c].diseaseName.get(d),
				"years of simulation", "prevalence rate", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		final ChartPanel chartPanel = new ChartPanel(chart); 
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
		try {

			ChartUtilities.saveChartAsJPEG(new File(
					"C:\\hendriek\\java\\chartPrevalenceByRiskClass"+dNumber+"scen"+thisScen+".jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart. for diseasenumber" + dNumber);
		}
		
			}	
		
	}}

	
	

	public void makeRiskFactorPlots(int thisScen) {
		
		 XYDataset xyDataset=null ;
		for (int r = 0; r < nRiskFactorClasses; r++){
		
		XYSeries  menSeries = new XYSeries(" riskfactor prevalence in men") ;
		XYSeries  womenSeries = new XYSeries("riskfactor prevalence in women") ;
       
		for (int steps = 0; steps < stepsInRun; steps++){
			
			menSeries.add((double) steps, pRiskClass[thisScen][r][steps][0]);
			womenSeries.add((double) steps, pRiskClass[thisScen][r][steps][1]);
			
		}
		 xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);}

		JFreeChart chart = ChartFactory.createXYLineChart("riskfactor",
				"years of simulation", "prevalence rate", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		final ChartPanel chartPanel = new ChartPanel(chart); 
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
		try {

			ChartUtilities.saveChartAsJPEG(new File(
					"C:\\hendriek\\java\\chartRiskFactorPrevalence.jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart. for riskfactor");
		}
		
			}	
		
	

	
	
	public void makeLifeExpectancyPlot() {

		double [][] lifeExp= new double[nScen+1][2];
		
		for (int scenario=0;scenario<nScen+1;scenario++)
			for (int s=0;s<2;s++){
			for (int steps=0;steps<stepsInRun;steps++)
		
			lifeExp[scenario][s]+=pPop[scenario][steps][s];
			
		lifeExp[scenario][s]=lifeExp[scenario][s]/pPop[scenario][0][s];
		
		}
		
		
		CategoryDataset dataset= DatasetUtilities.createCategoryDataset("scenario ", "gender ",lifeExp);
	    
	    JFreeChart chart = ChartFactory.createBarChart(
            "LifeExpectancy", "", "years", dataset,
            PlotOrientation.VERTICAL, true, true, false);
	    ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);
		
		try {

			ChartUtilities.saveChartAsJPEG(new File(
					"C:\\hendriek\\java\\chartLifeExpectancy.jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart. for lifeExpectancy");
		}
	}
		public void makePopulationPyramidPlot(int scenNumber, int timestep) {

			
			
			/* ChartFactory.createBarChart(
			            "Bar Chart Demo",         // chart title
			            "Category",               // domain axis label
			            "Value",                  // range axis label
			            dataset,                  // data
			            PlotOrientation.VERTICAL, // orientation
			            true,                     // include legend
			            true,                     // tooltips?
			            false                     // URLs?
			            
			            
			            // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

			        );
			        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
// set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

org.jfree.chart.renderer.category.CategoryItemRendererState
setBarWidth(double) 


*/
			
			
			    
			double[][] pyramidData1=new double [100][2];
			double [][] pyramidData2=new double [100][2];
			for (int a = 0;  a<  100; a++){
			 pyramidData1[a][0]=pPopByAge[scenNumber][a][timestep][0];
			 pyramidData2[a][0]=-pPopByAge[scenNumber][a][timestep][1];
			 pyramidData1[a][1]=1;
			                 pyramidData1[a][1]=1;}
				
			CategoryDataset dataset1= DatasetUtilities.createCategoryDataset("age ", "",pyramidData1);
			CategoryDataset dataset2= DatasetUtilities.createCategoryDataset("age ", "",pyramidData2);
			   
			
			
			JFreeChart  chart1 = ChartFactory.createBarChart(
	            "LifeExpectancy", "", "years", dataset1,
	            PlotOrientation.HORIZONTAL, false, true, false);
		    JFreeChart chart2 = ChartFactory.createBarChart(
		            "LifeExpectancy", "", "years", dataset2,
		            PlotOrientation.HORIZONTAL, false, true, false);
		    CategoryPlot subPlot1 = chart1.getCategoryPlot();
		    CategoryPlot subPlot2 = chart2.getCategoryPlot();
		   
		    ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart1);
		    final CategoryAxis domainAxis = new CategoryAxis("PopulationNumbers");
	        final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);

	        plot.add(subPlot1, 1);
	        plot.add(subPlot2, 1);

	      
	        
			frame1.setVisible(true);
			frame1.setSize(300, 300);
			final JFreeChart chart = new JFreeChart(
	                "Population", new Font("SansSerif", Font.BOLD, 12),
	                plot, false);
			try {

				ChartUtilities.saveChartAsJPEG(new File(
						"C:\\hendriek\\java\\chartPyramid.jpg"), chart, 500, 300);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Problem occurred creating chart. for Pyramid");
			}
			
	       
      
		
	}

	public void main(String[] args) {
		// Create a simple pie chart
		float[][][] prevalence = null;
		makePrevalencePlots(0);
		
	}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getNScen() {
		return nScen;
	}

	public void setNScen(int scen) {
		nScen = scen;
	}

	public int getStepsInRun() {
		return stepsInRun;
	}

	public void setStepsInRun(int stepsInRun) {
		this.stepsInRun = stepsInRun;
	}

	public DiseaseClusterStructure[] getStructure() {
		return structure;
	}

	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	/**
	 * @param structure2
	 */
	private void setNDiseases(DiseaseClusterStructure[] s) {
		nDiseases = 0;
		for (int i = 0; i < s.length; i++) {
			nDiseases += s[i].nInCluster;

		}
		// TODO Auto-generated method stub

	}

	public int getNDiseases() {
		return nDiseases;
	}

	public int getNRiskFactorClasses() {
		return nRiskFactorClasses;
	}

	public void setNRiskFactorClasses(int riskFactorClasses) {
		nRiskFactorClasses = riskFactorClasses;
	}

	public int getNDiseaseStates() {
		return nDiseaseStates;
	}

	public void setNDiseaseStates(DiseaseClusterStructure[] s) {
		nDiseaseStates = 1;

		for (int i = 0; i < s.length; i++) {
			if (s[i].nInCluster == 1)
				nDiseaseStates++;
			else if (s[i].isWithCuredFraction())
				nDiseaseStates += 2;
			else
				nDiseaseStates += Math.pow(2, s[i].nInCluster) - 1;

		}
	}

	public void setNDiseaseStates(int input) {
		nDiseaseStates = input;
	}
}
