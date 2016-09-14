import java.util.Arrays;
import java.util.Scanner;

/***
 * 
 * @author Daniel Stüwe - used pseudo code from the HMM paper of Mark Stamp
 *
 */

public class Lambda {

	private Matrix A;
	private Matrix B;

	private Matrix pi;

	private double[][] alpha;
	private double[] c;
	private double[][] beta;
	private double[][][] di_gamma;
	private double[][] gamma;
	
	private int N;
	private int M;

	public Lambda(Matrix A, Matrix B, Matrix pi) {
		this.A = A;
		this.B = B;
		this.pi = pi;
		this.N = A.m();
		this.M = B.n();
	}
	
	public Lambda(int N, int M) {
		this.A = new Matrix(N, N, true);
		this.B = new Matrix(N, M, true);
		this.pi = new Matrix(1, N, true);
		this.N = A.m();
		this.M = B.n();
	}
	
	public Lambda(int N, int M, Matrix pi) {
		this.A = new Matrix(N, N, true);
		this.B = new Matrix(N, M, true);
		this.pi = pi;
		this.N = A.m();
		this.M = B.n();
	}

	// public Lambda(Matrix A, Matrix B, Matrix pi, int[] O) {
	// this(A, B, pi);
	// this.O = O;
	// }

	public Lambda(Scanner sc) {
		this.A = new Matrix(sc);
		this.B = new Matrix(sc);
		this.pi = new Matrix(sc);
		this.N = A.m();
		this.M = B.n();
	}

	public String printA() {
		return A.toString();
	}
	
	public String printB() {
		return B.toString();
	}

	public String print_pi() {
		return pi.toString();
	}
	
	public void prettyPrint() {
		System.out.println(A.toPrettyString());
		System.out.println("");
		System.out.println(B.toPrettyString());
		System.out.println("");
		System.out.println(pi.toPrettyString());
	}

	public void A_step() {
		pi = Matrix.mult(Matrix.mult(pi, A), B);
	}

	public double forward(int[] O) {

		int T = O.length;
		alpha = new double[T][N];

		c = new double[T];

		// compute alpha[0][i]
		for (int i = 0; i < N; i++) {
			alpha[0][i] = pi.get(0, i) * B.get(i, O[0]);
			c[0] += alpha[0][i];
		}

		// scale the alpha[0][i]
		c[0] = (double) ((double) 1d) / c[0];
		for (int i = 0; i < N; i++) {
			alpha[0][i] *= c[0];
		}

		// compute alpha[t][i]
		for (int t = 1; t < T; t++) {
			for (int i = 0; i < N; i++) {

				for (int j = 0; j < N; j++) {
					alpha[t][i] += alpha[t - 1][j] * A.get(j, i);
				}

				alpha[t][i] *= B.get(i, O[t]);
				c[t] += alpha[t][i];
			}

			// scale the alpha[t][i]
			c[t] = (double) ((double) 1) / c[t];
			for (int i = 0; i < N; i++) {
				alpha[t][i] *= c[t];
			}
		}

		return 1 / Arrays.stream(c).reduce(1, (a, b) -> a * b);
	}

	public int[] delta(int[] O) {

		int T = O.length;
		double[][] delta = new double[T][N];
		int[][] deltaArg = new int[T - 1][N];

		double[] c = new double[T];

		// compute delta[0][i]
		for (int i = 0; i < N; i++) {
			delta[0][i] = pi.get(0, i) * B.get(i, O[0]);
			c[0] += delta[0][i];
		}

		// scale the delta[0][i]
		c[0] = 1 / c[0];
		for (int i = 0; i < N; i++) {
			delta[0][i] *= c[0];
		}

		// compute delta[t][i]
		for (int t = 1; t < T; t++) {
			for (int i = 0; i < N; i++) {

				int maxA = 0;
				double maxV = 0;
				for (int j = 0; j < N; j++) {
					double curr = delta[t - 1][j] * A.get(j, i) * B.get(i, O[t]);
					if (curr > maxV) {
						maxV = curr;
						maxA = j;
					}
				}
				delta[t][i] = maxV;
				deltaArg[t - 1][i] = maxA;
				c[t] += maxV;
			}

			// scale the delta[t][i]
			c[t] = 1 / c[t];
			for (int i = 0; i < N; i++) {
				delta[t][i] *= c[t];
			}
		}

		int maxA = 0;
		double maxV = 0;
		for (int j = 0; j < N; j++) {
			if (delta[T - 1][j] > maxV) {
				maxV = delta[T - 1][j];
				maxA = j;
			}
		}

		int[] X_opt = new int[T];
		X_opt[T - 1] = maxA;

		for (int t = T - 2; t >= 0; t--) {
			X_opt[t] = deltaArg[t][X_opt[t + 1]];
		}

		return X_opt;
	}

