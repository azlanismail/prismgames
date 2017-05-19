package planner;

public class Properties {
	int id;
	String name;
	double threshold;
	String comparator;
	
	public Properties(int i, String nm, double th, String cp) {
		id = i;
		name = nm;
		threshold = th;
		comparator = cp;
	}
	
	public int translateComparator() {
		int compId=-1;
		if (comparator.equalsIgnoreCase("<")) {
			compId = 0;
		}
		
		return compId;
	}
}
