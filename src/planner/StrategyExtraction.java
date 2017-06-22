package planner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class StrategyExtraction {

	protected String mapsPath = null;
	protected String transPath = null;
	protected String stratPath = null;
	//private String actionLabelPath = null;
	
	protected String stratCompPath = null;
	protected String stratMultiCompPath = null;
	protected String stratMulti1Path = null;
	protected String stratMulti2Path = null;
	protected String actionLabelAPath = null;
	protected String actionLabelBPath = null;
	
	protected ArrayList<String> mapslist;
	protected ArrayList<Integer> transStates;
	protected ArrayList<Integer> transActions;
	protected ArrayList<String> transLabels;
	protected ArrayList<Integer> substrat1States;
	protected ArrayList<Integer> substrat1Actions;
	protected ArrayList<Integer> substrat2States;
	protected ArrayList<Integer> substrat2Actions;
	protected ArrayList<String> actionLabelA;
	protected ArrayList<String> actionLabelB;
	protected ArrayList<String> nodeIdlist;
	protected String[] Solution;	//to store the solution
	protected Scanner readM, readT, readS, readA, readB;
	
	protected int decStateStrat1 = -1, decStateStrat2 =-1;
	protected int selAction1 = -1, selAction2 = -1;
	protected String selLabel1, selLabel2;
	
	protected int numofDec;
	protected ArrayList<ArrayList<String>> actLabel;
	protected String[][] actionLabel;
	
	public StrategyExtraction() {
		
	}
	
	/**
	 * Constructor for multi-strategies
	 * @param mPath
	 * @param tPath
	 * @param sPath
	 * @param aPath
	 * @param bPath
	 */
	public StrategyExtraction(String tPath, String sPath1, String sPath2, String sPath3, String sPath4,  String aPath, String bPath){
		this.transPath = tPath;
		this.stratCompPath = sPath1;
		this.stratMultiCompPath = sPath2;
		this.stratMulti1Path = sPath3;
		this.stratMulti2Path = sPath4;
		this.actionLabelAPath = aPath;
		this.actionLabelBPath = bPath;
	}
	
	public void setPath(String tPath, String sPath, String aPath) {
		this.transPath = tPath;
		this.stratPath = sPath;
		this.actionLabelAPath = aPath;
		
	}
	
	public void setPath(String tPath, String sPath) {
		this.transPath = tPath;
		this.stratPath = sPath;
	}
	
	public void setNumofDecision(int numNode) {
		this.numofDec = numNode;
	}
	
	public void setActionLabels(String[][] al) {
		this.actionLabel = al;
	}
	
	//this function is no longer used
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
	
	/**
	 * To read potential action list from multiple nodes (such as service composition)
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void readMultiActionLabelsFromFile()  {
		actLabel = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = new ArrayList<String>();
		
		try {
			readA = new Scanner(new BufferedReader(new FileReader(this.actionLabelAPath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String label = null;
		label = readA.next();
		//row.add(label);
		
		System.out.println("Beginning to read....");
		while (readA.hasNext()) {
			if (label.equalsIgnoreCase("Decision")) {
				label = readA.next();
				continue;
			}
			if (label.equalsIgnoreCase("Node")) {
				label = readA.next();
				continue;
			}
			
			if (label.equalsIgnoreCase("EndNode")) {
				System.out.println("adding to the main array...");
				//only add if row is not empty
				if (!row.isEmpty()) {
					actLabel.add(row);
				}
				label = readA.next();
				
			}else {
				System.out.println("adding to sub array...");
				row.add(label);
				label = readA.next();
			}
			
			if (label.equalsIgnoreCase("Complete")) break;
			
			
		}
		
		System.out.println(actLabel);
		
		readA.close();
	}
	
	/**
	 * To read predefined action labels for single-strategies
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void readSingleActionLabelFile()  {
		
		actionLabelA = new ArrayList<String>();
		
		try {
			readA = new Scanner(new BufferedReader(new FileReader(this.actionLabelAPath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String label = null;
	
		label = readA.next();
		actionLabelA.add(label);
				
		while (readA.hasNext()) {
			label = readA.next();
			actionLabelA.add(label);
		}
		readA.close();
	}
	
	/**
	 * To read predefined action labels for multi-strategies
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
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
	
	public void displayAllMappingList(){
	
		for(int i=0; i < mapslist.size(); i++) {
			System.out.println("The mapping list is "+mapslist.get(i));
		}
	}
	
	public void displayActionLabelAList(){
		if (actionLabelA.size() > 0)
			System.out.println("The list of action label A is "+actionLabelA);
		else
			System.out.println("The list for action label A is empty");
	}
	
	public void displayActionLabelBList(){
		if (actionLabelB.size() > 0)
			System.out.println("The list of action label B is "+actionLabelB);
		else
			System.out.println("The list for action label B is empty");
	}

	public void readTransitionFile()  {
		transStates = new ArrayList<Integer>();
		transActions = new ArrayList<Integer>();
		transLabels = new ArrayList<String>();
		
		try {
			readT = new Scanner(new BufferedReader(new FileReader(this.transPath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public void displayAllTrasitionList(){
		int maxSize = transStates.size();
		
		for(int i=0; i < maxSize; i++) {
			System.out.println("Transition list - state: "+transStates.get(i)+" action: "+transActions.get(i)+" label: "+transLabels.get(i));
		}
	}

	public void	readStrategiesProfile(int evalMethod) {
		//no need to differentiate since the strategies profiles have the same format
		if (evalMethod == 0) {
			readStrategiesfromSingObjSynthesis();
		}else if (evalMethod == 1) {
			readStrategiesfromMultiObjSynthesis();
		}else
			System.err.println("Unable to read strategies file...");
	}
	
	/**
	 * Specifically created for service-based scenario
	 * @param type
	 */
	public void readStrategiesfromSingObjSynthesis()  {
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();
			
		
		try {
			readS = new Scanner(new BufferedReader(new FileReader(this.stratPath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
			
		String inRead=null;
		int potState=0, potAction=0;
		
		//need to skip the first two lines
		readS.nextLine(); readS.nextLine();
		while (readS.hasNextLine()) {
			//inRead = readS.next();

			//read the first index (potential decision state)
			potState = Integer.parseInt(readS.next());
		
			//read the second index (potential action)
			potAction = Integer.parseInt(readS.next());
			
			//store into the array lists
			substrat1States.add(potState);
			substrat1Actions.add(potAction);
			
			//go to next line
			readS.nextLine(); 
	    }
		readS.close();		
	}

	
	
	/**
	 * Specifically created for service-based scenario
	 * @param type
	 */
	public void readStrategiesfromMultiObjSynthesis()  {
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();
			
		
		try {
			readS = new Scanner(new BufferedReader(new FileReader(this.stratPath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		String inRead=null;
		int potState=0, potAction=0;
		int stratId = 0; 
		boolean done = false;
		while (readS.hasNextLine()) {
			inRead = readS.nextLine();
			
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
					substrat1States.add(potState);
					substrat1Actions.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//read the next integer
					inRead = readS.next();
				}
				done = true;
			}
			if (done) break;
        }
		readS.close();		
	}


	/**
	 * To read non-compositional multi-objective strategies from a single strategies profile
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void readMultifromOneStrategiesProfile(int type)  {
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();
			
		if (type == 1) {
			try {
				readS = new Scanner(new BufferedReader(new FileReader(this.stratMulti1Path)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				readS = new Scanner(new BufferedReader(new FileReader(this.stratMulti2Path)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String inRead=null;
		int potState=0, potAction=0;
		int stratId = 0; 
		boolean done = false;
		while (readS.hasNextLine()) {
			inRead = readS.nextLine();
			
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
					substrat1States.add(potState);
					substrat1Actions.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//read the next integer
					inRead = readS.next();
				}
				done = true;
			}
			if (done) break;
        }
		readS.close();		
	}

	
	
	/**
	 * To read compositional multi-objective strategies from a single strategies profile
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void readMultiCompfromOneStrategiesProfile() throws IllegalArgumentException, FileNotFoundException {
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();		
		substrat2States = new ArrayList<Integer>();
		substrat2Actions = new ArrayList<Integer>();
		
		readS = new Scanner(new BufferedReader(new FileReader(this.stratMultiCompPath)));
	
		String inRead=null;
		int potState=0, potAction=0;
		int stratId = 0; 
		boolean done = false;
		while (readS.hasNextLine()) {
			inRead = readS.nextLine();
			
			if (inRead.equalsIgnoreCase("MemUpdMoves:")){
				//skip four lines
				readS.nextLine(); readS.nextLine(); readS.nextLine(); readS.nextLine();
				inRead = readS.next();
				while (readS.hasNextLine() && (!inRead.equalsIgnoreCase("Info:"))) {
					//read the first index (potential decision state)
					potState = Integer.parseInt(inRead);
				
					//read the second index (potential action)
					potAction = Integer.parseInt(readS.next());
				
					//store the same data into two arrays
					substrat1States.add(potState);
					substrat1Actions.add(potAction);
					substrat2States.add(potState);
					substrat2Actions.add(potAction);
				
					//skip the rest of the line
					readS.nextLine();
					
					//read the next integer
					inRead = readS.next();
				}
				done = true;
			}
			if (done) break;
        }
		readS.close();		
	}

	/**
	 * To read from multi-strategies profile
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void readCompfromTwoStrategiesProfile() throws IllegalArgumentException, FileNotFoundException {
		
		substrat1States = new ArrayList<Integer>();
		substrat1Actions = new ArrayList<Integer>();
		substrat2States = new ArrayList<Integer>();
		substrat2Actions = new ArrayList<Integer>();
		
		
		readS = new Scanner(new BufferedReader(new FileReader(this.stratCompPath)));
	
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
			if(stratId > 2) {
				System.err.println("This function only caters for single strategy");
				break;
			}
        
        }
		readS.close();		
	}

	public void displayAllStrategyList(){
		
		if(substrat1States != null) {
			if (substrat1States.size() > 0 ) {
				for(int i=0; i < substrat1States.size(); i++) {
					System.out.println("Sub strategy 1 list - state: "+substrat1States.get(i)+" action: "+substrat1Actions.get(i));
				}
			}
			else
				System.out.println("The states and actions from strategy 1 is empty");
		} 
		
		if(substrat2States != null) {
			if (substrat2States.size() > 0 ) {
				for(int i=0; i < substrat2States.size(); i++) {
					System.out.println("Sub strategy 2 list - state: "+substrat2States.get(i)+" action: "+substrat2Actions.get(i));
				}
			}
			else
				System.out.println("The states and actions from strategy 2 is empty");
		}
		
	}
	
	/**
	 * Objective: To extract selected actions for sequential structure
	 */
	public String findParSolutions(int n)
	{
		int maxSizeStrat1 = substrat1States.size();
		int maxTrans = transStates.size();
		int stateFromStrat1=0, stateFromTrans1=0, actionFromTrans1=0, actionFromStrat1=0;
		String labelFromTrans1 = null;
		boolean found;
		
		//repeat based on the number of node
	//	for(int n=0; n < this.numofDec; n++) {
			found = false;
			//search through strategies profile
			for(int s=0; s < maxSizeStrat1; s++) {
				
				//get the state from the strategy list
				stateFromStrat1 = substrat1States.get(s);
				actionFromStrat1 = substrat1Actions.get(s);
				
				//search through transition list
				for(int t=0; t < maxTrans; t++) {
					stateFromTrans1 = transStates.get(t);
					actionFromTrans1 = transActions.get(t);
					
					//check if the state is similar
					if (stateFromTrans1 == stateFromStrat1)	{
						//check if the action is similar
						if (actionFromTrans1 == actionFromStrat1) {
							//get the label from transition list
							labelFromTrans1 = transLabels.get(t);
							
							for(int i=0; i < this.actionLabel[n].length; i++) {
								//check if the label is an action label
								if (actionLabel[n][i].contains(labelFromTrans1)) {
									this.decStateStrat1 = stateFromStrat1;
									this.selAction1 = actionFromStrat1;
									this.selLabel1 = labelFromTrans1;
									//System.out.println("Decision state :"+stateFromStrat1+" with action :"+labelFromTrans1);
									found = true;
									break;
								}
							}//end of labels for							
						}//end of checking the actions
					}//end of checking the state
					if (found)
						break;	
				}//end of transition for
				if (found)
					break;	
			}//end of strategies for
			if(!found) {
				System.out.println("No decision for Node "+n);
				return null;
			}
			else {
				System.out.println("Decision node: "+n+", state :"+stateFromStrat1+" , action :"+labelFromTrans1);
				return labelFromTrans1;
			}	
		//}//end of number of node for	
	 }
	
	
	/**
	 * Objective: To extract selected actions for sequential structure
	 */
	public void findSolutions()
	{
		int maxSizeStrat1 = substrat1States.size();
		int maxTrans = transStates.size();
		int stateFromStrat1=0, stateFromTrans1=0, actionFromTrans1=0, actionFromStrat1=0;
		String labelFromTrans1 = null;
		boolean found;
		Solution = new String[this.numofDec];
		
		//repeat based on the number of node
		for(int n=0; n < this.numofDec; n++) {
			found = false;
			//search through strategies profile
			for(int s=0; s < maxSizeStrat1; s++) {
				
				//get the state from the strategy list
				stateFromStrat1 = substrat1States.get(s);
				actionFromStrat1 = substrat1Actions.get(s);
				
				//search through transition list
				for(int t=0; t < maxTrans; t++) {
					stateFromTrans1 = transStates.get(t);
					actionFromTrans1 = transActions.get(t);
					
					//check if the state is similar
					if (stateFromTrans1 == stateFromStrat1)	{
						//check if the action is similar
						if (actionFromTrans1 == actionFromStrat1) {
							//get the label from transition list
							labelFromTrans1 = transLabels.get(t);
							
							for(int i=0; i < this.actionLabel[n].length; i++) {
								//check if the label is an action label
								if (actionLabel[n][i].contains(labelFromTrans1)) {
									this.decStateStrat1 = stateFromStrat1;
									this.selAction1 = actionFromStrat1;
									this.selLabel1 = labelFromTrans1;
									//System.out.println("Decision state :"+stateFromStrat1+" with action :"+labelFromTrans1);
									found = true;
									break;
								}
							}//end of labels for							
						}//end of checking the actions
					}//end of checking the state
					if (found)
						break;	
				}//end of transition for
				if (found)
					break;	
			}//end of strategies for
			if(!found) {
				System.out.println("No decision for Node "+n);
			}
			else {
				Solution[n] = labelFromTrans1;
				System.out.println("Decision node: "+n+", state :"+stateFromStrat1+" , action :"+labelFromTrans1);
			}
		}//end of number of node for	
	 }
	
	public String[] getSolution() {
		return Solution;
	}
	
	public String getSingleSolution(int n) {
		return Solution[n];
	}
	
	/**
	 * Objective: To extract a single selected action from a single strategy profile
	 */
	public void findSingleDecision()
	{
		int maxSizeStrat1 = substrat1States.size();
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
		//	if (found)
		//		break;			
		}//end for substrategy 1
		
		if(!found)
			System.out.println("No decision....");
	 }
	
	
	
	
	/**
	 * Objective: To extract multiple selected actions from the strategy profiles
	 */
	public void findMultiDecision()
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

	/**
	 * Objective: To return the selected action based on the the respective game/component.
	 * @param type
	 * @return
	 */
	public String getDecision(int type) {
	
		String nodeName;
		
		if (type == 0) {
			nodeName = getSelectedNodeIdA();
		}
		else {
			nodeName = getSelectedNodeIdB();
		}
			
		return nodeName;
	}
	
	public void displayStrategies(){
		if (this.decStateStrat1 > -1)
			System.out.println("Substrategy 1:- decision state:"+this.decStateStrat1+" action:"+this.selAction1+" label:"+this.selLabel1);
		else
			System.out.println("Empty strategy 1");
		
		if (this.decStateStrat2 > -1)
			System.out.println("Substrategy 2:- decision state:"+this.decStateStrat2+" action:"+this.selAction2+" label:"+this.selLabel2);
		else
			System.out.println("Empty strategy 2");
	}
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
		// TODO Auto-generated method stub
		//Defining File Inputs/Outputs
		
		String linuxPath = "/home/azlan/git/PrismGames/";
		String mainPath = linuxPath;
		
		String stratCompPath = mainPath+"IOFiles/stratComp.txt";
		String stratMultiCompPath = mainPath+"IOFiles/stratMultiComp.txt";
		String stratMulti1Path = mainPath+"IOFiles/stratMulti1.txt";
		String stratMulti2Path = mainPath+"IOFiles/stratMulti2.txt";
		String transPath = mainPath+"IOFiles/transition.txt";
        String actionLabelAPath = mainPath+"IOFiles/actionlabelA";
        String actionLabelBPath = mainPath+"IOFiles/actionlabelB";
        
        //simulating the input configuration process
        //I need these inputs to provide the node information
		ConfigurationPlanner conf = new ConfigurationPlanner();
        conf.setAppRequirements(0, 1, 30.0, 300.0, 20, 20);
 		conf.setAppRequirements(1, 1, 25.0, 600.0, 20, 20);
 		
 		conf.setNodeCapabilities(0, "NODE0", 1, 200, 0.3, 1000, 500, "LOC0");
 		conf.setNodeCapabilities(1, "NODE1", 1, 200, 0.3, 1000, 500, "LOC1");
 		conf.setNodeCapabilities(2, "NODE2", 1, 2500, 0.5, 1000, 500, "LOC2");
 		conf.setNodeCapabilities(3, "NODE3", 1, 2500, 0.5, 1000, 500, "LOC3");
 		conf.setNodeCapabilities(4, "NODE4", 1, 2500, 0.7, 1000, 500, "LOC4");
 		conf.setNodeCapabilities(5, "NODE5", 1, 2500, 0.7, 1000, 500, "LOC5");
 		conf.setNodeCapabilities(6, "NODE6", 1, 2500, 0.7, 1000, 500, "LOC6");
 		conf.setNodeCapabilities(7, "NODE7", 1, 2500, 0.7, 1000, 500, "LOC7");
 		
		
		StrategyExtraction ste = new StrategyExtraction(transPath, stratCompPath, stratMultiCompPath, stratMulti1Path, stratMulti2Path, actionLabelAPath, actionLabelBPath);	
		ste.setNodeIdList(conf.getNodeIdList());
		
		ste.readActionLabelFile();
		//ste.displayActionLabelAList();
		//ste.displayActionLabelBList();

		ste.readTransitionFile();
		//ste.getAllTrasitionList();
		ste.readMultiCompfromOneStrategiesProfile();
		//ste.getAllStrategyList();
		ste.findMultiDecision();
		//ste.displayStrategies();
		
		System.out.println("Selected node from compositional multi-objective strategy: "+ste.getSelectedNodeIdA());
		System.out.println("Selected node from compositional multi-objective strategy: "+ste.getSelectedNodeIdB());
		
		ste.readMultifromOneStrategiesProfile(1);
		ste.findSingleDecision();
		System.out.println("Selected node from non-compositional mult-objective strategy: "+ste.getSelectedNodeIdA());
	
		ste.readMultifromOneStrategiesProfile(2);
		ste.findSingleDecision();
		System.out.println("Selected node from non-compositional mult-objective strategy: "+ste.getSelectedNodeIdA());
		
	}

}
