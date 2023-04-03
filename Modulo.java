/**
 * @author My Tran
 * @version 1.0
 * @description Implements operations of modulo with accounting for negative
 *              values
 */
public class Modulo {

    /**
     * a mod p but accounts for negatives
     * 
     * @param dividend
     * @param divisor
     * @return
     */
    public static int Mod(int dividend, int divisor) {
        if (dividend < 0) {
            // amount of times divisor goes into the abs value of the divisor to get closer
            // to 0 in less iterations
            int temp = (-dividend) / divisor;

            dividend += (divisor * temp);

            while (dividend < 0) {
                dividend += divisor;
            }

            return dividend;
        }

        return dividend % divisor;
    }

    /**
     * Calculates the multiplicative inverse modulo p of a
     * 
     * @param dividend a
     * @param modulo   p
     * @return
     */
    public static int InverseMod(int dividend, int modulo) {
        // pn = p(n-2) - p(n-1) * q(n-1) (mod m)
        boolean negativeFlag = false;
        if (dividend < 0) {
            negativeFlag = true;
            dividend *= -1;
        }

        int pInitial = modulo, p0 = 0, p1 = 1;

        // 2 mod 1 = 1
        if (modulo == 1) {
            return 2;
        }

        // once there is no more remainder, loop ends
        while (dividend > 1 && modulo > 0) {
            int quotient = dividend / modulo; // qi
            int temp = modulo;

            modulo = dividend % modulo;
            dividend = temp;
            temp = p0;

            // pn = p(n-2) - p(n-1)q(n-1)
            p0 = p1 - quotient * p0;
            p1 = temp;
        }

        if (p1 < 0) {
            p1 += pInitial;
        }

        if (negativeFlag) {
            p1 *= -1;
        }
        return p1;
    }

    public static void main(String args[]) {

        System.out.println("Possible random coefficients for s1 mod 31");
        for (int i = 0; i < 31; i++) {
            // add code here
            System.out.println("s1: " + i + "\ta1: " + Modulo.Mod(3 * (17 - i), 31));
        }

        System.out.println("Possible random coefficients for s2 mod 31");
        for (int i = 0; i < 31; i++) {
            // add code here
            System.out.println("s2: " + i + "\ta1: " + Modulo.Mod(3 * (26 - i), 31));
        }

        System.out.println("Possible pairs with x = 21, and q = 31");
        for (int i = 0; i < 31; i++) {
            // add code here
            System.out.println("s1: " + i + "\ts2 :" + Modulo.Mod(i - 22, 31));
        }
        System.out.println(InverseMod(21, 31));

    }

}
