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
	//Result rs, rsProb, rsRwd1, rsRwd2, rsRwd3, rsRwd4, rsCSMG, rsComp, rsMulti1, rsMulti2, rsMultiComp;
	Result rs, rsProb, rsRwd1, rsRwd2, rsRwd3, rsRwd4, rsCSMG, rsComp, rsMulti1, rsMulti2, rsMultiComp;
	Strategy stratComp, stratMultiComp, stratMulti1, stratMulti2;
	SMGModelChecker smg;

	StrategyExtraction ste;
	ConfigurationPlanner conf;
	
	//Defining File Inputs/Outputs
	String logPath = "./myLog.txt";


	//Defining internal attributes for the planner
	int stage, propId;
	boolean synthesisStatus = false;

	
	public StochasticPlanner() {  }
	
	
	public void initiatePlanner(){
		mainLog = new PrismFileLog(logPath);
        prism = new Prism(mainLog , mainLog);
        simEngine = new SimulatorEngine(prismCom, prism);
        prismEx = new PrismExplicit(mainLog, prism.getSettings());
       
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
	
	public void setPropertyId(int id) {
		propId = id;
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
	

	public void checkModelforMultiObjective() {		
	    
		 try {
	    	//create instance for non-compositional multi-objective properties synthesis
	    	smg = new SMGModelChecker(prism);
	    	
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
		   	   
		    
		    //for multi-objective evaluation
		
		    System.out.println("Synthesizing based on multi-objective properties.....");
		    rsMulti1 = smg.check(model, propertiesFile.getProperty(propId)); 
			     
		  	System.out.println("The result from model checking (SMG) is :"+ rsMulti1.getResult()); 
			    	
		  	if ((boolean)rsMulti1.getResult()) {
				this.synthesisStatus = true;
			}else {
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
    	//if((rs!=null) && (smg!=null)) {
    	if(rs!=null) {
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
		
		//set the input paths
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propTest.props";
		String modelPath = "/home/azlan/git/PrismGames/Prismfiles/appDeployModel.prism";
	
		//set the output paths
		String transPath = "/home/azlan/git/PrismGames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strat.txt";
	
		//Create a SG-Planner instance
		StochasticPlanner sp = new StochasticPlanner();
				
		//=================================
		//synthesis
		System.out.println("Synthesizing model...");
		sp.initiatePlanner();
		sp.parseModelandProperties(modelPath, propPath);
		
		//sp.setUndefinedValues(mdg.getDefinedValues());
		sp.setPropertyId(0);
		sp.checkModelforMultiObjective();
		//=================================
	
		//==================================
		//exporting
		System.out.println("Exporting transitions and strategies...");
		sp.exportTrans(transPath);
		sp.exportStrategy(stratPath);				
	}

}
