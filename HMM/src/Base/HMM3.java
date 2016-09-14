package Base;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

public class HMM3 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		sc.useLocale(Locale.US);
		DecimalFormat df = new DecimalFormat("###.0#####", new DecimalFormatSymbols(Locale.US));
		
		LambdaOld lambda = new LambdaOld(sc);
		
		int[] O = new int[sc.nextInt()];
		for (int i = 0; i < O.length; i++) {
			O[i] = sc.nextInt();
		}
		
		for(int i : lambda.delta(O)){
			System.out.print(i + " ");
		}

	}

}
