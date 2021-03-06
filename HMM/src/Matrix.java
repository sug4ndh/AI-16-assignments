import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.DoubleStream;

import java.text.*;

public class Matrix {
	
	
	/***
	 * 
	 * @param A right input matrix
	 * @param B left input matrix
	 * @return A*B
	 */
	public static Matrix mult(Matrix A, Matrix B) {
		
		if(A.n != B.m) throw new IllegalArgumentException("Matrices' dimensions doesn't fit!");
		
		double[][] res = new double[A.m][B.n];
		
		for(int i = 0; i < A.m; i++){
			for(int j = 0; j < B.n; j++){
				for(int k = 0; k < A.n; k++){
					res[i][j] += A.arr[i][k]*B.arr[k][j]; 
				}
			}
		}
		
		return new Matrix(res);
	}
	
	
	public int m() {
		return m;
	}

	public int n() {
		return n;
	}


	private int m, n;
	private double[][] arr;
	
	
	/***
	 * @return zero initialized matrix
	 * @param m vertical dimension
	 * @param n horizontal dimension
	 */
	public Matrix(int m, int n, boolean randomInit) { // , boolean rowStoch
		this.m = m;
		this.n = n;
		arr = new double[m][n];
		
		if(randomInit){
			
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					arr[i][j] = Math.random() + 20.0;
				}
			}
			
//			if (rowStoch){
				
				for (int i = 0; i < m; i++) {
					
					DoubleStream row = Arrays.stream(arr[i]);
					double acc = row.sum();
					row.close();
					
					// JAVA AAARRRGHH
					// arr[i] = row.map(x -> x/acc).toArray();
					
					for (int j = 0; j < n; j++) {
						arr[i][j] /= acc;
					}
					
				}
				
//			} else {
//				
//				for (int j = 0; j < n; j++) {
//					
//					double acc = 0;
//					for (int i = 0; i < m; i++) {
//						acc += arr[i][j];
//					}
//					
//					for (int i = 0; i < m; i++) {
//						arr[i][j] /= acc;
//					}
//					
//				}
//			}
			
		}
	}
	
	/**
	 * smooth a row stochastic matrix
	 */
	public void smooth(double amount) { 

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				arr[i][j] += amount;
			}
		}



		for (int i = 0; i < m; i++) {

			DoubleStream row = Arrays.stream(arr[i]);
			double acc = row.sum();
			row.close();

			for (int j = 0; j < n; j++) {
				arr[i][j] /= acc;
			}

		}


	}

	
	/***
	 * 
	 * @param arr matrix like (n x m) array, property is unchecked!
	 */
	public Matrix(double[][] arr) {
		this.m = arr.length;
		this.n = arr[0].length;
		
		this.arr = arr;
	}
	
	public Matrix(double[] arr) {
		this.m = 1;
		this.n = arr.length;
		this.arr = new double[1][];
		
		this.arr[0] = arr;
	}
	
	/***
	 * 
	 * @param stream scans matrix from input
	 */
	public Matrix(Scanner sc) {
		
		this.m = sc.nextInt();
		this.n= sc.nextInt();
		
		arr = new double[m][n];
		
		for(int i = 0; i < m; i++){
			for(int j = 0; j < n; j++){
				arr[i][j] = sc.nextDouble();
			}
		}
		
		// sc.nextLine();
		
		// sc.close();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(m).append(' ');
		sb.append(n).append(' ');
		
		DecimalFormat df = new DecimalFormat("##0.0#####", new DecimalFormatSymbols(Locale.US));
		
		for(int i = 0; i < m; i++){
			for(int j = 0; j < n; j++){
				sb.append(df.format(arr[i][j])).append(' ');
			}
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
		
	}
	
	public String toPrettyString() {
		StringBuilder sb = new StringBuilder();
		
		DecimalFormat df = new DecimalFormat("#.000", new DecimalFormatSymbols(Locale.US));
		
		for(int i = 0; i < m; i++){
			for(int j = 0; j < n; j++){
				sb.append(df.format(arr[i][j])).append(' ');
			}
			sb.append('\n');
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
		
	}
	
	public double get(int i, int j) {
		return arr[i][j];
	}
	
	public void set(int i, int j, double x) {
		arr[i][j] = x;
	}



}
