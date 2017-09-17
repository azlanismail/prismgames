package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import parser.Values;

public class AdaptationModelGenerator extends ModelGenerator{

	int numQuality;
	double cost[][], avail[][], rel[][], res[][];
	int time[][];
	
	double costS[], availS[], relS[], resS[];
	int timeS[];
	
	double reqResource;
	
	//constants quality parameters
	QualityAttributes[][][] qy;
	QualityAttributes[][] qyS;
	String idParams[][], costParams[][], availParams[][], timeParams[][], relParams[][], resParams[][];
	String idParamsS[], costParamsS[], availParamsS[], timeParamsS[], relParamsS[], resParamsS[];
	
	public AdaptationModelGenerator() {
		
	}
	
	/**
	 * can be referred to the number of application / functionality
	 * @param numNode
	 */
	public void setNumNode(int numNode) {
		super.numNode = numNode;
	}
	
	/**
	 * Not yet utilize........
	 * @param mxActionP1
	 * @param mxActionP2
	 * @param numQuality
	 */
	public void setQualityAttributesBounds(int mxActionP1, int mxActionP2, int numQuality) {
		super.maxActionP1 = mxActionP1; //to set max number of collaborator
		super.maxActionP2 = mxActionP2;  //to set max number of virtual machine
		this.numQuality = numQuality;
		
		if (mxActionP2 > 0) {
			//if number of action > 0 and number of variation > 0
			this.qy = new QualityAttributes[maxActionP1][maxActionP2][numQuality];
			
		}
		else {
			//if number of action > 0 but number of variation == 0
			this.qyS = new QualityAttributes[maxActionP1][numQuality];
		}	
		
	}
	
	public void setUpperBounds(int numNode, int mxActionP1, int mxActionP2) {
		super.numNode = numNode; //to set the max number of application
		super.maxActionP1 = mxActionP1; //to set max number of collaborator
		super.maxActionP2 = mxActionP2;  //to set max number of virtual machine
		
		if (mxActionP2 > 0) {
			//if number of action > 0 and number of variation > 0
			this.cost = new double[maxActionP1][maxActionP2];
			this.avail = new double[maxActionP1][maxActionP2];
			this.time = new int[maxActionP1][maxActionP2];
			this.rel = new double[maxActionP1][maxActionP2];
			this.res = new double[maxActionP1][maxActionP2];
		}
		else {
			//if number of action > 0 but number of variation == 0
			this.costS = new double[maxActionP1];
			this.availS = new double[maxActionP1];
			this.timeS = new int[maxActionP1];
			this.relS = new double[maxActionP1];
			this.resS = new double[maxActionP1];
		}	
	}
	
	public void setAppResource(double val) {
		this.reqResource = val;
	}
	
	public void setAllRandomServProfiles() {
		Random rand = new Random();
		
		//initialize values for each resource / variation
		double cost, rel, avail, res;
		int time;
		
		//initialize range value for reliability
		double minRel=0.8, maxRel=1.0;
		double rangeRel = maxRel - minRel;
		
		//initialize range value for availability
		double minAvail=0.8, maxAvail=1.0;
		double rangeAvail = maxAvail - minAvail;
		
		//initialize range value for the resource
		double minRes=0.1, maxRes=0.5;
		double rangeRes = maxRes - minRes;
		
		//setting up value for the application resource
		double appR = rand.nextDouble() * rangeRes + minRes;
		setAppResource(appR);
		
		for(int i=0; i < this.maxActionP1; i++) {
			if (maxActionP2 > 0) {
				for(int j=0; j < this.maxActionP2; j++) {
					cost = rand.nextInt(50) + 60;
					time = rand.nextInt(100) + 800;
					rel = rand.nextDouble() * rangeRel + minRel;
					avail = rand.nextDouble() * rangeAvail + minAvail;
					res = rand.nextDouble() * rangeRes + minRes;
					setProfiles(i, j, cost, avail, time, rel, res);
				}
			}
			else {
				//assuming no variation
				cost = rand.nextInt(50) + 50;
				time = rand.nextInt(100) + 500;
				rel = rand.nextDouble() * rangeRel + minRel;
				avail = rand.nextDouble() * rangeAvail + minAvail;
				res = rand.nextDouble() * rangeRes + minRes;
				setProfilesforSingle(i,cost, avail, time, rel, res);
			}
		}
	}
	
	public void setProfiles(int i, int j, double cost, double avail, int dur, double rel, double res ) {
		
		this.cost[i][j] = cost;
		this.avail[i][j] = avail;
		this.time[i][j] = dur;
		this.rel[i][j] = rel;
		this.res[i][j] = res;
	}
	
