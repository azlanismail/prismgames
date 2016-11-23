package planner;

public class PlanningSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathSingle = "/home/azlan/git/PrismGames/Prismfiles/singlemodel.prism";
		String pathSequential = "/home/azlan/git/PrismGames/Prismfiles/seqmodel.prism";
		String pathCondition = "/home/azlan/git/PrismGames/Prismfiles/condmodel.prism";
		String pathParallel = "/home/azlan/git/PrismGames/Prismfiles/parmodel.prism";
		
		String pathProps = "/home/azlan/git/PrismGames/Prismfiles/propSBSAdaptive.props";
		
		int pattern = 0;	//0-single, 1-sequential, 2-conditional, 3-parallel
		int numNode = 1;
		int numofService = 20;
		int numofResource = 2;
		
		int durR=10, costR=20;
		double relR=0.9, wcostR=0.3, wdurR=0.3, wrelR=0.4;
		
		//====Create a model generator instance==========
		ModelGenerator mdg = new ModelGenerator();
	
		//====Create a SG-Planner instance============
		StochasticPlanner sp = new StochasticPlanner();
		
		//set all the relevant parameters
		mdg.setParamsNames("p1", "p2", "planner", "environment");
		mdg.setUpperBounds(numNode, numofService, numofResource);
		mdg.setUserRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
		mdg.setAllProfiles(numNode, numofService, numofResource);
		
		if (pattern==0) {
			if(numNode <= 1) {
				mdg.setValuesStatus(true); 
				mdg.setPattern(pattern);
				mdg.setPath(pathSingle);										
				mdg.generateSGModel();
				System.out.println("Model has been generated...");
				
				sp.setPaths(pathSingle, pathProps);
				sp.initiatePlanner();
				sp.checkInitialModel();
				
			}
			else
				System.out.println("Require number of node to be 1 for single node...");	
		
		}
		
		else if (pattern==1) {
			//for sequential
			mdg.setValuesStatus(false); //create a model without fix values.
			mdg.setPattern(pattern);
			mdg.setPath(pathSequential);										
			mdg.generateSGModel();
		}
		
		else if (pattern==2) {
			//for conditional
			mdg.setValuesStatus(false); //create a model without fix values.
			mdg.setPattern(pattern);
			mdg.setPath(pathCondition);										
			mdg.generateSGModel();		
		}
		else if (pattern==3) {
			//for parallel
			mdg.setValuesStatus(false); //create a model without fix values.
			mdg.setPattern(pattern);
			for (int n=0; n < numNode; n++) {
				mdg.setPath(pathCondition);										
				mdg.generateSGModel();	
			}
		}
		else
			System.out.println("the value for the pattern type is invalid....");
			
		
		
		
		

	}

}
