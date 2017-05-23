package planner;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SynthesisSimulator {

	String propPath, modelPath, transPath, stratPath, actionPath;
	
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
		
	//to log the execution information
	String outfile = "/home/azlan/git/PrismGames/IOFiles/logfile";
	
	public SynthesisSimulator() {
		
	}
	
	public void setPath(String pPath, String mPath, String tPath, String sPath, String aPath) {
		propPath = pPath;
		modelPath = mPath;
		transPath = tPath;
		stratPath = sPath;
		actionPath = aPath;
	}
	
	public void setSimulationObjects(PropertiesGenerator pg, AdaptationModelGenerator mdg, 
										StochasticPlanner sp, MultiObjStrategyExtraction se) {
		this.pg = pg;
		this.mdg = mdg;
		this.sp = sp;
		this.se = se;
	}

	public void simulatePlanning(int sCycle) {
			
		simCycle = sCycle;
		time = new long[simCycle]; //log the synthesis execution time
		timeExt = new long[simCycle]; //log the extraction execution time
		statusRes = new boolean[simCycle]; //log the synthesis status
		
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
			
			//synthesis
			System.out.println("Synthesizing model...");
			sp.initiatePlanner();
			sp.parseModelandProperties(modelPath, propPath);
			
			//sp.setUndefinedValues(mdg.getDefinedValues());
			sp.setPropertyId(0);
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
			se.setPath(transPath, stratPath, actionPath);
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
	
	public void logInformation() {		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(outfile, false));
		
			String loginfo = "";
			out.println("CycleId NumofObj NumofCollab NumofResource SynTime SynStatus Solution");
			for(int m=0; m < simCycle; m++) {
				loginfo = loginfo+m+" "+mdg.getMaxActionP1()+" "+mdg.getMaxActionP2()+" "+
						  time[m]+" "+statusRes[m]+"\n";
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
	
		//==========PROPERTIES CREATION=====================
		//set properties
		Properties pp[] = new Properties[4];
		PropertiesGenerator pg = new PropertiesGenerator();
		
		//create the empty properties
		pp[0] = new Properties();
		pp[1] = new Properties();
		pp[2] = new Properties();
		pp[3] = new Properties();
		
		//specify the parameters
		pp[0].setProperties(0, "cost", "int", 90, 10, "<=");
		pp[1].setProperties(1, "time", "int", 1000, 100,"<=");
		pp[2].setProperties(2, "reliability", "double", 0.9, 0.1, ">=");
		pp[3].setProperties(3, "availability", "double", 0.9, 0.1, ">=");
		
		pg.setPropPath(propPath); //set the path
		pg.assignProperties(pp); //assign properties to the properties generator
		pg.encodeProperties(); //encode the properties specification with values
		
		//=========MODEL CREATION============================		
		//Create a model generator instance
		AdaptationModelGenerator mdg = new AdaptationModelGenerator();
		int numCollab = 8; //number of collaborator
		int numResource = 10;//set number of resources /vm
		
		//configuring model parameters and values
		System.out.println("Creating model for single application deployment...");
		mdg.setValuesStatus(true); //true-create model with values, false-create model without values (later stage)
		mdg.setPattern(0);	//set the current value of p=0 for single
		mdg.setParamsNames("p1", "p2", "planner", "environment");
		mdg.setUpperBounds(1, numCollab, numResource); //simply set numNode = 1
		
		//creating and assigning values to parameters
		System.out.println("Creating and assigning values to parameters...");
		mdg.setAllRandomServProfiles();
		mdg.createQualityParams();
		mdg.setQualityParamswithValues();
		mdg.setModelPath(modelPath);										
		mdg.encodeSGModelforAppDeployment();
		
		//=========INITIALIZE PLANNING-RELATED OBJECTS==============
		//Create a SG-Planner instance
		StochasticPlanner sp = new StochasticPlanner();
		MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
		
		//=========BEGIN SIMULATION=======================
		//create a simulator
		SynthesisSimulator syn = new SynthesisSimulator();
		int simCycle = 10;
		syn.setPath(propPath, modelPath, transPath, stratPath, actionListPath);
		syn.setSimulationObjects(pg, mdg, sp, se);
		syn.simulatePlanning(simCycle);
		syn.logInformation();
	}

}
