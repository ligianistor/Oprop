package testcases.cager.jexpr;

//on System B 
class ProxySum implements Sum {
    private RealSum realSum = null;
    private int n = 0;
    /**
     * Constructor
     * @param filename 
     */
    public ProxySum(final int n) { 
        this.n = n; 
    }

    /**
     * Calculates the sum
     */
    public int calculateSum() {
        if (realSum == null) {
           realSum = new RealSum(n);
        } 
      return realSum.calculateSum();
    }
    
	@Override
	public void displaySum() {
		System.out.println(realSum.calculateSum());	
	}

}