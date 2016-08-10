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
	private String actionLabelAPath = null;
	private String actionLabelBPath = null;
	
	private ArrayList<String> mapslist;
	private ArrayList<Integer> transStates;
	private ArrayList<Integer> transActions;
	private ArrayList<String> transLabels;
	private ArrayList<Integer> substrat1States;
	private ArrayList<Integer> substrat1Actions;
	private ArrayList<Integer> substrat2States;
	private ArrayList<Integer> substrat2Actions;
	private ArrayList<String> actionLabelA;
	private ArrayList<String> actionLabelB;
	private ArrayList<String> nodeIdlist;
	private Scanner readM, readT, readS, readA, readB;
	
	private int decStateStrat1, decStateStrat2;
	private int selAction1, selAction2;
	private String selLabel1, selLabel2;
	
	
	public StrategyExtraction(String mPath, String tPath, String sPath, String aPath, String bPath){
		this.mapsPath = mPath;
		this.transPath = tPath;
		this.stratPath = sPath;
		this.actionLabelAPath = aPath;
		this.actionLabelBPath = bPath;
	}
	
	
	
	public void readMappingFile() throws IllegalArgumentException, FileNotFoundException {
		mapslist = new ArrayList<String>();
		
		readM = new Scanner(new BufferedReader(new FileReader(this.mapsPath)));
		
		String label = null;
	
		label = readM.next();
		mapslist.add(label);
				
		while (readM.hasNext()) {
			label = readM.next();
			mapslist.add(label);
		}
		readM.close();
	}
	
	public void setNodeIdList(ArrayList<String> nodeId) {
		nodeIdlist = new ArrayList<String>();
		this.nodeIdlist = nodeId;
	}
	
	public void readActionLabelFile() throws IllegalArgumentException, FileNotFoundException {
		actionLabelA = new ArrayList<String>();
		actionLabelB = new ArrayList<String>();
		
		readA = new Scanner(new BufferedReader(new FileReader(this.actionLabelAPath)));
		readB = new Scanner(new BufferedReader(new FileReader(this.actionLabelBPath)));
		
		String label = null;
	
		label = readA.next();
		actionLabelA.add(label);
				
		while (readA.hasNext()) {
			label = readA.next();
			actionLabelA.add(label);
		}
		readA.close();
		
		label = readB.next();
		actionLabelB.add(label);
				
		while (readB.hasNext()) {
			label = readB.next();
			actionLabelB.add(label);
		}
		readB.close();
	}
	
	public void getAllMappingList(){
	
		for(int i=0; i < mapslist.size(); i++) {
			System.out.println("The mapping list is "+mapslist.get(i));
		}
	}
	
	public void displayActionLabelAList(){
		System.out.println("The list of action label A is "+actionLabelA);
	}
	
	public void displayActionLabelBList(){
		System.out.println("The list of action label B is "+actionLabelB);
	}

	public void readTransitionFile() throws IllegalArgumentException, FileNotFoundException {
		transStates = new ArrayList<Integer>();
		transActions = new ArrayList<Integer>();
		transLabels = new ArrayList<String>();
		
		readT = new Scanner(new BufferedReader(new FileReader(this.transPath)));
		
		int state=0, action=0;
		String label = null;
		
		//skip the first line
		readT.nextLine();
		
		//read the first index
		state = readT.nextInt();
		
		//read the second index
		action = readT.nextInt();
		
		//skip another two indexes
		readT.nextInt(); readT.nextDouble(); 
		
		//read the label
		label = readT.next();
		
		//add into the arraylist
		transStates.add(state);
		transActions.add(action);
		transLabels.add(label);
				
		while (readT.hasNext()) {
			
			//read the first index
			state = readT.nextInt();
			
			//read the second index
			action = readT.nextInt();
			
			//skip another two indexes
			readT.nextInt(); readT.nextDouble(); 
			
			//read the label
			label = readT.next();
			
			//add into the arraylist
			transStates.add(state);
			transActions.add(action);
			transLabels.add(label);
			
			readT.nextLine();
		}
		readT.close();
	}

	public void getAllTrasitionList(){
		int maxSize = transStates.size();
		
		for(int i=0; i < maxSize; i++) {
			System.out.println("Transition list - state: "+transStates.get(i)+" action: "+transActions.get(i)+" label: "+transLabels.get(i));
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
		int maxTrans = transStates.size();
		int stateFromStrat1=0, stateFromTrans1=0, actionFromTrans1=0, actionFromStrat1=0;
		String labelFromTrans1 = null;
		boolean found = false;
		
		//substrategy 1
		//starting from the last item of strategy list
		for(int i = maxSizeStrat1 -1; i >= 0; i--) {
			//get the state from the strategy list
			stateFromStrat1 = substrat1States.get(i);
			actionFromStrat1 = substrat1Actions.get(i);
			//System.out.println("state from sub strategy 1 list is "+stateFromStrat1);
					
			for(int j=0; j < maxTrans; j++) {
				stateFromTrans1 = transStates.get(j);
				actionFromTrans1 = transActions.get(j);
				
				//check if the state is similar
				if (stateFromTrans1 == stateFromStrat1)	{
					//check if the action is similar
					if (actionFromTrans1 == actionFromStrat1) {
						labelFromTrans1 = transLabels.get(j);
						//check if the label is an action label
						if (actionLabelA.contains(labelFromTrans1)) {
							this.decStateStrat1 = stateFromStrat1;
							this.selAction1 = actionFromStrat1;
							this.selLabel1 = labelFromTrans1;
							System.out.println("Decision state :"+stateFromStrat1+" with action :"+labelFromTrans1);
							found = true;
						}
					}
				}
			}	
			if (found)
				break;			
		}//end for substrategy 1
		
		
		int stateFromStrat2=0, stateFromTrans2=0, actionFromTrans2=0, actionFromStrat2=0;
		String labelFromTrans2 = null;
		found = false;
		//substrategy 2
		//starting from the last item of strategy list
		for(int i = maxSizeStrat2 -1; i >= 0; i--) {
			//get the state from the strategy list
			stateFromStrat2 = substrat2States.get(i);
			actionFromStrat2 = substrat2Actions.get(i);
			//System.out.println("state from sub strategy 2 list is "+stateFromStrat2);
					
			for(int j=0; j < maxTrans; j++) {
				stateFromTrans2 = transStates.get(j);
				actionFromTrans2 = transActions.get(j);
				
				//check if the state is similar
				if (stateFromTrans2 == stateFromStrat2)	{
					//check if the action is similar
					if (actionFromTrans2 == actionFromStrat2) {
						labelFromTrans2 = transLabels.get(j);
						//check if the label is an action label
						if (actionLabelB.contains(labelFromTrans2)) {
							this.decStateStrat2 = stateFromStrat2;
							this.selAction2 = actionFromStrat2;
							this.selLabel2 = labelFromTrans2;
							System.out.println("Decision state :"+stateFromStrat2+" with action :"+labelFromTrans2);
							found = true;
						}
					}
				}
			}	
			if (found)
				break;			
		}//endfor sub strategy 2
	 }
	
	public int getDecisionStateStrategy1() {
		return this.decStateStrat1;
	}
	
	public int getDecisionStateStrategy2() {
		return this.decStateStrat2;
	}
	
	public int getDecisionActionStrategy1() {
		return this.selAction1;
	}
	
	public int getDecisionActionStrategy2() {
		return this.selAction2;
	}
	
	public String getDecisionLabelStrategy1() {
		return this.selLabel1;
	}
	
	public String getDecisionLabelStrategy2() {
		return this.selLabel2;
	}
	
	public String getSelectedNodeIdA() {
		int index = 0;
		if (actionLabelA.contains(this.selLabel1)) {
			index = actionLabelA.indexOf(this.selLabel1);
			return nodeIdlist.get(index);
		}
		else
			return null;
	}
	
	public String getSelectedNodeIdB() {
		int index = 0;
		if(actionLabelB.contains(this.selLabel2)) {
			index = actionLabelB.indexOf(this.selLabel2);
			return nodeIdlist.get(index);
		}
		else
			return null;
	}

	public void displayStrategies(){
		System.out.println("Substrategy 1:- decision state:"+this.decStateStrat1+" action:"+this.selAction1+" label:"+this.selLabel1);
		System.out.println("Substrategy 2:- decision state:"+this.decStateStrat2+" action:"+this.selAction2+" label:"+this.selLabel2);
	}
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
		// TODO Auto-generated method stub
		//Defining File Inputs/Outputs
		
		String linuxPath = "/home/azlan/git/PrismGames/";
		String mainPath = linuxPath;
		
		String strategyPath = mainPath+"IOFiles/strategy";
		String transitionPath = mainPath+"IOFiles/transition";
        String mappingPath = mainPath+"IOFiles/mapping";
        String actionLabelAPath = mainPath+"IOFiles/actionlabelA";
        String actionLabelBPath = mainPath+"IOFiles/actionlabelB";
        
        //simulating the input configuration process
		ConfigurationPlanner conf = new ConfigurationPlanner();
		conf.setAppRequirements(1, 1, 0, 0.3, 3, 3);
		conf.setNodeCapabilities(1, "NODE1", 3, 3, 0.5, 3, 3, "LOC1");
		conf.setNodeCapabilities(1, "NODE2", 3, 3, 0.5, 3, 3, "LOC1");
		conf.setNodeCapabilities(1, "NODE3", 3, 3, 0.5, 3, 3, "LOC1");	
		
		StrategyExtraction ste = new StrategyExtraction(mappingPath, transitionPath, strategyPath, actionLabelAPath, actionLabelBPath);
		//ste.readMappingFile();
		ste.readActionLabelFile();
		ste.displayActionLabelAList();
		ste.displayActionLabelBList();
		//ste.getAllMappingList();
		ste.readTransitionFile();
		ste.getAllTrasitionList();
		ste.readStrategyFile();
		
		ste.getAllStrategyList();
		
		ste.findDecision();
		ste.displayStrategies();
		
		ste.setNodeIdList(conf.getNodeIdList());
		System.out.println("Node name from substrategy 1 is "+ste.getSelectedNodeIdA());
		System.out.println("Node name from substrategy 2 is "+ste.getSelectedNodeIdB());
		
	}

}
