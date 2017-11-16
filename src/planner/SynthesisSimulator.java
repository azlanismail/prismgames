package planner;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import parser.Values;

public class SynthesisSimulator {

	String propPath, modelPath, transPath, stratPath, actionListPath;
	
	//Planning related components	
	PropertiesGenerator pg;
	AdaptationModelGenerator mdg;
	StochasticPlanner sp;
	MultiObjStrategyExtraction se;
	
	//System environment components
	AutonomicCloud nodeSet[];
	
	//simulation parameters
	int simCycle;
	
	//logging parameters
	long time[]; //log the synthesis execution time
	long timeGen[]; //log the generation execution time
	long timeExp[]; //long the export execution time
	long timeExt[]; //log the extraction execution time
	boolean statusRes[]; //log the synthesis status without pareto
	boolean statusSyn[][]; //log the synthesis status with pareto
	int countCand[];  //log the number of candidates
		
	//to log the execution information
	String outfile;
	
	//to log the adjusted properties
	Properties adjustProp[];
	
	public SynthesisSimulator() {
		
	}
	
	public void setPath(String pPath, String mPath, String tPath, String sPath, String aPath) {
		propPath = pPath;
		modelPath = mPath;
		transPath = tPath;
		stratPath = sPath;
		actionListPath = aPath;
	}
	
	public void setSimulationObjects(PropertiesGenerator pg, AdaptationModelGenerator mdg, 
										StochasticPlanner sp, MultiObjStrategyExtraction se) {
		this.pg = pg;
		this.mdg = mdg;
		this.sp = sp;
		this.se = se;
	}
	
	public void encodeModel() {
		//creating and assigning values to the model parameters
		System.out.println("Generates random values...");
		mdg.setAllRandomServProfiles();
		System.out.println("Create quality attributes...");
		mdg.createQualityParams();
		System.out.println("Assign quantitative information...");
		mdg.setQualityParamswithValues();
		mdg.setModelPath(modelPath);	
		System.out.println("Encoding model specification...");
		mdg.encodeSGModelforComplexAppDeployment();
		mdg.exportActionList(actionListPath);
	}

	public void simulatePlanning(int sCycle) {
			
		simCycle = sCycle;
		time = new long[simCycle]; //log the synthesis execution time
		timeExt = new long[simCycle]; //log the extraction execution time
		statusRes = new boolean[simCycle]; //log the synthesis status
		countCand = new int[simCycle]; //log the number of candidates
		//begin the simulation per configuration	
		for(int m=0; m < simCycle; m++) {
			
			//to store the time
			TimeMeasure tm = new TimeMeasure();
			//TimeMeasure tmGen = new TimeMeasure();
			//TimeMeasure tmExp = new TimeMeasure();
			TimeMeasure tmExt = new TimeMeasure();
			
			//==========Synthesis===================
			//record the start time
			tm.start();
			
			//encoding the model
			encodeModel();
			
			//synthesis
			System.out.println("Synthesizing model...");
			sp.initiatePlanner();
			sp.parseModelandProperties(modelPath, propPath);
			
			if(!mdg.setValuesStatus)
				sp.setUndefinedModelValues(mdg.getDefinedValues());
			if(!pg.setValuesStatus)
				sp.setUndefinedPropertiesValues(pg.getDefinedValues());
			
			sp.setPropertyId(0);	//set to the properties id 0
			sp.checkModelforMultiObjective();
			
			
			boolean status = sp.getSynthesisStatus();
			
			if (status) {
				//exporting
				System.out.println("Exporting transitions and strategies...");
				sp.exportTrans(transPath);
				sp.exportStrategy(stratPath);		
			}
			//record the synthesis status
			statusRes[m] = sp.getSynthesisStatus();
			
			tm.stop();
 			time[m] = tm.getDuration(); //record the duration
 			
 			tmExt.start();
 			
			//=================================
			//extraction
			
			if (status) {
				System.out.println("Extracting strategies...");
				se.setPath(transPath, stratPath, actionListPath);
				se.readSingleActionLabelFile();
				se.readTransitionFile();
				se.readStrategiesfromMultiObjSynthesis();	
				se.findSingleDecision();
				countCand[m] = se.computeNumofPotentialCandidate();
			}
			else
				System.out.println("No decision since synthesis results in false");

			//===============================
			
			//stop the timer
 			tmExt.stop();
 			//record the duration
 			timeExt[m] = tmExt.getDuration();

		}//end of simulation cycle 
	
		System.out.println("Simulation is done...");
			
	}
	