	public void setProfilesforSingle(int i, double cost, double avail, int dur, double rel, double res ) {
		
		this.costS[i] = cost;
		this.availS[i] = avail;
		this.timeS[i] = dur;
		this.relS[i] = rel;
		this.resS[i] = res;
	}
	
	/**
	 * prepare the arrays to hold the identifier of the constant parameters 
	 */
	public void createQualityParams() {
		
		if (maxActionP2 > 0) {
			this.idParams = new String[this.maxActionP1][this.maxActionP2];
			this.costParams = new String[this.maxActionP1][this.maxActionP2];
			this.availParams = new String[this.maxActionP1][this.maxActionP2];
			this.timeParams = new String[this.maxActionP1][this.maxActionP2];
			this.relParams = new String[this.maxActionP1][this.maxActionP2];
			this.resParams = new String[this.maxActionP1][this.maxActionP2];
			
			for(int i=0; i < this.maxActionP1; i++) {
				for(int j=0; j < this.maxActionP2; j++) {
					this.idParams[i][j] = "n"+i+"rs"+j+"_id";
					this.costParams[i][j] = "n"+i+"rs"+j+"_cost";
					this.availParams[i][j] = "n"+i+"rs"+j+"_avail";
					this.timeParams[i][j] = "n"+i+"rs"+j+"_time";
					this.relParams[i][j] = "n"+i+"rs"+j+"_rel";
					this.resParams[i][j] = "n"+i+"rs"+j+"_res";
				}
			}
		}else {
			this.idParamsS = new String[this.maxActionP1];
			this.costParamsS = new String[this.maxActionP1];
			this.availParamsS = new String[this.maxActionP1];
			this.timeParamsS = new String[this.maxActionP1];
			this.relParamsS = new String[this.maxActionP1];
			this.resParamsS = new String[this.maxActionP1];
			
			for(int i=0; i < this.maxActionP1; i++) {
				this.idParamsS[i] = "n"+i+"_id";
				this.costParamsS[i] = "n"+i+"_cost";
				this.availParamsS[i] = "n"+i+"_avail";
				this.timeParamsS[i] = "n"+i+"_time";
				this.relParamsS[i] = "n"+i+"_rel";
				this.resParamsS[i] = "n"+i+"_res";
			}
		}
	}
	
	/**
	 * Assign values to the parameters during runtime
	 */
	public void setQualityParamswithValues() {
		
		if (vm == null) {
			vm = new Values();
		}
		
		vm.addValue("MXN", this.maxActionP1);
		vm.addValue("app_rs", this.reqResource);
		
		if (maxActionP2 > 0) {
			for(int i=0; i < this.maxActionP1; i++) {
				for(int j=0; j < this.maxActionP2; j++) {
					vm.addValue(this.idParams[i][j], i); 
					vm.addValue(this.costParams[i][j], this.cost[i][j]);
					vm.addValue(this.availParams[i][j], this.avail[i][j]);
					vm.addValue(this.timeParams[i][j], this.time[i][j]);
					vm.addValue(this.relParams[i][j], this.rel[i][j]);
					vm.addValue(this.resParams[i][j], this.res[i][j]);
				}
			}
		}
		else {
			for(int i=0; i < this.maxActionP1; i++) {
				vm.addValue(this.idParamsS[i], i); 
				vm.addValue(this.costParamsS[i], this.costS[i]);
				vm.addValue(this.availParamsS[i], this.availS[i]);
				vm.addValue(this.timeParamsS[i], this.timeS[i]);
				vm.addValue(this.relParamsS[i], this.relS[i]);
				vm.addValue(this.resParamsS[i], this.resS[i]);
			}
		}
	}
	
	public void exportActionList(String path) { 
		
		try {
			pa = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i < maxActionP1; i++) {
   	 		pa.println("r"+i);
		}
		pa.close();
	}
	
