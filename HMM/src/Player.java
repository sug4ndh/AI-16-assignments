
// package dhunt;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Player {
	
	final static int ASPECTED_NUM_BIRDS = 20;
	// final static int ASPECTED_NUM_MOVES = 20;
	final static int ASPECTED_NUM_STATES = 3;

	// Do we need all hmms? I don't think so... Just pick the last
	// ArrayList<List<Lambda>> listofbirds;
	Lambda[] hmms;
	List<List<List<Integer>>> moves;

	public Player() {
		
        hmms = new Lambda[Constants.COUNT_SPECIES];
        moves = new ArrayList<>(Constants.COUNT_SPECIES);

		for (int i = 0; i < Constants.COUNT_SPECIES; i++) {
			
			// listofbirds.add(new ArrayList());
            hmms[i] = new Lambda(ASPECTED_NUM_STATES, Constants.COUNT_MOVE);
            moves.add(new ArrayList<>(ASPECTED_NUM_BIRDS));
            
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
    	if(pState.getRound() == 0) return cDontShoot;

		return cDontShoot;

		// This line would predict that bird 0 will move right and shoot at it.
		// return Action(0, MOVE_RIGHT);
	}

	/**
	 * 
	 * Prints all moves of all birds
	 * 
	 * @param pState
	 *            the GameState to show
	 */
	@SuppressWarnings("unused")
	private void printState(GameState pState) {
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
			// TODO Auto-generated catch block
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
		// int guess;

		if (pState.getRound() == 0) {
			for (int i = 0; i < lGuess.length; i++) {
				// Constants.SPECIES_PIGEON = 0, so, that's kind of stupid
				// but who knows, maybe they change it
				Arrays.fill(lGuess, Constants.SPECIES_PIGEON);
			}
		} else {
			for (int i = 0; i < pState.getNumBirds(); i++)
				lGuess[i] = guess_species(pState.getBird(i));
		}

		return lGuess;
	}

	private int guess_species(Bird bird) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int[] observe(Bird bird) {
		int num_of_state = bird.getSeqLength();
		int observations[] = new int[num_of_state];
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
		System.err.println("HIT BIRD!!!");
	}

	@SuppressWarnings("unused")
	private void printSpecies(GameState pState, int[] pSpecies) {
		for (int sp : pSpecies)
			System.err.print(sp + " ");

		System.err.println("\n \n");

		System.err.println(pState.getRound());

		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<Integer> copyObservations(Bird bird) {
    	ArrayList<Integer> arr = new ArrayList<>(bird.getSeqLength());
		for(int i = 0; i < bird.getSeqLength(); i++){
			arr.set(i, bird.getObservation(i));
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

		// re-estimate the model after finding the species
		for (int i = 0; i < pSpecies.length; i++) {

			if (pSpecies[i] != Constants.SPECIES_UNKNOWN) {

//				listofbirds.get(pSpecies[i]).add(new Lambda(Constants.COUNT_SPECIES, Constants.COUNT_MOVE));
//				listofbirds.get(pSpecies[i]).get(listofbirds.get(pSpecies[i]).size() - 1)
//						.optimizeFor(Observe(pState.getBird(i)));
				
	    		Bird bird = pState.getBird(i);
	    		ArrayList<Integer> bird_moves = copyObservations(bird);
	    		moves.get(pSpecies[i]).add(bird_moves);
	    		hmms[pSpecies[i]].optimizeFor(bird_moves);

			}

		}

	}

	public static final Action cDontShoot = new Action(-1, -1);
}
