package testcases.cager.jexpr;

class StateContext {
    private Statelike myState;
    StateContext() {
        setState(new StateMultiplyTwo());
    }

    /**
     * Setter method for the state.
     * Normally only called by classes implementing the State interface.
     * @param newState the new state of this context
     */
    void setState(final Statelike newState) {
        myState = newState;
    }

    public void computeResult(final int num) {
        myState.computeResult(this, num);
    }
    
    public int getIntCellStatelike() {
    	return myState.getValueIntCell();
    }
}