	public void simulatePlanningwithPareto(int sCycle) {
		
		simCycle = sCycle;
		time = new long[simCycle]; //log the synthesis execution time
		timeExt = new long[simCycle]; //log the extraction execution time
		statusSyn = new boolean[simCycle][3]; //log synthesis status with pareto, whilst 3 refers to max attempt
		countCand = new int[simCycle]; //log the number of candidates
		
		//begin the simulation per configuration	
		for(int m=0; m < simCycle; m++) {
			
			//to store the time
			TimeMeasure tm = new TimeMeasure();
			TimeMeasure tmExt = new TimeMeasure();
			
			//==========Synthesis===================
			//record the start time
			tm.start();
			
			//encoding the model
			encodeModel();
			
			System.out.println("Synthesizing model...");
			sp.initiatePlanner();
			sp.parseModelandProperties(modelPath, propPath);
			sp.setPropertyId(0);
			
			if(!mdg.setValuesStatus)
				sp.setUndefinedModelValues(mdg.getDefinedValues());
			if(!pg.setValuesStatus)
				sp.setUndefinedPropertiesValues(pg.getDefinedValues());
			
			int count=0;
			boolean status = false;
			while (true) {
				//perform model checking
				sp.checkModelforMultiObjective();
				status = sp.getSynthesisStatus();
				
				//record the synthesis status
				statusSyn[m][count] = status;
				
				if ((status==true) || (count > 2)) {
					System.out.println("Terminating the model checking loop");
					break;
				}
				else {
					//get new thresholds values via pareto computation
					sp.computeParetoforThresholds();
					
					//re-assign the new thresholds
					adjustProp = sp.assignThresholdParamswithValues(pg.getProperties());
					//sp.assignThresholdParamswithValues();
				}
				count++;
			}
			
			//==================================
			//exporting
			if (status) {
				//exporting
				System.out.println("Exporting transitions and strategies...");
				sp.exportTrans(transPath);
				sp.exportStrategy(stratPath);		
			}	
			else
				System.out.println("No exporting since synthesis results in false");	
			
			tm.stop();
 			time[m] = tm.getDuration(); //record the duration
 			
 			tmExt.start();
 			
			//=================================
 			//extraction
 			if (status) {
 				System.out.println("Extracting strategies...");
 				se.setPath(transPath, stratPath, actionListPath);
 				se.readSingleActionLabelFile();
 				se.readTransitionFile();
 				se.readStrategiesfromMultiObjSynthesis();	
 				se.findSingleDecision();
 			}
 			else
 				System.out.println("No decision since synthesis results in false");	

			//===============================
			
			//stop the timer
 			tmExt.stop();
 			//record the duration
 			timeExt[m] = tmExt.getDuration();

		}//end of simulation cycle 
	
		System.out.println("Simulation is done...");
			
	}
	/**
	 * log performance data
	 */
	public void logInformation(String outfile) {
		this.outfile = outfile;
		String fileName = outfile + "_" + mdg.getMaxActionP1() +"_" + mdg.getMaxActionP2();
		try {
			PrintWriter out = new PrintWriter(new FileWriter(fileName, false));
		
			String loginfo = "";
			out.println("CycleId NumofAct NumofEnv SynTime SynStatus NumofCandidate");
			for(int m=0; m < simCycle; m++) {
				loginfo = loginfo+m+" "+mdg.getMaxActionP1()+" "+mdg.getMaxActionP2()+" "+
						  time[m]+" "+statusRes[m]+" "+countCand[m]+"\n";
			}
			out.println(loginfo);
			out.close();
		}
 		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * log performance data
	 */
	public void logInformationwithPareto(String outfile, double cost, int tm, double rel) {	
		
		//set the file name
		this.outfile = outfile;
		String fileName = outfile + "_Pareto_" + mdg.getMaxActionP1() +"_" + mdg.getMaxActionP2();
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(fileName, false));
		
			String loginfo = "";
			out.println("CycleId NumofAct NumofEnv SynTime SynStatus NumofCandidate InitialProp(C,T,R) AdjustedProp(C,T,R)");
			for(int m=0; m < simCycle; m++) {
				loginfo = loginfo+m+" "+mdg.getMaxActionP1()+" "+mdg.getMaxActionP2()+" "+
						  time[m];
				//print the synthesis status before and after computing pareto set
				for(int c=0; c < statusSyn[m].length; c++) {
					loginfo = loginfo+" "+statusSyn[m][c]+",";
				}
				
				//print number of potential candidate
				loginfo = loginfo+" "+countCand[m]+" ";
				
				//print the initial thresholds
				loginfo = loginfo +cost+","+tm+","+rel+" ";
			
				//print the adjusted thresholds where the values are obtained during pareto based model checking
				for(int g=0; g < adjustProp.length; g++) {
					loginfo = loginfo+adjustProp[g].values;
					if (g+1 >= adjustProp.length)
						loginfo = loginfo+" ";
					else
						loginfo = loginfo+",";
				}
				//print next line
				loginfo = loginfo +"\n";
			}
			
			out.println(loginfo);
			out.close();
		}
 		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propTest.props";
		String modelPath = "/home/azlan/git/prismgames/Prismfiles/appDeployModel.prism";
		String transPath = "/home/azlan/git/prismgames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/prismgames/IOFiles/strat.txt";
		String actionListPath = "/home/azlan/git/prismgames/IOFiles/actionList.txt";
		//to log the execution information
		String outfile = "/home/azlan/git/prismgames/IOFiles/logfile";
				
