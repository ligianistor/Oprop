package testcases.cager.jexpr;

class StateMultiplyTwo implements Statelike {

    @Override
	
    public void computeResult(final StateContext context, final int num) {
        context.setState(new StateMultiplyThree());
	cell.setValue(num*2);
	System.out.println("StateMultiplyTwo" + num*2);
    }
    
    public int getValueIntCell() {
	return cell.getValue();
}

}