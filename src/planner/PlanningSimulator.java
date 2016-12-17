package planner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PlanningSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//define paths for I/O files
		String singlePath = "/home/azlan/git/prismgames/Prismfiles/singlemodel.prism";
		String seqPath = "/home/azlan/git/prismgames/Prismfiles/seqmodel.prism";
		String condPath = "/home/azlan/git/prismgames/Prismfiles/condmodel.prism";
		//String parPath = "/home/azlan/git/PrismGames/Prismfiles/parmodel.prism";
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propSBSPlanner.props";
		String transPath = "/home/azlan/git/prismgames/IOFiles/transitions.txt";
		String stratPath = "/home/azlan/git/prismgames/IOFiles/strategies.txt";
		//String actLabelPath = "/home/azlan/git/PrismGames/IOFiles/labels.txt";
				
				
		//define parameters for the simulation
		int maxConf = 1;
		int simCycle = 10;
		long time[] = new long[simCycle]; //log the execution time
		//TimeMeasure tm = new TimeMeasure(); //create the time instance
		boolean statusRes[] = new boolean[simCycle]; //log the synthesis status
		int[] nodeSet = new int[maxConf]; //to set numNode per configuration
		int [] servSet = new int[maxConf];  //to set numService per configuration
		int maxPattern = 4;	//set the total number of pattern
		int maxEval = 2;	//set the total number of evaluation method
		
		//define parameters for the planning
		int pattern = 1;	//0-single, 1-sequential, 2-conditional, 3-parallel	
		int numNode = 2;	//set the number of node/task/activity in the compositional structure
		
		int numofService = 10;	//set the number of services per each node
		int numofBehavior = 2;	//set the number of behavior per each service
		boolean setVal = true;  //true-during model generation, false-during model checking
		int evalMethod = 1; 	//0-utility-based, 1-multi-objective

		//define thresholds of QoS requirements (assuming as global requirements)
		int costR;		//max cost
		int durR; 		//max duration
		double relR;	//min reliability
		double wcostR, wdurR, wrelR;	//utility preferences
		
		//begin the simulation
		for(int c=0; c < maxConf; c++) {
			
			//define parameters for the planning that different for each configuration	
			if (c == 0)
				nodeSet[c] = numNode;	//to increase numNode per configuration
			else
				nodeSet[c] = nodeSet[c-1] + 1;	//to increase numNode per configuration
			
			//define parameters for the planning that different for each configuration	
			//if (c == 0)
				servSet[c] = numofService;	//to increase numServ per configuration
			//else
			//	servSet[c] = servSet[c-1] + 50;	//to increase numNode per configuration
			
		
			
			//define requirements per configuration
			costR=50; durR=120; relR=0.5; //for multi-objective properties	
			wcostR=0.3; wdurR=0.3; wrelR=0.4;	//for single-objective properties	
						
			for(int p=0; p < maxPattern; p++) {
				for(int v=0; v < maxEval; v++) {
					for(int m=0; m < simCycle; m++) {
						
						//to store the time
						TimeMeasure tm = new TimeMeasure();
						
						//to store the solution
						String[] selServ = new String[nodeSet[c]];
						
						//Create a model generator instance
						ModelGenerator mdg = new ModelGenerator();
					
						//Create a SG-Planner instance
						StochasticPlanner sp = new StochasticPlanner();
						
						//Create a strategy extraction instance
						StrategyExtraction se = new StrategyExtraction();
						
						String patName = null;
						
						//create the model and synthesize according to a pattern
						if (p==0) {
							patName = "single";		
							
							//record the start time
							tm.start();
							
							//configuring model parameters and values
							System.out.println("Solving single node...");
							mdg.setValuesStatus(setVal); //true-create model with values, false-create model without values (later stage)
							mdg.setPattern(p);	//set the current value of p
							mdg.setParamsNames("p1", "p2", "planner", "environment");
							mdg.setUpperBounds(1, servSet[c], numofBehavior); //simply set numNode = 1
							
							//creating and assigning values to parameters
							System.out.println("Creating and assigning values to parameters...");
							mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
							mdg.setAllRandomServProfiles();
							mdg.createServParams();
							mdg.setReqParamswithValues();
							mdg.setServParamswithValues();
							
							//create the specifications
							System.out.println("Generating model and properties specifications...");
							mdg.setModelPath(singlePath);										
							mdg.setPropPath(propPath);
							mdg.generateSGModel(0);	//value for >0 is only for parallel structure
							mdg.generateProperties();
										
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);				
							
							//synthesis
							System.out.println("Synthesizing model...");
							sp.initiatePlanner();
							sp.parseModelandProperties(singlePath, propPath);
							sp.setUndefinedValues(mdg.getDefinedValues());
							sp.checkModel(v);	//based on evaluation method
							
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(transPath);
							sp.exportStrategy(stratPath);
							
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(transPath, stratPath);
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
							
							//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
							//stop the timer
				 			tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
						
						}//end of single task
						
						else if (p==1) {
							//for sequential
							patName = "sequential";	
							
							//record the start time
							tm.start();
							
							//configuring model parameters and values
							System.out.println("Solving sequential pattern...");
							mdg.setValuesStatus(setVal); //true-create model with values, false-create model without values (later stage)
							mdg.setPattern(p);	//set the current value of p
							mdg.setParamsNames("p1", "p2", "planner", "environment");
							mdg.setUpperBounds(nodeSet[c], servSet[c], numofBehavior);
						
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
							sp.checkModel(v);	//based on evaluation method
							
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(transPath);
							sp.exportStrategy(stratPath);
							
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(transPath, stratPath);
								se.setNumofDecision(nodeSet[c]); //to control the searching for solution
								se.setActionLabels(mdg.getActionLabels());
								System.out.println("Reading data...");
								se.readTransitionFile();
								se.readStrategiesProfile(v);	//based on evaluation method
								System.out.println("Looking for solution...");
								se.findSolutions();
								selServ = se.getSolution();
								for (int n=0; n < selServ.length; n++) {
									System.out.println("Decision node: "+n+", solution :"+selServ[n]);
								}
							}
							else
								System.out.println("Synthesis results in false.....");
							
							//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
							//stop the timer
				 			tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
				 			
						}//end of sequential pattern
						
						else if (p==2) {
							//for conditional
							patName = "conditional";	
							//record the start time
							tm.start();
							
							//configuring model parameters and values
							System.out.println("Solving conditional pattern...");
							mdg.setValuesStatus(setVal); //true-create model with values, false-create model without values (later stage)
							mdg.setPattern(p);	//set the current value of p
							mdg.setParamsNames("p1", "p2", "planner", "environment");
							mdg.setUpperBounds(nodeSet[c], servSet[c], numofBehavior);
						
							//creating and assigning values to parameters
							System.out.println("Creating and assigning values to parameters...");
							mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
							mdg.setAllRandomServProfiles();
							mdg.createServParams();
							mdg.setReqParamswithValues();
							mdg.setServParamswithValues();
							
							//create the specifications
							System.out.println("Generating model and properties specifications...");
							mdg.setModelPath(condPath);										
							mdg.setPropPath(propPath);
							mdg.generateSGModel(0);
							mdg.generateProperties();
										
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);
							
							//synthesis
							System.out.println("Synthesizing model...");
							sp.initiatePlanner();
							sp.parseModelandProperties(condPath, propPath);
							sp.setUndefinedValues(mdg.getDefinedValues());
							sp.checkModel(v);	//based on evaluation method
							
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(transPath);
							sp.exportStrategy(stratPath);
									
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(transPath, stratPath);
								se.setNumofDecision(nodeSet[c]); //to control the searching for solution
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
							
							//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
							//stop the timer
				 			tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
									
						}//end of conditional pattern
						
						else if (p==3) {
							//for parallel
							patName = "parallel";
							
							//record the start time
							tm.start();
							
							//configuring model parameters and values
							System.out.println("Solving parallel pattern...");
							mdg.setValuesStatus(setVal); //true-create model with values, false-create model without values (later stage)
							mdg.setPattern(p);	//set the current value of p
							mdg.setParamsNames("p1", "p2", "planner", "environment");
							mdg.setUpperBounds(nodeSet[c], servSet[c], numofBehavior);
									
							//creating and assigning values to parameters
							System.out.println("Creating and assigning values to parameters...");
							mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
							mdg.setAllRandomServProfiles();
							mdg.createServParams();
							mdg.setReqParamswithValues();
							mdg.setServParamswithValues();
							
							//create multiple specifications to be executed in parallel
							System.out.println("Creating multiple paths for models and properties specifications...");
							String[] multiModelPath = new String[nodeSet[c]];
							String[] multiPropPath = new String[nodeSet[c]];
							for (int n=0; n < nodeSet[c]; n++) {
								multiModelPath[n] = "/home/azlan/git/prismgames/Prismfiles/parmodel"+n+".prism";									
								multiPropPath[n] = "/home/azlan/git/prismgames/Prismfiles/propSBSPlanner"+n+".props";
							}
							//System.out.println("Paths have been created...");
							System.out.println("Generating multiple models and properties specifications...");
							for (int n=0; n < nodeSet[c]; n++) {
								mdg.setModelPath(multiModelPath[n]);										
								mdg.generateSGModel(n);	
								mdg.setPropPath(multiPropPath[n]);
								mdg.generateProperties();
							}
							
							//synthesis
							System.out.println("Synthesizing model...");
							//preparing transition and strategies file
							String[] multiTransPath = new String[nodeSet[c]];
							String[] multiStratPath = new String[nodeSet[c]];
							for (int n=0; n < nodeSet[c]; n++) {
								multiTransPath[n] = "/home/azlan/git/prismgames/Prismfiles/trans"+n+".prism";									
								multiStratPath[n] = "/home/azlan/git/prismgames/Prismfiles/strat"+n+".props";
							}
							sp.initiatePlanner();
							for (int n=0; n < nodeSet[c]; n++) {
								sp.parseModelandProperties(multiModelPath[n], multiPropPath[n]);
								sp.setUndefinedValues(mdg.getDefinedValues());
								sp.checkModel(v); //based on evaluation method
								
								//exporting
								System.out.println("Exporting transitions and strategies of node "+n);
								sp.exportTrans(multiTransPath[n]);
								sp.exportStrategy(multiStratPath[n]);
								
								if (sp.getSynthesisStatus()) {
									//extraction
									System.out.println("Extracting strategies...");
									se.setPath(multiTransPath[n], multiStratPath[n]);
									se.setNumofDecision(nodeSet[c]); //to control the searching for solution
									se.setActionLabels(mdg.getActionLabels());
									se.readTransitionFile();
									se.readStrategiesProfile(v);	// based on evaluation method	
									selServ[n] = se.findParSolutions(n);
									System.out.println("Decision node: "+n+", solution :"+selServ[n]);
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
							if(p==0) {
								gatherData(c, m, time, 1, servSet[c], numofBehavior, statusRes, p, v, selServ, 
									   mdg.getActionLabels(),mdg.getGeneratedCost(), mdg.getGeneratedAvail(), mdg.getGeneratedDuration(), mdg.getGeneratedReliability(),
									   sp.getNumStates(), sp.getNumTransitions());
							}else
								gatherData(c, m, time, nodeSet[c], servSet[c], numofBehavior, statusRes, p, v, selServ, 
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
						
					}//end of simulation cycle per pattern and per method
				}//end of simulation of all method per pattern
			}//end of simulation for all pattern of all method
		
		}//end of configuration
		
		
		System.out.println("Simulation is done...");
		
		//analyzePerformance(simCycle, maxPattern, maxEval, numNode, numofService);
		
		//look at successful generation
		//look at quality of selection
		
		//look at the quality of selected service by increasing number of services
		//analyzeSingle
		
		//look at the quality of selected service by increasing 
		//analyzeStructure
		
		//look at the quality of selected services by comparing between method/properties
		//analyzeMethod
		
		
	}//end of main
	
    public static void gatherData(int conf, int cycle, long time[], int numNode, int numServ, int numBehavior, boolean statusRes[], int pattern, int evalMethod, String[] Solution,
    							  String[][] actLabel, int[][] cost, boolean[][] avail, int[][][] dur, double[][][] rel,
    							  int numStates, int numTrans) throws FileNotFoundException {
   	 	//String outfile1 = "/home/azlan/git/prismgames/IOFiles/logSelect_"+pattern+"_"+evalMethod+"_"+numNode+"_"+numServ+".txt";
   	   // String outfile2 = "/home/azlan/git/prismgames/IOFiles/logQoS_"+pattern+"_"+evalMethod+"_"+numNode+"_"+numServ+".txt";

   		String outfile1 = "/home/azlan/git/prismgames/IOFiles/logSelect_"+conf+".txt";
   	    String outfile2 = "/home/azlan/git/prismgames/IOFiles/logQoS_"+conf+".txt";

   	    
       PrintWriter pw, pq;
       try {
    	   pw = new PrintWriter(new FileWriter(outfile1, true));
    	   pq = new PrintWriter(new FileWriter(outfile2, true));
    	   
    	   //long total = 0;
    	   //==========log selection behavior==================
    	   if ((conf==0) && (cycle==0))  {     
    		   pw.println("cycle pattern evalMethod numNode numService numBehavior time avgTime status size numSolution Solution");
    	   }
    	   pw.print(cycle+" "+pattern+" "+evalMethod+" "+numNode+" "+numServ+" "+numBehavior+" "+time[cycle]+" "+" "+statusRes[cycle]+" "+numStates+"/"+numTrans+" "+Solution.length);
    	   
    	   for(int s=0; s < Solution.length; s++) {
    		  // pw.print(" "+Solution[s]);
    		   for(int n=0; n < numNode; n++) {
        		   for(int i=0; i < numServ; i++) {
        			   if(actLabel[n][i].equals(Solution[n])) {
        				   pw.print(" "+Solution[n]+" "+" "+" "+cost[n][i]+" "+avail[n][i]);
        				   for(int r=0; r < numBehavior; r++) {
        					   pw.print(" "+dur[n][i][r]+" "+rel[n][i][r]);
        				   }
        			   }
        		   }
        	   }
    	   }
    	   pw.println();
    	   
    	   //==========log QoS values===================
    	   if (cycle == 0)  {     
    		   pq.println("solution actionLabel status task service cost availability duration reliability");
    	   }
    	   for(int n=0; n < numNode; n++) {
    		   for(int i=0; i < numServ; i++) {
    			   if(actLabel[n][i].equals(Solution[n])) {
    				   pq.print(pattern+" "+Solution[n]+" "+" "+statusRes[cycle]+" "+n+" "+i+" "+cost[n][i]+" "+avail[n][i]);
    				   for(int r=0; r < numBehavior; r++) {
    					   pq.print(" "+dur[n][i][r]+" "+rel[n][i][r]);
    				   }
    			   }
    		   }
    		   pq.println();
    	   }
    	   pw.close();
    	   pq.close();
       
       } catch (IOException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
       }
    }//end of gather data
    
    /**
     * To compare the planning time of each structure in relation to increasing the numNode
     * @param sim
     * @param maxPattern
     * @param maxEval
     * @param numNode
     * @param numServ
     */
    public static void analyzePerformance(int sim, int maxPattern, int maxEval, int numNode, int numServ) {
    	//the assumption is: I have a set of log files that contains the planning time 
    	//for each structure, each method and each num node
    	
    	for(int p=0; p < maxPattern; p++) {
    		for(int v=0; v <maxEval; v++) {
    	    	//============start reading and computing process==========
    	    	String infile1 = "/home/azlan/git/prismgames/IOFiles/logSelect_"+p+"_"+v+"_"+numNode+"_"+numServ+".txt";
    	   	    
    	   	    Scanner readS;
    	   	    try {
    				readS = new Scanner(new BufferedReader(new FileReader(infile1)));   	
    		   	    Long[] time = new Long[sim];
    		   	    Long total = 12345678910L;  	   
    		   	    int i=0;
    		   	    //need to skip the first line
    				readS.nextLine(); 
    				while (readS.hasNextLine()) {
    					//skip cycle
    					readS.next();
    					//read the time
    					time[i] = Long.parseLong(readS.next());
    					total +=time[i];
    					i++;
    					readS.nextLine();
    				}
    		 	    long avg = (total/sim);
    				readS.close();	
    			
    	   	    } catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}	
    		}
    	}
    	
    	
   	    
		String outfile = "/home/azlan/git/prismgames/IOFiles/performance.txt";
   	    
   	    PrintWriter pw;
   	    try {
    	   pw = new PrintWriter(new FileWriter(outfile, true));
    	 
    	   pw.println("pattern method numNode numSer numBeha avgTime");
    	 
    	   pw.close();
    	       
       } catch (IOException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
       }
    }//end of analyze performance

    public static void analyzeSelection(int pattern, int evalMethod, int numNode, int numServ) {
    	//read the files
    	String infile1 = "/home/azlan/git/prismgames/IOFiles/logSelect_"+pattern+"_"+evalMethod+"_"+numNode+"_"+numServ+".txt";
   	    String infile2 = "/home/azlan/git/prismgames/IOFiles/logQoS_"+pattern+"_"+evalMethod+"_"+numNode+"_"+numServ+".txt";

   	    Scanner readS;
   	    try {
			readS = new Scanner(new BufferedReader(new FileReader(infile1)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   
    	
    	//write the outcome
    }


}//end of class
