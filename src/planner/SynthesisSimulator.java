package planner;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	boolean statusRes[]; //log the synthesis status
	int countCand[];  //log the number of candidates
		
	//to log the execution information
	String outfile = "/home/azlan/git/PrismGames/IOFiles/logfile";
	
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
			
			//encoding the values
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
			
			//exporting
			System.out.println("Exporting transitions and strategies...");
			sp.exportTrans(transPath);
			sp.exportStrategy(stratPath);		
			
			//record the synthesis status
			statusRes[m] = sp.getSynthesisStatus();
			
			tm.stop();
 			time[m] = tm.getDuration(); //record the duration
 			
 			tmExt.start();
 			
			//=================================
			//extraction
			
			boolean status = sp.getSynthesisStatus();
			
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
	
	/**
	 * log performance data
	 */
	public void logInformation() {	
		String fileName = outfile + "_" + mdg.getMaxActionP1() +"_" + mdg.getMaxActionP2();
		try {
			PrintWriter out = new PrintWriter(new FileWriter(fileName, false));
		
			String loginfo = "";
			out.println("CycleId NumofObj NumofAct NumofEnv SynTime SynStatus NumofCandidate");
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propTest.props";
		String modelPath = "/home/azlan/git/PrismGames/Prismfiles/appDeployModel.prism";
		String transPath = "/home/azlan/git/PrismGames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strat.txt";
		String actionListPath = "/home/azlan/git/PrismGames/IOFiles/actionList.txt";
	
		//==========CONFIGURATION SETTING==================
		int numAct = 10;  //number of collaborator
		int numEnv = 5;  //number of environment variation
		int simCycle = 100; //number of simulation cycle
		int numQyObj = 3; //number of quality objectives
		boolean assignValue = true; //true-assign values while encoding, false-later stage
		
		for(int conf=0; conf < 1; conf++) {
			//==========UPDATING CONFIGURATION SETTING==================
			//numAct += 20;
			//==========PROPERTIES CREATION=====================
			//set properties
			Properties pp[] = new Properties[numQyObj];
			PropertiesGenerator pg = new PropertiesGenerator();
			
			//create the empty properties
			pp[0] = new Properties();
			pp[1] = new Properties();
			pp[2] = new Properties();
			//pp[3] = new Properties();
			
			//specify the parameters
			pp[0].setProperties(0, "cost", "double", 90, 10, "<");
			pp[1].setProperties(1, "time", "int", 1000, 100,"<");
			pp[2].setProperties(2, "reliability", "double", 0.9, 0.1, ">");
			//pp[3].setProperties(3, "availability", "double", 0.9, 0.1, ">=");
			
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
			
			//creating and assigning values to the model parameters
//			System.out.println("Generates random values...");
//			mdg.setAllRandomServProfiles();
//			System.out.println("Create quality attributes...");
//			mdg.createQualityParams();
//			System.out.println("Assign quantitative information...");
//			mdg.setQualityParamswithValues();
//			mdg.setModelPath(modelPath);	
//			System.out.println("Encoding model specification...");
//			mdg.encodeSGModelforComplexAppDeployment();
//			mdg.exportActionList(actionListPath);
			
			//=========INITIALIZE PLANNING-RELATED OBJECTS==============
			//Create a SG-Planner instance
			StochasticPlanner sp = new StochasticPlanner();
			MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
			
			//=========BEGIN SIMULATION=======================
			//create a simulator
			SynthesisSimulator syn = new SynthesisSimulator();
			syn.setPath(propPath, modelPath, transPath, stratPath, actionListPath);
			syn.setSimulationObjects(pg, mdg, sp, se);
			syn.simulatePlanning(simCycle);
			syn.logInformation();
		}//end of configuration
		
	}

}
