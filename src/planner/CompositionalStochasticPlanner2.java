package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
import simulator.GenerateSimulationPath;
import simulator.Path;
import strat.Strategy;


public class CompositionalStochasticPlanner2 {
	//Classes from Prism-games
		PrismLog mainLog;
		PrismExplicit prismEx;
		PrismComponent prismCom;
		Prism prism;
		Values vm, vp;
		ModulesFile modulesFile;
		PropertiesFile propertiesFile;
		SimulatorEngine simEngine;
		GenerateSimulationPath simPath;
		Path path;
		Model model;
		Result rsProb, rsRwd1, rsRwd2, rsRwd3, rsRwd4, rsCSMG, rsComp, rsMulti1, rsMulti2, rsMultiComp;
		Strategy stratComp, stratMultiComp, stratMulti1, stratMulti2;
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
		String modelPath = mainPath+"Prismfiles/compCollaborateModel_v26.prism";
		String propPath = mainPath+"Prismfiles/propCloudAdaptive_v1.props";
		//String modelConstPath = mainPath+"IOFiles/ModelConstants.txt";
		//String propConstPath = mainPath+"IOFiles/PropConstants.txt";
		String stratCompPath = mainPath+"IOFiles/stratComp.txt";
		String stratMultiCompPath = mainPath+"IOFiles/stratMultiComp.txt";
		String stratMulti1Path = mainPath+"IOFiles/stratMulti1.txt";
		String stratMulti2Path = mainPath+"IOFiles/stratMulti2.txt";
		String transPath = mainPath+"IOFiles/transition.txt";
		//String stratPath1 = mainPath+"IOFiles/strategyInitial";
		//String transPath1 = mainPath+"IOFiles/transitionInitial";
		//String stratPath2 = mainPath+"IOFiles/strategy";
		//String transPath2 = mainPath+"IOFiles/transition";
		//String mappingPath = mainPath+"IOFiles/mapping";
		String actionLabelAPath = mainPath+"IOFiles/actionlabelA";
	    String actionLabelBPath = mainPath+"IOFiles/actionlabelB";
	    
	    //Defining the type of property
	    int maxCpuSpeedG0=1, maxCpuLoadG0=2, maxCpuSpeedG1=3, maxCpuLoadG1=4;
	    int compImpli=5;
	    int MultiObj1=8;
	    // int MultiObj2=0; not needed at the moment
	    int compMultiObj=10;

		//Defining internal attributes for the planner
		private int stage;
		private boolean synthesisStatus = false;
		

		
		public CompositionalStochasticPlanner2() {
			//this.stage = sg;
			initiatePlanner();
			//initializeServiceProfile();
			//setDelay();
		}
		
		public void setModelPath(String path) {
			
		}
		
		public void setPropsPath(String path) {
			
		}
		
		public void initiatePlanner(){
			mainLog = new PrismFileLog(logPath);
	        prism = new Prism(mainLog , mainLog);
	        //prismCom = new PrismComponent();
	        simEngine = new SimulatorEngine(prismCom, prism);
	        //simPath = new GenerateSimulationPath(prism.getSimulator(), prism.getLog());
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
	    	
	      	//for extracting strategy
	    	ste = new StrategyExtraction(transPath, stratCompPath, stratMultiCompPath, stratMulti1Path, stratMulti2Path, actionLabelAPath, actionLabelBPath);	
	    }
			
		public void setApplicationRequirements(int id, int cpuCores, double cpuLoads, double cpuSpeed, int totalMemory, int freeMemory){
			conf.setAppRequirements(id, cpuCores, cpuLoads, cpuSpeed, totalMemory, freeMemory);
		}
		
		public void setNodeCapabilities(int id, String name, int cpuCores, double cpuLoads, double cpuSpeed, int totalMemory, int freeMemory, String location){
			conf.setNodeCapabilities(id, name, cpuCores, cpuLoads, cpuSpeed, totalMemory, freeMemory, location);
		}

