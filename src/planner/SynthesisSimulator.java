package planner;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SynthesisSimulator {

	String propPath, modelPath, transPath, stratPath, actionPath;
	
	//Planning related components	
	Properties[] prop;
	PropertiesGenerator pg;
	AdaptationModelGenerator mdg;
	StochasticPlanner sp;
	MultiObjStrategyExtraction se;
	
	//System environment components
	AutonomicCloud nodeSet[];
	
	//configuration parameters
	int simCycle, numNode, numCollab, numResource;
	long time[];
	
	public SynthesisSimulator() {
		
	}
	
	public void setPath(String pPath, String mPath, String tPath, String sPath, String aPath) {
		propPath = pPath;
		modelPath = mPath;
		transPath = tPath;
		stratPath = sPath;
		actionPath = aPath;
	}
	
	public void setModelParameters(int app, int collab, int res) {
		
		numNode = app;  //number of application
		numCollab = collab; //number of collaborator
		numResource = res;//set number of resources /vm
		nodeSet = new AutonomicCloud[numCollab];  //to create a set of collaborator
		
		//set the number of vms for each cloud collaborator
		for (int i=0; i < nodeSet.length; i++) 
			nodeSet[i].setVM(numResource);
	}
	

	
	public void simulatePlanning(int simCycle, StochasticPlanner sp, MultiObjStrategyExtraction se) {
		
		long time[] = new long[simCycle]; //log the synthesis execution time
		long timeGen[] = new long[simCycle]; //log the generation execution time
		long timeExp[] = new long[simCycle]; //long the export execution time
		long timeExt[] = new long[simCycle]; //log the extraction execution time
		boolean statusRes[] = new boolean[simCycle]; //log the synthesis status
		boolean statusResPar[][] = new boolean[simCycle][numNode]; //log the synthesis status for parallel
		
		
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
			
			tm.stop();
 			//record the duration
 			time[m] = tm.getDuration();
 			
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
 			
 			//record the synthesis status
			statusRes[m] = sp.getSynthesisStatus();
			
		}//end of simulation cycle 
	
		System.out.println("Simulation is done...");
			
	}
	
	public void collectTimeInformation() {
		
		try {
			if(p==0) {
				gatherData(c, m, time, timeGen, timeExp, timeExt, 1, servSet[c], numofBehavior, statusRes, p, v, selServ, 
					   mdg.getActionLabels(),mdg.getGeneratedCost(), mdg.getGeneratedAvail(), mdg.getGeneratedDuration(), mdg.getGeneratedReliability(),
					   sp.getNumStates(), sp.getNumTransitions());
			}else
				gatherData(c, m, time, timeGen, timeExp, timeExt, nodeSet[c], servSet[c], numofBehavior, statusRes, p, v, selServ, 
						   mdg.getActionLabels(),mdg.getGeneratedCost(), mdg.getGeneratedAvail(), mdg.getGeneratedDuration(), mdg.getGeneratedReliability(),
						   sp.getNumStates(), sp.getNumTransitions());
				
			
		}
 		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gatherData(int conf, int cycle, long time[], long timeGen[], long timeExp[], long timeExt[], int numNode, int numServ, int numBehavior, boolean statusRes[], int pattern, int evalMethod, String[] Solution,
			  String[][] actLabel, int[][] cost, boolean[][] avail, int[][][] dur, double[][][] rel,
			  int numStates, int numTrans) throws FileNotFoundException {

		String file1 = null;
		String file2 = null;
		if(pattern == 0) {
		file1 = "logSelectSingle_"+conf+".txt";
		file2 = "logQoSSingle_"+conf+".txt";
		}
		else if(pattern == 1) {
		file1 = "logSelectSeq_"+conf+".txt";
		file2 = "logQoSSeq_"+conf+".txt";
		}
		else if(pattern == 2) {
		file1 = "logSelectCond_"+conf+".txt";
		file2 = "logQoSCond_"+conf+".txt";
		}
		else {
		file1 = "logSelectPar_"+conf+".txt";
		file2 = "logQoSPar_"+conf+".txt";
		}
		
		String outfile1 = "/home/azlan/git/prismgames/IOFiles/"+file1;
		String outfile2 = "/home/azlan/git/prismgames/IOFiles/"+file2;
		
		PrintWriter pw, pq;
		try {
		pw = new PrintWriter(new FileWriter(outfile1, true));
		pq = new PrintWriter(new FileWriter(outfile2, true));
		
		//long total = 0;
		//==========log selection behavior==================
		// if ((conf==0) && (cycle==0))  {     
		//   pw.println("cycle pattern evalMethod numNode numService numBehavior timeGen timeSyn timeExp timeExt avgTime status size numSolution Solution");
		//  }
		pw.print(cycle+" "+pattern+" "+evalMethod+" "+numNode+" "+numServ+" "+numBehavior+" "+timeGen[cycle]+" "+time[cycle]+" "+timeExp[cycle]+" "+timeExt[cycle]+" "+" "+statusRes[cycle]+" "+numStates+"/"+numTrans+" "+Solution.length);
		
		for(int s=0; s < Solution.length; s++) {
		// pw.print(" "+Solution[s]);
		for(int n=0; n < numNode; n++) {
		for(int i=0; i < numServ; i++) {
		  if(actLabel[n][i].equals(Solution[n])) {
			   pw.print(" "+Solution[n]+" "+" "+" "+cost[n][i]+" "+avail[n][i]);
			   for(int r=0; r < numBehavior; r++) {
				   pw.print(" "+dur[n][i][r]+" "+rel[n][i][r]);
			   }
		  }
		}
		}
		}
		pw.println();
		
		//==========log QoS values===================
		if (cycle == 0)  {     
		pq.println("solution actionLabel status task service cost availability duration reliability");
		}
		for(int n=0; n < numNode; n++) {
		for(int i=0; i < numServ; i++) {
		if(actLabel[n][i].equals(Solution[n])) {
		  pq.print(pattern+" "+Solution[n]+" "+" "+statusRes[cycle]+" "+n+" "+i+" "+cost[n][i]+" "+avail[n][i]);
		  for(int r=0; r < numBehavior; r++) {
			   pq.print(" "+dur[n][i][r]+" "+rel[n][i][r]);
		  }
		}
		}
		pq.println();
		}
		pw.close();
		pq.close();
		
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}//end of gather data

	
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
		
		//set the path
		pg.setPropPath(propPath);
				
		//assign properties to the properties generator
		pg.assignProperties(pp);
				
		pg.encodeProperties();
		
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
		
		//=========INITIALIZE PLANNING==================
		//Create a SG-Planner instance
		StochasticPlanner sp = new StochasticPlanner();
		
		//=========BEGIN SIMULATION=======================
		//create a simulator
		SynthesisSimulator syn = new SynthesisSimulator();
		int simCycle = 100;
		syn.setPath(propPath, modelPath, transPath, stratPath, actionListPath);
		
		syn.simulatePlanning(simCycle, sp);
		
		
		
	}

}
