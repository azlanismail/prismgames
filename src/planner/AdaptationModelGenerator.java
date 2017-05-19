package planner;

public class AdaptationModelGenerator extends ModelGenerator{

	double costR, availR, reliaR;
	int timeR;
	
	public void setUpperBounds(int numNode, int mxActionP1, int mxActionP2) {
		super.numNode = numNode;
		super.maxActionP1 = mxActionP1;
		super.maxActionP2 = mxActionP2;
		
		cost = new int[numNode][maxActionP1];
		avail = new boolean[numNode][maxActionP1];
		dur = new int[numNode][maxActionP1][maxActionP2];
		rel = new double[numNode][maxActionP1][maxActionP2];
	}
	
	public void setRequirements(int reqId, double cost, int dur, double rel, double avail) {
		super.idR = reqId;
		this.costR = cost;
		this.timeR = dur;
		this.reliaR = rel;
		this.availR = avail;
	}
	
	
}