		public void buildModelbyPrismEx() throws PrismLangException, PrismException
		{
			//assign constants values to the model 
	    	modulesFile.setUndefinedConstants(conf.getDefinedValues());  
	    	//System.out.println("the constants are "+modulesFile.getAllConstants().toString());
	    	//System.out.println("The values "+modulesFile.getConstantValues().toString());
	    	
	    	//modulesFile.setUndefinedConstants(conf.getDefinedValues());
	    	//modulesFile.setUndefinedConstants(vm);
	    	
	    	//build the model
		    model = prismEx.buildModel(modulesFile, prism.getSimulator());
		    //model = prismEx.buildModel(modulesFile, simEngine);
		   
		}
		
		/**
		 * To check and synthesis the model
		 * @throws PrismLangException
		 * @throws PrismException
		 */
		public void checkModelbyPrismEx() throws PrismLangException, PrismException{		
		   		    
		    try {
		    	//create instance for non-compositional multi-objective properties synthesis
		    	smg = new SMGModelChecker(prism);
		    	
		    	//set the constants parameters
		    	modulesFile.setUndefinedConstants(conf.getDefinedValues());  
		    	
		    	System.out.println("Building the model representation.....");
		    	//building a model representation for non-compositional synthesis
			    model = prismEx.buildModel(modulesFile, prism.getSimulator());
		      
			    if(model != null){
			    	System.out.println("Number of states (Model Building) :"+model.getNumStates());
			    	System.out.println("Number of transitions (Model Building) :"+model.getNumTransitions());
			    }
		    	//parse the specifications to smg instance    	
		    	smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
		    	
		    	//create instance for compositional synthesis with parsing the specifications
	    	    csmg = new CompositionalSMGModelChecker(prism, modulesFile, propertiesFile, prism.getSimulator());
			} catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		     
		    csmg.setComputeParetoSet(false);
		    csmg.setGenerateStrategy(true);
		    smg.setComputeParetoSet(false);
		    smg.setGenerateStrategy(true);
		    
		   
		   
		    if (smg.geterrorOnNonConverge() == false) {
		    	System.out.println("Synthesizing expected max rewards.....");
		    	rsRwd1 = smg.check(model, propertiesFile.getProperty(this.maxCpuSpeedG0)); //max reward of cpu speed of G0
		    	rsRwd2 = smg.check(model, propertiesFile.getProperty(this.maxCpuLoadG0)); //max reward of cpu load of G0
		    	rsRwd3 = smg.check(model, propertiesFile.getProperty(this.maxCpuSpeedG1)); //max reward of cpu speed of G1
		    	rsRwd4 = smg.check(model, propertiesFile.getProperty(this.maxCpuLoadG1)); //max reward of cpu load of G1
		    
		    	System.out.println("The result from model checking (SMG) is :"+ rsRwd1.getResult()); 
		    	System.out.println("The result from model checking (SMG) is :"+ rsRwd2.getResult()); 
		    	System.out.println("The result from model checking (SMG) is :"+ rsRwd3.getResult()); 
		    	System.out.println("The result from model checking (SMG) is :"+ rsRwd4.getResult()); 
		    	 
		    	conf.setUpperBoundsMultiObjectives((double)rsRwd1.getResult(), (double)rsRwd2.getResult(), (double)rsRwd3.getResult(), (double)rsRwd4.getResult());   	
		    }
		    else {
			 	System.out.println("Not able to synthesize the expected max rewards, so set to the defaults.....");
			 	conf.setDefaultUpperBoundsMultiObjectives();
		   }
		    		    
		    //set the constant parameters for properties
		    propertiesFile.setUndefinedConstants(conf.getDefinedProperties());
			
		    //need to reset the propertiesFile due to new values for the properties
			smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
			
			System.out.println("Synthesizing compositional games.....");
			boolean synMultiComp=false, synMulti1=false, synMulti2=false;
			//set the default synthesis status to true
			//this.synthesisStatus=true;
			
			//synthesize the compositional of implication
			rsComp=csmg.check(propertiesFile.getProperty(compImpli));
			if ((boolean)rsComp.getResult()) {
				System.out.println("Compositional implication synthesis is success");	
				System.out.println("The result from model checking (SMG) is :"+ rsComp.getResult());
			}else {
				System.out.println("The assumed properties are not satisfied...");
			}
			
			//synthesize the compositional of conjunction
			rsMultiComp = smg.check(model, propertiesFile.getProperty(compMultiObj));
				
			if((boolean)rsMultiComp.getResult()) {
				System.out.println("Compositional multi-objective is success");
				System.out.println("The result from model checking (SMG) is :"+ rsMultiComp.getResult());
				synMultiComp=true;
			}else {
				System.out.println("Perform multi-objective on each component game");
				rsMulti1 = smg.check(model, propertiesFile.getProperty(MultiObj1));

				//rsMulti2 = smg.check(model, propertiesFile.getProperty(MultiObj2));
				System.out.println("The result from model checking (SMG) is :"+ rsMulti1.getResult());
		    	//System.out.println("The result from model checking (SMG) is :"+ rsMulti2.getResult()); 
		    	
		    	//the only reason to cause overall synthesis to return false to the requester
				if (rsMulti1.getResult()!=null && (boolean)rsMulti1.getResult()) {			
					synMulti1=true;
				}	
				else
					synMulti1=false;
			}	
			
			if (synMultiComp | synMulti1 | synMulti2) {
				System.out.println("at least one type of synthesis is success....");
				this.synthesisStatus = true;
			}else {
				System.out.println("All synthesis fail");
				this.synthesisStatus = false;
			}
		}	
	    
