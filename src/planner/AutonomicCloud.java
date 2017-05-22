package planner;

public class AutonomicCloud {
	
	int numResource;
	
	public AutonomicCloud() {
		
	}
	
	public AutonomicCloud(int vm) {
		numResource = vm;
	}
	
	public void setVM(int vm) {
		numResource = vm;
	}
	
	public int getVM() {
		return numResource;
	}
}
