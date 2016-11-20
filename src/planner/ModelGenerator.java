package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import explicit.PrismExplicit;
import prism.Prism;
import prism.PrismFileLog;
import prism.PrismLangException;
import simulator.SimulatorEngine;

public class ModelGenerator {

	File outfile;
	PrintWriter pw;
	
	public ModelGenerator(String path) throws FileNotFoundException {
		outfile = new File(path);
		pw = new PrintWriter(outfile);
	}
	
	public void generateSingleNodeModel()  {

   	 	String p1="p1", p2="p2";
   	 	String mod1="planner", mod2="environment";
   	 	int maxActionP1 = 20, maxActionP2=3;
   	 	
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
	 		if(i != maxActionP2-1)
	 			pw.print("[e"+i+"],");
	 		else
	 			pw.print("[e"+i+"]");
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

	public void generateSeqModel()  {

   	 	String p1="p1", p2="p2";
   	 	String mod1="planner", mod2="environment";
   	 	int nodeNum = 2;	//number of activity/task/node in the service composition
   	 	int maxActionP1 =10, maxActionP2 =5;
   	 	
   	 	pw.println("smg");
   	 	pw.println("//=========Player definition=======");
   	 	pw.println("player "+p1);
   	 	pw.print(""+mod1+",");
   	 	for(int n=0; n < nodeNum; n++) {
   	 		for(int i=0; i < maxActionP1; i++) {
   	 			pw.print("[n"+n+"r"+i+"]");
   	 			if ((n !=(nodeNum-1)) && (i != (maxActionP1-1))) {
   	 			pw.print(",");
   	 			}
   	 		}
		}
   	 	pw.println();
   	 	pw.println("endplayer");
   	 	pw.println();
   	 	
   	 	pw.println("player "+p2);
   	 	pw.print(""+mod2+",");
   	 	for(int n=0; n < nodeNum; n++) {
   	 		for(int i=0; i < maxActionP2; i++) {
   	 			pw.print("[n"+n+"r"+i+"]");
   	 			if ((n !=(nodeNum-2)) && (i != (maxActionP2-1))) {
   	 			pw.print(",");
   	 			}
   	 		}
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
	 	pw.println("cost int MXN="+nodeNum+";");
	 	for(int n=0; n < nodeNum; n++) {
	 		pw.println("const int N"+n+"_MAX_SV="+maxActionP1+";	//finite number of services");		
	 		pw.println("const int N"+n+"_MAX_EV="+maxActionP2+";	//finite number of computing nodes");		
	 	}
	 	pw.println();
	 	
	 	for (int n=0; n < nodeNum; n++) {
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.println("const int N"+n+"_RS"+i+"_ID;");
		 		pw.println("const double N"+n+"_RS"+i+"_COST;	//cost");
		 		pw.println("const bool N"+n+"_RS"+i+"_AVAIL;	//availability status");
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("const int N"+n+"_RS"+i+"_DUR"+j+";	//duration "+j+";");
		 		}
		 		for(int j=0; j < maxActionP2; j++) {
		 			pw.println("const int N"+n+"_RS"+i+"_REL"+j+";	//reliability "+j+";");
		 		}
		 		pw.println();
		 	}
	 	}
	 	pw.println();
   	 	
		pw.println("//=========Global Parameters=======");
	 	pw.println("const int TE=0;	//plater 2 state");	
	 	pw.println("const int TP=1;	//player 1 state");
		pw.println("const int TS=2;	//coordinator state");
	 	pw.println("global t:[TE..TS] init TS;	//to control the turn");
	 	pw.println("global end : bool init false;	//(absorbing state)");		
	 	pw.println("global n:[0..MXN-1] init 0;  //to control the sequence");
	 	pw.println();		
	

		pw.println("//=========Module for Player 1=======");
	 	pw.println("module "+mod1);
	 	for(int n=0; n < nodeNum; n++) {
	 		pw.println("n"+n+":[-1..N"+n+"_MAX_SV-1] init -1;");
	 	}
	 	
	 	pw.println("//P1 moves :");
	 	pw.println("[] (t=TS) & (n < MXN) -> (t'=TP);");
	 	pw.println("[] (t=TS) & (n >= MXN) -> (end'=true);");
	 	for(int n=0; n < nodeNum; n++) {
		 	for(int i=0; i < maxActionP1; i++) {
		 		pw.println("[n"+n+"r"+i+"] (t=TP) & (n="+n+") & (a0_n"+n+"_rs"+i+"_sat_all=true) -> (n"+n+"'="+i+") & (t'=TE);");
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	pw.println("//=========Module for Player 2=======");
	 	pw.println("module "+mod2);
	 	for(int n=0; n < nodeNum; n++) {
	 		pw.println("n"+n+"ev:[-1..N"+n+"_MAX_EV] init -1;");
	 	}
	 	pw.println("//P2 moves :");	
	 	for(int n=0; n < nodeNum; n++) {
		 	for(int i=0; i < maxActionP2; i++) {
		 		pw.println("[n"+n+"e"+i+"] (t=TE) & (n="+n+") -> n"+n+"ev"+i+"_rel:(n"+n+"ev'="+i+") & (t'=TS) & (n'=n+1) + 1-n"+n+"ev"+i+"_rel:(n"+n+"ev'=-1) & (t'=TS);");			
		 	}
	 	}
	 	pw.println("endmodule");
	 	pw.println();
	 	
	 	
	 	pw.close();
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String pathSingle = "/home/azlan/git/PrismGames/Prismfiles/singlemodel.prism";
		String pathSeq = "/home/azlan/git/PrismGames/Prismfiles/seqmodel.prism";
		
		int pattern = 1;
		
		if (pattern==0) {
			try {
				ModelGenerator mdg = new ModelGenerator(pathSingle);
				mdg.generateSingleNodeModel();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (pattern==1) {
			try {
				ModelGenerator mdg = new ModelGenerator(pathSeq);
				mdg.generateSeqModel();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			System.out.println("no pattern has been selected");
			
		
		
		
		System.out.println("Model has been generated...");
		
		//StochasticPlanner sp = new StochasticPlanner();

		
		//get the inputs; 
		//1) number of services(p1's actions)
		//2) number of servers(p2's actions)
		
		//generate the model
		
	}

}