		/**
		 * To return the strategy generation status of compositional synthesis
		 * @return
		 */
		public boolean getCompositionalSynthesisStatus() {
			return this.synthesisStatus;
		}
			       
	     
	    /**
	     * Objective: It extracts the transitions which have been synthesized
	     * @throws PrismException
	     */
	    public void exportTrans() throws PrismException
	    {
    		File transFile = new File(transPath);
       		model.exportToPrismExplicitTra(transFile);
	    }
	    
	    
	   /**
	    * Objective: To export the synthesize strategy into an external file
	    * @param straFile
	    */
	    public void exportStrategy()
	    {
	    	//assign the pointer from SMGModelChecker to strategy
	    	System.out.println("exporting all strategies profiles...");
	    	
	    	//exporting the strategies for compositional of implication
	    	if(rsComp!=null && (boolean)rsComp.getResult()) {
	    		stratComp = rsComp.getStrategy();
	    		stratComp.exportToFile(stratCompPath);
	    	}
	    	
	    	//exporting the strategies for compositional of conjunction
	    	if(rsMultiComp!=null && (boolean)rsMultiComp.getResult()) {
	    		stratMultiComp = rsMultiComp.getStrategy();
	    		stratMultiComp.exportToFile(stratMultiCompPath);
	    	}
	    	
	    	//exporting the strategies for multi-objective of game 0
	    	if(rsMulti1!=null && (boolean)rsMulti1.getResult()) {
	    		stratMulti1 = rsMulti1.getStrategy();
	    		stratMulti1.exportToFile(stratMulti1Path);
	    	}
	    	
	    	//exporting the strategies for multi-objective of game 1
	    	if(rsMulti2!=null && (boolean)rsMulti2.getResult()) {
	    		stratMulti2 = rsMulti2.getStrategy();
		    	stratMulti2.exportToFile(stratMulti2Path);
	    	}
	    	
	    }
	    
