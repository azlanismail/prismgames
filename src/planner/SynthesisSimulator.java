package planner;

import java.io.FileNotFoundException;

public class SynthesisSimulator {

	String propPath, modelPath, transPath, stratPath;
	
	int simCycle, numNode, numCollab, numResource;
	
	AutonomicCloud nodeSet[];

	Properties[] prop;
	
	PropertiesGenerator pg;
	
	public SynthesisSimulator() {
		
	}
	
	public void setPath(String pPath, String mPath, String tPath, String sPath) {
		propPath = pPath;
		modelPath = mPath;
		transPath = tPath;
		stratPath = sPath;
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
	

	
	public void Simulation(int simCycle) {
		//begin the simulation per configuration	
		for(int m=0; m < simCycle; m++) {
			
			//to store the time
			TimeMeasure tm = new TimeMeasure();
			TimeMeasure tmGen = new TimeMeasure();
			TimeMeasure tmExp = new TimeMeasure();
			TimeMeasure tmExt = new TimeMeasure();
			
			//to store the solution
			//String[] selServ = new String[nodeSet[c]];
			
			
		
			//Create a SG-Planner instance
			StochasticPlanner sp = new StochasticPlanner();
			
			//Create a strategy extraction instance
			StrategyExtraction se = new StrategyExtraction();
			
			
			//create the model and synthesize according to a pattern
			
			patName = "single";		
			String singleModelPath = modelPath+"modelSingle.prism";
			String singleTransPath = transPath+"transSingle.txt";
			String singleStratPath = stratPath+"stratSingle.txt";
			
			//record the start time
			tmGen.start();
				
			//========================================
			//configuring model parameters and values
			System.out.println("Solving single node...");
			mdg.setValuesStatus(true); //true-create model with values, false-create model without values (later stage)
			mdg.setPattern(0);	//set the current value of p=0 for single
			mdg.setParamsNames("p1", "p2", "planner", "environment");
			mdg.setUpperBounds(1, numNode, numResource); //simply set numNode = 1
			
			//creating and assigning values to parameters
			System.out.println("Creating and assigning values to parameters...");
			mdg.setRequirements(0, costR, durR, relR, );
			mdg.setAllRandomServProfiles();
			mdg.createServParams();
			mdg.setReqParamswithValues();
			mdg.setServParamswithValues();
			
			//create the specifications
			System.out.println("Generating model and properties specifications...");
			mdg.setModelPath(singleModelPath);										
			mdg.setPropPath(propPath);
			mdg.generateSGModel(0);	//value for >0 is only for parallel structure
			mdg.generateProperties();
			//=================================
				
			tmGen.stop();
 			//record the duration
 			timeGen[m] = tmGen.getDuration();
 			
			//export the actions (for strategy extraction)
			//System.out.println("Exporting action labels...");
			//mdg.exportActionLabels(actLabelPath);				
			
			//record the start time
			tm.start();
			//=================================
			//synthesis
			System.out.println("Synthesizing model...");
			sp.initiatePlanner();
			sp.parseModelandProperties(singleModelPath, propPath);
			sp.setUndefinedValues(mdg.getDefinedValues());
			sp.checkModel(v);	//based on evaluation method
			//=================================
			tm.stop();
 			//record the duration
 			time[m] = tm.getDuration();
	 			
 			tmExp.start();
 			//==================================
			//exporting
			System.out.println("Exporting transitions and strategies...");
			sp.exportTrans(singleTransPath);
			sp.exportStrategy(singleStratPath);
			//=================================
			tmExp.stop();
 			//record the duration
 			timeExp[m] = tmExp.getDuration();
 			
 			tmExt.start();
 			//================================
			if (sp.getSynthesisStatus()) {
				//extraction
				System.out.println("Extracting strategies...");
				se.setPath(singleTransPath, singleStratPath);
				se.setNumofDecision(1); //to control the searching for solution, simply set to one
				se.setActionLabels(mdg.getActionLabels());
				se.readTransitionFile();
				se.readStrategiesProfile(v);	//based on evaluation method	
				se.findSolutions();
				selServ = se.getSolution();
				for (int n=0; n < selServ.length; n++) {
					System.out.println("Decision node: "+n+", solution :"+selServ[n]);
				}
			}
			else
				System.out.println("Synthesis results in false.....");
			//===============================
			
			//stop the timer
 			tmExt.stop();
 			//record the duration
 			timeExt[m] = tmExt.getDuration();
 			
 			//record the synthesis status
			statusRes[m] = sp.getSynthesisStatus();
			
			
						
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
			
		}//end of simulation cycle 
	
		System.out.println("Simulation is done...");
			
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propTest.props";
		String modelPath = "/home/azlan/git/PrismGames/Prismfiles/";
		String transPath = "/home/azlan/git/PrismGames/IOFiles/";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/";
	
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
		pp[0].setProperties(0, "cost", "int", 90, "<=");
		pp[1].setProperties(1, "time", "int", 1000, "<=");
		pp[2].setProperties(2, "reliability", "double", 0.9, ">=");
		pp[3].setProperties(3, "availability", "double", 0.9, ">=");
		
		//set the path
		pg.setPropPath(propPath);
				
		//assign properties to the properties generator
		pg.assignProperties(pp);
				
		pg.encodeProperties();
		
		//=========MODEL CREATION============================
		//Create a model generator instance
		AdaptationModelGenerator mdg = new AdaptationModelGenerator();
		int numNode = 1;  //number of application
		int numCollab = 8; //number of collaborator
		int numResource = 10;//set number of resources /vm
		AutonomicCloud cloud[] = new AutonomicCloud[numCollab];  //to create a set of collaborator
				
		//set the number of vms for each cloud collaborator
		for (int i=0; i < cloud.length; i++) 
			cloud[i].setVM(numResource);
		
		//configuring model parameters and values
		System.out.println("Creating model for single application deployment...");
		mdg.setValuesStatus(true); //true-create model with values, false-create model without values (later stage)
		mdg.setPattern(0);	//set the current value of p=0 for single
		mdg.setParamsNames("p1", "p2", "planner", "environment");
		mdg.setUpperBounds(1, numCollab, numResource); //simply set numNode = 1
		
		//creating and assigning values to parameters
		System.out.println("Creating and assigning values to parameters...");
		mdg.setAllRandomServProfiles();
		mdg.createServParams();
		mdg.setServParamswithValues();
		mdg.setModelPath(modelPath);										
		mdg.generateSGModel(0);	//value for >0 is only for parallel structure
		
		//=========BEGIN SIMULATION=======================
		//create a simulator
		SynthesisSimulator syn = new SynthesisSimulator();
		
		//generate properties specification
		
		//generate model specification
		
		//simulate
	}

}
