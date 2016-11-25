package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import parser.Values;


public class ModelGenerator {

	PrintWriter pw, pp;
	
	//for assigning constant parameters
	Values vm, vp;
	Values[] vpar;
	
	//model configuration
	private int numNode; //number of activity/task/node in the service composition
	private int maxActionP1; //finite number of candidates to be selected
	private int maxActionP2; //finite number of resources
	private String p1;	//to define the name of P1
	private String p2;	//to define the name of P2
	private String mod1;	//to define the name of P1's module
	private String mod2;	//to define the name of P2's module
	private boolean setValuesStatus;	//to define null parameters or parameters with values
	private int pattern;	//to determine the type of pattern; 0-single, 1-seq, 2-or, 3-parallel
	
	//requirements
	private int idR;
	private int costR;
	private int durR;
	private double relR;
	private double wcostR;
	private double wdurR;
	private double wrelR;
	
	//service constants parameters
	private String[][] idParams;
	private String[][] costParams;
	private String[][] availParams;
	private String[][][] durParams;
	private String[][][] relParams;
	
	//service profiles
	private int[][] cost;
	private boolean[][] avail;
	private int[][][] dur;
	private double[][][] rel;
	
	
	public ModelGenerator() {
		
	}
	
	public ModelGenerator(String path) {
		try {
			pw = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setModelPath(String path) {
		try {
			pw = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPropPath(String path) {
		try {
			pp = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setParamsNames(String p1Name, String p2Name, String mod1, String mod2) {
		this.p1 = p1Name;
		this.p2 = p2Name;
		this.mod1 = mod1;
		this.mod2 = mod2;
	}
	
	public void setRequirements(int reqId, int cost, int dur, double rel, double wcost, double wdur, double wrel) {
		this.idR = reqId;
		this.costR = cost;
		this.durR = dur;
		this.relR = rel;
		this.wcostR = wcost;
		this.wdurR = wdur;
		this.wrelR = wrel;
	}
	 	
	public void setUpperBounds(int numNode, int mxActionP1, int mxActionP2) {
		this.numNode = numNode;
		this.maxActionP1 = mxActionP1;
		this.maxActionP2 = mxActionP2;
		
		cost = new int[numNode][maxActionP1];
		avail = new boolean[numNode][maxActionP1];
		dur = new int[numNode][maxActionP1][maxActionP2];
		rel = new double[numNode][maxActionP1][maxActionP2];
	}
	
	public void setPattern(int p) {
		this.pattern = p;
	}
	
	public void setReqParamswithValues() {
		
		if (vm == null) {
			vm = new Values();
		}
		vm.addValue("A0_ID", this.idR); 
		vm.addValue("A0_DUR", this.costR); 
		vm.addValue("A0_REL", this.durR); 
		vm.addValue("A0_COST", this.relR); 
		vm.addValue("A0_WG_COST", this.wcostR); 
		vm.addValue("A0_WG_DUR", this.wdurR); 
		vm.addValue("A0_WG_REL",this.wrelR); 
	}
	
	/**
	 * prepare the arrays to hold the identifier of the constant parameters 
	 */
	public void createServParams() {
		//constant parameters for service profiles
		idParams = new String[this.numNode][this.maxActionP1];
		costParams = new String[this.numNode][this.maxActionP1];
		availParams = new String[this.numNode][this.maxActionP1];
		durParams = new String[this.numNode][this.maxActionP1][this.maxActionP2];
		relParams = new String[this.numNode][this.maxActionP1][this.maxActionP2];
	}
	
	public void setServParamswithValues() {
		
		if (vm == null) {
			vm = new Values();
		}
				
		for(int n=0; n < this.numNode; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				this.idParams[n][i] = "N"+n+"_RS"+i+"_ID";
				this.costParams[n][i] = "N"+n+"_RS"+i+"_COST";
				this.availParams[n][i] = "N"+n+"_RS"+i+"_AVAIL";
				for(int j=0; j < this.maxActionP2; j++) {
					this.durParams[n][i][j] = "N"+n+"_RS"+i+"_DUR"+j;
					this.relParams[n][i][j] = "N"+n+"_RS"+i+"_REL"+j;
				}
			}
		}
		
		for(int n=0; n < this.numNode; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				vm.addValue(this.idParams[n][i], i); 
				vm.addValue(this.costParams[n][i], this.cost[n][i]);
				vm.addValue(this.availParams[n][i], this.avail[n][i]);
				for(int j=0; j < this.maxActionP2; j++) {
					vm.addValue(this.durParams[n][i][j], this.dur[n][i][j]);
					vm.addValue(this.relParams[n][i][j], this.rel[n][i][j]);
				}
			}
		}
		
	}
	
	public void createValueInstances(int maxIns) {
		if (vpar == null) {
			vpar = new Values[maxIns];
		}
	}
	
	public void setParReqParamswithValues(int k) {
	
		if (vpar[k] == null) {
			vpar[k] = new Values();
		}
		
		vpar[k].addValue("A0_ID", this.idR); 
		vpar[k].addValue("A0_DUR", this.costR); 
		vpar[k].addValue("A0_REL", this.durR); 
		vpar[k].addValue("A0_COST", this.relR); 
		vpar[k].addValue("A0_WG_COST", this.wcostR); 
		vpar[k].addValue("A0_WG_DUR", this.wdurR); 
		vpar[k].addValue("A0_WG_REL",this.wrelR); 
	}

	public void setParServParamswithValues(int k) {
		
		if (vpar[k] == null) {
			vpar[k] = new Values();
		}
	
		//creating the constant parameters
		for(int n=0; n < this.numNode; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				this.idParams[n][i] = "N"+n+"_RS"+i+"_ID";
				this.costParams[n][i] = "N"+n+"_RS"+i+"_COST";
				this.availParams[n][i] = "N"+n+"_RS"+i+"_AVAIL";
				for(int j=0; j < this.maxActionP2; j++) {
					this.durParams[n][i][j] = "N"+n+"_RS"+i+"_DUR"+j;
					this.relParams[n][i][j] = "N"+n+"_RS"+i+"_REL"+j;
				}
			}
		}
		
		//assign the values to the parameters
		for(int n=0; n < this.numNode; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				vpar[k].addValue(this.idParams[n][i], i); 
				vpar[k].addValue(this.costParams[n][i], this.cost[k][i]);
				vpar[k].addValue(this.availParams[n][i], this.avail[k][i]);
				for(int j=0; j < this.maxActionP2; j++) {
					vpar[k].addValue(this.durParams[n][i][j], this.dur[k][i][j]);
					vpar[k].addValue(this.relParams[n][i][j], this.rel[k][i][j]);
				}
			}
		}
		
	}
	/**
	 * 
	 * @param n - index of node/activity/task
	 * @param i - index of P1's actions
	 * @param j - index of P2's actions
	 * @param cost
	 * @param avail
	 * @param dur
	 * @param rel
	 */
	public void setProfiles(int n, int i, int j, int cost, boolean avail, int dur, double rel ) {
		
		this.cost[n][i] = cost;
		this.avail[n][i] = avail;
		this.dur[n][i][j] = dur;
		this.rel[n][i][j] = rel;
		
		//System.out.println(""+this.cost[n][i]+","+this.avail[n][i]+","+this.dur[n][i][j]+","+this.rel[n][i][j]);
	}
	
	public void setAllRandomServProfiles() {
		Random rand = new Random();
		int cost, dur;
		double rel;
		boolean avail;
		for(int n=0; n < this.numNode; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				cost = rand.nextInt(50) + 10;
				avail = rand.nextBoolean();
				for(int j=0; j < this.maxActionP2; j++) {
					dur = rand.nextInt(100);
					rel = rand.nextDouble();
					setProfiles(n, i, j, cost, avail, dur, rel);
				}
			}
		}
	}
	
	public void setParofAllRandomServProfiles(int maxIns) {
		Random rand = new Random();
		int cost, dur;
		double rel;
		boolean avail;
		for(int n=0; n < maxIns; n++) {
			for(int i=0; i < this.maxActionP1; i++) {
				cost = rand.nextInt(50) + 10;
				avail = rand.nextBoolean();
				for(int j=0; j < this.maxActionP2; j++) {
					dur = rand.nextInt(100);
					rel = rand.nextDouble();
					setProfiles(n, i, j, cost, avail, dur, rel);
				}
			}
		}
	}
	
	
	public void setValuesStatus(boolean status) {
		this.setValuesStatus = status;
	}
	
	public Values getDefinedValues() {
		return vm;
	}
	
	public Values getDefinedValuesforPar(int k) {
		return vpar[k];
	}
	
	public void generateSingleNodeModel()  {
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int i=0; i < maxActionP1; i++) {
   	 		pw.print("[r"+i+"],");
   	 	}
   	 	pw.println("[end]");
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
	 	pw.print(""+mod2+",");
	 	for(int i=0; i < maxActionP2; i++) {
	 		pw.print("[e"+i+"]");
	 		if(i != maxActionP2-1)
	 			pw.print(",");
	 	}
	 	pw.println();
	 	pw.println("endplayer");
	 	pw.println();
	 	
	 	pw.println("//=========User Requirements=======");
	 	pw.println("const int A0_ID = 0;");
	 	pw.println("const int A0_DUR = 5;	//max duration");
	 	pw.println("const double A0_REL = 0.6; //reliability");
	 	pw.println("const double A0_COST = 10.0; //max cost");
	 	pw.println("const double A0_WG_COST = 0.3; //weight for cost");
	 	pw.println("const double A0_WG_DUR = 0.3; //weight for duration");
	 	pw.println("const double A0_WG_REL = 0.4; //weight for reliability");
	 	pw.println();
	 	
	 	pw.println("//=========Resource Profiles=======");
	 	pw.println("const int MAX_SV="+maxActionP1+";");		
	 	pw.println("const int MAX_EV="+maxActionP2+";");		
	 	pw.println();
	 	
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.println("const int RS"+i+"_ID;");
	 		pw.println("const double RS"+i+"_COST;	//cost");
	 		pw.println("const bool RS"+i+"_AVAIL;	//availability status");
	 		for(int j=0; j < maxActionP2; j++) {
	 			pw.println("const int RS"+i+"_DUR"+j+";	//duration "+j+";");
	 		}
	 		for(int j=0; j < maxActionP2; j++) {
	 			pw.println("const int RS"+i+"_REL"+j+";	//reliability "+j+";");
	 		}
	 		pw.println();
	 	}
	 	pw.println();
  
