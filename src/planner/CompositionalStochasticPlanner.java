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


public class CompositionalStochasticPlanner extends StochasticPlanner {

		CompositionalSMGModelChecker csmg;
		
		public CompositionalStochasticPlanner() {
			super();
		}
		
		//I need to set multiple requirements and resource conditions, 
		//but this can be handled at the main method	
	
		
		public void checkCompositionModel(int propsId) {		
		    
			 try {
		    	//create instance for ompositional multi-objective properties synthesis
		    	csmg = new CompositionalSMGModelChecker(prism, modulesFile, propertiesFile, prism.getSimulator());
		    	 
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
		    	//smg.setModulesFileAndPropertiesFile(modulesFile, propertiesFile);
		    	
		        //set the status for pareto and strategy generation
			    csmg.setComputeParetoSet(false);
			    csmg.setGenerateStrategy(true);
			   	   
			    //for utility-based evaluation
			    if (propsId==0) {
			    	System.out.println("Synthesizing based on compositional single objective properties....."); 
				    rs=csmg.check(propertiesFile.getProperty(propsId));
				  	System.out.println("The result from model checking (SMG) is :"+ rs.getResult());
				  				    
				  	if(rs!=null) {
				  		super.synthesisStatus = true;
			    	}else {
						super.synthesisStatus = false;
					}
			    }			
				
			    //for multi-objective evaluation
			    if (propsId==1) {
				    System.out.println("Synthesizing based on compositional multi-objective properties.....");
				    rsMulti1 = csmg.check(propertiesFile.getProperty(propsId)); 
				     
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
				        
	    public static void main(String[] args) {
	 		// TODO Auto-generated method stub
	    	//to store the time
			
	    	//define overall paths	
			String propPath = "/home/azlan/git/prismgames/Prismfiles/";
			String modelPath = "/home/azlan/git/prismgames/Prismfiles/";
			String transPath = "/home/azlan/git/prismgames/IOFiles/";
			String stratPath = "/home/azlan/git/prismgames/IOFiles/";
					
			//define parameters for the planning
			int numNode = 2;	//set the number of node/task/activity in the compositional structure
			int numofService = 10;	//set the number of services per each node
			int numofBehavior = 2;	//set the number of behavior per each service
			boolean setVal = true;  //true-during model generation, false-during model checking
			
			//define thresholds of QoS requirements (assuming as global requirements)
			int costR;		//max cost
			int durR; 		//max duration
			double relR;	//min reliability
			double wcostR, wdurR, wrelR;	//utility preferences
		
			//define parameters for the simulation
			int maxConf = 2;
			int initConf = 0; 
			int simCycle = 2;
			long time[] = new long[simCycle]; //log the synthesis execution time
			long timeGen[] = new long[simCycle]; //log the generation execution time
			long timeExp[] = new long[simCycle]; //long the export execution time
			long timeExt[] = new long[simCycle]; //log the extraction execution time
			boolean statusRes[] = new boolean[simCycle]; //log the synthesis status
			boolean statusResPar[][] = new boolean[simCycle][numNode]; //log the synthesis status for parallel
			int[] nodeSet = new int[maxConf]; //to set numNode per configuration
			int [] servSet = new int[maxConf];  //to set numService per configuration
			int maxPattern = 4;	//set the total number of pattern : 0-single, 1-sequential, 2-conditional, 3-parallel	
			int initPattern = 3; //to limit for specific pattern
			int maxEval = 2;	//set the total number of evaluation method : //0-utility-based, 1-multi-objective
			int initEval = 0;	//to limit for specific evaluation method
			boolean incNode = true; //to control the increment of task
			boolean incServ = false; //to control the increment of services
		
	    	
	    	TimeMeasure tm = new TimeMeasure();
			TimeMeasure tmGen = new TimeMeasure();
			TimeMeasure tmExp = new TimeMeasure();
			TimeMeasure tmExt = new TimeMeasure();
			
			//Create a model generator instance
			ModelGenerator mdg = new ModelGenerator();
		
			//Create a compositional SG-Planner instance
			CompositionalStochasticPlanner csp = new CompositionalStochasticPlanner();
			
			//Create a strategy extraction instance
			StrategyExtraction se = new StrategyExtraction();
		
			
			String mPath = modelPath+"modelComp.prism";
			String pPath = propPath+"propComp.props";
			String tPath = transPath+"transComp.txt";
			String sPath = stratPath+"stratComp.txt";	
			
			//record the start time
			tm.start();
			//=================================
			//synthesis
			System.out.println("Synthesizing model...");
			csp.initiatePlanner();
			csp.parseModelandProperties(mPath, pPath);
			//sp.setUndefinedValues(mdg.getDefinedValues());
			csp.checkCompositionModel(0);	//based on evaluation method
			//=================================
			tm.stop();
 			//record the duration
 			time[0] = tm.getDuration();
 			
 			tmExp.start();
 			//==================================
			//exporting
			System.out.println("Exporting transitions and strategies...");
			csp.exportTrans(tPath);
			csp.exportStrategy(sPath);
			//=================================
			tmExp.stop();
 			//record the duration
 			timeExp[0] = tmExp.getDuration();
 			
 			tmExt.start();
 			//================================
		//	if (csp.getSynthesisStatus()) {
				//extraction
		//		System.out.println("Extracting strategies...");
		//		se.setPath(singleTransPath, singleStratPath);
		//		se.setNumofDecision(1); //to control the searching for solution, simply set to one
		//		se.setActionLabels(mdg.getActionLabels());
		//		se.readTransitionFile();
		//		se.readStrategiesProfile(v);	//based on evaluation method	
		//		se.findSolutions();
		//		selServ = se.getSolution();
		//		for (int n=0; n < selServ.length; n++) {
		//			System.out.println("Decision node: "+n+", solution :"+selServ[n]);
		//		}
		//	}
		//	else
		//		System.out.println("Synthesis results in false.....");
			//===============================
			
			//stop the timer
 			tmExt.stop();
 			//record the duration
 			timeExt[0] = tmExt.getDuration();
 			
 			//record the synthesis status
			statusRes[0] = csp.getSynthesisStatus();
		
	 	}
	     
	    
}
