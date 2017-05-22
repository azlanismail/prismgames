package planner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class MultiObjStrategyExtraction extends StrategyExtraction{

	
	
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
		}
		else
			System.out.println("No decision since synthesis results in false");

	}

}
