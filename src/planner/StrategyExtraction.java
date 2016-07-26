package planner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class StrategyExtraction {

	private String mapsPath = null;
	private String transPath = null;
	private String stratPath = null;
	
	private ArrayList<String> mapslist;
	private ArrayList<Integer> translist1;
	private ArrayList<String> translist2;
	private ArrayList<Integer> stratlist1;
	private ArrayList<Integer> stratlist2;
	private Scanner readM, readT, readS;
	
	
	public StrategyExtraction(String mPath, String tPath, String sPath){
		this.mapsPath = mPath;
		this.transPath = tPath;
		this.stratPath = sPath;
	}
	
	
	
	public void readMappingFile() throws IllegalArgumentException, FileNotFoundException {
		mapslist = new ArrayList<String>();
		
		readM = new Scanner(new BufferedReader(new FileReader(this.mapsPath)));
		
		int id=0;
		String label = null;
		
		id = readM.nextInt();
		label = readM.next();
		mapslist.add(id, label);
				
		while (readM.hasNext()) {
			id = readM.nextInt();
			label = readM.next();
			mapslist.add(id, label);
			readM.nextLine();
		}
		readM.close();
	}
	
	public void getAllMappingList(){
	
		for(int i=0; i < mapslist.size(); i++) {
			System.out.println("The mapping list is "+mapslist.get(i));
		}
	}
	
	private boolean mapping(String labeltoEvaluate){
		
		boolean status = false;
		
		for(int i=0; i < mapslist.size(); i++)
		{
			if (labeltoEvaluate.equalsIgnoreCase(mapslist.get(i))) {
				status = true;
				break;
			}
			else
				status = false;
		}
		return status;
   }

	public void readTransitionFile() throws IllegalArgumentException, FileNotFoundException {
		translist1 = new ArrayList<Integer>();
		translist2 = new ArrayList<String>();
		
		readT = new Scanner(new BufferedReader(new FileReader(this.transPath)));
		
		int id=0;
		String label = null;
		
		//skip the first line
		readT.nextLine();
		
		//read the first int
		id = readT.nextInt();
		
		//skip three integers
		readT.next(); readT.nextInt(); readT.nextInt(); 
		
		//read the label
		label = readT.next();
		
		//add into the arraylist
		translist1.add(id);
		translist2.add(label);
				
		while (readT.hasNext()) {
			
			//read the first int
			id = readT.nextInt();
			
			//skip three integers
			readT.next(); readT.nextInt(); readT.nextInt(); 
			
			//read the label
			label = readT.next();
			
			//add into the arraylist
			translist1.add(id);
			translist2.add(label);
			
			readT.nextLine();
		}
		readT.close();
	}

	public void getAllTrasitionList(){
		int maxSize = translist1.size();
		
		for(int i=0; i < maxSize; i++) {
			System.out.println("Transition list - state: "+translist1.get(i)+" label: "+translist2.get(i));
		}
	}
	

	public void readStrategyFile() throws IllegalArgumentException, FileNotFoundException {
		
		stratlist1 = new ArrayList<Integer>();
		stratlist2 = new ArrayList<Integer>();
		
		readS = new Scanner(new BufferedReader(new FileReader(this.stratPath)));
	
		String inRead=null;
		int potState=0, potAction=0;
		 
		while (readS.hasNextLine()) {
			inRead = readS.nextLine();
			//System.out.println("the data read from the file "+inRead);
			if (inRead.equalsIgnoreCase("MemUpdMoves:")){
				//skip four lines
				readS.nextLine(); readS.nextLine(); readS.nextLine(); readS.nextLine();
				inRead = readS.next();
				while (readS.hasNextLine() && (!inRead.equalsIgnoreCase("Info:"))) {
					//read the first index (potential decision state)
					potState = Integer.parseInt(inRead);
				
					//read the second index (potential action)
					potAction = Integer.parseInt(readS.next());
				
					//store into the array lists
					stratlist1.add(potState);
					stratlist2.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//System.out.println("Read as "+inRead);
					
					//read the next integer
					inRead = readS.next();
				}
				break;
			}
        }
		readS.close();		
	}

	public void getAllStrategyList(){
		
		int maxSize = stratlist1.size();
		
		for(int i=0; i < maxSize; i++) {
			System.out.println("Strategy list - state: "+stratlist1.get(i)+" action: "+stratlist2.get(i));
		}
	}
	
	public void findDecisionState()
	{
		int maxSizeStrat = stratlist1.size();	
		int stateFromStrat = 0, indexFromTrans = 0;
		String labelFromTrans = null;
		
		//starting from the last item of strategy list
		for(int i = maxSizeStrat -1; i >= 0; i--) {
			//get the state from the strategy list
			stateFromStrat = stratlist1.get(i);
			System.out.println("state from strategy list is "+stateFromStrat);
			
		
			//get the index of a state from the transition list that equals to state from strategy list
			indexFromTrans = translist1.indexOf(stateFromStrat);
			//System.out.println("index from trans list is "+indexFromTrans);
			
			//get the label from the transition list
			labelFromTrans = translist2.get(indexFromTrans);
			//System.out.println("label from trans list is "+labelFromTrans);
				
			//if the label equals to any label in the map list
			if (mapslist.contains(labelFromTrans)) {
				System.out.println("Decision state is found, state "+stateFromStrat);
				break;
			}	
		}
	 }
	
		
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
		// TODO Auto-generated method stub
		//Defining File Inputs/Outputs
		
		String linuxPath = "/home/azlan/git/PrismGames/";
		String mainPath = linuxPath;
		
		String strategyPath = mainPath+"IOFiles/strategy";
		String transitionPath = mainPath+"IOFiles/transition";
        String mappingPath = mainPath+"IOFiles/mapping";
		
		StrategyExtraction ste = new StrategyExtraction(mappingPath, transitionPath, strategyPath);
		ste.readMappingFile();
		ste.getAllMappingList();
		ste.readTransitionFile();
		ste.getAllTrasitionList();
		ste.readStrategyFile();
		ste.getAllStrategyList();
		
		ste.findDecisionState();
	}

}
