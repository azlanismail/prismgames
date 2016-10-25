package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import explicit.Model;
import explicit.PrismExplicit;
import explicit.SMGModelChecker;
import explicit.CompositionalSMGModelChecker;
import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismComponent;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import prism.PrismSettings;
import prism.Result;
import simulator.SimulatorEngine;
import strat.Strategy;


public class CompositionalMultiPlanner {
	//Classes from Prism-games
		PrismLog mainLog;
		PrismExplicit prismEx;
		PrismComponent prismCom;
		Prism prism;
		Values vm, vp;
		ModulesFile modulesFile;
		PropertiesFile propertiesFile;
		SimulatorEngine simEngine;
		Model model;
		Result rsProb, rsRwd1, rsRwd2, rsRwd3, rsRwd4, rsCSMG, rsComp, rsMulti1, rsMulti2;
		Strategy strategy;
		SMGModelChecker smg;
		CompositionalSMGModelChecker csmg;
		PrismSettings ps;
		StrategyExtraction ste;
		ConfigurationPlanner conf;
		
		//Defining File Inputs/Outputs
		String logPath = "./myLog.txt";
		String laptopPath = "C:/Users/USER/git/MultiPlanner/PlanningComp2/";
		String desktopPath = "H:/git/MultiPlanner/PlanningComp2/";
		String linuxPath = "/home/azlan/git/PrismGames/";
		String mainPath = linuxPath;
		String modelPath = mainPath+"Prismfiles/compCollaborateModel_v23.prism";
		String propPath = mainPath+"Prismfiles/propCloudAdaptive.props";
		String modelConstPath = mainPath+"IOFiles/ModelConstants.txt";
		String propConstPath = mainPath+"IOFiles/PropConstants.txt";
		String stratPath1 = mainPath+"IOFiles/strategyInitial";
		String transPath1 = mainPath+"IOFiles/transitionInitial";
		String stratPath2 = mainPath+"IOFiles/strategy";
		String transPath2 = mainPath+"IOFiles/transition";
		String mappingPath = mainPath+"IOFiles/mapping";
		String actionLabelAPath = mainPath+"IOFiles/actionlabelA";
	    String actionLabelBPath = mainPath+"IOFiles/actionlabelB";
	        

		//Defining properties for the planner
		private int stage;
		
		public CompositionalMultiPlanner(int sg) {
			this.stage = sg;
			initiatePlanner();
			//initializeServiceProfile();
			//setDelay();
		}
		
