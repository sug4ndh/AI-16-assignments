import java.util.*;

/***
 * 
 * @author Daniel Stüwe - used pseudo code from the HMM paper of Mark Stamp
 *
 */

public class Lambda {

	private Matrix state_transion_prob;
	private Matrix state_emission_prob;

	private Matrix initial_state_dist;

	private double[][] alpha;
	private double[] scaling_factor;
	private double[][] beta;
	private double[][][] di_gamma;
	private double[][] gamma;
	
	private int numStates;
	private int numEmissions;

	public Lambda(Matrix A, Matrix B, Matrix pi) {
		this.state_transion_prob = A;
		this.state_emission_prob = B;
		this.initial_state_dist = pi;
		this.numStates = A.m();
		this.numEmissions = B.n();
	}
	
	/***
	 * 
	 * Initializes matrixes with random, but row stochastic values
	 * 
	 * @param N number of states
	 * @param M number of emissions
	 */
	public Lambda(int N, int M) {
		this.state_transion_prob = new Matrix(N, N, true);
		this.state_emission_prob = new Matrix(N, M, true);
		this.initial_state_dist = new Matrix(1, N, true);
		this.numStates = state_transion_prob.m();
		this.numEmissions = state_emission_prob.n();
	}
	
	/***
	 * 
	 * Initializes matrixes with random, but row stochastic values
	 * 
	 * @param N number of states
	 * @param M number of emissions
	 * @param pi initial state distribution
	 */
	public Lambda(int N, int M, Matrix pi) {
		this.state_transion_prob = new Matrix(N, N, true);
		this.state_emission_prob = new Matrix(N, M, true);
		this.initial_state_dist = pi;
		this.numStates = state_transion_prob.m();
		this.numEmissions = state_emission_prob.n();
	}

	// public Lambda(Matrix A, Matrix B, Matrix pi, int[] O) {
	// this(A, B, pi);
	// this.O = O;
	// }

	/**
	 * 
	 * @param sc Scanner with stream where the matrix is encoded in
	 */
	public Lambda(Scanner sc) {
		this.state_transion_prob = new Matrix(sc);
		this.state_emission_prob = new Matrix(sc);
		this.initial_state_dist = new Matrix(sc);
		this.numStates = state_transion_prob.m();
		this.numEmissions = state_emission_prob.n();
	}
	
	

	public String printA() {
		return state_transion_prob.toString();
	}
	
	public String printB() {
		return state_emission_prob.toString();
	}

	public String print_pi() {
		return initial_state_dist.toString();
	}
	
	public void prettyPrint() {
		System.err.println(state_transion_prob.toPrettyString());
		System.err.println("");
		System.err.println(state_emission_prob.toPrettyString());
		System.err.println("");
		System.err.println(initial_state_dist.toPrettyString());
		System.err.println();
	}

	/***
	 * 
	 * @return state distribution after one step
	 */
	public Matrix A_step() {
		return Matrix.mult(Matrix.mult(initial_state_dist, state_transion_prob), state_emission_prob);
	}
	
	/***
	 * 
	 * @param lambdas HMMs to test
	 * @param O observation sequence
	 * @return index of best HMM
	 */
	public static int bestOf(List<Lambda> hmms, ArrayList<Integer> O) {
		
		double bestV = Double.NEGATIVE_INFINITY;
		int bestA = 0, n = hmms.size();
		
		for(int i = 0; i < n; i++){
			Lambda lambda = hmms.get(i);
			if (lambda == null) {
				continue;
			}
//			System.err.println(i);
//			lambda.prettyPrint();
			double prob = lambda.forward(O);
			
			// System.err.print(" " + prob + " " + Math.exp(prob));
			// if(prob == Double.NaN) System.err.println("ALARM __ !!");
			
			if(bestV < prob) { 
				bestA = i;
				bestV = prob;
			}
		}
		// System.err.println(" ");

		return bestA;
	}
	
	public int nextEmission(int lastIndex) {
		
		int T = lastIndex, emission = 0;
		double scaling = Double.MAX_VALUE;
		
		for(int o = 0; o < numEmissions; o++){

			double newscaling = 0;

			for (int i = 0; i < numStates; i++) {
				int newalpha = 0;
				for (int j = 0; j < numStates; j++) {
					
					newalpha += alpha[T - 1][j] * state_transion_prob.get(j, i);
//					System.err.println("alpha T " + j + "  = " + alpha[T - 1][j]);
				}

				newscaling += newalpha * state_emission_prob.get(i, o);
			}
			
			newscaling = divide(1.0, newscaling);
			
			// System.err.println(newscaling);
			
			if(newscaling < scaling) {
				scaling = newscaling;
				emission = o;
			}

		}
		
		if(!Double.isFinite(scaling)) return -1;
		
		return emission;
	}
	
	public int nextEmission(ArrayList<Integer> O) {
		
		int n = O.size(), curr = 0;
		O.add(0); 
		double old = Double.NEGATIVE_INFINITY;
		for(int o = 0; o < numEmissions; o++){
			O.set(n, o);
			double now = forward(O);
			if(now > old){
				old = now;
				curr = o;
			}
		}
		
		return curr;
	}

