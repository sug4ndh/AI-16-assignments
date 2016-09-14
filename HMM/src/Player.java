package dhunt;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Player {
    
    int num_of_species = Constants.COUNT_SPECIES;
    int num_of_moves = Constants.COUNT_MOVE;
    public ArrayList<List<Lambda>> listofbirds = new ArrayList<>();
    public Player() {
        
        for (int i=0;i<num_of_species;i++){
            listofbirds.add(new ArrayList());
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
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */

        // This line chooses not to shoot.
    	
    	// Use this to see 'every' data we get
    	// printState(pState);
    	
        return cDontShoot; 
        
        

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    }
    
    /**
     * 
     * Prints all moves of all birds
     * 
     * @param pState the GameState to show
     */
    private void printState(GameState pState) {
    	int numBirds = pState.getNumBirds();
    	for(int i = 0; i < numBirds; i++){
    		Bird curr = pState.getBird(i);
    		int numMoves = curr.getSeqLength();
    		System.err.println("\n \n Bird " + i);
    		for(int j = 0; j < numMoves; j++){
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
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    

    
    public int[] Observe(Bird a) {
        int observations[] = new int[a.getSeqLength()];
        for (int i = 0; i < a.getSeqLength(); i++) {
            if(a.wasDead(i)){
                continue;
            }
            observations[i] = a.getObservation(i);
        }
        return observations;
    }
    
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */

      //  int[] lGuess = new int[pState.getNumBirds()];
      //  for (int i = 0; i < pState.getNumBirds(); ++i)
      //      lGuess[i] = Constants.SPECIES_PIGEON;
      //  return lGuess;
        
        //Random randgen = new Random();
        int[] lGuess = new int[pState.getNumBirds()];
        //int guess;

        if (pState.getRound() == 0) {
            for (int i = 0; i < lGuess.length; i++) {
                //guess = randgen.nextInt(6);
                lGuess[i] = Constants.SPECIES_PIGEON;
            }
        } else {
            for (int i = 0; i < pState.getNumBirds(); i++) 
                lGuess[i] = find_bird(pState.getBird(i));
        }

        return lGuess;
    }
    //}

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    	
    	/*for(int sp : pSpecies) System.err.println(sp);
    	
    	System.err.println("\n \n");
    	
    	System.err.println(pState.getRound());
    	
    	try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	*/
    	
    	//re-estimate the model after finding the species
        for (int i = 0; i < pSpecies.length; i++) {

            if(pSpecies[i]!=Constants.SPECIES_UNKNOWN){

                listofbirds.get(pSpecies[i]).add(new Lambda(num_of_species, num_of_moves));

                listofbirds.get(pSpecies[i]).get(listofbirds.get(pSpecies[i]).size()-1).fixit(Observe(pState.getBird(i)));

            }

        }

    }

    public static final Action cDontShoot = new Action(-1, -1);
}

