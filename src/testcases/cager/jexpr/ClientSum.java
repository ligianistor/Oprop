package testcases.cager.jexpr;

class ProxyExample {

   /**
    * Test method
    */
   public static void main(String[] args) {
        final Sum sum1 = new ProxySum(5);
        final Sum sum2 = new ProxySum(3);
        
        sum1.calculateSum(); // calculation necessary
        sum1.displaySum();
        sum1.calculateSum(); // calculation unnecessary
        sum1.displaySum();
        sum2.calculateSum(); // calculation necessary
        sum2.displaySum();
        sum2.calculateSum(); // calculation unnecessary
        sum2.displaySum();
        sum1.calculateSum(); // calculation unnecessary
        sum1.displaySum();
    }
}