	private double[][] backward(int[] O) {

		int T = O.length;
		beta = new double[T][N];

		// Initialize beta[T-1][i]
		for (int i = 0; i < N; i++) {
			beta[T - 1][i] = c[T - 1];
		}

		// compute beta[t][i]
		for (int t = T - 2; t >= 0; t--) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					beta[t][i] += beta[t + 1][j]  * B.get(j, O[t+1]) * A.get(i, j);
				}

				// scale the beta[t][i]
				beta[t][i] *= c[t];
			}
		} 
		
		return beta;
	}
	
	private void gammas(int[] O) {

		int T = O.length;
		di_gamma = new double[T-1][N][N];
		gamma = new double[T][N];
		
		for(int t = 0; t < T-1; t++){
			// Denominator is different from formula because of scaling
			double denom = 0;
			for(int i = 0; i < N; i++){
				for (int j = 0; j < N; j++) {
					denom += alpha[t][i] * A.get(i, j) * B.get(j, O[t+1]) * beta[t+1][j];
				}
			}
			
//			for(int i = 0; i < N; i++){
//				denom += alpha[T-1][i];
//			}
			
			// denom = 1/denom;

			for(int i = 0; i < N; i++){
				for (int j = 0; j < N; j++) {
					di_gamma[t][i][j] = alpha[t][i] * A.get(i, j) * B.get(j, O[t+1]) *
							beta[t+1][j] / denom; // * denom;
					gamma[t][i] += di_gamma[t][i][j]; 
				}
			} 
		}
		
		// no di_gamme here
		double denom = 0;
		for (double a : alpha[T-1]) {
			denom += a;
		}
		
		// denom = 1/denom;
		
		for (int i = 0; i < N; i++) {
			gamma[T-1][i] = alpha[T-1][i] / denom; // *denom;
		}
		
	}
	
	public void fixit(int[] O) {
	
		int T = O.length;
		
		pi = new Matrix(gamma[0]); // safe link
	
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				double num = 0, denom = 0;
				
				for(int t = 0; t < T - 1; t++){
					num += di_gamma[t][i][j];
					denom += gamma[t][i];
				}
				
				A.set(i, j, num/denom);
				
			}
		}
		

		for(int i = 0; i < N; i++){
			for(int j = 0; j < M; j++){
				double num = 0, denom = 0;
				
				for(int t = 0; t < T - 1; t++){
					num += (O[t] == j) ? gamma[t][i] : 0;
					denom += gamma[t][i];
				}
				
				B.set(i, j, num/denom);
				
			}
		}
	}
	
	public double measure() {
		
		double magic = 0;
		int T = c.length;
		
		for(int i = 0; i < T; i++){
			magic += Math.log(c[i]);
		}
		
		return -magic;
	}
	
	public void optimize(int[] O) {
		
		for(int i = 0; i < 000; i++){
			forward(O);
			backward(O);
			gammas(O);
			fixit(O);
		}
		
		int max = 0;
		double niveau, currniveau = Double.NEGATIVE_INFINITY;
		do {
			max++;
			niveau = currniveau;
			forward(O);
			backward(O);
			gammas(O);
			fixit(O);
			currniveau = measure();
		} while (niveau < currniveau && max < 10000);
		
		System.err.println(max);
		System.err.flush();
		
		// while ((niveau / currniveau)-1 > 0.0001 && max < 50);

	}
	
	
}
