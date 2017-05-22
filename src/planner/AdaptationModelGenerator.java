package planner;

import java.util.Random;

import parser.Values;

public class AdaptationModelGenerator extends ModelGenerator{

	double cost[][], avail[][], rel[][];
	int timeR, time[][];
	
	Values vm;
	
	//service constants parameters
	String idParams[][], costParams[][], availParams[][], timeParams[][], relParams[][];
	
	public AdaptationModelGenerator() {
		
	}
	
	public void setUpperBounds(int numNode, int mxActionP1, int mxActionP2) {
		super.numNode = numNode; //to set the max number of application
		super.maxActionP1 = mxActionP1; //to set max number of collaborator
		super.maxActionP2 = mxActionP2;  //to set max number of virtual machine
		
		this.cost = new double[maxActionP1][maxActionP2];
		this.avail = new double[maxActionP1][maxActionP2];
		this.time = new int[maxActionP1][maxActionP2];
		this.rel = new double[maxActionP1][maxActionP2];
	}
	
	public void setAllRandomServProfiles() {
		Random rand = new Random();
		double cost, rel, avail;
		int time;
		double minRel=0.8, maxRel=1.0;
		double minAvail=0.8, maxAvail=1.0;
		double rangeRel = maxRel - minRel;
		double rangeAvail = maxAvail - minAvail;
		
		for(int i=0; i < this.maxActionP1; i++) {			
			for(int j=0; j < this.maxActionP2; j++) {
				cost = rand.nextInt(50) + 10;
				time = rand.nextInt(50) + 10;
				rel = rand.nextDouble() * rangeRel + minRel;
				avail = rand.nextDouble() * rangeAvail + minAvail;
				setProfiles(i, j, cost, avail, time, rel);
			}
		}
	}
	
	public void setProfiles(int i, int j, double cost, double avail, int dur, double rel ) {
		
		this.cost[i][j] = cost;
		this.avail[i][j] = avail;
		this.time[i][j] = dur;
		this.rel[i][j] = rel;
	}
	
	/**
	 * prepare the arrays to hold the identifier of the constant parameters 
	 */
	public void createQualityParams() {
		this.idParams = new String[this.maxActionP1][this.maxActionP2];
		this.costParams = new String[this.maxActionP1][this.maxActionP2];
		this.availParams = new String[this.maxActionP1][this.maxActionP2];
		this.timeParams = new String[this.maxActionP1][this.maxActionP2];
		this.relParams = new String[this.maxActionP1][this.maxActionP2];
		
		for(int i=0; i < this.maxActionP1; i++) {
			for(int j=0; j < this.maxActionP2; j++) {
				this.idParams[i][j] = "n"+i+"rs"+j+"_id";
				this.costParams[i][j] = "n"+i+"rs"+j+"_cost";
				this.availParams[i][j] = "n"+i+"rs"+j+"_avail";
				this.timeParams[i][j] = "n"+i+"rs"+j+"_time";
				this.relParams[i][j] = "n"+i+"rs"+j+"_rel";
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
			
		for(int i=0; i < this.maxActionP1; i++) {
			for(int j=0; j < this.maxActionP2; j++) {
				vm.addValue(this.idParams[i][j], i); 
				vm.addValue(this.costParams[i][j], this.cost[i][j]);
				vm.addValue(this.availParams[i][j], this.avail[i][j]);
				vm.addValue(this.timeParams[i][j], this.time[i][j]);
				vm.addValue(this.relParams[i][j], this.rel[i][j]);
			}
		}
	}
	
	/**
	 * To generate a generic stochastic games model for service selection 
	 */
	public void encodeSGModelforAppDeployment()  {
   	 	
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
	
	public static void main(String args[]) {
    //=========MODEL CREATION============================
		String modelPath = "/home/azlan/git/PrismGames/Prismfiles/appDeployModel.prism";
		
		//Create a model generator instance
		AdaptationModelGenerator mdg = new AdaptationModelGenerator();
		int numCollab = 8; //number of collaborator
		int numResource = 10;//set number of resources /vm
		
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
		mdg.encodeSGModelforAppDeployment();
		
		System.out.println("done");
	}
	
}
