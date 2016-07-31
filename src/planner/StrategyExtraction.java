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
	private ArrayList<Integer> substrat1States;
	private ArrayList<Integer> substrat1Actions;
	private ArrayList<Integer> substrat2States;
	private ArrayList<Integer> substrat2Actions;
	private Scanner readM, readT, readS;
	
	private int decStateStrat1, decStateStrat2;
	private String selAction1, selAction2;
	
	
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
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();
		substrat2States = new ArrayList<Integer>();
		substrat2Actions = new ArrayList<Integer>();
		
		
		readS = new Scanner(new BufferedReader(new FileReader(this.stratPath)));
	
		String inRead=null;
		int potState=0, potAction=0;
		int stratId = 0; 
		
		while (readS.hasNextLine()) {
			inRead = readS.nextLine();
			
			if (inRead.contains("StrategyIndex")) {
				//get the strategy index
				 String[] arr = inRead.split(" ");    
				 stratId = Integer.parseInt(arr[1]);
				 //System.out.println("Strategy Index is :"+arr[1]);
			}
			
			//System.out.println("the data read from the file "+inRead);
			if (inRead.equalsIgnoreCase("MemUpdMoves:") && (stratId == 1)){
				//skip four lines
				readS.nextLine(); readS.nextLine(); readS.nextLine(); readS.nextLine();
				inRead = readS.next();
				while (readS.hasNextLine() && (!inRead.equalsIgnoreCase("Info:"))) {
					//read the first index (potential decision state)
					potState = Integer.parseInt(inRead);
				
					//read the second index (potential action)
					potAction = Integer.parseInt(readS.next());
				
					//store into the array lists
					substrat1States.add(potState);
					substrat1Actions.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//System.out.println("Read as "+inRead);
					
					//read the next integer
					inRead = readS.next();
				}
			}
			//System.out.println("the data read from the file "+inRead);
			if (inRead.equalsIgnoreCase("MemUpdMoves:") && (stratId == 2)){
				//skip four lines
				readS.nextLine(); readS.nextLine(); readS.nextLine(); readS.nextLine();
				inRead = readS.next();
				while (readS.hasNextLine() && (!inRead.equalsIgnoreCase("Info:"))) {
					//read the first index (potential decision state)
					potState = Integer.parseInt(inRead);
				
					//read the second index (potential action)
					potAction = Integer.parseInt(readS.next());
				
					//store into the array lists
					substrat2States.add(potState);
					substrat2Actions.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//System.out.println("Read as "+inRead);
					
					//read the next integer
					inRead = readS.next();
				}
			}
			if(stratId > 2)
				System.err.println("This function only caters for 2 sub strategies");
        
        }
		readS.close();		
	}

	public void getAllStrategyList(){
		
		int maxSize1 = substrat1States.size();
		
		for(int i=0; i < maxSize1; i++) {
			System.out.println("Sub strategy 1 list - state: "+substrat1States.get(i)+" action: "+substrat1Actions.get(i));
		}
		
		int maxSize2 = substrat2States.size();
		
		for(int i=0; i < maxSize2; i++) {
			System.out.println("Sub strategy 2 list - state: "+substrat2States.get(i)+" action: "+substrat2Actions.get(i));
		}
		
	}
	
	public void findDecision()
	{
		int maxSizeStrat1 = substrat1States.size();
		int maxSizeStrat2 = substrat2States.size();
		int stateFromStrat = 0, indexFromTrans = 0;
		String labelFromTrans = null;
		
		//starting from the last item of strategy list
		for(int i = maxSizeStrat1 -1; i >= 0; i--) {
			//get the state from the strategy list
			stateFromStrat = substrat1States.get(i);
			System.out.println("state from strategy list is "+stateFromStrat);
			
		
			//get the index of a state from the transition list that equals to state from strategy list
			indexFromTrans = translist1.indexOf(stateFromStrat);
			//System.out.println("index from trans list is "+indexFromTrans);
			
			//get the label from the transition list
			labelFromTrans = translist2.get(indexFromTrans);
			//System.out.println("label from trans list is "+labelFromTrans);
				
			//if the label equals to any label in the map list
			if (mapslist.contains(labelFromTrans)) {
				this.decStateStrat1 = stateFromStrat;
				this.selAction1 = labelFromTrans;
				System.out.println("Decision state :"+stateFromStrat+" with action :"+labelFromTrans);
				break;
			}	
		}
		
		//starting from the last item of strategy list
		for(int j = maxSizeStrat2 -1; j >= 0; j--) {
			//get the state from the strategy list
			stateFromStrat = substrat1States.get(j);
			System.out.println("state from strategy list is "+stateFromStrat);
			
		
			//get the index of a state from the transition list that equals to state from strategy list
			indexFromTrans = translist1.indexOf(stateFromStrat);
			//System.out.println("index from trans list is "+indexFromTrans);
			
			//get the label from the transition list
			labelFromTrans = translist2.get(indexFromTrans);
			//System.out.println("label from trans list is "+labelFromTrans);
				
			//if the label equals to any label in the map list
			if (mapslist.contains(labelFromTrans)) {
				this.decStateStrat2 = stateFromStrat;
				this.selAction2 = labelFromTrans;
				System.out.println("Decision state :"+stateFromStrat+" with action :"+labelFromTrans);
				break;
			}	
		}
	 }
	
	public int getDecisionStateStrategy1() {
		return this.decStateStrat1;
	}
	
	public int getDecisionStateStrategy2() {
		return this.decStateStrat2;
	}
	
	public String getDecisionLabelStrategy1() {
		return this.selAction1;
	}
	
	public String getDecisionLabelStrategy2() {
		return this.selAction1;
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
		
		ste.findDecision();
	}

}
