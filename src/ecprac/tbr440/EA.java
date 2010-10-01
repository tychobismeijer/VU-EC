package ecprac.daniel;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class EA {

        int population_size = 10;    
        Genome[] population = new Genome[population_size];

	public void run() {

		// initialize population
		initialize();

		Utilities.saveGenome(population[0], "best.genome");
	}

	private void initialize() {
              for (NeuralNetwork genome in popultion) {
                  genome.nn = new NeuralNetwork;

              }
	}

        private void mutate() {
            //TODO
        }

        private void recombine() {
            //TODO
        }

        public void show() {
            //TODO
        }

	public static void main(String[] args) {
		
		/*
		 * 
		 * Start without arguments to run the EA, start with -show to show the best found
		 * 
		 */
		
		if(args.length > 0 && args[0].equals("-show")){
			new EA().show();
		} else {
			new EA().run();
		}
	}

}