		private void initiatePlanner(){
			mainLog = new PrismFileLog(logPath);
	        prism = new Prism(mainLog , mainLog);
	        prismEx = new PrismExplicit(mainLog, prism.getSettings());
	       
	        //for parsing model and property file
	    	try {
	    			modulesFile = prism.parseModelFile(new File(modelPath));
	    			propertiesFile = prism.parsePropertiesFile(modulesFile, new File(propPath));
			} catch (FileNotFoundException | PrismLangException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	    	
	    	//for assigning values of constants
	    	conf = new ConfigurationPlanner();
	    	   	
	    	//I need to access SMGModelChecker directly to manipulate the strategy
	    	try {
	    		smg = new SMGModelChecker(prism);
	    		smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
				csmg = new CompositionalSMGModelChecker(prism, modulesFile, propertiesFile, prism.getSimulator());
			} catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	//for generating and extracting strategy
	    	ste = new StrategyExtraction(mappingPath, transPath2, stratPath2, actionLabelAPath, actionLabelBPath);	
	    }
			
		public void setApplicationRequirements(int id, int cpuCores, double cpuLoads, double cpuSpeed, int totalMemory, int freeMemory){
			conf.setAppRequirements(id, cpuCores, cpuLoads, cpuSpeed, totalMemory, freeMemory);
		}
		
		public void setNodeCapabilities(int id, String name, int cpuCores, double cpuSpeed, double cpuLoads, int totalMemory, int freeMemory, String location){
			conf.setNodeCapabilities(id, name, cpuCores, cpuSpeed, cpuLoads, totalMemory, freeMemory, location);
		}

		public void buildModelbyPrismEx() throws PrismLangException, PrismException
		{
			//assign constants values to the model 
	    	modulesFile.setUndefinedConstants(conf.getDefinedValues());
	 		
	    	//build the model
		    model = prismEx.buildModel(modulesFile, prism.getSimulator());
		   
		}
		
		public void checkModelbyPrismEx() throws PrismLangException, PrismException
		{
			csmg.setComputeParetoSet(false);
		    csmg.setGenerateStrategy(true);
		    
		    System.out.println("Planning is based on compositional games");
			rsProb = smg.check(model, propertiesFile.getProperty(0));
			double resProb = (double)rsProb.getResult();
			
			if(resProb > 0.0) {
				rsRwd1 = smg.check(model, propertiesFile.getProperty(1)); //max reward of cpu speed of G0
				rsRwd2 = smg.check(model, propertiesFile.getProperty(2)); //max reward of cpu load of G0
				rsRwd3 = smg.check(model, propertiesFile.getProperty(3)); //max reward of cpu speed of G1
				rsRwd4 = smg.check(model, propertiesFile.getProperty(4)); //max reward of cpu load of G1
				
				//assign the UB rewards to the compositional properties
				conf.setUpperBoundsMultiObjectives((double)rsRwd1.getResult(), (double)rsRwd2.getResult(), (double)rsRwd3.getResult(), (double)rsRwd4.getResult());
				propertiesFile.setUndefinedConstants(conf.getDefinedProperties());
				rsComp= csmg.check(propertiesFile.getProperty(5));
				boolean compStatus = (boolean) rsComp.getResult();
				if (compStatus) {
					rsMulti1 = smg.check(model, propertiesFile.getProperty(8));
					rsMulti2 = smg.check(model, propertiesFile.getProperty(9));
					//resultCSMG = csmg.check(propertiesFile.getProperty(9));
				}
			}
		    
			//System.out.println("Planning is based on compositional games");
			//resultCSMG = csmg.check(propertiesFile.getProperty(0));
			
		}	
	    
	    public void outcomefromModelChecking()
	    {
	    	 System.out.println("The result from model checking (SMG) is :"+ rsProb.getResult()); 
	    	 System.out.println("The result from model checking (SMG) is :"+ rsRwd1.getResult()); 
	    	 System.out.println("The result from model checking (SMG) is :"+ rsRwd2.getResult()); 
	    	 System.out.println("The result from model checking (SMG) is :"+ rsRwd3.getResult()); 
	    	 System.out.println("The result from model checking (SMG) is :"+ rsRwd4.getResult()); 
	    	 System.out.println("The result from model checking (SMG) is :"+ rsComp.getResult());
	    	 System.out.println("The result from model checking (SMG) is :"+ rsMulti1.getResult());
	    	 System.out.println("The result from model checking (SMG) is :"+ rsMulti2.getResultString());
	    	// System.out.println("The result from model checking (SMG) is :"+ resultCSMG.getResult()); 
	    }
	    
	    public void outcomefromModelBuilding()
	    {
	    	System.out.println("Number of states (Model Building) :"+model.getNumStates());
	    	System.out.println("Number of transitions (Model Building) :"+model.getNumTransitions());
	    }
	       
	     
	    /**
	     * Objective: It extracts the transitions which have been synthesized
	     * @throws PrismException
	     */
	    public void exportTrans() throws PrismException
	    {
	    	if (this.stage == 0){
	    		File transFile = new File(transPath1);
	       		model.exportToPrismExplicitTra(transFile);
	    	}else{
	    		File transFile = new File(transPath2);
	    		model.exportToPrismExplicitTra(transFile);
	    	}
	    	
	    }
	    
	    
	   /**
	    * Objective: To export the synthesize strategy into an external file
	    * @param straFile
	    */
	    public void exportStrategy()
	    {
	    	//assign the pointer from SMGModelChecker to strategy
	    	System.out.println("exporting the strategy");
	    	strategy =  rsCSMG.getStrategy(); // smc.getStrategy();
	    	
	    	if (this.stage == 0) {
	    	//export to .adv file
	    	strategy.exportToFile(stratPath1);
	    	}else {
	    		strategy.exportToFile(stratPath2);
	    	}
	    	
	    }
	    
	       
	    public void getAdaptStrategy() 
	    {   
	    	try {
			//ste.readMappingFile();
			ste.readActionLabelFile();
			ste.displayActionLabelAList();
			ste.displayActionLabelBList();
			//ste.getAllMappingList();
			ste.readTransitionFile();
			//ste.getAllTrasitionList();
			ste.readStrategyFile();
			//ste.getAllStrategyList();
			
			ste.findDecision();
			ste.displayStrategies();
			
			}
	    	catch(IllegalArgumentException ie) {
	    		ie.printStackTrace();
	    	}
	    	catch(FileNotFoundException e) {
	    		e.printStackTrace();
	    	}
	    }
	   
	        
	    /**
	     * Objective: To generate the adaptation plan
	     */
	    public void generate()
	    {        
	    	
	    	 
	         //build and check the model
	         try {
				buildModelbyPrismEx();
				checkModelbyPrismEx();
			} catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	              
	         outcomefromModelBuilding();
	         outcomefromModelChecking();
	        
	        try {
				exportTrans();
			} catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //strategy related process
	     //  	exportStrategy();
	       	
	       	//get the adaptation strategy
	   //    	try {
 		//		getAdaptStrategy();
 		//	} 
 		//	catch (IllegalArgumentException e) {
 		//		e.printStackTrace();
 		//	}
	    }//end of synthesis
	    
	    public int getMaxResource() {
	    	return conf.getMaxResource();
	    }
	    
	    public String getDecision(int decId) {
	    	String nodeName;
	    	
	    	ste.setNodeIdList(conf.getNodeIdList());
	    	nodeName = ste.getDecision(decId);
	    	//ste.setNodeIdList(conf.getNodeIdList());
			System.out.println("Selected node name is "+nodeName);
		//	System.out.println("Node name from substrategy 2 is "+ste.getSelectedNodeIdB());
	      
	    	return nodeName;
	    }
	     
	     public static void main(String[] args) {
	 		// TODO Auto-generated method stub

	    	//0-means the initial stage
	    	//1-means the adaptation stage
	    	int stage = 1;
	 		CompositionalMultiPlanner plan = new CompositionalMultiPlanner(stage); 		
			
	 		
	 		plan.setApplicationRequirements(0, 1, 30.0, 300.0, 20, 20);
	 		plan.setApplicationRequirements(1, 1, 25.0, 600.0, 20, 20);
	 		
	 		plan.setNodeCapabilities(0, "NODE0", 1, 200, 0.3, 1000, 500, "LOC0");
	 		plan.setNodeCapabilities(1, "NODE1", 1, 200, 0.3, 1000, 500, "LOC1");
	 		plan.setNodeCapabilities(2, "NODE2", 1, 2500, 0.5, 1000, 500, "LOC2");
	 		plan.setNodeCapabilities(3, "NODE3", 1, 2500, 0.5, 1000, 500, "LOC3");
	 		plan.setNodeCapabilities(4, "NODE4", 1, 2500, 0.7, 1000, 500, "LOC4");
	 		plan.setNodeCapabilities(5, "NODE5", 1, 2500, 0.7, 1000, 500, "LOC5");
	 		plan.setNodeCapabilities(6, "NODE6", 1, 2500, 0.7, 1000, 500, "LOC6");
	 		plan.setNodeCapabilities(7, "NODE7", 1, 2500, 0.7, 1000, 500, "LOC7");
	 		//Random rand = new Random();
	 		//int serviceType = -1;
	 		int cycle =1;
	 		//int goalType = 4;
	 		//int retry = 1;
	 		long time[] = new long[cycle];
	 		TimeMeasure tm = new TimeMeasure();
	 		
	 		for (int i=0; i < cycle; i++)
	 	    {
	 			tm.start();
	 			System.out.println("number of cycle :"+i);
	 			plan.generate();
	 			//plan.getDecision(0);
	 			tm.stop();
	 			time[i] = tm.getDuration();
	 	    }
	 		
	 		long total = 0;
	 		for(int k=0; k < cycle; k++)
	 			total +=time[k];
	 		
	 		System.out.println("total is "+total);
	 		long avg = (total/cycle);
	 		System.out.println("The average time is "+avg);
	 	}
}
