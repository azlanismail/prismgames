package planner;

import java.io.FileNotFoundException;

public class SynthesisSimulator {

	String propPath, modelPath, transPath, stratPath;
	
	int simCycle, nodeSet[], numNode, numResource;
	
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
	
	public void setSimulationSetting(int c, int n, int r) {
		simCycle = c;  
		numNode = n;  //set number of node / collaborator
		numResource = r;//set number of resources /vm
		nodeSet = new int[n];
		
		for (int i=0; i < nodeSet.length; i++) 
			nodeSet[i] = r;
	}
	
	public void setNumObjective(int num) {
		prop = new Properties[num];
	}
	
	public void setObjective(int i, String nm, double th, String cp) {
		prop[i] = new Properties(i,nm,th,cp);
	}
	
	public void generateProperties() {
		pg = new PropertiesGenerator(prop);
	}
	
	public void Simulation() {
		//begin the simulation per configuration	
		for(int m=0; m < simCycle; m++) {
			
			//to store the time
			TimeMeasure tm = new TimeMeasure();
			TimeMeasure tmGen = new TimeMeasure();
			TimeMeasure tmExp = new TimeMeasure();
			TimeMeasure tmExt = new TimeMeasure();
			
			//to store the solution
			//String[] selServ = new String[nodeSet[c]];
			
			//Create a model generator instance
			AdaptationModelGenerator mdg = new AdaptationModelGenerator();
		
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
		
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propSBSPlanner.props";
		String modelPath = "/home/azlan/git/prismgames/Prismfiles/";
		String transPath = "/home/azlan/git/prismgames/IOFiles/";
		String stratPath = "/home/azlan/git/prismgames/IOFiles/";
		
		//create a simulator
		SynthesisSimulator syn = new SynthesisSimulator();
		
		//set path
		syn.setPath(propPath, modelPath, transPath, stratPath);
		
		//set requirements
		syn.setNumObjective(4);
		syn.setObjective(0, "cost", 90, "<");
		syn.setObjective(1, "time", 1000, "<");
		syn.setObjective(2, "reliability", 0.9, ">");
		syn.setObjective(3, "availability", 0.9, ">");
		
		//generate properties specification
		
		//generate model specification
		
		//simulate
	}

}
