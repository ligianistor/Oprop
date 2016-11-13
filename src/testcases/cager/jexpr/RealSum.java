package testcases.cager.jexpr;

//on System A 
class RealSum implements Sum {

    private int sum = 0;
    /**
     * Constructor
     * @param filename
     */
    public RealSum(int n) { 
        calculateRealSum(n);
    }

    /**
     * Acually calculates the sum.
     */
    private void calculateRealSum(int n) {
        this.sum = n * (n+1) / 2;
    }

    /**
     * Returns the sum.
     */
    public int calculateSum() { 
       return sum;
    }

	@Override
	public void displaySum() {
		System.out.println(sum);	
	}

}