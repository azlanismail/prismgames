package planner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MultiObjStrategyExtraction extends StrategyExtraction{

	private Candidates[] potCand;
	
	public int computeNumofPotentialCandidate() {
		
		potCand = new Candidates[actionLabelA.size()];
		
		//assign predefined action label into the potential candidates object list
		for(int a=0; a < actionLabelA.size(); a++) {
			potCand[a] = new Candidates(actionLabelA.get(a));
			System.out.println("action "+actionLabelA.get(a)+" has been added");
		}
		
		
		
		//search from the adversaries data
		for(int i=0; i < substrat1States.size(); i++) {
			//search from the transition data
			for(int j=0; j < transStates.size(); j++) {
				
				if (transStates.get(j) == substrat1States.get(i))	{
					//check if the action is similar
					if (transActions.get(j) == substrat1Actions.get(i)) {
						
						//check if the label of similar action exists in the action label
						for(int a=0; a < potCand.length; a++) {
							if( potCand[a].getName().equalsIgnoreCase(transLabels.get(j))) {
								potCand[a].addTotal(1);
								break;
							}		
						}
					}
				}
			}		
		}
		
		int count = 0;
		for(int a=0; a < potCand.length; a++) {
			if (potCand[a].getTotal() > 0) {
				System.out.println("Total : "+potCand[a]);
				count++;
			}
		}
		System.out.println("Total candidate is "+count);
		
		return count;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//set the output paths
		String transPath = "/home/azlan/git/PrismGames/IOFiles/trans.txt";
		String stratPath = "/home/azlan/git/PrismGames/IOFiles/strat.txt";
		String actionPath = "/home/azlan/git/PrismGames/IOFiles/actionList.txt";
			
		MultiObjStrategyExtraction se = new MultiObjStrategyExtraction();
		//=================================
		//extraction
		
		boolean status = true;
		
		if (status) {
		System.out.println("Extracting strategies...");
		se.setPath(transPath, stratPath, actionPath);
		se.readSingleActionLabelFile();
		se.readTransitionFile();
		se.readStrategiesfromMultiObjSynthesis();	
		se.findSingleDecision();
		se.computeNumofPotentialCandidate();
		}
		else
			System.out.println("No decision since synthesis results in false");

	}

}
