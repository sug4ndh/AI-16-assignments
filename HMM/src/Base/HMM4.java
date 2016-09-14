package Base;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

public class HMM4 {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		sc.useLocale(Locale.US);
		
		LambdaOld lambda = new LambdaOld(sc);
		
		int[] O = new int[sc.nextInt()];
		for (int i = 0; i < O.length; i++) {
			O[i] = sc.nextInt();
		}
		
		lambda.optimize(O);
		
		System.out.println(lambda.printA());
		System.out.println(lambda.printB());

	}

}