	/**
	 * 
	 * @param O observation matrix
	 * @return logarithmic probability of given observation sequence
	 */
	public double forward(ArrayList<Integer> O) {

		int numObservations = O.size();
		alpha = new double[numObservations][numStates];

		scaling_factor = new double[numObservations];

		// compute alpha[0][i]
		for (int i = 0; i < numStates; i++) {
			alpha[0][i] = initial_state_dist.get(0, i) * 
					state_emission_prob.get(i, O.get(0));
			scaling_factor[0] += alpha[0][i];
		}

		// scale the alpha[0][i]
		scaling_factor[0] = divide(1.0, scaling_factor[0]);
		for (int i = 0; i < numStates; i++) {
			alpha[0][i] *= scaling_factor[0];
		}

		// compute alpha[t][i]
		for (int t = 1; t < numObservations; t++) {
			for (int i = 0; i < numStates; i++) {

				for (int j = 0; j < numStates; j++) {
					alpha[t][i] += alpha[t - 1][j] * state_transion_prob.get(j, i);
				}

				alpha[t][i] *= state_emission_prob.get(i, O.get(t));
				scaling_factor[t] += alpha[t][i];
			}

			// scale the alpha[t][i]
			scaling_factor[t] = divide(1.0, scaling_factor[t]);
//			if(scaling_factor[t] == Double.NaN) System.err.println("ALARM11");
			for (int i = 0; i < numStates; i++) {
				alpha[t][i] *= scaling_factor[t];
			}
		}

		// return 1 / Arrays.stream(c).reduce(1, (a, b) -> a * b);
		double logProb = measure();
		if (!Double.isFinite(logProb)) {
			logProb = Double.NEGATIVE_INFINITY;
//			System.err.println("ALARM");
//			for (double d : scaling_factor)
//				System.err.print(d + " ");
//			System.err.println();
//			prettyPrint();
//			try {
//				TimeUnit.MILLISECONDS.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		return logProb;
		// return 1.0 / Arrays.stream(scaling_factor).reduce(1, (a, b) -> a * b);
	}

	/***
	 * 
	 * @param O observation sequence
	 * @return probability of given observation sequence
	 */
	public int[] delta(ArrayList<Integer> O) {

		int T = O.size();
		double[][] delta = new double[T][numStates];
		int[][] deltaArg = new int[T - 1][numStates];

		double[] c = new double[T];

		// compute delta[0][i]
		for (int i = 0; i < numStates; i++) {
			delta[0][i] = initial_state_dist.get(0, i) * state_emission_prob.get(i, O.get(0));
			c[0] += delta[0][i];
		}

		// scale the delta[0][i]
		c[0] = divide(1.0, c[0]);
		for (int i = 0; i < numStates; i++) {
			delta[0][i] *= c[0];
		}

		// compute delta[t][i]
		for (int t = 1; t < T; t++) {
			for (int i = 0; i < numStates; i++) {

				int maxA = 0;
				double maxV = 0;
				for (int j = 0; j < numStates; j++) {
					// alpa[t][j] +=
					double curr = delta[t - 1][j] * state_transion_prob.get(j, i) * state_emission_prob.get(i, O.get(t));
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
			c[t] = divide(1.0, c[t]);
			for (int i = 0; i < numStates; i++) {
				delta[t][i] *= c[t];
			}
		}

		int maxA = 0;
		double maxV = 0;
		for (int j = 0; j < numStates; j++) {
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

	private double[][] backward(ArrayList<Integer> O) {

		int T = O.size();
		beta = new double[T][numStates];

		// Initialize beta[T-1][i]
		for (int i = 0; i < numStates; i++) {
			beta[T - 1][i] = scaling_factor[T - 1];
		}

		// compute beta[t][i]
		for (int t = T - 2; t >= 0; t--) {
			for (int i = 0; i < numStates; i++) {
				for (int j = 0; j < numStates; j++) {
					beta[t][i] += beta[t + 1][j]  * state_emission_prob.get(j, O.get(t+1)) * state_transion_prob.get(i, j);
				}

				// scale the beta[t][i]
				beta[t][i] *= scaling_factor[t];
			}
		} 
		
		return beta;
	}
	
	private void gammas(ArrayList<Integer> O) {

		int T = O.size();
		di_gamma = new double[T-1][numStates][numStates];
		gamma = new double[T][numStates];
		
		for(int t = 0; t < T-1; t++){
			// Denominator is different from formula because of scaling
			double denom = 0;
			for(int i = 0; i < numStates; i++){
				for (int j = 0; j < numStates; j++) {
					denom += alpha[t][i] * state_transion_prob.get(i, j) * state_emission_prob.get(j, O.get(t+1)) * beta[t+1][j];
				}
			}
			
//			for(int i = 0; i < N; i++){
//				denom += alpha[T-1][i];
//			}
			
			// denom = 1/denom;

			for(int i = 0; i < numStates; i++){
				for (int j = 0; j < numStates; j++) {
					di_gamma[t][i][j] = alpha[t][i] * state_transion_prob.get(i, j) * state_emission_prob.get(j, O.get(t+1)) *
						divide(beta[t+1][j], denom); // * denom;
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
		
		for (int i = 0; i < numStates; i++) {
			gamma[T-1][i] = divide(alpha[T-1][i], denom); // *denom;
		}
		
	}
	
	private void fixit(ArrayList<Integer> O) {
	
		int T = O.size();
		
		double error = measure();
		
		// If something goes wrong, stop!
		if(! Double.isFinite(error) ) return;
		
		initial_state_dist = new Matrix(gamma[0]); // safe link
	
		for(int i = 0; i < numStates; i++){
			for(int j = 0; j < numStates; j++){
				double num = 0, denom = 0;
				
				for(int t = 0; t < T - 1; t++){
					num += di_gamma[t][i][j];
					denom += gamma[t][i];
				}
				
				state_transion_prob.set(i, j, num/denom);
				
			}
		}
		

		for(int i = 0; i < numStates; i++){
			for(int j = 0; j < numEmissions; j++){
				double num = 0, denom = 0;
				
				for(int t = 0; t < T - 1; t++){
					num += (O.get(t) == j) ? gamma[t][i] : 0;
					denom += gamma[t][i];
				}
				
				state_emission_prob.set(i, j, num/denom);
				
			}
		}
	}
	
	/***
	 * 
	 * Call forward() first!
	 * 
	 * @return logarithmic (due to underflow) probability of seen observation sequence
	 */
	private double measure() {
		
//		for(double d : scaling_factor)
//			System.err.print(d + " ");
//		System.err.println();
		
		double magic = 0;
		int T = scaling_factor.length;
		
		for(int i = 0; i < T; i++){
			magic += Math.log(scaling_factor[i]);
		}
		
		return -magic;
	}
	
	/***
	 * 
	 * Optimizes the HMM for the given observation sequence
	 * This operation manipulates this object!
	 * 
	 * @param O observation sequence
	 */
	public void optimizeFor(ArrayList<Integer> O) {
		
//		System.err.println("\n Input Seq");
//		
//		for(Integer obs : O){
//			System.err.print(obs + " ");
//		}
//		System.err.println();
//		
//		System.err.println("Before");
//		prettyPrint();
		
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
		} while (niveau < currniveau && max < 100);
		
//		System.err.println("After");
//		prettyPrint();
		
//		System.err.println(max);
//		System.err.flush();
		

	}
	
	/***
	 * 
	 * Optimizes the HMM for the given observation sequence
	 * This operation manipulates this object!
	 * Like optimizeFor but smoothes the matrixes before to avoid NaN due to 0 in matrix
	 * 
	 * @param O observation sequence
	 */
	public void train(ArrayList<Integer> O) {

		this.smooth(0.1);
		this.optimizeFor(O);
		this.smooth(0.02);
		
	}
	
	public void smooth(double degree) {
		state_transion_prob.smooth(degree);
		state_emission_prob.smooth(degree);
		initial_state_dist.smooth(degree);
	}
	
	/***
	 * 
	 * For testing only
	 * 
	 */
	
	private final static double[][] A =
			{{.000, .000, .670, .330, .000},
	{.000, .930, .000, .000, .070},
	{.000, .486, .514, .000, .000},
	{1.000, .000, .000, .000, .000},
	{.178, .000, .000, .000, .822}};

	private final static double[][] B =
	{{.173, .127, .080, .080, .080, .218, .080, .080, .080},
	{.221, .124, .080, .144, .090, .085, .085, .090, .080},
	{.308, .080, .080, .130, .080, .080, .080, .080, .080},
	{.080, .080, .080, .080, .080, .357, .080, .080, .080},
	{.099, .080, .326, .080, .080, .093, .080, .080, .080}};

	private final static double[][] pi =
	{{.067, .067, .733, .067, .067}};
	
	private final static Integer[] obs = {0, 3, 1, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0 ,0, 2, 5, 2,
	 2, 2, 5, 2, 2, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 7, 7, 7, 7, 7, 7, 2,
	 2, 2, 3, 3, 0, 0, 0, 0, 0, 4, 0, 0, 3, 1, 3, 0, 3, 4, 3, 0, 1, 3, 0, 0, 0,
	 0, 3, 5, 5, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 3, 0, 1, 0, 3, 4, 0, 3,
	 0, 7, 6};
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		sc.useLocale(Locale.US);
		
		// Lambda lambda = new Lambda(new Matrix(A), new Matrix(B), new Matrix(pi));
		Lambda lambda = new Lambda(sc);
		
		// ArrayList<Integer> O = new ArrayList<>(Arrays.asList(obs));
		ArrayList<Integer> O = new ArrayList<>();
		int size = sc.nextInt();
		for(int i = 0; i < size; i++){
			O.add(sc.nextInt());
		}
		
		System.err.println(lambda.forward(O));
		
		lambda.prettyPrint();
		
		sc.close();

		
		// System.out.println(lambda.printA());
		// System.out.println(lambda.printB());

	}
	
	public static final double divide(double a, double b) {
		return b == 0 ? 0 : a/b;
	}
	
}
