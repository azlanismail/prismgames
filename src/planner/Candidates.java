package planner;

public class Candidates {

	String name;
	int total;
	
	public Candidates(String n) {
		name = n;
		total =0;
	}
	
	public void setName(String n) {
		name = n;
	}
	public void addTotal(int i) {
		total = total + i;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTotal() {
		return total;
	}
	
	public String toString() {
		return "name : "+name+", total: "+total;
	}
	
}