	/**
	 * To generate a generic stochastic games model with single variation
	 */
	public void encodeSGModelforSimpleAppDeployment()  {
   	 	
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
   	 	pw.print(""+mod2);
   		for(int i=0; i < maxActionP1; i++) {
	   	 	if (i != maxActionP1) {
				pw.print(",");
			}
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.print("[n"+i+"rs"+j+"]");
   	 			if (j != maxActionP2-1) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
	 
   		
	 	pw.println("//=========Resource Profiles=======");
	 	//pw.println("const int MXN="+numNode+";");
	 	//for(int n=0; n < numNode; n++) {
	 	//	pw.println("const int N"+n+"_MAX_SV="+maxActionP1+";	//finite number of services");		
	 	//	pw.println("const int N"+n+"_MAX_EV="+maxActionP2+";	//finite number of computing nodes");		
	 	//}
	 	//pw.println();
	 	pw.println("const int MXN="+maxActionP1+";");
	 	if(this.setValuesStatus) {
		 	for (int i=0; i < maxActionP1; i++) {
			 	for(int j=0; j < maxActionP2; j++) {
			 		pw.println("const int "+ this.idParams[i][j]+" = "+i+";");
			 		pw.println("const double "+this.costParams[i][j]+" = "+this.cost[i][j]+";	//cost");
			 		pw.println("const double "+this.availParams[i][j]+" = "+this.avail[i][j]+";	//avail");
			 		pw.println("const double "+this.relParams[i][j]+" = "+this.rel[i][j]+";		//rel");
			 		pw.println("const int "+this.timeParams[i][j]+" = "+this.time[i][j]+";		//time");
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 	else {
	 		for (int i=0; i < maxActionP1; i++) {
			 	for(int j=0; j < maxActionP2; j++) {
			 		pw.println("const int "+ this.idParams[i][j]+" = "+i+";");
			 		pw.println("const double "+this.costParams[i][j]+";	//cost");
			 		pw.println("const double "+this.availParams[i][j]+"; //avail");
			 		pw.println("const double "+this.relParams[i][j]+";	//rel");
			 		pw.println("const time "+this.timeParams[i][j]+";	//time");
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 		
		pw.println("//=========Global Parameters=======");
	 	pw.println("const int TP=0;	//plater 1 state");	
	 	pw.println("const int TE=1;	//player 2 state");
		pw.println("const int NI=-1;	//initial node");
	 	pw.println("global t:[TP..TE] init TP;	//to control the turn");
	 	pw.println("global goal : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[-1..MXN] init NI;  //number of computing node");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1); 	
	 	pw.println("//P1's coordinator :");
	 	
	 	pw.println("[end] (t=TP) & (goal=true) -> true; //to end the selection");
 		 		
	 	pw.println("//P1 moves :");
	 	for(int i=0; i< maxActionP1; i++) {
		 	pw.println("[r"+i+"] (t=TP) & (goal=false) -> (n'="+i+") & (t'=TE);");

	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 
	 	pw.println("//P2 moves for single or sequential pattern:");	
		for(int i=0; i < maxActionP1; i++) {
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[n"+i+"rs"+j+"] (t=TE) & (n="+i+") -> n"+i+"rs"+j+"_rel:(goal'=true) & (t'=TP) + 1-n"+i+"rs"+j+"_rel:(goal'=false) & (t'=TP);");
		 	}
		 	pw.println();
		}
	 	
	 	pw.println("endmodule");
	 	pw.println();
	 
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"rw_cost\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_cost;");
   	 		}
		}
	 	pw.println("endrewards");
	 	
		pw.println("rewards \"rw_time\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_time;");
   	 		}
		}
	 	pw.println("endrewards");
	
		pw.println("rewards \"rw_reliability\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_rel;");
   	 		}
		}
	 	pw.println("endrewards");
		
