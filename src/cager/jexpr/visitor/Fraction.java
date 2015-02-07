package cager.jexpr.visitor;

public class Fraction {
	//up==numerator
	//down==denominator
	int up;
	int down;
	
	public Fraction(int u, int d) {
		up=u;
		down=d;
	}
	
	public void multiplyFraction(int k) {
		up=up*k;
	}

	public void divideFraction(int k) {
		down=down*k;
	}
	
	public void changeTo(int u, int d) {
		up= u;
		down = d;
	}
}
