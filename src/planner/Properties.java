package planner;

public class Properties<T> extends QualityAttributes{

	T fuzzyVal;
	String comparator;
	
	public Properties() { }
	
	public void setProperties(int i, String nm, String ty, T th, T fv, String cp) {
		id = i;
		name = nm;
		type = ty;
		values = th;	//value of the threshold
		fuzzyVal = fv;
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
