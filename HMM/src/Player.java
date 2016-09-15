
// package dhunt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Player {
	
	final static int ASPECTED_NUM_BIRDS = 20;
	final static int ASPECTED_NUM_MOVES = 20;
	final static int ASPECTED_NUM_STATES = 3;

	List<List<Lambda>> hmms;
	List<List<List<Integer>>> moves;

	public Player() {
		
        hmms = new ArrayList<>(Constants.COUNT_SPECIES);
        moves = new ArrayList<>(Constants.COUNT_SPECIES);

		for (int i = 0; i < Constants.COUNT_SPECIES; i++) {
			
            hmms.add(new ArrayList<>(ASPECTED_NUM_BIRDS));
            moves.add(new ArrayList<>(ASPECTED_NUM_MOVES));
            
		}
	}

	/**
	 * Shoot!
	 *
	 * This is the function where you start your work.
	 *
	 * You will receive a variable pState, which contains information about all
	 * birds, both dead and alive. Each bird contains all past moves.
	 *
	 * The state also contains the scores for all players and the number of time
	 * steps elapsed since the last time this function was called.
	 *
	 * @param pState
	 *            the GameState object with observations etc
	 * @param pDue
	 *            time before which we must have returned
	 * @return the prediction of a bird we want to shoot at, or cDontShoot to
	 *         pass
	 */
	public Action shoot(GameState pState, Deadline pDue) {
		/*
		 * Here you should write your clever algorithms to get the best action.
		 * This skeleton never shoots.
		 */

		// This line chooses not to shoot.

    	// Never shoot in the first round, we don't no anything.
		
    	if(pState.getRound() < 7) return cDontShoot;
    	
    	int numBirds = pState.getNumBirds();
    	
    	if(pState.getBird(pState.getNumBirds() -1).getSeqLength() < 97) return cDontShoot;
    	
//    	for(int i = 0; i < numBirds; i++){
//    		Bird bird = pState.getBird(i);
//    		if(bird.isAlive()){
//
//    			ArrayList<Integer> mov = copyObservations(bird);
//    			
//    			int specie = majorityGuess(mov);
//    			
//    			if(specie == Constants.SPECIES_BLACK_STORK) continue;
//    			
//    			Lambda hmm = new Lambda(6, Constants.COUNT_MOVE);
//    			hmm.optimizeFor(mov);
//
//    			return new Action(i, hmm.nextEmission(mov)); // hmm.nextEmission(mov.size())
//    		}
//    	}
    	
//    	for(int i = 0; i < numBirds; i++){
//    		Bird bird = pState.getBird(i);
//    		if(bird.isAlive()){
//    			return new Action(i, majorityGuessMove(bird));
//    		}
//    	}

		return cDontShoot;

		// This line would predict that bird 0 will move right and shoot at it.
		// return Action(0, MOVE_RIGHT);
	}
	
	private int majorityGuess(Bird bird) {
	
		ArrayList<Integer> moves = copyObservations(bird);
		
		return majorityGuess(moves);
	}

	private int majorityGuess(ArrayList<Integer> moves) {
		double currV = Double.NEGATIVE_INFINITY;
		int currA = 0;
		for(int i = 0; i < Constants.COUNT_SPECIES; i++){
			double var = hmms.get(i).stream().
					mapToDouble(l -> l.forward(moves)).
					max().
					orElse(Double.NEGATIVE_INFINITY);
			// System.err.print(var + " ");
			if(var > currV){
				currA = i;
				currV = var;
			}
		}
		
		// System.err.println("final " + currV + " " + currA);
		
		return currA;
	}
	
	public int majorityGuessMove(Bird bird) {
		
		ArrayList<Integer> moves = copyObservations(bird);
		
		int specie = majorityGuess(moves);
		
		if(specie == Constants.SPECIES_BLACK_STORK) return -1;
		
		int[] posMoves = new int[Constants.COUNT_MOVE];
			
			hmms.get(specie).stream().
					mapToInt(l -> l.nextEmission(moves)).
					forEach(i -> posMoves[i]++);

		int move = 0, score = 0;
		for(int i = 0; i < Constants.COUNT_MOVE; i++){
			int newScore = posMoves[i];
			if(newScore > score){
				score = newScore;
				move = i;
			}
		}
		
		return move;
	}
	

	/**
	 * 
	 * Prints all moves of all birds
	 * 
	 * @param pState
	 *            the GameState to show
	 */
	@SuppressWarnings("unused")
	private static void printState(GameState pState) {
		int numBirds = pState.getNumBirds();
		for (int i = 0; i < numBirds; i++) {
			Bird curr = pState.getBird(i);
			int numMoves = curr.getSeqLength();
			System.err.println("\n \n Bird " + i);
			for (int j = 0; j < numMoves; j++) {
				System.err.print(curr.getObservation(j) + " ");
			}
		}

		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.err.println("\n NÄSTA");
	}

	/**
	 * Guess the species! This function will be called at the end of each round,
	 * to give you a chance to identify the species of the birds for extra
	 * points.
	 *
	 * Fill the vector with guesses for the all birds. Use SPECIES_UNKNOWN to
	 * avoid guessing.
	 *
	 * @param pState
	 *            the GameState object with observations etc
	 * @param pDue
	 *            time before which we must have returned
	 * @return a vector with guesses for all the birds
	 */

	public int[] guess(GameState pState, Deadline pDue) {
		/*
		 * Here you should write your clever algorithms to guess the species of
		 * each bird. This skeleton makes no guesses, better safe than sorry!
		 */

		int[] lGuess = new int[pState.getNumBirds()];

		if (pState.getRound() == 0) {
			// Constants.SPECIES_PIGEON = 0, so, that's kind of stupid
			// but who knows, maybe they change it
			Arrays.fill(lGuess, Constants.SPECIES_PIGEON);
		} else {
			for (int i = 0; i < pState.getNumBirds(); i++)
				lGuess[i] = majorityGuess(pState.getBird(i));
		}
		
		// Arrays.fill(lGuess, Constants.SPECIES_PIGEON);
		
//		System.err.println("\nGuessing");
//		printSpecies(lGuess);

		return lGuess;
	}

	private int guess_species(Bird bird) {
		
		ArrayList<Integer> moves = copyObservations(bird);
		ArrayList<Lambda> lasts = new ArrayList<>(Constants.COUNT_SPECIES);
		for(List<Lambda> specie : hmms){
			if(specie.isEmpty()) {
				lasts.add(null);
			} else {
				lasts.add(specie.get(specie.size() - 1));
			}
		}
		
		return Lambda.bestOf(lasts, moves); 
	}

	/*
	 *  Maybe you wanted copyObseravations(Bird) ?
	 */
	private int[] observe(Bird bird) {
		int num_of_state = bird.getSeqLength();
		int observations[] = new int[num_of_state];
		// Why #states and not bird.getSeqLength()? Hä?
		for (int state = 0; state < num_of_state; state++) {
			if (bird.wasDead(state)) {
				continue;
			}
			// that's strange --> ??
			observations[state] = bird.getObservation(state);
		}
		return observations;
	}

	/**
	 * If you hit the bird you were trying to shoot, you will be notified
	 * through this function.
	 *
	 * @param pState
	 *            the GameState object with observations etc
	 * @param pBird
	 *            the bird you hit
	 * @param pDue
	 *            time before which we must have returned
	 */
	public void hit(GameState pState, int pBird, Deadline pDue) {
//		System.err.println("HIT BIRD!!!");
	}

	@SuppressWarnings("unused")
	private static void printSpecies(GameState pState, int[] pSpecies) {

		System.err.println(pState.getRound());
		
		printSpecies(pSpecies);
	}

	private static void printSpecies(int[] pSpecies) {
		
		for (int sp : pSpecies)
			System.err.print(sp + " ");

		System.err.println("\n \n");

		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param bird
	 * @return all moves of this bird
	 */
	private static ArrayList<Integer> copyObservations(Bird bird) {
		ArrayList<Integer> arr = new ArrayList<>(bird.getSeqLength());
		for (int i = 0; i < bird.getSeqLength(); i++) {
			int move = bird.getObservation(i);
			if (move == Constants.MOVE_DEAD)
				break;
			else
				arr.add(move);
		}
		return arr;
	}

	/**
	 * If you made any guesses, you will find out the true species of those
	 * birds through this function.
	 *
	 * @param pState
	 *            the GameState object with observations etc
	 * @param pSpecies
	 *            the vector with species
	 * @param pDue
	 *            time before which we must have returned
	 */
	public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
		
		// printSpecies(pState, pSpecies);

		// re-estimate the model after finding the species
		for (int i = 0; i < pSpecies.length; i++) {

			if (pSpecies[i] != Constants.SPECIES_UNKNOWN || hmms.get(pSpecies[i]).size() > 15) {
				
	    		Bird bird = pState.getBird(i);
	    		
	    		ArrayList<Integer> bird_moves = copyObservations(bird);
	    		moves.get(pSpecies[i]).add(bird_moves);

	    		Lambda birdhmm = new Lambda(ASPECTED_NUM_STATES, Constants.COUNT_MOVE);
	    		birdhmm.train(bird_moves);
	    		birdhmm.train(bird_moves);
	    		hmms.get(pSpecies[i]).add(birdhmm);
	    		

			}

		}

	}

	public static final Action cDontShoot = new Action(-1, -1);
}
