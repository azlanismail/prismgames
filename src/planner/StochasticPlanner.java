package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import explicit.CompositionalSMGModelChecker;
import explicit.Model;
import explicit.PPLSupport;
import explicit.Pareto;
import explicit.PrismExplicit;
import explicit.ProbModelChecker;
import explicit.SMGModelChecker;
import explicit.StateModelChecker;
import parma_polyhedra_library.Polyhedron;
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

	StrategyExtraction ste;
	ConfigurationPlanner conf;
	
	ArrayList<String> thresholdSets;  //for threshold set obtained via pareto 
	String logPath = "./myLog.txt";	//Defining File Inputs/Outputs

	int propId;	//for storing the property index 
	boolean synthesisStatus = false;	//for storing the synthesis status

	//Properties prop[]; //for properties adjustment / re-synthesizing
	
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

	public void setUndefinedModelValues(Values vm) {
		try {
			modulesFile.setUndefinedConstants(vm);
		} catch (PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public Properties[] assignThresholdParamswithValues(Properties prop[]) {
		
		if (vp == null) {
			vp = new Values();
		}
		
		//assuming random approach
		Random rand = new Random();
		
		//initialize range value for the resource
		int minInd=3, maxInd=thresholdSets.size()-1;
		int rangeInd = maxInd - minInd;
				
		//select one of the pair of threshold candidates, except those 0.0, 1.0, -ve values
		int chooseInd = rand.nextInt(thresholdSets.size()-3) + 3;
		//int chooseInd = rand.nextInt(thresholdSets.size()-1) * rangeInd + minInd;
		System.out.println("Chosen index is "+chooseInd);
		
		//split the pair
		String str = thresholdSets.get(chooseInd);
		String[] splitStr = str.split("\\,");
		
		if (splitStr.length == 3) {
			for(int i=0; i < prop.length; i++) {
				if (prop[i].name.equalsIgnoreCase("cost")) {
					System.out.println("The cost is "+splitStr[0]);
					prop[i].values = Double.parseDouble(splitStr[0]) + 0.1;
				}
				else if (prop[i].name.equalsIgnoreCase("time")) {
					String[] ss = splitStr[1].split("\\.");
					System.out.println("The time is "+ss[0]);
					prop[i].values = Integer.parseInt(ss[0].trim()) + 1;
				}
				else if (prop[i].name.equalsIgnoreCase("reliability")) {
					System.out.println("The reliability is "+splitStr[2]);
					prop[i].values = Double.parseDouble(splitStr[2]) - 0.01;
				}
				else
					System.out.println("No values from the threshold set");
			}
		}
		else
			System.out.println("The element of pair is insufficent");
		
		//reassign the values into the properties specification
		for(int i=0; i < prop.length; i++) {
			vp.addValue(prop[i].name, prop[i].values); 
		}
		
		try {
			propertiesFile.setUndefinedConstants(vp);
		} catch (PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return prop;
	}
	
	public void setUndefinedPropertiesValues(Values vp) {
		try {
			propertiesFile.setUndefinedConstants(vp);
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
	 * To generate the acceptable thresholds based on the computed Pareto set
	 */
	public void computeParetoforThresholds() {		
	    
		 try {
	    	
	    	System.out.println("Computing Pareto set.....");
	    	
	    	//parse the specifications to smg instance    	
	    	//smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
	    	
	        //set the status for pareto and strategy generation
		    smg.setComputeParetoSet(true);
		  
		    System.out.println("Compute pareto set");
		    smg.check(model, propertiesFile.getProperty(propId)); 
		 
		    //define the thresholds set to store the values from computed Pareto set
		    thresholdSets = new ArrayList<String>();
		    
		    //extract the threshold values from Pareto set
		    String[] str = PPLSupport.getParetoString();
		    for(int i=0; i < str.length; i++) {
		    	String[] splitTemp = str[i].split("\\[|\\]");
		    	for(int j=0; j < splitTemp.length; j++) {
		    		if(!splitTemp[j].isEmpty() && !splitTemp[j].contentEquals("r:")) {
		    			thresholdSets.add(splitTemp[j]); 
			    	    System.out.println("Pareto is "+splitTemp[j]);
		    		}
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
	
	public void chooseTresholds() {
		
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
         
    
    public String getDecision(int decId) {
    	String nodeName;
    	
    	ste.setNodeIdList(conf.getNodeIdList());
    	nodeName = ste.getDecision(decId);
    	System.out.println("Selected node name is for decision "+decId+" is: "+nodeName);
		      
    	return nodeName;
    }
     
	
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		//set the input paths
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propTest.props";
		String modelPath = "/home/azlan/git/prismgames/Prismfiles/appDeployModel.prism";
	
		//set the output paths
		String transPath = "/home/azlan/git/prismgames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/prismgames/IOFiles/strat.txt";
		
		//set intermediate path
		String actionListPath = "/home/azlan/git/prismgames/IOFiles/actionList.txt";
	
		//==========PROPERTIES CREATION=====================
		//set properties
		Properties pp[] = new Properties[3];
		PropertiesGenerator pg = new PropertiesGenerator();
		
		//create the empty properties
		pp[0] = new Properties();
		pp[1] = new Properties();
		pp[2] = new Properties();
		
		//specify the parameters
		pp[0].setProperties(0, "cost", "double", 90, 10, "<");
		pp[1].setProperties(1, "time", "int", 1000, 100,"<");
		pp[2].setProperties(2, "reliability", "double", 0.9, 0.1, ">");
		
		//========PROPERTIES ENCODING=====================
		pg.setPropPath(propPath); //set the path
		pg.assignProperties(pp); //assign properties to the properties generator
		pg.setValuesStatus(false); //true-encode properties with threshold, false-without threshold (later stage)
		pg.setThresholdParamswithValues();
		pg.encodeProperties(); //encode the properties specification with values
		
		//========SYNTHESIS==============================
			
		//Create a SG-Planner instance
		StochasticPlanner sp = new StochasticPlanner();
		MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
		
		//=================================
		//synthesis
		
		System.out.println("Synthesizing model...");
		sp.initiatePlanner();
		sp.parseModelandProperties(modelPath, propPath);
		sp.setPropertyId(0);
		//sp.setUndefinedModelValues(mdg.getDefinedValues());
		sp.setUndefinedPropertiesValues(pg.getDefinedValues());
		
		int count=0;
		boolean status = false;
		while (true) {
			//perform model checking
			sp.checkModelforMultiObjective();
			status = sp.getSynthesisStatus();
			
			if ((status==true) || (count > 2)) {
				System.out.println("Terminating the model checking loop");
				break;
			}
			else {
				//get new thresholds values via pareto computation
				sp.computeParetoforThresholds();
				//re-assign the new thresholds
				sp.assignThresholdParamswithValues(pp);
			}
			count++;
		}
		
		
		//==================================
		//exporting
		if (status) {
			//exporting
			System.out.println("Exporting transitions and strategies...");
			sp.exportTrans(transPath);
			sp.exportStrategy(stratPath);		
		}	
		else
			System.out.println("No exporting since synthesis results in false");	
		
		//================================
		//extraction
		if (status) {
			System.out.println("Extracting strategies...");
			se.setPath(transPath, stratPath, actionListPath);
			se.readSingleActionLabelFile();
			se.readTransitionFile();
			se.readStrategiesfromMultiObjSynthesis();	
			se.findSingleDecision();
		}
		else
			System.out.println("No decision since synthesis results in false");	

	}
	
	

}