		pw.println("rewards \"rw_availability\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_avail;");
   	 		}
		}
	 	pw.println("endrewards");
		
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (goal=true);");
	 	
	 	pw.close();
    }
	
	
	/**
	 * To generate a generic stochastic games model for service selection with multiple variation 
	 */
	public void encodeSGModelforComplexAppDeployment()  {
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int i=0; i < this.maxActionP1; i++) {
   	 		pw.print("[r"+i+"],");
		}
   	 	pw.println("[end]");
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
   	 	pw.print(""+mod2);
   		for(int i=0; i < this.maxActionP1; i++) {
	   	 	if (i != this.maxActionP1) {
				pw.print(",");
			}
   	 		for(int j=0; j < this.maxActionP2; j++) {
   	 			pw.print("[n"+i+"rs"+j+"]");
   	 			if (j != this.maxActionP2-1) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
	 
   		
	 	pw.println("//=========Resource Profiles=======");
	 	
	 	//if assignment occurs during encoding
	 	if(this.setValuesStatus) {
	 		pw.println("const int MXN="+this.maxActionP1+";");
	 		pw.println("const double app_rs="+this.reqResource+";");
	 		pw.println("const double mx_rs=1.0; //set the max resource per slot");
	 		pw.println();
	 		
		 	for (int i=0; i < this.maxActionP1; i++) {
			 	for(int j=0; j < this.maxActionP2; j++) {
			 		pw.println("const int "+ this.idParams[i][j]+" = "+i+";");
			 		pw.println("const double "+this.costParams[i][j]+" = "+this.cost[i][j]+";	//cost");
			 		pw.println("const double "+this.availParams[i][j]+" = "+this.avail[i][j]+";	//avail");
			 		pw.println("const double "+this.relParams[i][j]+" = "+this.rel[i][j]+";		//rel");
			 		pw.println("const int "+this.timeParams[i][j]+" = "+this.time[i][j]+";		//time");
			 		pw.println("const double "+this.resParams[i][j]+" = "+this.res[i][j]+";		//time");
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 	//if assignment occurs after model instantiation
	 	else {
	 		pw.println("const int MXN;");
	 		pw.println("const double app_rs;");
	 		pw.println("const double mx_rs=1.0; //set the max resource per slot");
	 		pw.println();
	 		
	 		for (int i=0; i < this.maxActionP1; i++) {
			 	for(int j=0; j < this.maxActionP2; j++) {
			 		pw.println("const int "+ this.idParams[i][j]+" = "+i+";");
			 		pw.println("const double "+this.costParams[i][j]+";	//cost");
			 		pw.println("const double "+this.availParams[i][j]+"; //avail");
			 		pw.println("const double "+this.relParams[i][j]+";	//rel");
			 		pw.println("const int "+this.timeParams[i][j]+";	//time");
			 		pw.println("const double "+this.resParams[i][j]+";	//time");
			 		pw.println();
			 	}
		 	}
		 	pw.println();
	 	}
	 		
		pw.println("//=========Global Parameters=======");
	 //	pw.println("const int TP=0;	//plater 1 state");	
	 //	pw.println("const int TE=1;	//player 2 state");
	 	pw.println("global t:[0..1] init 0;	//to control the turn");
	 	pw.println("global goal : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[-1..MXN] init -1;  //number of computing node");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1); 	
	 	pw.println("//P1's coordinator :");
	 	
	 	pw.println("[end] (t=0) & (goal=true) -> true; //to end the selection");
 		 		
	 	pw.println("//P1 moves :");
	 	for(int i=0; i< maxActionP1; i++) {
		 	pw.println("[r"+i+"] (t=0) & (goal=false) -> (n'="+i+") & (t'=1);");

	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 
	 	pw.println("//P2 moves for single or sequential pattern:");	
		for(int i=0; i < maxActionP1; i++) {
		 	for(int j=0; j < maxActionP2; j++) {
		 		pw.println("[n"+i+"rs"+j+"] (t=1) & (n="+i+") & (app_rs + "+this.resParams[i][j]+" <= mx_rs) -> n"+i+"rs"+j+"_rel:(goal'=true) & (t'=0) + 1-n"+i+"rs"+j+"_rel:(goal'=false) & (t'=0);");
		 	}
		 	pw.println();
		}
	 	
	 	pw.println("endmodule");
	 	pw.println();
	 
	 	pw.println("//=========Reward Structure=======");
	 	pw.println("rewards \"rw_cost\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_cost;");
   	 		}
		}
	 	pw.println("endrewards");
	 	
		pw.println("rewards \"rw_time\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_time;");
   	 		}
		}
	 	pw.println("endrewards");
	
		pw.println("rewards \"rw_reliability\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_rel;");
   	 		}
		}
	 	pw.println("endrewards");
		
		pw.println("rewards \"rw_availability\"");
	 	for(int i=0; i < maxActionP1; i++) {
   	 		for(int j=0; j < maxActionP2; j++) {
   	 			pw.println("[n"+i+"rs"+j+"] true : n"+i+"rs"+j+"_avail;");
   	 		}
		}
	 	pw.println("endrewards");
		
	 	pw.println("//=========Labels=======");
	 	pw.println("label \"done\" = (goal=true);");
	 	
	 	pw.close();
    }
	
	public static void main(String args[]) {
    //=========MODEL CREATION============================
		String modelPath = "/home/azlan/git/PrismGames/Prismfiles/appDeployModel.prism";
		String actionListPath = "/home/azlan/git/PrismGames/IOFiles/actionList.txt";
		
		//Create a model generator instance
		AdaptationModelGenerator mdg = new AdaptationModelGenerator();
		int numCollab = 5; //number of collaborator
		int numResource = 2;//set number of resources /vm
		
		//configuring model parameters and values
		System.out.println("Creating model for single application deployment...");
		mdg.setValuesStatus(true); //true-create model with values, false-create model without values (later stage)
		mdg.setPattern(0);	//set the current value of p=0 for single
		mdg.setParamsNames("p1", "p2", "planner", "environment");
		mdg.setUpperBounds(1, numCollab, numResource); //simply set numNode = 1
		
		//creating and assigning values to parameters
		System.out.println("Creating and assigning values to parameters...");
		mdg.setAllRandomServProfiles();
		mdg.createQualityParams();
		mdg.setQualityParamswithValues();
		mdg.setModelPath(modelPath);										
		mdg.encodeSGModelforComplexAppDeployment();
		mdg.exportActionList(actionListPath);
		
		System.out.println("done");
	}
	
}
