package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ModelGenerator {

	File outfile;
	PrintWriter pw;
	
	public ModelGenerator(String path) throws FileNotFoundException {
		outfile = new File(path);
		pw = new PrintWriter(outfile);
	}
	
	public void generateModel()  {

   	 	String p1="p1", p2="p2";
   	 	String mod1="planner", mod2="environment";
   	 	int maxActionP1 = 8, maxActionP2=2;
   	 	
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
	 	pw.println(""+mod2+",");
	 	for(int i=0; i < maxActionP2; i++) {
	 		if(i != maxActionP2-1)
	 			pw.print("[e"+i+"],");
	 		else
	 			pw.print("[e"+i+"],");
	 	}
	 	pw.println();
	 	pw.println("endplayer");
	 	pw.println();
	 	
	 	pw.println("//=========User Requirements=======");
	 	pw.println("const int A0_ID = 0;");
	 	pw.println("const int A0_Q1 = 5;	//max duration");
	 	pw.println("const double A0_Q2 = 0.6; //reliability");
	 	pw.println("const double A0_Q3 = 10.0; //max cost");
	 	pw.println("const double WG_COST = 0.3; //weight for cost");
	 	pw.println("const double WG_DUR = 0.3; //weight for duration");
	 	pw.println("const double WG_REL = 0.4; //weight for reliability");
	 	pw.println();
	 	
	 	pw.println("//=========Resource Profiles=======");
	 	pw.println("const int MAX_SV="+maxActionP1);		
	 	pw.println("const int MAX_EV="+maxActionP2);		
	 	pw.println();
	 	
	 	for(int i=0; i < maxActionP1; i++) {
	 		pw.println("const int RS"+i+"_ID;");
	 		pw.println("const int RS"+i+"_DUR1;	//duration 1");
	 		pw.println("const int RS"+i+"_DUR2;	//duration 2");
	 		pw.println("const double RS"+i+"_REL1;	//reliability 1");
	 		pw.println("const double RS"+i+"_REL2;	//reliability 2");
	 		pw.println("const double RS"+i+"_COST;	//cost");
	 		pw.println("const bool RS"+i+"_AVAIL;	//availability status");
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
	 			pw.print(" ( sel = "+j+" ? RS"+j+"_REL1 :");
	 		}
	 		pw.print("0.0");
	 		for(int j=0; j < maxActionP1; j++) {
	 			pw.print(")");
	 		}
	 	 	pw.print(";");
	 	 	pw.println();
	 	}
 		
	 	pw.close();
    }

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String path = "/home/azlan/git/PrismGames/IOFiles/mymodel.prism";
		
		
		try {
			ModelGenerator mdg = new ModelGenerator(path);
			mdg.generateModel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done....");
		
		//get the inputs; 
		//1) number of services(p1's actions)
		//2) number of servers(p2's actions)
		
		//generate the model
		
	}

}
