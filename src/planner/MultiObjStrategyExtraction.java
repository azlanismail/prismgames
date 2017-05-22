package planner;

public class MultiObjStrategyExtraction extends StrategyExtraction{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//set the output paths
		String transPath = "/home/azlan/git/PrismGames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strat.txt";
			
		MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
		//=================================
		//extraction
		
		//if the synthesis results in true -> sp.getSynthesisStatus() 
		if (true) {
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
		//===============================		
	}

}
