package testcases.cager.jexpr;

public class StateClient {
    	public static void main(String[] args) {
        final StateContext sc = new StateContext();
        int result = 0;

        sc.computeResult(1);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(2);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(3);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(4);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(5);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(6);
        result = result + sc.getIntCellStatelike();
        sc.computeResult(7);
        result = result + sc.getIntCellStatelike();
	// assert result == 72
	// System.out.println(result);
    }
}