		//==========CONFIGURATION SETTING==================
		int numConf = 1; //number of configuration
		int numAct = 5;  //number of collaborator
		int numDisAct = 0;  //how many to accumulate 
		int numEnv = 5;  //number of environment variation
		int numDisEnv = 0;  //how many to accumulate
		int simCycle = 1; //number of simulation cycle
		int numQyObj = 3; //number of quality objectives
		boolean assignValue = false; //true-assign values while encoding, false-later stage
		boolean reSynthesis = true; //true-need pareto computation, false-not needed
		
		for(int conf=0; conf < numConf; conf++) {
			//==========UPDATING CONFIGURATION SETTING==================
			numAct += numDisAct;
			//==========PROPERTIES CREATION=====================
			//set properties
			Properties pp[] = new Properties[numQyObj];
			PropertiesGenerator pg = new PropertiesGenerator();
			
			//create the empty properties
			pp[0] = new Properties();
			pp[1] = new Properties();
			pp[2] = new Properties();
			
			Random rand = new Random();
			
			//initialize values for each resource / variation
			double cost, rel;
			int time;

			//initialize range value for reliability
			double minRel=0.8, maxRel=1.0;
			double rangeRel = maxRel - minRel;
			
			cost = rand.nextInt(20) + 80;
			time = rand.nextInt(200) + 700;
			rel = rand.nextDouble() * rangeRel + minRel;
				
			//specify the parameters
			pp[0].setProperties(0, "cost", "double", cost, 10, "<");
			pp[1].setProperties(1, "time", "int", time, 100,"<");
			pp[2].setProperties(2, "reliability", "double", rel, 0.1, ">");
			
			//========PROPERTIES ENCODING=====================
			pg.setPropPath(propPath); //set the path
			pg.assignProperties(pp); //assign properties to the properties generator
			pg.setValuesStatus(assignValue); //true-encode properties with threshold, false-without threshold (later stage)
			pg.setThresholdParamswithValues();
			pg.encodeProperties(); //encode the properties specification with values
			
			
			//=========MODEL ENCODING============================		
			//Create a model generator instance
			AdaptationModelGenerator mdg = new AdaptationModelGenerator();
			
			//configuring model parameters and values
			System.out.println("Creating model for single application deployment...");
			mdg.setValuesStatus(assignValue); //true-encode model with values, false-create model without values (later stage)
			mdg.setPattern(0);	//set the current value of p=0 for single
			mdg.setParamsNames("p1", "p2", "planner", "environment");
			mdg.setUpperBounds(1, numAct, numEnv); //simply set numNode = 1
			
			
			//=========INITIALIZE PLANNING-RELATED OBJECTS==============
			//Create a SG-Planner instance
			StochasticPlanner sp = new StochasticPlanner();
			MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
			
			//=========BEGIN SIMULATION=======================
			//create a simulator
			SynthesisSimulator syn = new SynthesisSimulator();
			syn.setPath(propPath, modelPath, transPath, stratPath, actionListPath);
			syn.setSimulationObjects(pg, mdg, sp, se);
			
			if(reSynthesis) {
				syn.simulatePlanningwithPareto(simCycle);
				syn.logInformationwithPareto(outfile, cost, time, rel);
			}
			else {
				syn.simulatePlanning(simCycle);
				syn.logInformation(outfile);
			}
			
			//==========UPDATING CONFIGURATION SETTING FOR THE NEXT CYCLE==================
			numAct += numDisAct;
			numEnv += numDisEnv;
			
			
		}//end of configuration
		
	}

}