	 	pw.println("//=========Global Parameters=======");
	 	pw.println("const int TE=0;");	
	 	pw.println("const int TP=1;");
	 	pw.println("global t:[TE..TP] init TP;	//to control the turn");
	 	pw.println("global goal : bool init false;	//to determine the selection");
	 	pw.println("global exec : bool init false;	//to determine the execution");		
	 	pw.println("global end : bool init false;	//(absorbing state)");			
	 	pw.println();		
		
	 	pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1);
	 	pw.println("sel:[-1..MAX_SV] init -1;");
	 	pw.println("//P1 moves :");
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.println("[r"+i+"] (t=TP) & (goal=false) & (a0_rs"+i+"_sat_ml_all=true) -> (goal'=true) & (sel'="+i+") & (t'=TE);");
	 	}
	 	pw.println("[end] (t=TP) & (end=false) & (goal=true) -> (end'=true) & (t'=TE);");
	 	pw.println("[end] (t=TP) & (end=false) & (exec=true) & (goal=true) -> (end'=true) & (t'=TE);");
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 	pw.println("ev:[-1..MAX_EV] init -1;");
	 	pw.println("//P2 moves :");		
	 	for(int i=0; i < maxActionP2; i++) {
	 		pw.println("[e"+i+"] (t=TE) & (exec=false) & (goal=true) -> succ_rs"+i+":(exec'=true) & (ev'="+i+") & (t'=TP) + 1-succ_rs"+i+":(exec'=false) & (t'=TP);");			
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Assign reliability values=======");
	 	for(int i=0; i < maxActionP2; i++) {
	 		pw.print("formula succ_rs"+i+" =");
	 		for(int j=0; j < maxActionP1; j++) {
	 			pw.print(" ( sel = "+j+" ? RS"+j+"_REL"+i+" :");
	 		}
	 		pw.print("0.0");
	 		for(int j=0; j < maxActionP1; j++) {
	 			pw.print(")");
	 		}
	 	 	pw.print(";");
	 	 	pw.println();
	 	}
 		pw.println();
	 	
	 	pw.println("//=========QoS Constraints Checking=======");
	 	for(int i=0; i < maxActionP1; i++){
	 		pw.println("//=====Application 0 and RS"+i);
	 		for(int j=0; j < maxActionP2; j++) {
	 			pw.println("formula a0_rs"+i+"_sat_dur"+j+" = ( (A0_DUR <= RS"+i+"_DUR"+j+") ? true:false);");
	 		}
	 		for(int j=0; j < maxActionP2; j++) {
	 			pw.println("formula a0_rs"+i+"_sat_rel"+j+" = ( (A0_DUR <= RS"+i+"_REL"+j+") ? true:false);");
	 		}
	 		pw.println("formula a0_rs"+i+"_sat_cost = ( (A0_COST <= RS"+i+"_COST) ? true:false);");
	 		pw.println("formula a0_rs"+i+"_sat_avail = RS"+i+"_AVAIL;");
	 		pw.println("formula a0_rs"+i+"_sat_ml_all = a0_rs"+i+"_sat_avail;");
	 		pw.println();
	 	}
	 	pw.println();
	 	
	 	pw.println("//=========Utility-based Decision Making=======");
	 	pw.println("//get the cost of selected node..");
	 	pw.print("formula rs_cost =");
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.print("( sel = "+i+" ? RS"+i+"_COST :");
	 	}
	 	pw.print("0.0");
 		for(int i=0; i < maxActionP1; i++) {
 			pw.print(")");
 		}
	 	pw.println(";");
	 	pw.println("//Computing the utility value..");
	 	pw.print("formula mx_cost = max(");
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.print("RS"+i+"_COST");
	 		if (i != maxActionP1-1)
	 			pw.print(",");
	 	}
	 	pw.println(");");
	 	pw.print("formula mn_cost = min(");
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.print("RS"+i+"_COST");
	 		if (i != maxActionP1-1)
	 			pw.print(",");
	 	}
	 	pw.println(");");
	 	pw.println("formula uv_cost = (mx_cost - rs_cost) / (mx_cost - mn_cost);");
	 	pw.println();
	 	
	 	//Preparing the utility function for duration
	 	for(int j=0; j < maxActionP2; j++) {
	 		pw.println("//get the duration "+j+" of selected node..");
	 		pw.print("formula rs_dur"+j+"=");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print("( (sel = "+i+" & ev = "+j+") ? RS"+i+"_DUR"+j+" :");
	 		}
	 		pw.print("0.0");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print(")");
	 		}
	 		pw.println(";");
	 		
	 		//compute max and min value
		 	pw.println("//Computing the utility value..");
		 	pw.print("formula mx_dur"+j+" = max(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("RS"+i+"_DUR"+j);
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.print("formula mn_dur"+j+" = min(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("RS"+i+"_DUR"+j);
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	
		 	//compute the utility value
		 	pw.println("formula uv_dur"+j+" = (mx_dur"+j+" - rs_dur"+j+") / (mx_dur"+j+" - mn_dur"+j+");");
		 	pw.println();
	 		
	 	}
	 	
	 	//Preparing utility function for reliability
		for(int j=0; j < maxActionP2; j++) {
	 		pw.println("//get the reliability "+j+" of selected node..");
	 		pw.print("formula rs_rel"+j+"=");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print("( (sel = "+i+" & ev = "+j+") ? RS"+i+"_REL"+j+" :");
	 		}
	 		pw.print("0.0");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print(")");
	 		}
	 		pw.println(";");
	 		
	 		//compute the max and min value
		 	pw.println("//Computing the utility value..");
		 	pw.print("formula mx_rel"+j+" = max(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("RS"+i+"_REL"+j);
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.print("formula mn_rel"+j+" = min(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("RS"+i+"_REL"+j);
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	
		 	//compute utility value
		 	pw.println("formula uv_rel"+j+" = (mx_rel"+j+" - rs_rel"+j+") / (mx_rel"+j+" - mn_rel"+j+");");
		 	pw.println();
	 	}
		pw.println();

		pw.println("//Preventing from non-evaluated problem..");
		pw.println("formula v_cost = (uv_cost > 0.0 ? uv_cost: 0.0);");
		for(int j=0; j < maxActionP2; j++) {
			pw.println("formula v_dur"+j+" = (uv_dur"+j+" > 0.0 ? uv_dur"+j+": 0.0);");
		}
		for(int j=0; j < maxActionP2; j++) {
			pw.println("formula v_rel"+j+" = (uv_rel"+j+" > 0.0 ? uv_rel"+j+": 0.0);");
		}
	 	pw.println();
	 	
		pw.println("//Compute the overall utility value..");
		pw.print("formula uv_all = ");
		for (int j=0; j < maxActionP2; j++) {
			pw.print("(ev="+j+" ? (uv_cost * A0_WG_COST + uv_dur"+j+" * A0_WG_DUR + uv_rel"+j+" * A0_WG_REL):");
		}
		pw.print("0.0");
		for (int j=0; j < maxActionP2; j++) {
			pw.print(")");
		}
		pw.println(";");
	 	pw.println();
	 	
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"cost\"");
	 	pw.println("[end] true: rs_cost;");
	 	pw.println("endrewards");

	 	pw.println("rewards \"time\"");
	 	for(int j=0; j < maxActionP2; j++) {
	 		pw.println("[end] true: rs_dur"+j+";");
	 	}	 	
	 	pw.println("endrewards");

		pw.println("rewards \"reliability\"");
	 	for(int j=0; j < maxActionP2; j++) {
	 		pw.println("[end] true: rs_rel"+j+";");
	 	}	 	
	 	pw.println("endrewards");

		pw.println("rewards \"utility\"");
	 	pw.println("[end] true: uv_all;");
	 	pw.println("endrewards");
	 	pw.println();
	 	
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (end=true);");

	 	pw.close();
    }

	/**
	 * To generate sequence-based stochastic games model 
	 */
	public void generateSeqModel()  {
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int n=0; n < numNode; n++) {
   	 		//if (n != numNode) {
			//	pw.print(",");
			//}
   	 		for(int i=0; i < maxActionP1; i++) {
   	 			pw.print("[n"+n+"r"+i+"],");
   	 		//	if (i != maxActionP1-1) {
   	 		//		pw.print(",");
   	 		//	}
   	 		}
		}
   	 	pw.println("[be],[end]");
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
   	 	pw.print(""+mod2);
   	 	for(int n=0; n < numNode; n++) {
	   	 	if (n != numNode) {
				pw.print(",");
			}
   	 		for(int i=0; i < maxActionP2; i++) {
   	 			pw.print("[n"+n+"e"+i+"]");
   	 			if (i != maxActionP2-1) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
	 	
   		pw.println("//=========User Requirements=======");
   		if (this.setValuesStatus) {
		 	pw.println("const int A0_ID = 0;");
		 	pw.println("const int A0_DUR = "+this.durR+"; //cost"); 
		 	pw.println("const double A0_REL = "+this.relR+"; //reliability");
		 	pw.println("const double A0_COST= "+this.costR+"; //max cost");
		 	pw.println("const double A0_WG_COST = "+this.wcostR+"; //weight for cost");
		 	pw.println("const double A0_WG_DUR = "+this.wdurR+"; //weight for duration");
		 	pw.println("const double A0_WG_REL = "+this.wrelR+"; //weight for reliability");
		 	pw.println();
   		}
   		else {
   		 	pw.println("const int A0_ID; // = 0;");
   		 	pw.println("const int A0_DUR; // = 5;	//max duration");
   		 	pw.println("const double A0_REL; // = 0.6; //reliability");
   		 	pw.println("const double A0_COST; // = 10.0; //max cost");
   		 	pw.println("const double A0_WG_COST; // = 0.3; //weight for cost");
   		 	pw.println("const double A0_WG_DUR; // = 0.3; //weight for duration");
   		 	pw.println("const double A0_WG_REL; // = 0.4; //weight for reliability");
   		 	pw.println();
   		}
   		
   		
	 	pw.println("//=========Resource Profiles=======");
	 	pw.println("const int MXN="+numNode+";");
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("const int N"+n+"_MAX_SV="+maxActionP1+";	//finite number of services");		
	 		pw.println("const int N"+n+"_MAX_EV="+maxActionP2+";	//finite number of computing nodes");		
	 	}
	 	pw.println();
	 	
	 	if(this.setValuesStatus) {
		 	for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID = "+i+";");
			 		pw.println("const int N"+n+"_RS"+i+"_COST = "+this.cost[n][i]+";	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL = "+this.avail[n][i]+";	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+" = "+this.dur[n][i][j]+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+" = "+this.rel[n][i][j]+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 	else {
	 		for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID;");
			 		pw.println("const int N"+n+"_RS"+i+"_COST;	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL;	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 		
		pw.println("//=========Global Parameters=======");
	 	pw.println("const int TE=0;	//plater 2 state");	
	 	pw.println("const int TP=1;	//player 1 state");
		pw.println("const int TS=2;	//coordinator state");
	 	pw.println("global t:[TE..TS] init TS;	//to control the turn");
	 	pw.println("global end : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[0..MXN] init 0;  //to control the sequence");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+":[-1..N"+n+"_MAX_SV-1] init -1;");
	 	}
	 	
	 	pw.println("//P1 moves :");
	 	pw.println("[be] (t=TS) & (n < MXN) -> (t'=TP);");
	 	pw.println("[end] (t=TS) & (n >= MXN) -> (end'=true);");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.println("[n"+n+"r"+i+"] (t=TP) & (n="+n+") & (a0_n"+n+"_rs"+i+"_sat_all=true) -> (n"+n+"'="+i+") & (t'=TE);");
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+"ev:[-1..N"+n+"_MAX_EV] init -1;");
	 	}
	 	pw.println("//P2 moves :");	
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.println("[n"+n+"e"+i+"] (t=TE) & (n="+n+") -> n"+n+"ev"+i+"_rel:(n"+n+"ev'="+i+") & (t'=TS) & (n'=n+1) + 1-n"+n+"ev"+i+"_rel:(n"+n+"ev'=-1) & (t'=TS) & (n'=n+1);");			
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Assign reliability values=======");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.print("formula n"+n+"ev"+i+"_rel =");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(" ( n"+n+" = "+j+" ? N"+n+"_RS"+j+"_REL"+i+" :");
		 		}
		 		pw.print("0.0");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(")");
		 		}
		 	 	pw.print(";");
		 	 	pw.println();
		 	}
		 	pw.println();
	 	}
 		pw.println();
	 	
 	 	pw.println("//=========QoS Constraints Checking=======");
 	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++){
		 		pw.println("//=====Application 0, Node"+n+" and RS"+i);
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_dur"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_DUR"+j+") ? true:false);");
		 		}
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_rel"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_REL"+j+") ? true:false);");
		 		}
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_cost = ( (A0_COST <= N"+n+"_RS"+i+"_COST) ? true:false);");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_avail = N"+n+"_RS"+i+"_AVAIL;");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_all = a0_n"+n+"_rs"+i+"_sat_avail;");
		 		pw.println();
		 	}
 	 	}
	 	pw.println();
 			 	
	 	pw.println("//=========Utility-based Decision Making=======");
	 	
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the cost of selected node..");
	 		pw.print("formula n"+n+"_rs_cost =");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("( n"+n+" = "+i+" ? N"+n+"_RS"+i+"_COST :");
		 	}
		 	pw.print("0.0");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print(")");
	 		}
		 	pw.println(";");
		 	
		 	pw.println("//Computing the utility value..");
		 	pw.print("formula n"+n+"_mx_cost = max(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.print("formula n"+n+"_mn_cost = min(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.println("formula n"+n+"_uv_cost = (n"+n+"_mx_cost - n"+n+"_rs_cost) / (n"+n+"_mx_cost - n"+n+"_mn_cost);");
		 	pw.println();
		 	
	// 	} //end of n
	 	
	 	//Preparing the utility function for duration
	 //	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the duration of selected node..");
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_dur"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_DUR"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute max and min value
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_dur"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	pw.print("formula n"+n+"_mn_dur"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute the utility value
			 	pw.println("formula n"+n+"_uv_dur"+j+" = (n"+n+"_mx_dur"+j+" - n"+n+"_rs_dur"+j+") / (n"+n+"_mx_dur"+j+" - n"+n+"_mn_dur"+j+");");
			 	pw.println();
		 		
		 	}
	// 	}//end of n
	 	
	  	
	 	
	 	//Preparing utility function for reliability
	// 	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the reliability of selected node..");
			for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_rel"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_REL"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute the max
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_rel"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	//compute the min
			 	pw.print("formula n"+n+"_mn_rel"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute utility value
			 	pw.println("formula n"+n+"_uv_rel"+j+" = (n"+n+"_mx_rel"+j+" - n"+n+"_rs_rel"+j+") / (n"+n+"_mx_rel"+j+" - n"+n+"_mn_rel"+j+");");
			 	pw.println();	
		 	}
			pw.println();
	// 	}//end of n
	 	
		pw.println("//Preventing from non-evaluated problem..");
		//for(int n=0; n <numNode; n++) {
			pw.println("formula n"+n+"_ut_cost = (n"+n+"_uv_cost > 0.0 ? n"+n+"_uv_cost: 0.0);");
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_dur"+j+" = (n"+n+"_uv_dur"+j+" > 0.0 ? n"+n+"_uv_dur"+j+": 0.0);");
			}
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_rel"+j+" = (n"+n+"_uv_rel"+j+" > 0.0 ? n"+n+"_uv_rel"+j+": 0.0);");
			}
		 	pw.println();
		
		
			pw.println("//Compute the overall utility value..");
			pw.print("formula n"+n+"_ut_all = ");
			for (int j=0; j < maxActionP2; j++) {
				pw.print("(n"+n+"ev="+j+" ? (n"+n+"_ut_cost * A0_WG_COST + n"+n+"_ut_dur"+j+" * A0_WG_DUR + n"+n+"_ut_rel"+j+" * A0_WG_REL):");
			}
			pw.print("0.0");
			for (int j=0; j < maxActionP2; j++) {
				pw.print(")");
			}
			pw.println(";");
		 	pw.println();
		}//end of n
	 
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"cost\"");
	 	for(int n=0; n<numNode; n++) {
		 	pw.println("[] true: n"+n+"_rs_cost;"); 	
	 	}//end of n
	 	pw.println("endrewards");
	 	
		pw.println("rewards \"time\"");
		for(int n=0; n<numNode; n++) {
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[] true: n"+n+"_rs_dur"+j+";");
		 	}
		}//end of n
	 	pw.println("endrewards");
	
		pw.println("rewards \"reliability\"");
		for(int n=0; n<numNode; n++) {
			for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[] true: n"+n+"_rs_rel"+j+";");
		 	}	
		}
		pw.println("endrewards");
	
		pw.println("rewards \"utility\"");
		for(int n=0; n<numNode; n++) {
		 	pw.println("[] true: n"+n+"_ut_all;");
	 	}//end of n
		pw.println("endrewards");
	 	pw.println();
	 	
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (end=true);");
	 	
	 	pw.close();
    }
	
	
	/**
	 * To generate branching-based stochastic games model 
	 */
	public void generateCondModel()  {
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int n=0; n < numNode; n++) {
   	 		for(int i=0; i < maxActionP1; i++) {
   	 			pw.print("[n"+n+"r"+i+"],");
   	 		}
		}
   	 	pw.println("[be],[end]");
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
   	 	pw.print(""+mod2);
   	 	for(int n=0; n < numNode; n++) {
	   	 	if (n != numNode) {
				pw.print(",");
			}
   	 		for(int i=0; i < maxActionP2; i++) {
   	 			pw.print("[n"+n+"e"+i+"]");
   	 			if (i != maxActionP2-1) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
	 	
   		pw.println("//=========User Requirements=======");
   		if (this.setValuesStatus) {
		 	pw.println("const int A0_ID = 0;");
		 	pw.println("const int A0_DUR = "+this.durR+"; //cost"); 
		 	pw.println("const double A0_REL = "+this.relR+"; //reliability");
		 	pw.println("const double A0_COST= "+this.costR+"; //max cost");
		 	pw.println("const double A0_WG_COST = "+this.wcostR+"; //weight for cost");
		 	pw.println("const double A0_WG_DUR = "+this.wdurR+"; //weight for duration");
		 	pw.println("const double A0_WG_REL = "+this.wrelR+"; //weight for reliability");
		 	pw.println();
   		}
   		else {
   		 	pw.println("const int A0_ID; // = 0;");
   		 	pw.println("const int A0_DUR; // = 5;	//max duration");
   		 	pw.println("const double A0_REL; // = 0.6; //reliability");
   		 	pw.println("const double A0_COST; // = 10.0; //max cost");
   		 	pw.println("const double A0_WG_COST; // = 0.3; //weight for cost");
   		 	pw.println("const double A0_WG_DUR; // = 0.3; //weight for duration");
   		 	pw.println("const double A0_WG_REL; // = 0.4; //weight for reliability");
   		 	pw.println();
   		}
   		
   		
	 	pw.println("//=========Resource Profiles=======");
	 	pw.println("const int MXN="+numNode+";");
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("const int N"+n+"_MAX_SV="+maxActionP1+";	//finite number of services");		
	 		pw.println("const int N"+n+"_MAX_EV="+maxActionP2+";	//finite number of computing nodes");		
	 	}
	 	pw.println();
	 	
	 	if(this.setValuesStatus) {
		 	for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID = "+i+";");
			 		pw.println("const int N"+n+"_RS"+i+"_COST = "+this.cost[n][i]+";	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL = "+this.avail[n][i]+";	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+" = "+this.dur[n][i][j]+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+" = "+this.rel[n][i][j]+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 	else {
	 		for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID;");
			 		pw.println("const int N"+n+"_RS"+i+"_COST;	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL;	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 		
		pw.println("//=========Global Parameters=======");
	 	pw.println("const int TE=0;	//plater 2 state");	
	 	pw.println("const int TP=1;	//player 1 state");
		pw.println("const int TS=2;	//coordinator state");
	 	pw.println("global t:[TE..TS] init TS;	//to control the turn");
	 	pw.println("global end : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[0..MXN] init 0;  //to control the sequence");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+":[-1..N"+n+"_MAX_SV-1] init -1;");
	 	}
	 	
	 	pw.println("//P1 moves :");
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("[be] (t=TS) & (n < MXN) -> (n'="+n+") & (t'=TP);");
	 	}
	 	pw.println("[end] (t=TS) & (n >= MXN) -> (end'=true);");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.println("[n"+n+"r"+i+"] (t=TP) & (n="+n+") & (a0_n"+n+"_rs"+i+"_sat_all=true) -> (n"+n+"'="+i+") & (t'=TE);");
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+"ev:[-1..N"+n+"_MAX_EV] init -1;");
	 	}
	 	pw.println("//P2 moves :");	
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.println("[n"+n+"e"+i+"] (t=TE) & (n="+n+") -> n"+n+"ev"+i+"_rel:(n"+n+"ev'="+i+") & (t'=TS) & (n'=MXN) + 1-n"+n+"ev"+i+"_rel:(n"+n+"ev'=-1) & (t'=TS) & (n'=MXN);");			
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Assign reliability values=======");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.print("formula n"+n+"ev"+i+"_rel =");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(" ( n"+n+" = "+j+" ? N"+n+"_RS"+j+"_REL"+i+" :");
		 		}
		 		pw.print("0.0");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(")");
		 		}
		 	 	pw.print(";");
		 	 	pw.println();
		 	}
		 	pw.println();
	 	}
 		pw.println();
	 	
 	 	pw.println("//=========QoS Constraints Checking=======");
 	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++){
		 		pw.println("//=====Application 0, Node"+n+" and RS"+i);
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_dur"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_DUR"+j+") ? true:false);");
		 		}
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_rel"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_REL"+j+") ? true:false);");
		 		}
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_cost = ( (A0_COST <= N"+n+"_RS"+i+"_COST) ? true:false);");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_avail = N"+n+"_RS"+i+"_AVAIL;");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_all = a0_n"+n+"_rs"+i+"_sat_avail;");
		 		pw.println();
		 	}
 	 	}
	 	pw.println();
 			 	
	 	pw.println("//=========Utility-based Decision Making=======");
	 	
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the cost of selected node..");
	 		pw.print("formula n"+n+"_rs_cost =");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("( n"+n+" = "+i+" ? N"+n+"_RS"+i+"_COST :");
		 	}
		 	pw.print("0.0");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print(")");
	 		}
		 	pw.println(";");
		 	
		 	pw.println("//Computing the utility value..");
		 	pw.print("formula n"+n+"_mx_cost = max(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.print("formula n"+n+"_mn_cost = min(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.println("formula n"+n+"_uv_cost = (n"+n+"_mx_cost - n"+n+"_rs_cost) / (n"+n+"_mx_cost - n"+n+"_mn_cost);");
		 	pw.println();
		 	
	// 	} //end of n
	 	
	 	//Preparing the utility function for duration
	 //	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the duration of selected node..");
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_dur"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_DUR"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute max and min value
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_dur"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	pw.print("formula n"+n+"_mn_dur"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute the utility value
			 	pw.println("formula n"+n+"_uv_dur"+j+" = (n"+n+"_mx_dur"+j+" - n"+n+"_rs_dur"+j+") / (n"+n+"_mx_dur"+j+" - n"+n+"_mn_dur"+j+");");
			 	pw.println();
		 		
		 	}
	// 	}//end of n
	 	
	  	
	 	
	 	//Preparing utility function for reliability
	// 	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the reliability of selected node..");
			for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_rel"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_REL"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute the max
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_rel"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	//compute the min
			 	pw.print("formula n"+n+"_mn_rel"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute utility value
			 	pw.println("formula n"+n+"_uv_rel"+j+" = (n"+n+"_mx_rel"+j+" - n"+n+"_rs_rel"+j+") / (n"+n+"_mx_rel"+j+" - n"+n+"_mn_rel"+j+");");
			 	pw.println();	
		 	}
			pw.println();
	// 	}//end of n
	 	
		pw.println("//Preventing from non-evaluated problem..");
		//for(int n=0; n <numNode; n++) {
			pw.println("formula n"+n+"_ut_cost = (n"+n+"_uv_cost > 0.0 ? n"+n+"_uv_cost: 0.0);");
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_dur"+j+" = (n"+n+"_uv_dur"+j+" > 0.0 ? n"+n+"_uv_dur"+j+": 0.0);");
			}
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_rel"+j+" = (n"+n+"_uv_rel"+j+" > 0.0 ? n"+n+"_uv_rel"+j+": 0.0);");
			}
		 	pw.println();
		
		
			pw.println("//Compute the overall utility value..");
			pw.print("formula n"+n+"_ut_all = ");
			for (int j=0; j < maxActionP2; j++) {
				pw.print("(n"+n+"ev="+j+" ? (n"+n+"_ut_cost * A0_WG_COST + n"+n+"_ut_dur"+j+" * A0_WG_DUR + n"+n+"_ut_rel"+j+" * A0_WG_REL):");
			}
			pw.print("0.0");
			for (int j=0; j < maxActionP2; j++) {
				pw.print(")");
			}
			pw.println(";");
		 	pw.println();
		}//end of n
	 
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"cost\"");
	 	for(int n=0; n<numNode; n++) {
		 	pw.println("[end] true: n"+n+"_rs_cost;"); 	
	 	}//end of n
	 	pw.println("endrewards");
	 	
		pw.println("rewards \"time\"");
		for(int n=0; n<numNode; n++) {
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[end] true: n"+n+"_rs_dur"+j+";");
		 	}
		}//end of n
	 	pw.println("endrewards");
	
		pw.println("rewards \"reliability\"");
		for(int n=0; n<numNode; n++) {
			for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[end] true: n"+n+"_rs_rel"+j+";");
		 	}	
		}
		pw.println("endrewards");
	
		pw.println("rewards \"utility\"");
		for(int n=0; n<numNode; n++) {
		 	pw.println("[end] true: n"+n+"_ut_all;");
	 	}//end of n
		pw.println("endrewards");
	 	pw.println();
	 	
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (end=true);");
	 	
	 	pw.close();
    }
	
	
	/**
	 * To generate a generic stochastic games model for service selection 
	 */
	public void generateSGModel(int initialNode)  {
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int n=0; n < numNode; n++) {
   	 		for(int i=0; i < maxActionP1; i++) {
   	 			pw.print("[n"+n+"r"+i+"],");
   	 		}
		}
   	 	pw.println("[be],[end],[ter]");
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
   	 	pw.print(""+mod2);
   	 	for(int n=0; n < numNode; n++) {
	   	 	if (n != numNode) {
				pw.print(",");
			}
   	 		for(int i=0; i < maxActionP2; i++) {
   	 			pw.print("[n"+n+"e"+i+"]");
   	 			if (i != maxActionP2-1) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
	 	
   		pw.println("//=========User Requirements=======");
   		if (this.setValuesStatus) {
		 	pw.println("const int A0_ID = 0;");
		 	pw.println("const int A0_DUR = "+this.durR+"; //cost"); 
		 	pw.println("const double A0_REL = "+this.relR+"; //reliability");
		 	pw.println("const double A0_COST= "+this.costR+"; //max cost");
		 	pw.println("const double A0_WG_COST = "+this.wcostR+"; //weight for cost");
		 	pw.println("const double A0_WG_DUR = "+this.wdurR+"; //weight for duration");
		 	pw.println("const double A0_WG_REL = "+this.wrelR+"; //weight for reliability");
		 	pw.println();
   		}
   		else {
   		 	pw.println("const int A0_ID; // = 0;");
   		 	pw.println("const int A0_DUR; // = 5;	//max duration");
   		 	pw.println("const double A0_REL; // = 0.6; //reliability");
   		 	pw.println("const double A0_COST; // = 10.0; //max cost");
   		 	pw.println("const double A0_WG_COST; // = 0.3; //weight for cost");
   		 	pw.println("const double A0_WG_DUR; // = 0.3; //weight for duration");
   		 	pw.println("const double A0_WG_REL; // = 0.4; //weight for reliability");
   		 	pw.println();
   		}
   		
   		
	 	pw.println("//=========Resource Profiles=======");
	 	pw.println("const int MXN="+numNode+";");
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("const int N"+n+"_MAX_SV="+maxActionP1+";	//finite number of services");		
	 		pw.println("const int N"+n+"_MAX_EV="+maxActionP2+";	//finite number of computing nodes");		
	 	}
	 	pw.println();
	 	
	 	if(this.setValuesStatus) {
		 	for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID = "+i+";");
			 		pw.println("const int N"+n+"_RS"+i+"_COST = "+this.cost[n][i]+";	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL = "+this.avail[n][i]+";	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+" = "+this.dur[n][i][j]+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+" = "+this.rel[n][i][j]+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 	else {
	 		for (int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.println("const int N"+n+"_RS"+i+"_ID;");
			 		pw.println("const int N"+n+"_RS"+i+"_COST;	//cost");
			 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL;	//availability status");
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+";	//duration "+j+";");
			 		}
			 		for(int j=0; j < maxActionP2; j++) {
			 			pw.println("const double N"+n+"_RS"+i+"_REL"+j+";	//reliability "+j+";");
			 		}
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 		
		pw.println("//=========Global Parameters=======");
	 	pw.println("const int TE=0;	//plater 2 state");	
	 	pw.println("const int TP=1;	//player 1 state");
		pw.println("const int TS=2;	//coordinator state");
		pw.println("const int NI="+initialNode+";	//coordinator state");
	 	pw.println("global t:[TE..TS] init TS;	//to control the turn");
	 	pw.println("global end : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[0..MXN] init NI;  //to control the sequence");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+":[-1..N"+n+"_MAX_SV-1] init -1;");
	 	}
	 	
	 	pw.println("//P1's coordinator :");
	 	
	 	if(this.pattern <= 1) {
	 		//if single or sequential pattern
	 		pw.println("//for single or sequential pattern");
	 		pw.println("[be] (t=TS) & (n < MXN) -> (t'=TP);");
	 	}
	 	
	 	if (this.pattern == 2) {
	 		//if conditional pattern
	 		pw.println("//for conditional pattern");
		 	for(int n=0; n < numNode; n++) {
		 		pw.println("[be] (t=TS) & (n < MXN) -> (n'="+n+") & (t'=TP);");
		 	}
		}
	 	
	 	if (this.pattern == 3) {
	 		//if parallel pattern
	 		pw.println("//for parallel pattern");
		 	for(int n=0; n < numNode; n++) {
		 		pw.println("[be] (t=TS) & (n < MXN) -> (t'=TP);");
		 	}
		}
	 	
	 	pw.println("[end] (t=TS) & (n >= MXN) & (end=false) -> (end'=true); //for ending the selection");
 		pw.println("[ter] (t=TS) & (n >= MXN) & (end=true) -> true;	//for absorbing state");
 		
	 	pw.println("//P1 moves :");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.println("[n"+n+"r"+i+"] (t=TP) & (n="+n+") & (a0_n"+n+"_rs"+i+"_sat_all=true) -> (n"+n+"'="+i+") & (t'=TE);");
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("n"+n+"ev:[-1..N"+n+"_MAX_EV] init -1;");
	 	}
	 	
	 	if (pattern <= 1) {
			pw.println("//P2 moves for single or sequential pattern:");	
		 	for(int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP2; i++) {
			 		pw.println("[n"+n+"e"+i+"] (t=TE) & (n="+n+") -> n"+n+"ev"+i+"_rel:(n"+n+"ev'="+i+") & (t'=TS) & (n'=n+1) + 1-n"+n+"ev"+i+"_rel:(n"+n+"ev'=-1) & (t'=TS) & (n'=MXN);");			
			 	}
		 	}
	 	}
	 	if (pattern >= 2) {
		 	pw.println("//P2 moves for conditional/parallel pattern:");	
		 	for(int n=0; n < numNode; n++) {
			 	for(int i=0; i < maxActionP2; i++) {
			 		pw.println("[n"+n+"e"+i+"] (t=TE) & (n="+n+") -> n"+n+"ev"+i+"_rel:(n"+n+"ev'="+i+") & (t'=TS) & (n'=MXN) + 1-n"+n+"ev"+i+"_rel:(n"+n+"ev'=-1) & (t'=TS) & (n'=MXN);");			
			 	}
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Assign reliability values=======");
	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.print("formula n"+n+"ev"+i+"_rel =");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(" ( n"+n+" = "+j+" ? N"+n+"_RS"+j+"_REL"+i+" :");
		 		}
		 		pw.print("0.0");
		 		for(int j=0; j < maxActionP1; j++) {
		 			pw.print(")");
		 		}
		 	 	pw.print(";");
		 	 	pw.println();
		 	}
		 	pw.println();
	 	}
 		pw.println();
	 	
 	 	pw.println("//=========QoS Constraints Checking=======");
 	 	for(int n=0; n < numNode; n++) {
		 	for(int i=0; i < maxActionP1; i++){
		 		pw.println("//=====Application 0, Node"+n+" and RS"+i);
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_dur"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_DUR"+j+") ? true:false);");
		 		}
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("formula a0_n"+n+"_rs"+i+"_sat_rel"+j+" = ( (A0_DUR <= N"+n+"_RS"+i+"_REL"+j+") ? true:false);");
		 		}
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_cost = ( (A0_COST <= N"+n+"_RS"+i+"_COST) ? true:false);");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_avail = N"+n+"_RS"+i+"_AVAIL;");
		 		pw.println("formula a0_n"+n+"_rs"+i+"_sat_all = a0_n"+n+"_rs"+i+"_sat_avail;");
		 		pw.println();
		 	}
 	 	}
	 	pw.println();
 			 	
	 	pw.println("//=========Utility-based Decision Making=======");
	 	
	 	for(int n=0; n < numNode; n++) {
	 		pw.println("//get the cost of selected node..");
	 		pw.print("formula n"+n+"_rs_cost =");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("( n"+n+" = "+i+" ? N"+n+"_RS"+i+"_COST :");
		 	}
		 	pw.print("0.0");
	 		for(int i=0; i < maxActionP1; i++) {
	 			pw.print(")");
	 		}
		 	pw.println(";");
		 	
		 	pw.println("//Computing the utility value..");
		 	pw.print("formula n"+n+"_mx_cost = max(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.print("formula n"+n+"_mn_cost = min(");
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.print("N"+n+"_RS"+i+"_COST");
		 		if (i != maxActionP1-1)
		 			pw.print(",");
		 	}
		 	pw.println(");");
		 	pw.println("formula n"+n+"_uv_cost = (n"+n+"_mx_cost - n"+n+"_rs_cost) / (n"+n+"_mx_cost - n"+n+"_mn_cost);");
		 	pw.println();
		 	
		 	//get the duration of selected node...
	 		pw.println("//get the duration of selected node..");
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_dur"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_DUR"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute max and min value
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_dur"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	pw.print("formula n"+n+"_mn_dur"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_DUR"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute the utility value
			 	pw.println("formula n"+n+"_uv_dur"+j+" = (n"+n+"_mx_dur"+j+" - n"+n+"_rs_dur"+j+") / (n"+n+"_mx_dur"+j+" - n"+n+"_mn_dur"+j+");");
			 	pw.println();
		 		
		 	}
	
	 		pw.println("//get the reliability of selected node..");
			for(int j=0; j < maxActionP2; j++) {
		 		pw.print("formula n"+n+"_rs_rel"+j+"=");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print("( (n"+n+" = "+i+" & n"+n+"ev = "+j+") ? N"+n+"_RS"+i+"_REL"+j+" :");
		 		}
		 		pw.print("0.0");
		 		for(int i=0; i < maxActionP1; i++) {
		 			pw.print(")");
		 		}
		 		pw.println(";");
		 		
		 		//compute the max
			 	pw.println("//Computing the utility value..");
			 	pw.print("formula n"+n+"_mx_rel"+j+" = max(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	//compute the min
			 	pw.print("formula n"+n+"_mn_rel"+j+" = min(");
			 	for(int i=0; i < maxActionP1; i++) {
			 		pw.print("N"+n+"_RS"+i+"_REL"+j);
			 		if (i != maxActionP1-1)
			 			pw.print(",");
			 	}
			 	pw.println(");");
			 	
			 	//compute utility value
			 	pw.println("formula n"+n+"_uv_rel"+j+" = (n"+n+"_mx_rel"+j+" - n"+n+"_rs_rel"+j+") / (n"+n+"_mx_rel"+j+" - n"+n+"_mn_rel"+j+");");
			 	pw.println();	
		 	}
			pw.println();
	
	 	
			pw.println("//Preventing from non-evaluated problem..");
			pw.println("formula n"+n+"_ut_cost = (n"+n+"_uv_cost > 0.0 ? n"+n+"_uv_cost: 0.0);");
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_dur"+j+" = (n"+n+"_uv_dur"+j+" > 0.0 ? n"+n+"_uv_dur"+j+": 0.0);");
			}
			for(int j=0; j < maxActionP2; j++) {
				pw.println("formula n"+n+"_ut_rel"+j+" = (n"+n+"_uv_rel"+j+" > 0.0 ? n"+n+"_uv_rel"+j+": 0.0);");
			}
		 	pw.println();
		
		
			pw.println("//Compute the overall utility value..");
			pw.print("formula n"+n+"_ut_all = ");
			for (int j=0; j < maxActionP2; j++) {
				pw.print("(n"+n+"ev="+j+" ? (n"+n+"_ut_cost * A0_WG_COST + n"+n+"_ut_dur"+j+" * A0_WG_DUR + n"+n+"_ut_rel"+j+" * A0_WG_REL):");
			}
			pw.print("0.0");
			for (int j=0; j < maxActionP2; j++) {
				pw.print(")");
			}
			pw.println(";");
		 	pw.println();
		}//end of n
	 
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"cost\"");
	 	for(int n=0; n<numNode; n++) {
		 	pw.println("[end] true: n"+n+"_rs_cost;"); 	
	 	}//end of n
	 	pw.println("endrewards");
	 	
		pw.println("rewards \"time\"");
		for(int n=0; n<numNode; n++) {
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[end] true: n"+n+"_rs_dur"+j+";");
		 	}
		}//end of n
	 	pw.println("endrewards");
	
		pw.println("rewards \"reliability\"");
		for(int n=0; n<numNode; n++) {
			for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[end] true: n"+n+"_rs_rel"+j+";");
		 	}	
		}
		pw.println("endrewards");
	
		pw.println("rewards \"utility\"");
		for(int n=0; n<numNode; n++) {
		 	pw.println("[end] true: n"+n+"_ut_all;");
	 	}//end of n
		pw.println("endrewards");
	 	pw.println();
	 	
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (end=true);");
	 	
	 	pw.close();
    }
	
	
	public void generateProperties() {
		
		//pp.println("const double MAXCS = 300;");
		//pp.println("const double MAXFR = 0.25;");
		//pp.println("const double MAXRT = 200;");
		
		pp.println("const int MAXCS = "+this.costR+";");
		pp.println("const int MAXRT = "+this.durR+";");
		pp.println("const double MAXFR = "+this.relR+";");
		
		
		//pp.println("const int MINCS = 20;");
		//pp.println("const int MINRT = 40;");
		//pp.println();
		pp.println("<<p1>> R{\"cost\"}max=? [ F \"done\" ]");
		pp.println("<<p1>> R{\"time\"}max=? [ F \"done\" ]");
		pp.println("<<p1>> R{\"reliability\"}max=? [ F \"done\" ]");
		pp.println("<<p1>> R{\"utility\"}max=? [ F \"done\" ]");
		pp.println("<<p1>> (R{\"cost\"}<=MAXCS [C] & R{\"time\"}<=MAXRT [C ])");
				
		pp.close();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String pathSingle = "/home/azlan/git/PrismGames/Prismfiles/singlemodel.prism";
		String pathSequential = "/home/azlan/git/PrismGames/Prismfiles/seqmodel.prism";
		String pathCondition = "/home/azlan/git/PrismGames/Prismfiles/condmodel.prism";
		String pathParallel = "/home/azlan/git/PrismGames/Prismfiles/parmodel.prism";
		
		int pattern = 1;	//0-single, 1-sequential, 2-conditional, 3-parallel
		int numNode = 2;
		int numofService = 20;
		int numofResource = 2;
		
		int durR=10, costR=20;
		double relR=0.9, wcostR=0.3, wdurR=0.3, wrelR=0.4;
				
		ModelGenerator mdg = new ModelGenerator();
		mdg.setValuesStatus(true);
		mdg.setPattern(pattern);
		
		//set all the relevant parameters
		mdg.setParamsNames("p1", "p2", "planner", "environment");
		mdg.setUpperBounds(numNode, numofService, numofResource);
		mdg.setRequirements(0, costR, durR, relR, wcostR, wdurR, wrelR);
		mdg.setAllRandomServProfiles();
		
		
		if (pattern==0) {
			if(numNode <= 1) {
				mdg.setModelPath(pathSingle);										
				mdg.generateSGModel(0);
			}
			else
				System.out.println("Require number of node to be 1 for single node...");	
		
		}
		
		else if (pattern==1) {
			//for sequential
			mdg.setModelPath(pathSequential);										
			mdg.generateSGModel(0);
		}
		
		else if (pattern==2) {
			//for conditional
			mdg.setModelPath(pathCondition);										
			mdg.generateSGModel(0);		
		}
		else
			System.out.println("no pattern has been selected");
			
		
		
		
		System.out.println("Model has been generated...");
		
		//StochasticPlanner sp = new StochasticPlanner();

		
		//get the inputs; 
		//1) number of services(p1's actions)
		//2) number of servers(p2's actions)
		
		//generate the model
		
	}

}
