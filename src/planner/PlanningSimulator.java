package planner;

public class PlanningSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String singlePath = "/home/azlan/git/PrismGames/Prismfiles/singlemodel.prism";
		String seqPath = "/home/azlan/git/PrismGames/Prismfiles/seqmodel.prism";
		String condPath = "/home/azlan/git/PrismGames/Prismfiles/condmodel.prism";
		String parPath = "/home/azlan/git/PrismGames/Prismfiles/parmodel.prism";
		
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propSBSPlanner.props";
		
		String transPath = "/home/azlan/git/PrismGames/IOFiles/transitions.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strategies.txt";
		String actLabelPath = "/home/azlan/git/PrismGames/IOFiles/labels.txt";
		
		int pattern = 1;	//0-single, 1-sequential, 2-conditional, 3-parallel
		int numNode = 2;
		int numofService = 20;
		int numofResource = 2;
		
		//define the values for the requirement
		int durR=100, costR=50;
		double relR=0.9, wcostR=0.3, wdurR=0.3, wrelR=0.4;
		
		//Create a model generator instance
		ModelGenerator mdg = new ModelGenerator();
	
		//Create a SG-Planner instance
		StochasticPlanner sp = new StochasticPlanner();
		
		//Create a strategy extraction instance
		StrategyExtraction se = new StrategyExtraction();
		
		
		//create the model and synthesize according to a pattern
		if (pattern==0) {
			if(numNode <= 1) {
				//set all the relevant parameters
				System.out.println("Solving single node...");
				mdg.setParamsNames("p1", "p2", "planner", "environment");
				mdg.setUpperBounds(numNode, numofService, numofResource);
				mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
				mdg.setAllRandomServProfiles();
				mdg.setValuesStatus(false); 
				mdg.setPattern(pattern);
				
				//create the specifications
				mdg.setModelPath(singlePath);		
				mdg.setPropPath(propPath);
				mdg.generateSGModel(0);
				System.out.println("Model specification has been generated...");
				mdg.generateProperties();
				System.out.println("Properties specification has been generated...");
				mdg.createServParams();
				mdg.setReqParamswithValues();
				mdg.setServParamswithValues();
				
				//synthesize and extract
				sp.initiatePlanner();
				sp.parseModelandProperties(singlePath, propPath);
				sp.setUndefinedValues(mdg.getDefinedValues());
				sp.checkInitialModel();
				sp.exportTrans(transPath);
				sp.exportStrategy(stratPath);
				
			}
			else
				System.out.println("Require number of node to be 1 for single node...");	
		
		}
		
		else if (pattern==1) {
			//for sequential
			
			//configuring model parameters and values
			System.out.println("Solving sequential pattern...");
			mdg.setValuesStatus(false); //true-create model with values, false-create model without values (later stage)
			mdg.setPattern(pattern);
			mdg.setParamsNames("p1", "p2", "planner", "environment");
			mdg.setUpperBounds(numNode, numofService, numofResource);
		
			
			//create the specifications
			System.out.println("Generating model and properties specifications...");
			mdg.setModelPath(seqPath);										
			mdg.setPropPath(propPath);
			mdg.generateSGModel(0);
			mdg.generateProperties();
						
			//export the actions (for strategy extraction)
			System.out.println("Exporting action labels...");
			mdg.exportActionLabels(actLabelPath);
			
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
			sp.parseModelandProperties(seqPath, propPath);
			sp.setUndefinedValues(mdg.getDefinedValues());
			sp.checkInitialModel();
			
			//exporting
			System.out.println("Exporting transitions and strategies...");
			sp.exportTrans(transPath);
			sp.exportStrategy(stratPath);
			
			//extraction
			//se.setPath(transPath, stratPath, actLabelPath);
			//se.readSingleActionLabelFile();
			//se.readTransitionFile();
			//se.readMultiStrategiesProfile();	
			//se.findSingleDecision();
		}
		
		else if (pattern==2) {
			//for conditional
			//set all the relevant parameters
			System.out.println("Solving conditional pattern...");
			mdg.setParamsNames("p1", "p2", "planner", "environment");
			mdg.setUpperBounds(numNode, numofService, numofResource);
			mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
			mdg.setAllRandomServProfiles();
			mdg.setValuesStatus(false); 
			mdg.setPattern(pattern);
			
			//create the specifications
			mdg.setModelPath(condPath);										
			mdg.setPropPath(propPath);
			mdg.generateSGModel(0);
			System.out.println("Model specification has been generated...");
			mdg.generateProperties();
			System.out.println("Properties specification has been generated...");
			
			
			//assign parameters with values
			mdg.createServParams();
			mdg.setReqParamswithValues();
			mdg.setServParamswithValues();
			
			//synthesize and extract the strategy
			sp.initiatePlanner();
			sp.parseModelandProperties(condPath, propPath);
			sp.setUndefinedValues(mdg.getDefinedValues());
			sp.checkInitialModel();
			sp.exportTrans(transPath);
			sp.exportStrategy(stratPath);
					
		}
		else if (pattern==3) {
			//for parallel
			//set all the relevant parameters
			System.out.println("Solving parallel pattern...");
			mdg.setParamsNames("p1", "p2", "planner", "environment");
			//use a single node pattern which will then be replicated
			mdg.setPattern(3);
			mdg.setUpperBounds(numNode, numofService, numofResource);
			
			//prepare the values for requirement. No issue since a single requirement
			mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
			
			//prepare a set of values for parallel structure, even though the upperbound is limited to single
			//mdg.setParofAllRandomServProfiles(numNode);	
			mdg.setAllRandomServProfiles();
			
			//create multiple specifications to be executed in parallel
			mdg.setValuesStatus(false);
			String[] multiModelPath = new String[numNode];
			String[] multiPropPath = new String[numNode];
			for (int n=0; n < numNode; n++) {
				multiModelPath[n] = "/home/azlan/git/PrismGames/Prismfiles/parmodel"+n+".prism";									
				multiPropPath[n] = "/home/azlan/git/PrismGames/Prismfiles/propSBSPlanner"+n+".props";
			}
			System.out.println("Paths have been created...");
			
			for (int n=0; n < numNode; n++) {
				mdg.setModelPath(multiModelPath[n]);										
				mdg.generateSGModel(n);	
				mdg.setPropPath(multiPropPath[n]);
				mdg.generateProperties();
			}
			System.out.println("Model and properties specification have been generated...");
		
			//prepare the constant parameters for multiple specifications
			//mdg.createValueInstances(numNode);
			
			
			//assign parameters with values
			mdg.createServParams();
			mdg.setReqParamswithValues();
			mdg.setServParamswithValues();
			
			//for(int n=0; n < numNode; n++) {
			//	mdg.setParReqParamswithValues(n);
			//	mdg.setParServParamswithValues(n);
			//}
			//synthesis process
			sp.initiatePlanner();
			for(int n=0; n < numNode; n++) {
				sp.parseModelandProperties(multiModelPath[n], multiPropPath[n]);
				sp.setUndefinedValues(mdg.getDefinedValues());
				//sp.setUndefinedValues(mdg.getDefinedValuesforPar(n));
				sp.checkInitialModel();
				//sp.exportTrans();
				//sp.exportStrategy();
			}
			
			
			//need to get the data and partition them
			
			//execute multiple synthesis 
		//	for (int n=0; n < numNode; n++) {
		//		mdg.setModelPath(pathCondition);										
		//		mdg.generateSGModel();	
		//	}
		}
		else
			System.out.println("the value for the pattern type is invalid....");
			
		
		
		
		

	}

}
