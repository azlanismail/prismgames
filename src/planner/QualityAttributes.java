package planner;

public class QualityAttributes<T> {
	int id;
	String name;
	String type;
	T values;
	
	public QualityAttributes() { }
	
	public void setQualityAttributes(int i, String nm, String ty, T vl) {
		id = i;
		name = nm;
		type = ty;
		values = vl; 
	}

}
