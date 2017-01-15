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
		
		//define overall paths	
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propSBSPlanner.props";
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
			
		//begin the simulation
		for(int c=initConf; c < maxConf; c++) {
			
			//define parameters for the planning that different for each configuration	
			if (c == 0)
				nodeSet[c] = numNode;	//to increase numNode per configuration
			else if (incNode == true)
				nodeSet[c] = nodeSet[c-1] + 1;	//to increase numNode per configuration
			else
				nodeSet[c] = numNode;
			
			//define parameters for the planning that different for each configuration	
			if (c == 0)
				servSet[c] = numofService;	//to increase numServ per configuration
			else if (incServ == true)
				servSet[c] = servSet[c-1] + 20;	//to increase numNode per configuration
			else
				servSet[c] = numofService;
		
			
			//define requirements per configuration
			costR=50; durR=120; relR=0.5; //for multi-objective properties	
			wcostR=0.3; wdurR=0.3; wrelR=0.4;	//for single-objective properties	
						
			for(int p=initPattern; p < maxPattern; p++) {
				for(int v=initEval; v < maxEval; v++) {
					for(int m=0; m < simCycle; m++) {
						
						//to store the time
						TimeMeasure tm = new TimeMeasure();
						TimeMeasure tmGen = new TimeMeasure();
						TimeMeasure tmExp = new TimeMeasure();
						TimeMeasure tmExt = new TimeMeasure();
						
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
							String singleModelPath = modelPath+"modelSingle.prism";
							String singleTransPath = transPath+"transSingle.txt";
							String singleStratPath = stratPath+"stratSingle.txt";
							
							//record the start time
							tmGen.start();
							
							//========================================
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
							mdg.setModelPath(singleModelPath);										
							mdg.setPropPath(propPath);
							mdg.generateSGModel(0);	//value for >0 is only for parallel structure
							mdg.generateProperties();
							//=================================
							
							tmGen.stop();
				 			//record the duration
				 			timeGen[m] = tmGen.getDuration();
				 			
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);				
							
							//record the start time
							tm.start();
							//=================================
							//synthesis
							System.out.println("Synthesizing model...");
							sp.initiatePlanner();
							sp.parseModelandProperties(singleModelPath, propPath);
							sp.setUndefinedValues(mdg.getDefinedValues());
							sp.checkModel(v);	//based on evaluation method
							//=================================
							tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
				 			
				 			tmExp.start();
				 			//==================================
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(singleTransPath);
							sp.exportStrategy(singleStratPath);
							//=================================
							tmExp.stop();
				 			//record the duration
				 			timeExp[m] = tmExp.getDuration();
				 			
				 			tmExt.start();
				 			//================================
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(singleTransPath, singleStratPath);
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
							//===============================
							
							//stop the timer
				 			tmExt.stop();
				 			//record the duration
				 			timeExt[m] = tmExt.getDuration();
				 			
				 			//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
						
						}//end of single task
						
						else if (p==1) {
							//for sequential
							patName = "sequential";	
							String seqModelPath = modelPath+"modelSeq.prism";
							String seqTransPath = transPath+"transSeq.txt";
							String seqStratPath = stratPath+"stratSeq.txt";
							
							//record the start time
							tmGen.start();
							//========================================
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
							mdg.setModelPath(seqModelPath);										
							mdg.setPropPath(propPath);
							mdg.generateSGModel(0);
							mdg.generateProperties();
							//=================================
							
							tmGen.stop();
				 			//record the duration
				 			timeGen[m] = tmGen.getDuration();
				 			
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);				
							
							//record the start time
							tm.start();
							//=================================
							//synthesis
							System.out.println("Synthesizing model...");
							sp.initiatePlanner();
							sp.parseModelandProperties(seqModelPath, propPath);
							sp.setUndefinedValues(mdg.getDefinedValues());
							sp.checkModel(v);	//based on evaluation method
							//=================================
							tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
				 			
				 			tmExp.start();
				 			//================================
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(seqTransPath);
							sp.exportStrategy(seqStratPath);
							//=================================
							tmExp.stop();
				 			//record the duration
				 			timeExp[m] = tmExp.getDuration();
				 			
				 			tmExp.start();
				 			//================================
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(seqTransPath, seqStratPath);
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
							//==============================
							//stop the timer
				 			tmExt.stop();
				 			//record the duration
				 			timeExt[m] = tmExt.getDuration();
				 			
				 			//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
				 			
						}//end of sequential pattern
						
						else if (p==2) {
							//for conditional
							patName = "conditional";	
							String condModelPath = modelPath+"modelCond.prism";
							String condTransPath = transPath+"transCond.txt";
							String condStratPath = stratPath+"stratCond.txt";
							
							//record the start time
							tmGen.start();
							//========================================
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
							mdg.setModelPath(condModelPath);										
							mdg.setPropPath(propPath);
							mdg.generateSGModel(0);
							mdg.generateProperties();
							//=================================
							tmGen.stop();
				 			//record the duration
				 			timeGen[m] = tmGen.getDuration();
				 			
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);				
							
							//record the start time
							tm.start();
							//=================================
							//synthesis
							System.out.println("Synthesizing model...");
							sp.initiatePlanner();
							sp.parseModelandProperties(condModelPath, propPath);
							sp.setUndefinedValues(mdg.getDefinedValues());
							sp.checkModel(v);	//based on evaluation method
							//=================================
							tm.stop();
				 			//record the duration
				 			time[m] = tm.getDuration();
				 			
				 			tmExp.start();
				 			//================================
							//exporting
							System.out.println("Exporting transitions and strategies...");
							sp.exportTrans(condTransPath);
							sp.exportStrategy(condStratPath);
							//=================================
							tmExp.stop();
				 			//record the duration
				 			timeExp[m] = tmExp.getDuration();
				 			
				 			tmExt.start();
				 			//================================
							if (sp.getSynthesisStatus()) {
								//extraction
								System.out.println("Extracting strategies...");
								se.setPath(condTransPath, condStratPath);
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
							//==============================
							//stop the timer
				 			tmExt.stop();
				 			//record the duration
				 			timeExt[m] = tmExt.getDuration();
				 			
				 			//record the synthesis status
							statusRes[m] = sp.getSynthesisStatus();
									
						}//end of conditional pattern
						
						else if (p==3) {
							//for parallel
							patName = "parallel";
							
							//create multiple specifications to be executed in parallel
							System.out.println("Creating multiple paths for models, properties, transition, and strategies...");
							String[] multiModelPath = new String[nodeSet[c]];
							String[] multiPropPath = new String[nodeSet[c]];
							String[] multiTransPath = new String[nodeSet[c]];
							String[] multiStratPath = new String[nodeSet[c]];
			
							for (int n=0; n < nodeSet[c]; n++) {
								multiModelPath[n] = modelPath+"modelPar"+n+".prism";									
								multiPropPath[n] = modelPath+"propSBSPlanner"+n+".props";
								multiTransPath[n] = transPath+"transPar"+n+".txt";									
								multiStratPath[n] = stratPath+"stratPar"+n+".txt";
							
							}
							
							//record the start time
							tmGen.start();
							//========================================
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
							
							
							//System.out.println("Paths have been created...");
							System.out.println("Generating multiple models and properties specifications...");
							for (int n=0; n < nodeSet[c]; n++) {
								mdg.setModelPath(multiModelPath[n]);										
								mdg.generateSGModel(n);	
								mdg.setPropPath(multiPropPath[n]);
								mdg.generateProperties();
							}
							//=================================
							tmGen.stop();
				 			//record the duration
				 			timeGen[m] = tmGen.getDuration();
				 			
							//export the actions (for strategy extraction)
							//System.out.println("Exporting action labels...");
							//mdg.exportActionLabels(actLabelPath);				
							
							
							//=================================
							//synthesis
							System.out.println("Synthesizing model...");
							
							long temp1[][] = new long[simCycle][nodeSet[c]]; //only for temporary
							long temp2[][] = new long[simCycle][nodeSet[c]]; //only for temporary
							for (int n=0; n < nodeSet[c]; n++) {
								//record the start time
								tm.start();
								//=================================
								sp.initiatePlanner();
								sp.parseModelandProperties(multiModelPath[n], multiPropPath[n]);
								sp.setUndefinedValues(mdg.getDefinedValues());
								sp.checkModel(v); //based on evaluation method
								//=================================
								tm.stop();
								
					 			//get the duration value
								time[m] = tm.getDuration();
								
								//to record only the maximum duration
								if (n==0) {
									temp1[m][n] = time[m];
								}
								else {
									if (temp1[m][n-1] > time[m])
										time[m] = temp1[m][n-1];
								}
									
					 			tmExp.start();
					 			//================================
								//exporting
								System.out.println("Exporting transitions and strategies of node "+n);
								sp.exportTrans(multiTransPath[n]);
								sp.exportStrategy(multiStratPath[n]);
								//record the synthesis status
								statusRes[m] = sp.getSynthesisStatus();	//need to revise this statement in the future....
								//=================================
								tmExp.stop();
								
								//record the maximum duration
								timeExp[m] = tmExp.getDuration();
					 			if (n==0) {
									temp2[m][n] = timeExp[m];
								}
								else {
									if (temp2[m][n-1] > timeExp[m])
										timeExp[m] = temp2[m][n-1];
								}
								
							}
							//=================================
										 			
				 			tmExt.start();
				 			//================================
					 		for (int n=0; n < nodeSet[c]; n++) {
								if (statusRes[m]) {
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
							
							//==============================
							//stop the timer
				 			tmExt.stop();
				 			//record the duration
				 			timeExt[m] = tmExt.getDuration();
				 			
				 			//record the synthesis status / already recorded above
							//statusRes[m] = sp.getSynthesisStatus();
				 		
						}//end of if for parallel
						else
							System.out.println("the value for the pattern type is invalid....");
						
						
						try {
							if(p==0) {
								gatherData(c, m, time, timeGen, timeExp, timeExt, 1, servSet[c], numofBehavior, statusRes, p, v, selServ, 
									   mdg.getActionLabels(),mdg.getGeneratedCost(), mdg.getGeneratedAvail(), mdg.getGeneratedDuration(), mdg.getGeneratedReliability(),
									   sp.getNumStates(), sp.getNumTransitions());
							}else
								gatherData(c, m, time, timeGen, timeExp, timeExt, nodeSet[c], servSet[c], numofBehavior, statusRes, p, v, selServ, 
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
	
    public static void gatherData(int conf, int cycle, long time[], long timeGen[], long timeExp[], long timeExt[], int numNode, int numServ, int numBehavior, boolean statusRes[], int pattern, int evalMethod, String[] Solution,
    							  String[][] actLabel, int[][] cost, boolean[][] avail, int[][][] dur, double[][][] rel,
    							  int numStates, int numTrans) throws FileNotFoundException {
    	
    	String file1 = null;
    	String file2 = null;
    	if(pattern == 0) {
    		file1 = "logSelectSingle_"+conf+".txt";
    		file2 = "logQoSSingle_"+conf+".txt";
    	}
    	else if(pattern == 1) {
    		file1 = "logSelectSeq_"+conf+".txt";
    		file2 = "logQoSSeq_"+conf+".txt";
    	}
    	else if(pattern == 2) {
    		file1 = "logSelectCond_"+conf+".txt";
    		file2 = "logQoSCond_"+conf+".txt";
    	}
    	else {
    		file1 = "logSelectPar_"+conf+".txt";
    		file2 = "logQoSPar_"+conf+".txt";
    	}
   	    
    	String outfile1 = "/home/azlan/git/prismgames/IOFiles/"+file1;
		String outfile2 = "/home/azlan/git/prismgames/IOFiles/"+file2;
		
       PrintWriter pw, pq;
       try {
    	   pw = new PrintWriter(new FileWriter(outfile1, true));
    	   pq = new PrintWriter(new FileWriter(outfile2, true));
    	   
    	   //long total = 0;
    	   //==========log selection behavior==================
    	  // if ((conf==0) && (cycle==0))  {     
    		//   pw.println("cycle pattern evalMethod numNode numService numBehavior timeGen timeSyn timeExp timeExt avgTime status size numSolution Solution");
    	 //  }
    	   pw.print(cycle+" "+pattern+" "+evalMethod+" "+numNode+" "+numServ+" "+numBehavior+" "+timeGen[cycle]+" "+time[cycle]+" "+timeExp[cycle]+" "+timeExt[cycle]+" "+" "+statusRes[cycle]+" "+numStates+"/"+numTrans+" "+Solution.length);
    	   
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
