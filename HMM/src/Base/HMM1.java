package Base;
import java.util.Locale;
import java.util.Scanner;

/**
 * @author Daniel Stüwe
 *
 */

public class HMM1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		sc.useLocale(Locale.US);
		
		
		Lambda lambda = new Lambda(sc);
		
		lambda.A_step();
		
		System.out.println(lambda.print_pi());
		
	}

}
