package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import explicit.CompositionalSMGModelChecker;
import explicit.Model;
import explicit.PrismExplicit;
import explicit.ProbModelChecker;
import explicit.SMGModelChecker;
import explicit.StateModelChecker;
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
import simulator.GenerateSimulationPath;
import simulator.Path;
import simulator.SimulatorEngine;
import strat.Strategy;

public class StochasticPlanner {

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
	Result rs, rsProb, rsRwd1, rsRwd2, rsRwd3, rsRwd4, rsCSMG, rsComp, rsMulti1, rsMulti2, rsMultiComp;
	Strategy stratComp, stratMultiComp, stratMulti1, stratMulti2;
	SMGModelChecker smg;
	ProbModelChecker stpg;
	CompositionalSMGModelChecker csmg;
	PrismSettings ps;
	StrategyExtraction ste;
	ConfigurationPlanner conf;
	
	//Defining File Inputs/Outputs
	String logPath = "./myLog.txt";
	String modelPath; // = mainPath+"Prismfiles/compCollaborateModel_v26.prism";
	String propPath; // = mainPath+"Prismfiles/propCloudAdaptive_v1.props";
    
    //Defining the type of property
    int maxCpuSpeedG0=1, maxCpuLoadG0=2, maxCpuSpeedG1=3, maxCpuLoadG1=4;
    int compImpli=5;
    int MultiObj1=8;
    // int MultiObj2=0; not needed at the moment
    int compMultiObj=10;

	//Defining internal attributes for the planner
	private int stage;
	private boolean synthesisStatus = false;
	
	public StochasticPlanner() {  }
	
	
	public void initiatePlanner(){
		mainLog = new PrismFileLog(logPath);
        prism = new Prism(mainLog , mainLog);
        simEngine = new SimulatorEngine(prismCom, prism);
        prismEx = new PrismExplicit(mainLog, prism.getSettings());
       
    	//for assigning values of constants
    	//conf = new ConfigurationPlanner();
    	
      	//for extracting strategy
    	//ste = new StrategyExtraction(transPath, stratCompPath, stratMultiCompPath, stratMulti1Path, stratMulti2Path, actionLabelAPath, actionLabelBPath);	
    }
	
	public void parseModelandProperties(String modelPath, String propsPath) {
		 //for parsing model and property file
    	try {
    			modulesFile = prism.parseModelFile(new File(modelPath));
    			propertiesFile = prism.parsePropertiesFile(modulesFile, new File(propsPath));
		} catch (FileNotFoundException | PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void setApplicationRequirements(int id, int cpuCores, double cpuLoads, double cpuSpeed, int totalMemory, int freeMemory){
		conf.setAppRequirements(id, cpuCores, cpuLoads, cpuSpeed, totalMemory, freeMemory);
	}
	
	public void setNodeCapabilities(int id, String name, int cpuCores, double cpuLoads, double cpuSpeed, int totalMemory, int freeMemory, String location){
		conf.setNodeCapabilities(id, name, cpuCores, cpuLoads, cpuSpeed, totalMemory, freeMemory, location);
	}

	public void setUndefinedValues(Values vm) {
		try {
			modulesFile.setUndefinedConstants(vm);
		} catch (PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public void checkModel(int propsId) {		
		    
		 try {
	    	//create instance for non-compositional multi-objective properties synthesis
	    	smg = new SMGModelChecker(prism);
	    	//set the constants parameters
	    	//modulesFile.setUndefinedConstants(conf.getDefinedValues());  
	    	
	    	System.out.println("Building the model representation.....");
	    	//building a model representation for non-compositional synthesis
		    model = prismEx.buildModel(modulesFile, prism.getSimulator());
	      
		    if(model != null){
		    	System.out.println("Number of states (Model Building) :"+model.getNumStates());
		    	System.out.println("Number of transitions (Model Building) :"+model.getNumTransitions());
		    }
	    	//parse the specifications to smg instance    	
	    	smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
	    	
	        //set the status for pareto and strategy generation
		    smg.setComputeParetoSet(false);
		    smg.setGenerateStrategy(true);
		   	   
		    //for utility-based evaluation
		    if (propsId==0) {
		    	System.out.println("Synthesizing based on single objective properties.....");
			    rs = smg.check(model, propertiesFile.getProperty(propsId)); 
			  	System.out.println("The result from model checking (SMG) is :"+ rs.getResult());
			  				    
			  	if(rs!=null) {
			  		this.synthesisStatus = true;
		    	}else {
					this.synthesisStatus = false;
				}
		    }
		    
		    //for multi-objective evaluation
		    if (propsId==1) {
			    System.out.println("Synthesizing based on multi-objective properties.....");
			    rsMulti1 = smg.check(model, propertiesFile.getProperty(propsId)); 
			     
			  	System.out.println("The result from model checking (SMG) is :"+ rsMulti1.getResult()); 
			    	
			  	if ((boolean)rsMulti1.getResult()) {
					this.synthesisStatus = true;
				}else {
					this.synthesisStatus = false;
				}
		    }
		  	
		 }//end of try
		 catch (PrismLangException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }	
		 catch (PrismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }	

	}	
	
	/**
	 * To check and synthesis the model
	 * @throws PrismLangException
	 * @throws PrismException
	 */
	public void checkModelbyPrismEx() {		
	   		    
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
    	
        //set the status for pareto and strategy generation
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
		
		
	 }//end of try
	 catch (PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	 }	
	 catch (PrismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	 }	

	}	
    
	/**
	 * To return the strategy generation status of compositional synthesis
	 * @return
	 */
	public boolean getSynthesisStatus() {
		return this.synthesisStatus;
	}

	public int getNumStates() {
		int result=0;
		if(model != null){
	    	result = model.getNumStates();
	    }
		return result;
	}
	
	public int getNumTransitions() {
		int result=0;
		if(model != null){
	    	result = model.getNumTransitions();
	    }
		return result;
	}
	    
    /**
     * Objective: It extracts the transitions which have been synthesized
     * @throws PrismException
     */
    public void exportTrans(String transPath)
    {
   		try {
			model.exportToPrismExplicitTra(new File(transPath));
		} catch (PrismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void exportStrategy(String sPath)
    {   
    	//exporting strategies for single objective
    	if((rs!=null) && (smg!=null)) {
    		stratMulti1 = rs.getStrategy();
    		stratMulti1.exportToFile(sPath);
    	}
    	
    	//exporting the strategies for multi-objective
    	if(rsMulti1!=null ) {
    		if((boolean)rsMulti1.getResult()) {
    			stratMulti1 = rsMulti1.getStrategy();
    			stratMulti1.exportToFile(sPath);
    		}
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
 		
 		StochasticPlanner plan; // = new CompositionalMultiPlanner[cycle]; 
 		
 		for (int i=0; i < cycle; i++)
 	    {		 	
 			System.out.println("number of cycle :"+i);
 			plan= new StochasticPlanner();
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
 			//plan.generate();
 			statusRes[i] = plan.getSynthesisStatus();
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
 		
	}

}
