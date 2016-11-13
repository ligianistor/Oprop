package testcases.cager.jexpr;

public interface Statelike {
	final static IntCell cell =  new IntCell(1);    
	void computeResult(StateContext context, int num); 
	int getValueIntCell();
}
