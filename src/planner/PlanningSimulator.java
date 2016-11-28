package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PlanningSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//define parameters for the simulation
		int simCycle = 10;
		long time[] = new long[simCycle]; //log the execution time
		TimeMeasure tm = new TimeMeasure(); //create the time instance
		boolean statusRes[] = new boolean[simCycle]; //log the synthesis status
		
		//define paths for I/O files
		String singlePath = "/home/azlan/git/PrismGames/Prismfiles/singlemodel.prism";
		String seqPath = "/home/azlan/git/PrismGames/Prismfiles/seqmodel.prism";
		String condPath = "/home/azlan/git/PrismGames/Prismfiles/condmodel.prism";
		//String parPath = "/home/azlan/git/PrismGames/Prismfiles/parmodel.prism";
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propSBSPlanner.props";
		String transPath = "/home/azlan/git/PrismGames/IOFiles/transitions.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strategies.txt";
		//String actLabelPath = "/home/azlan/git/PrismGames/IOFiles/labels.txt";
		
		//define parameters for the model
		int pattern = 0;	//0-single, 1-sequential, 2-conditional, 3-parallel
		int numNode = 1;	//set the number of node/task/activity in the compositional structure
		int numofService = 200;	//set the number of services per each node
		int numofResource = 2;	//set the number of behavior per each service
		
		//define values for the requirement parameters (assuming as global requirements)
		int durR=100, costR=50;
		double relR=0.9, wcostR=0.3, wdurR=0.3, wrelR=0.4;
		
		//begin the simulation
		for(int m=0; m < simCycle; m++) {
			//Create a model generator instance
			ModelGenerator mdg = new ModelGenerator();
		
			//Create a SG-Planner instance
			StochasticPlanner sp = new StochasticPlanner();
			
			//Create a strategy extraction instance
			StrategyExtraction se = new StrategyExtraction();
			
			String patName = null;
			
			//create the model and synthesize according to a pattern
			if (pattern==0) {
				if(numNode <= 1) {
					patName = "single";		
					//configuring model parameters and values
					System.out.println("Solving single node...");
					mdg.setValuesStatus(false); //true-create model with values, false-create model without values (later stage)
					mdg.setPattern(pattern);
					mdg.setParamsNames("p1", "p2", "planner", "environment");
					mdg.setUpperBounds(numNode, numofService, numofResource);
					
					//create the specifications
					System.out.println("Generating model and properties specifications...");
					mdg.setModelPath(singlePath);										
					mdg.setPropPath(propPath);
					mdg.generateSGModel(0);
					mdg.generateProperties();
								
					//export the actions (for strategy extraction)
					//System.out.println("Exporting action labels...");
					//mdg.exportActionLabels(actLabelPath);
					
					//creating and assigning values to parameters
					System.out.println("Creating and assigning values to parameters...");
					mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
					mdg.setAllRandomServProfiles();
					mdg.createServParams();
					mdg.setReqParamswithValues();
					mdg.setServParamswithValues();
					
					//synthesis
					System.out.println("Synthesizing model...");
					sp.initiatePlanner();
					sp.parseModelandProperties(singlePath, propPath);
					sp.setUndefinedValues(mdg.getDefinedValues());
					sp.checkInitialModel();
					
					//exporting
					System.out.println("Exporting transitions and strategies...");
					sp.exportTrans(transPath);
					sp.exportStrategy(stratPath);
					
					if (sp.getSynthesisStatus()) {
						//extraction
						se.setPath(transPath, stratPath);
						se.setNumofDecision(numNode); //to control the searching for solution
						se.setActionLabels(mdg.getActionLabels());
						se.readTransitionFile();
						se.readMultiStrategiesProfile();	
						se.findSolutions();
					}
					else
						System.out.println("Synthesis results in false.....");
					
					//record the synthesis status
					statusRes[m] = sp.getSynthesisStatus();
					//stop the timer
		 			tm.stop();
		 			//record the duration
		 			time[m] = tm.getDuration();
				}
				else
					System.out.println("Require number of node to be 1 for single node...");	
			
			}
			
			else if (pattern==1) {
				//for sequential
				patName = "sequential";	
				//configuring model parameters and values
				System.out.println("Solving sequential pattern...");
				mdg.setValuesStatus(false); //true-create model with values, false-create model without values (later stage)
				mdg.setPattern(pattern);
				mdg.setParamsNames("p1", "p2", "planner", "environment");
				mdg.setUpperBounds(numNode, numofService, numofResource);
			
				//export the actions (for strategy extraction)
				//System.out.println("Exporting action labels...");
				//mdg.exportActionLabels(actLabelPath);
				
				//creating and assigning values to parameters
				System.out.println("Creating and assigning values to parameters...");
				mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
				mdg.setAllRandomServProfiles();
				mdg.createServParams();
				mdg.setReqParamswithValues();
				mdg.setServParamswithValues();
				
				//create the specifications
				System.out.println("Generating model and properties specifications...");
				mdg.setModelPath(seqPath);										
				mdg.setPropPath(propPath);
				mdg.generateSGModel(0);
				mdg.generateProperties();
				
				//synthesis
				System.out.println("Synthesizing model...");
				sp.initiatePlanner();
				sp.parseModelandProperties(seqPath, propPath);
				sp.setUndefinedValues(mdg.getDefinedValues());
				sp.checkInitialModel();
				
				//exporting
				System.out.println("Exporting transitions and strategies...");
				sp.exportTrans(transPath);
				sp.exportStrategy(stratPath);
				
				if (sp.getSynthesisStatus()) {
					//extraction
					se.setPath(transPath, stratPath);
					se.setNumofDecision(numNode); //to control the searching for solution
					se.setActionLabels(mdg.getActionLabels());
					se.readTransitionFile();
					se.readMultiStrategiesProfile();	
					se.findSolutions();
				}
				else
					System.out.println("Synthesis results in false.....");
				
				//record the synthesis status
				statusRes[m] = sp.getSynthesisStatus();
				//stop the timer
	 			tm.stop();
	 			//record the duration
	 			time[m] = tm.getDuration();
			}
			
			else if (pattern==2) {
				//for conditional
				patName = "conditional";	
				//configuring model parameters and values
				System.out.println("Solving conditional pattern...");
				mdg.setValuesStatus(false); //true-create model with values, false-create model without values (later stage)
				mdg.setPattern(pattern);
				mdg.setParamsNames("p1", "p2", "planner", "environment");
				mdg.setUpperBounds(numNode, numofService, numofResource);
			
				
				//create the specifications
				System.out.println("Generating model and properties specifications...");
				mdg.setModelPath(condPath);										
				mdg.setPropPath(propPath);
				mdg.generateSGModel(0);
				mdg.generateProperties();
							
				//export the actions (for strategy extraction)
				//System.out.println("Exporting action labels...");
				//mdg.exportActionLabels(actLabelPath);
				
				//creating and assigning values to parameters
				System.out.println("Creating and assigning values to parameters...");
				mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
				mdg.setAllRandomServProfiles();
				mdg.createServParams();
				mdg.setReqParamswithValues();
				mdg.setServParamswithValues();
				
				//synthesis
				System.out.println("Synthesizing model...");
				sp.initiatePlanner();
				sp.parseModelandProperties(condPath, propPath);
				sp.setUndefinedValues(mdg.getDefinedValues());
				sp.checkInitialModel();
				
				//exporting
				System.out.println("Exporting transitions and strategies...");
				sp.exportTrans(transPath);
				sp.exportStrategy(stratPath);
						
				if (sp.getSynthesisStatus()) {
					//extraction
					se.setPath(transPath, stratPath);
					se.setNumofDecision(numNode); //to control the searching for solution
					se.setActionLabels(mdg.getActionLabels());
					se.readTransitionFile();
					se.readMultiStrategiesProfile();	
					se.findSolutions();
				}
				else
					System.out.println("Synthesis results in false.....");
				
				//record the synthesis status
				statusRes[m] = sp.getSynthesisStatus();
				//stop the timer
	 			tm.stop();
	 			//record the duration
	 			time[m] = tm.getDuration();
						
			}
			else if (pattern==3) {
				//for parallel
				patName = "parallel";
				
				//record the start time
				tm.start();
				
				//configuring model parameters and values
				System.out.println("Solving parallel pattern...");
				mdg.setValuesStatus(false); //true-create model with values, false-create model without values (later stage)
				mdg.setPattern(pattern);
				mdg.setParamsNames("p1", "p2", "planner", "environment");
				mdg.setUpperBounds(numNode, numofService, numofResource);
						
				//creating and assigning values to parameters
				System.out.println("Creating and assigning values to parameters...");
				mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
				mdg.setAllRandomServProfiles();
				mdg.createServParams();
				mdg.setReqParamswithValues();
				mdg.setServParamswithValues();
				
				//create multiple specifications to be executed in parallel
				System.out.println("Creating multiple paths for models and properties specifications...");
				String[] multiModelPath = new String[numNode];
				String[] multiPropPath = new String[numNode];
				for (int n=0; n < numNode; n++) {
					multiModelPath[n] = "/home/azlan/git/PrismGames/Prismfiles/parmodel"+n+".prism";									
					multiPropPath[n] = "/home/azlan/git/PrismGames/Prismfiles/propSBSPlanner"+n+".props";
				}
				//System.out.println("Paths have been created...");
				System.out.println("Generating multiple models and properties specifications...");
				for (int n=0; n < numNode; n++) {
					mdg.setModelPath(multiModelPath[n]);										
					mdg.generateSGModel(n);	
					mdg.setPropPath(multiPropPath[n]);
					mdg.generateProperties();
				}
				
				//creating and assigning values to parameters
				System.out.println("Creating and assigning values to parameters...");
				mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
				mdg.setAllRandomServProfiles();
				mdg.createServParams();
				mdg.setReqParamswithValues();
				mdg.setServParamswithValues();
				
				//synthesis
				System.out.println("Synthesizing model...");
				//preparing transition and strategies file
				String[] multiTransPath = new String[numNode];
				String[] multiStratPath = new String[numNode];
				for (int n=0; n < numNode; n++) {
					multiTransPath[n] = "/home/azlan/git/PrismGames/Prismfiles/trans"+n+".prism";									
					multiStratPath[n] = "/home/azlan/git/PrismGames/Prismfiles/strat"+n+".props";
				}
				sp.initiatePlanner();
				for (int n=0; n < numNode; n++) {
					sp.parseModelandProperties(multiModelPath[n], multiPropPath[n]);
					sp.setUndefinedValues(mdg.getDefinedValues());
					sp.checkInitialModel();
					
					//exporting
					System.out.println("Exporting transitions and strategies of node "+n);
					sp.exportTrans(multiTransPath[n]);
					sp.exportStrategy(multiStratPath[n]);
					
					if (sp.getSynthesisStatus()) {
						//extraction
						se.setPath(multiTransPath[n], multiStratPath[n]);
						se.setNumofDecision(numNode); //to control the searching for solution
						se.setActionLabels(mdg.getActionLabels());
						se.readTransitionFile();
						se.readMultiStrategiesProfile();	
						se.findParSolutions(n);
					}
					else
						System.out.println("Synthesis results in false.....");
				} 
				
				//record the synthesis status
				statusRes[m] = sp.getSynthesisStatus();
				//stop the timer
	 			tm.stop();
	 			//record the duration
	 			time[m] = tm.getDuration();
	 		
			}//end of if for parallel
			else
				System.out.println("the value for the pattern type is invalid....");
			
			try {
				analyzePerformanceData(time, simCycle, numNode, numofService, statusRes, patName);
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
	}//end of main
	
    public static void analyzePerformanceData(long time[], int cycle, int numNode, int numofS, boolean statusRes[], String pattern) throws FileNotFoundException {
   	 File outfile = new File("/home/azlan/git/PrismGames/IOFiles/log"+pattern+"_"+numNode+"_"+numofS+".txt");

        PrintWriter pw = new PrintWriter(outfile);

       long total = 0;
       int maxR=9;
       int countSame=0, countDif=0, same=-1;
       int countA[] = new int[maxR];
       int countB[] = new int[maxR];
       int val=0;
       pw.println("recorded data: cycle, time, status");
       for(int k=0; k < cycle; k++) {
    	   pw.println(" "+k+" "+time[k]+" "+statusRes[k]);
 		   total +=time[k];
       }
       pw.println();
       long avg = (total/cycle);
       pw.println("Average: "+avg);
 		
       pw.close();
    }
    
    public static int getResourceId(String[] result, int index) {
   	 
   	 int id=-1;
   	 
   	 if(result[index].equalsIgnoreCase("NODE0")) id = 0;
   	 else if (result[index].equalsIgnoreCase("NODE1"))	id = 1;
   	 else if (result[index].equalsIgnoreCase("NODE2"))	id = 2;
   	 else if (result[index].equalsIgnoreCase("NODE3"))	id = 3;
   	 else if (result[index].equalsIgnoreCase("NODE4"))	id = 4;
   	 else if (result[index].equalsIgnoreCase("NODE5"))	id = 5;
   	 else if (result[index].equalsIgnoreCase("NODE6"))	id = 6;
   	 else if (result[index].equalsIgnoreCase("NODE7"))	id = 7;
   	 else
   		 id = 8;
   	 
   	 return id;
    }

}//end of class
