package testcases.cager.jexpr;

class StateMultiplyThree implements Statelike {
    /** Counter local to this state */
    private int count = 0;

    @Override
    public void computeResult(final StateContext context, final int num) {
        /* Change state after StateMultiplyThree's computeResult() gets invoked twice */
        if(++count > 1) {
            context.setState(new StateMultiplyTwo());
        }
	cell.setValue(num*3);
	System.out.println("StateMultiplyThree" + num*3 + "; count = "+count);
    }

    public int getValueIntCell() {
	return cell.getValue();
}
    
}