	    public void simulatePath() {
	    	try{
	    		simEngine = prism.getSimulator();
	    		
	    		//simEngine.createNewOnTheFlyPath(modulesFile, model);
	    		path = simEngine.getPath();
	    		//simEngine.initialisePath(path.getCurrentState());
	    		int count=0, maxCount = 10;
	    		
	    		while(true) {
	    			System.out.println("The current state "+path.getCurrentState().toString());
	    			simEngine.automaticTransition();	    		
	    			if (simEngine.isPathLooping() || count == maxCount) break;
	    			count++;
	    		}
	    		System.out.println("The size of the path is "+path.size());
	    		System.out.println("The overall path are "+path.toString());
	    	}
	    	catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    
	    /**
	     * To extract a single strategy
	     */
	    public void extractSingleStrategy() 
	    {   
	    	try {
			ste.readSingleActionLabelFile();
			ste.displayActionLabelAList();
			ste.readTransitionFile();
			ste.readMultiCompfromOneStrategiesProfile();
			
			ste.findSingleDecision();
			ste.displayStrategies();
			
			}
	    	catch(IllegalArgumentException ie) {
	    		ie.printStackTrace();
	    	}
	    	catch(FileNotFoundException e) {
	    		e.printStackTrace();
	    	}
	    }
	
	    
	    
	    public void extractStrategy() 
	    {   
	    	try {
			ste.readActionLabelFile();
			//ste.displayActionLabelAList();
			//ste.displayActionLabelBList();
			ste.readTransitionFile();
			ste.readMultiCompfromOneStrategiesProfile();		
			ste.findMultiDecision();
			//ste.displayStrategies();
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
	     try {
	    	 	//synthesize the model
				checkModelbyPrismEx();
				//export the transitions
				exportTrans();
				//export the strategies
				exportStrategy();
			} catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	           
	       	//get the adaptation strategy
	       	try {
 				extractStrategy();
 				//extractSingleStrategy();
 			} 
 			catch (IllegalArgumentException e) {
 				e.printStackTrace();
 			}
	    }//end of synthesis
	    
	    public int getMaxResource() {
	    	return conf.getMaxResource();
	    }
	    
	    public String getDecision(int decId) {
	    	String nodeName;
	    	
	    	ste.setNodeIdList(conf.getNodeIdList());
	    	nodeName = ste.getDecision(decId);
	    	System.out.println("Selected node name is for decision "+decId+" is: "+nodeName);
			      
	    	return nodeName;
	    }
	     
	     public static void main(String[] args) {
	 		// TODO Auto-generated method stub

	    	//0-means the initial stage
	    	//1-means the adaptation stage
	    	//int stage = 1;
	 		//CompositionalMultiPlanner plan = new CompositionalMultiPlanner(); 
	    	 
	 		
	 		//plan.setApplicationRequirements(0, 1,10.0, 300.0, 20, 20);
	 		//plan.setApplicationRequirements(1, 1,15.0, 600.0, 20, 20);
	 
	 		//plan.setNodeCapabilities(0, "NODE0", 1, 200, 0.3, 1000, 500, "LOC0");
	 		//plan.setNodeCapabilities(1, "NODE1", 1, 200, 0.3, 1000, 500, "LOC1");
	 		//plan.setNodeCapabilities(2, "NODE2", 1, 2500, 0.5, 1000, 500, "LOC2");
	 		//plan.setNodeCapabilities(3, "NODE3", 1, 2500, 0.5, 1000, 500, "LOC3");
	 		//plan.setNodeCapabilities(4, "NODE4", 1, 2500, 0.7, 1000, 500, "LOC4");
	 		//plan.setNodeCapabilities(5, "NODE5", 1, 2500, 0.7, 1000, 500, "LOC5");
	 		//plan.setNodeCapabilities(6, "NODE6", 1, 2500, 0.7, 1000, 500, "LOC6");
	 		//plan.setNodeCapabilities(7, "NODE7", 1, 2500, 0.7, 1000, 500, "LOC7");
	 		
	 		Random rand = new Random();
	 		int cycle = 100;
	 		int maxResource = 8;
	 		//int goalType = 4;		
	 	
	 		TimeMeasure tm = new TimeMeasure();
	 		long time[] = new long[cycle]; //log the execution time
	 		String res0[] = new String[cycle]; //log the selection
	 		String res1[] = new String[cycle]; //log the selection
	 		boolean statusRes[] = new boolean[cycle]; //log the synthesis status
	 		
	 		double cpulApp0, cpulApp1, cpusApp0, cpusApp1; //to randomize values for applications
	 		double cpulRs, cpusRs; //to randomize values for resources
	 		
	 		CompositionalStochasticPlanner2 plan; // = new CompositionalMultiPlanner[cycle]; 
	 		
	 		for (int i=0; i < cycle; i++)
	 	    {		 	
	 			System.out.println("number of cycle :"+i);
	 			plan= new CompositionalStochasticPlanner2();
	 			cpulApp0 = rand.nextInt(20);
	 			cpulApp1 = rand.nextInt(30);		
	 			cpusApp0 = rand.nextInt(300) + 100;
	 			cpusApp1 = rand.nextInt(300) + 100;
	 			
	 			plan.setApplicationRequirements(0, 1,cpulApp0, cpusApp0, 20, 20);
		 		plan.setApplicationRequirements(1, 1,cpulApp1, cpusApp1, 20, 20);
		 		
		 		for(int j=0; j < maxResource; j++) {
		 			cpulRs = rand.nextInt(50) + 10;
		 			cpusRs = rand.nextInt(400) + 600;
		 			plan.setNodeCapabilities(j, "NODE"+j, 1, cpulRs, cpusRs, 1000, 500, "LOC"+j);
		 		}
		 	
	 			tm.start();
	 			plan.generate();
	 			statusRes[i] = plan.getCompositionalSynthesisStatus();
	 			if (statusRes[i]) {
	 				res0[i] = plan.getDecision(0);
	 				res1[i] = plan.getDecision(1);
	 			}else {
	 				res0[i] = "Fail";
	 				res1[i] = "Fail";
	 			}
	 				tm.stop();
	 			time[i] = tm.getDuration();
	 			//plan.simulatePath();
	 	    }//end of for
	 		
	 		
	 		try {
				analyzeData(time, cycle, statusRes, res0, res1);
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
	     
	     public static void analyzeData(long time[], int cycle, boolean statusRes[], String res0[], String res1[]) throws FileNotFoundException {
	    	 File outfile = new File("/home/azlan/git/PrismGames/IOFiles/log.txt");

             PrintWriter pw = new PrintWriter(outfile);

            long total = 0;
            int maxR=9;
            int countSame=0, countDif=0, same=-1;
            int countA[] = new int[maxR];
            int countB[] = new int[maxR];
            int val=0;
            pw.println("recorded data: cycle, time, status, result1, result2, similar");
 	 		for(int k=0; k < cycle; k++) {
 	 			if(res0[k] == res1[k]) {
 	 				countSame+=1;
 	 				same=1;
 	 			}else {
 	 				countDif+=1;
 	 				same=0;
 	 			}
 	 			pw.println(" "+k+" "+time[k]+" "+statusRes[k]+" "+res0[k]+" "+res1[k]+" "+same);
 	 			if (res0[k] != null){
 	 				System.out.println("res0: "+res0[k]);
 	 				val = getResourceId(res0, k);
 	 				System.out.println("res0: "+val);
 	 				countA[val] +=1;
 	 			}
 	 			if (res1[k] != null){
 	 				System.out.println("res1: "+res1[k]);
 	 				val = getResourceId(res1, k);
 	 				System.out.println("res1: "+val);
 	 				countB[val] +=1;
 	 			}
 	 			total +=time[k];
 	 		}
 	 		pw.println();
 	 		for(int j=0; j < countA.length; j++) {
 	 			pw.println(" "+j+" "+countA[j]+" "+countB[j]);	
 	 		}
 	 		pw.println("Same Collab:"+countSame);
 	 		pw.println("Diff Collab:"+countDif);
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
}
