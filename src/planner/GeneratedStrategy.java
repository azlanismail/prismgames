package planner;

public class GeneratedStrategy {


	private int current_state;
	private int current_move;
	private int current_corner;
	private int next_state;
	
	public GeneratedStrategy(){ }
	
	public GeneratedStrategy(int cs, int cm, int cc, int ns){
		this.current_state = cs;
		this.current_move = cm;
		this.current_corner = cc;
		this.next_state = ns;
	}
	
	public void setCurrentState(int cs) {
		this.current_state = cs;
	}
	
	public void setCurrentMove(int cm) {
		this.current_move = cm;
	}
	
	public void setCurrentCorner(int cc) {
		this.current_corner = cc;
	}
	
	public void setNextState(int ns) {
		this.next_state = ns;
	}
	
	public int getCurrentState() {
		return this.current_state;
	}
	
	public int getCurrentMove() {
		return this.current_move;
	}
	
	public int getCurrentCorner() {
		return this.current_corner;
	}
	
	public int getNextState() {
		return this.next_state;
	}
}
