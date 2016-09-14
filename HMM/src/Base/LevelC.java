package Base;
import java.util.Locale;
import java.util.Scanner;

public class LevelC {

	private static final double[][] A = 
		   {{0.54, 0.05, 0.25},
	        {0.19, 0.53, 0.28},
	        {0.22, 0.18, 0.6}};
	
	private static final double[][] B =
		{{0.5 , 0.2 , 0.11, 0.19},
	     {0.22, 0.28, 0.23, 0.27},
	     {0.19, 0.21, 0.15, 0.45}};
	
	private static final double[][] Aa =
		{{1.0, 0.0, 0.0},
	     {0.0, 1.0, 0.0},
	     {0.0, 0.0, 1.0}};
	
	private static final double[][] pia = {{0.0, 0.0, 1,0}};
	
	private static final double[][] pi = {{0.3, 0.2, 0.5}};
	
	private static final double[][] pi2 = {{0.27, 0.26, 0.24, 0.23}};
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		sc.useLocale(Locale.US);;
		
		LambdaOld lambda = new LambdaOld(new Matrix(Aa), new Matrix(B), new Matrix(pia));
		
		int[] O = new int[sc.nextInt()];
		for (int i = 0; i < O.length; i++) {
			O[i] = sc.nextInt();
		}
		
//		lambda.prettyPrint();
		lambda.optimize(O);
		lambda.prettyPrint();
		
//		Lambda lambda2 = new Lambda(new Matrix(A), new Matrix(B), new Matrix(pi));
//		lambda2.optimize(O);
//		lambda2.prettyPrint();
		

	}
	
}
